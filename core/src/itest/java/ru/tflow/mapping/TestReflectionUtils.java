package ru.tflow.mapping;

import org.junit.Test;
import ru.tflow.mapping.utils.ReflectionUtils;
import ru.tflow.mapping.utils.Tuple2;

import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * Created by nagakhl on 5/26/2014.
 */
public class TestReflectionUtils {

    @Test
    public void test() {

        Object o = new Object();

        List rawList = new LinkedList();

        List<String> genericList = new ArrayList<>();

        assertFalse(ReflectionUtils.isGenericType(o.getClass()));
        assertTrue(ReflectionUtils.isGenericType(rawList.getClass()));
        assertTrue(ReflectionUtils.isGenericType(genericList.getClass()));
        assertTrue(ReflectionUtils.isGenericType(new IntegerTuple2(0, 0).getClass()));

    }

    class IntegerTuple2 extends Tuple2<Integer, Integer> {

        public IntegerTuple2(Integer element1, Integer element2) {
            super(element1, element2);
        }
    }

}
