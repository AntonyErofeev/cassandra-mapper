package ru.tflow.mapping;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.TableMetadata;
import ru.tflow.mapping.exceptions.CorruptedMappingException;
import ru.tflow.mapping.utils.MappingUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * Interface defining basic methods (and default implementation for them) capable of managing mapped datatable
 * <p/>
 * Created by nagakhl on 3/24/2014.
 */
public interface DatatableManager<E, K> extends CassandraRepository<E, K> {

    /**
     * Initialize data table for entity. If table doesn't exist - create it, else - try to update.
     */
    public default void initTable() {
        EntityMetadata m = conf().metadata(getClass());

        TableMetadata tm = conf().session().getCluster().getMetadata().getKeyspace(conf().keyspace()).getTable(m.getTable());

        if (tm == null) {
            log.info("=====> Table {} for entity {} not found. Creating new.", m.getTable(), m.getEntityClass().getName());

            String command = createTableCommand();
            log.info("=====> Command for table creation is: \n{}", command);

            conf().session().execute(command);
        } else {
            log.info("=====> Table {} for entity {} found. Checking if update is needed.", m.getTable(), m.getEntityClass().getName());

            List<String> updateCommands = updateTableCommands(tm);
            if (updateCommands.isEmpty()) {
                log.info("=====> No changes found.");
            } else {
                log.info("=====> Generated update commands are: \n{}", updateCommands.stream().collect(joining("\n")));
                updateCommands.forEach(c -> conf().session().execute(c));
            }
        }
    }

    /**
     * Generate table creation CQL command
     *
     * @return Table creation command
     */
    public default String createTableCommand() {
        String template = "create table %s.%s (%s, primary key(%s))";

        String fieldsString = conf().fields(getClass()).stream()
            .map(md -> md.getName() + " " + MappingUtils.formatFieldType(md))
            .collect(joining(", "));

        List<FieldMetadata> keys = new ArrayList<>();
        keys.add(conf().metadata(getClass()).getPrimaryKey());
        keys.addAll(conf().metadata(getClass()).getKeys());

        String keysString = keys.stream().map(FieldMetadata::getName).collect(joining(", "));

        return String.format(template, conf().keyspace(), conf().metadata(getClass()).getTable(), fieldsString, keysString);
    }

    /**
     * Generate list of update commands to alter table to consistent state
     *
     * @param tm TableMetadata
     * @return List of commands (empty if there is nothing to update)
     */
    public default List<String> updateTableCommands(TableMetadata tm) {
        String addColumnTemplate = "alter table %s.%s add %s %s";
        String dropColumnTemplate = "alter table %s.%s drop %s";
        List<String> commands = new ArrayList<>();

        conf().metadata(getClass()).getFields().forEach(f -> {
            if (tm.getColumn(f.getName()) != null) {
                DataType columnType = tm.getColumn(f.getName()).getType();

                DataType cBytesType = DataType.custom("org.apache.cassandra.db.marshal.BytesType");

                //Table metadata returns this type for blob mappings instead of original one
                if (columnType.equals(cBytesType)) {
                    columnType = DataType.blob();
                }

                //The same strange behaviour with blobs for type arguments of collections
                if (columnType.isCollection() && columnType.getTypeArguments().contains(cBytesType)) {
                    if (columnType.equals(DataType.list(columnType.getTypeArguments().get(0)))) {
                        columnType = DataType.list(DataType.blob());
                    } else if (columnType.equals(DataType.set(columnType.getTypeArguments().get(0)))) {
                        columnType = DataType.set(DataType.blob());
                    } else if (columnType.equals(DataType.map(columnType.getTypeArguments().get(0), columnType.getTypeArguments().get(1)))) {
                        columnType = DataType.map(
                                columnType.getTypeArguments().get(0).equals(cBytesType) ? DataType.blob() : columnType.getTypeArguments().get(0),
                                columnType.getTypeArguments().get(1).equals(cBytesType) ? DataType.blob() : columnType.getTypeArguments().get(1));
                    }
                }

                DataType mappedType = f.getFieldType().getMappedType();
                if (!columnType.equals(mappedType)) {
                    //A strange bug or feature of driver||database. Blob type in datatable is interpreted as custom type, not DataType.Name.BLOB
                    log.debug("===> Checking column: {}. Type is: {}, Corresponding field type is {}",
                        tm.getColumn(f.getName()), tm.getColumn(f.getName()).getType(), f.getFieldType().getMappedType());
                    if (!conf().failOnUpdate()) {
                        commands.add(String.format(dropColumnTemplate, conf().keyspace(), conf().metadata(getClass()).getTable(), f.getName()));
                        commands.add(String.format(addColumnTemplate,
                            conf().keyspace(), conf().metadata(getClass()).getTable(), f.getName(), MappingUtils.formatFieldType(f)));
                    } else {
                        throw new CorruptedMappingException(
                            String.format("Cannot update table %s, column %s is of type %s instead of %s ",
                                conf().metadata(getClass()).getTable(),
                                f.getName(),
                                tm.getColumn(f.getName()).getType().getName().toString(),
                                f.getFieldType().getMappedType().getName().toString()), conf().metadata(getClass()).getEntityClass()
                        );
                    }

                }
            } else {
                commands.add(String.format(addColumnTemplate,
                    conf().keyspace(), conf().metadata(getClass()).getTable(), f.getName(), MappingUtils.formatFieldType(f)));
            }
        });
        return commands;
    }

}
