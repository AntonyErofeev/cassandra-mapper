package ru.tflow.mapping;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.tflow.mapping.entity.SimpleEntity;
import ru.tflow.mapping.repository.ConfigurationHolder;

/**
 *
 * Created by nagakhl on 3/25/2014.
 */
public class TestCassandraRepository {

    private static AbstractMapperConfiguration conf;

    private SimpleEntity.AdvancedSimpleEntity

    @BeforeClass
    public static void init() {
        conf = ConfigurationHolder.configuration();
    }

    @Test
    public void test() throws Exception {
        testInit();
        testSave();
        testRetrieveAll();
        testRetrieveOne();
    }

    protected void testInit() {

    }

    protected void testSave() {

    }

    protected void testRetrieveOne() {

    }

    protected void testRetrieveAll() {

    }
}
