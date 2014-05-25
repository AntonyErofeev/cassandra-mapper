package ru.tflow.mapping.utils;

/**
 * Three element immutable tuple
 * <p/>
 * Created by erofeev on 12/10/13.
 */
public class Tuple3<E1, E2, E3> {

    private final E1 element1;

    private final E2 element2;

    private final E3 element3;

    public Tuple3(E1 element1, E2 element2, E3 element3) {
        this.element1 = element1;
        this.element2 = element2;
        this.element3 = element3;
    }

    public E1 getElement1() {
        return element1;
    }

    public E2 getElement2() {
        return element2;
    }

    public E3 getElement3() {
        return element3;
    }
}
