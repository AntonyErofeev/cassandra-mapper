package ru.tflow.mapping.extended;

import org.junit.Before;
import org.junit.Test;
import ru.tflow.mapping.extended.entity.JsonContainingEntity;
import ru.tflow.mapping.extended.repository.JsonContainingEntityRepository;

import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * User: nagakhl
 * Date: 05.06.2014
 * Time: 16:29
 */
public class TestJsonContainingEntityRepository {

    private JsonContainingEntityRepository repository;

    private JsonContainingEntity entity;

    @Before
    public void setup() {
        repository = new JsonContainingEntityRepository();
        repository.initTable();

        entity = new JsonContainingEntity();
        entity.setUid(UUID.randomUUID());

        JsonContainingEntity.SomeData data = new JsonContainingEntity.SomeData();
        data.setName("some name");
        data.setValue(1);

        Map<String, Object> params = new HashMap<>();
        params.put("k", "v");
        params.put("ak", UUID.randomUUID().toString());
        params.put("lk", Arrays.asList("one", "two", "three"));
        data.setParameters(params);

        entity.setData(data);

        entity.setAdditionalData(params);
        repository.save(entity);
    }

    @Test
    public void test() {
        Optional<JsonContainingEntity> one = repository.findOne(entity.getUid());
        assertTrue(entity.equals(one.get()));
    }
}
