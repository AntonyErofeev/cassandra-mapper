package ru.tflow.mapping;

import org.junit.Before;
import org.junit.Test;
import ru.tflow.mapping.entity.CompoundKeyEntity;
import ru.tflow.mapping.exceptions.DuplicateKeyException;
import ru.tflow.mapping.repository.CompoundRepository;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 *
 * Created by nagakhl on 3/28/2014.
 */
public class TestCompoundRepository {

    private CompoundRepository repository;

    private CompoundKeyEntity entity1, entity2, entity3;

    @Before
    public void init() throws UnknownHostException {
        repository = new CompoundRepository();
        repository.initTable();
        UUID uuid = UUID.randomUUID();
        entity1 = new CompoundKeyEntity();
        entity1.setClusteringId(uuid);
        entity1.setAddress(InetAddress.getLocalHost());
        entity1.setNumber(BigDecimal.ONE);
        entity1.setPartId(10);
        entity1.setTime(ZonedDateTime.now());

        entity2 = new CompoundKeyEntity();
        entity2.setClusteringId(uuid);
        entity2.setAddress(InetAddress.getLocalHost());
        entity2.setNumber(BigDecimal.ONE);
        entity2.setPartId(20);
        entity2.setTime(ZonedDateTime.now());

        entity3 = new CompoundKeyEntity();
        entity3.setClusteringId(uuid);
        entity3.setAddress(InetAddress.getLocalHost());
        entity3.setNumber(BigDecimal.ONE);
        entity3.setPartId(30);
        entity3.setTime(ZonedDateTime.now());
    }

    @Test
    public void test() throws Exception {
        testSave();
        testFindOne();
        testFind();
        testFindAll();
    }

    protected void testSave() {
        repository.save(entity1);
        repository.save(entity2);
        repository.save(entity3);
    }

    protected void testFindOne() {
        assertEquals(entity1, repository.findOne(entity1.getClusteringId(), entity1.getTime(), entity1.getPartId()).get());
        try {
            repository.findOne(entity1.getClusteringId());
            fail("Should throw exception on attempt to find one.");
        } catch (DuplicateKeyException ex) {
            //do nothing here
        }
        assertEquals(entity2, repository.find(entity2.getClusteringId(), entity2.getTime(), entity2.getPartId()));
        assertEquals(entity3, repository.find(entity3.getClusteringId(), entity3.getTime(), entity3.getPartId()));
    }

    protected void testFindAll() {
        assertArrayEquals(new CompoundKeyEntity[]{entity1, entity2, entity3}, repository.findAll(Integer.MAX_VALUE).toArray(new CompoundKeyEntity[3]));
    }

    protected void testFind() {
        assertArrayEquals(new CompoundKeyEntity[]{entity1, entity2, entity3}, repository.find(entity1.getClusteringId()).toArray(new CompoundKeyEntity[3]));
    }

}
