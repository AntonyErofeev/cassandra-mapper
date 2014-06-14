package ru.tflow.mapping;

import org.junit.Before;
import org.junit.Test;
import ru.tflow.mapping.entity.SimpleEntity;
import ru.tflow.mapping.repository.AdvancedSimpleEntityRepository;

import java.net.URL;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;
import static ru.tflow.mapping.entity.SimpleEntity.AdvancedSimpleEntity;

/**
 *
 * Created by nagakhl on 3/25/2014.
 */
public class TestSimpleEntityRepository {

    private AdvancedSimpleEntity entity, entity2;

    private AdvancedSimpleEntityRepository repository;

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
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        entity.setDate(calendar.getTime());

        entity2 = new AdvancedSimpleEntity();
        entity2.setKey(UUID.randomUUID());
        entity2.setName("some_name2");
        entity2.setData(ByteBuffer.wrap(new byte[]{0x01, 0x02, 0x03, 0x04}));
        entity2.settString("temp2");
        entity2.setDt(ZonedDateTime.now());
        entity2.setType(SimpleEntity.Type.ADVANCED);
        entity2.setUrl(new URL("http://localhost:9999/"));
        GregorianCalendar calendar2 = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        entity2.setDate(calendar2.getTime());
    }

    @Test
    public void test() throws Exception {
        testSave();
        testRetrieveOne();
        testRetrieveAll();
    }

    protected void testSave() {
        repository.save(entity);
        repository.save(entity2);
    }

    protected void testRetrieveOne() {
        Optional<AdvancedSimpleEntity> _entity = repository.findOne(entity.getKey());
        assertTrue(entity.equals(_entity.get()));

        List<AdvancedSimpleEntity> several = repository.findSeveral(entity.getKey(), entity2.getKey());
        assertTrue(several.contains(entity));
        assertTrue(several.contains(entity2));
    }

    protected void testRetrieveAll() {
        List<AdvancedSimpleEntity> all = repository.findAll(Integer.MAX_VALUE);
        Stream<AdvancedSimpleEntity> stream = all.stream().filter(e -> e.equals(entity));
        assertTrue(stream.findAny().isPresent());
    }
}
