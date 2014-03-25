package ru.tflow.mapping.utils;

/**
 * Created with IntelliJ IDEA.
 * User: erofeev
 * Date: 12/2/13
 * Time: 11:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class Tuple2<E1, E2> {

    private final E1 element1;

    private final E2 element2;

    public Tuple2(E1 element1, E2 element2) {
        this.element1 = element1;
        this.element2 = element2;
    }

    public E2 getElement2() {
        return element2;
    }

    public E1 getElement1() {
        return element1;
    }
}
