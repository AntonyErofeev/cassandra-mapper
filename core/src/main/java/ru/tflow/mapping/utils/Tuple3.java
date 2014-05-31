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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple3)) return false;

        Tuple3 tuple3 = (Tuple3) o;

        return !(element1 != null ? !element1.equals(tuple3.element1) : tuple3.element1 != null)
            && !(element2 != null ? !element2.equals(tuple3.element2) : tuple3.element2 != null)
            && !(element3 != null ? !element3.equals(tuple3.element3) : tuple3.element3 != null);

    }

    @Override
    public int hashCode() {
        int result = element1 != null ? element1.hashCode() : 0;
        result = 31 * result + (element2 != null ? element2.hashCode() : 0);
        result = 31 * result + (element3 != null ? element3.hashCode() : 0);
        return result;
    }
}
