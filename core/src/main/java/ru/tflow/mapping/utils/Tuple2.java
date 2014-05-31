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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple2)) return false;

        Tuple2 tuple2 = (Tuple2) o;

        return !(element1 != null ? !element1.equals(tuple2.element1) : tuple2.element1 != null)
            && !(element2 != null ? !element2.equals(tuple2.element2) : tuple2.element2 != null);

    }

    @Override
    public int hashCode() {
        int result = element1 != null ? element1.hashCode() : 0;
        result = 31 * result + (element2 != null ? element2.hashCode() : 0);
        return result;
    }
}
