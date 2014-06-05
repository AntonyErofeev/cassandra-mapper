package ru.tflow.mapping;

import org.junit.Before;
import org.junit.Test;
import ru.tflow.mapping.entity.CollectionContainingEntity;
import ru.tflow.mapping.repository.CollectionEntityRepository;

import java.net.URL;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * User: nagakhl
 * Date: 05.06.2014
 * Time: 13:15
 */
public class TestCollectionEntityRepository {

    private CollectionEntityRepository repository;

    private CollectionContainingEntity entity;

    @Before
    public void setup() throws Exception {
        repository = new CollectionEntityRepository();
        repository.initTable();

        entity = new CollectionContainingEntity();

        entity.setUid(UUID.randomUUID());

        entity.setIntList(Arrays.asList(2, 5, 8, 907654, -1098, 0));
        entity.setDateTimeList(Arrays.asList(ZonedDateTime.now(), ZonedDateTime.now(ZoneId.of("UTC"))));

        Set<URL> urlSet = new HashSet<>();
        urlSet.addAll(Arrays.asList(new URL("http://localhost:8080/"), new URL("https://www.google.com/")));

        entity.setUrlSet(urlSet);

        Set<Instant> instantSet = new HashSet<>();
        instantSet.addAll(Arrays.asList(Instant.now(), Instant.ofEpochMilli(0)));

        entity.setInstantSet(instantSet);

        Map<String, CollectionContainingEntity.MapType> map1 = new HashMap<>();
        map1.put("key1", CollectionContainingEntity.MapType.ONE);
        map1.put("key2", CollectionContainingEntity.MapType.TWO);
        map1.put("key3", CollectionContainingEntity.MapType.THREE);

        entity.setStringKeyedMap(map1);

        Map<UUID, ByteBuffer> map2 = new HashMap<>();
        map2.put(UUID.randomUUID(), ByteBuffer.wrap(new byte[]{0x01, 0x02, 0xA}));
        map2.put(UUID.randomUUID(), ByteBuffer.wrap(new byte[]{0x05, 0x1A, 0xF}));

        entity.setUidMappedMap(map2);

        repository.save(entity);
    }

    @Test
    public void test() throws Exception {
        Optional<CollectionContainingEntity> _entity = repository.findOne(entity.getUid());
        assertTrue(entity.equals(_entity.get()));
    }


}
