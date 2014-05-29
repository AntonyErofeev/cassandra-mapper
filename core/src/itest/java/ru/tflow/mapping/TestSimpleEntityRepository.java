package ru.tflow.mapping;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.tflow.mapping.entity.SimpleEntity;
import ru.tflow.mapping.repository.AdvancedSimpleEntityRepository;
import ru.tflow.mapping.repository.ConfigurationHolder;

import java.net.URL;
import java.nio.ByteBuffer;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;
import static ru.tflow.mapping.entity.SimpleEntity.AdvancedSimpleEntity;

/**
 *
 * Created by nagakhl on 3/25/2014.
 */
public class TestSimpleEntityRepository {

    private static AbstractMapperConfiguration conf;

    private AdvancedSimpleEntity entity;

    private AdvancedSimpleEntityRepository repository;

    @BeforeClass
    public static void init() {
        conf = ConfigurationHolder.configuration();
    }

    @Before
    public void dInit() throws Exception {
        repository = new AdvancedSimpleEntityRepository();
        repository.initTable();
        entity = new AdvancedSimpleEntity();
        entity.setKey(UUID.randomUUID());
        entity.setName("some_name");
        entity.setData(ByteBuffer.wrap(new byte[]{0x01, 0x02, 0x03, 0x04}));
        entity.settString("temp");
        entity.setDt(ZonedDateTime.now());
        entity.setType(SimpleEntity.Type.ADVANCED);
        entity.setUrl(new URL("http://localhost:8888/"));
    }

    @Test
    public void test() throws Exception {
        testSave();
        testRetrieveAll();
        testRetrieveOne();
    }

    protected void testSave() {
        repository.save(entity);
    }

    protected void testRetrieveOne() {
        Optional<AdvancedSimpleEntity> _entity = repository.findOne(entity.getKey());
        _entity.get().setDt(_entity.get().getDt().withZoneSameInstant(ZoneId.systemDefault()));
        assertTrue(entity.equals(_entity.get()));
    }

    protected void testRetrieveAll() {
        List<AdvancedSimpleEntity> all = repository.findAll(Integer.MAX_VALUE);
        all.forEach(e -> e.setDt(e.getDt().withZoneSameInstant(ZoneId.systemDefault())));
        Stream<AdvancedSimpleEntity> stream = all.stream().filter(e -> e.equals(entity));
        assertTrue(stream.findAny().isPresent());
    }
}
