package ru.tflow.mapping;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.tflow.mapping.repository.AdvancedSimpleEntityRepository;
import ru.tflow.mapping.repository.ConfigurationHolder;
import ru.tflow.mapping.repository.SimpleEntityRepository;

import static org.junit.Assert.assertTrue;

/**
 *
 * Created by nagakhl on 3/25/2014.
 */
public class TestMappingResolver {

    private static AbstractMapperConfiguration conf;

    @BeforeClass
    public static void init() {
        conf = ConfigurationHolder.configuration();
    }

    @Test
    public void test() throws Exception {
        dropAllBeforeTest();
        testCreateSimple();
        testCreateAdvanced();
    }

    protected void dropAllBeforeTest() {
        conf.session().getCluster().getMetadata().getKeyspace(conf.keyspace()).getTables().forEach(
            t -> conf.session().execute("drop table " + conf.keyspace() + "." + t.getName())
        );
    }

    protected void testCreateSimple() {
        SimpleEntityRepository s = new SimpleEntityRepository();
        s.initTable();
        assertTrue(conf.session().getCluster().getMetadata().getKeyspace(conf.keyspace()).getTables().stream().filter(t -> t.getName().equals("simple")).findAny().isPresent());
    }

    protected void testCreateAdvanced() {
        AdvancedSimpleEntityRepository a = new AdvancedSimpleEntityRepository();
        a.initTable();
    }
}
