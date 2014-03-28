package ru.tflow.mapping;

import org.junit.Before;
import org.junit.Test;
import ru.tflow.mapping.entity.CompoundKeyEntity;
import ru.tflow.mapping.repository.CompoundRepository;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 *
 * Created by nagakhl on 3/28/2014.
 */
public class TestCompoundRepository {

    private static AbstractMapperConfiguration conf;

    private CompoundRepository repository;

    private CompoundKeyEntity entity1,entity2, entity3;

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

    }

    protected void testSave() {
        repository.save(entity1);
        repository.save(entity2);
        repository.save(entity3);
    }

    protected void testFindOne() {
//        repository.get
    }

    protected void testFindAll() {

    }

}
