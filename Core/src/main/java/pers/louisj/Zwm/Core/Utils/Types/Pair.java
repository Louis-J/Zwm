package pers.louisj.Zwm.Core.Utils.Types;

import java.lang.reflect.Array;

public class Pair<T1, T2> {
    public T1 t1;
    public T2 t2;

    public Pair() {
    }

    public Pair(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    @Override
    public String toString() {
        return "Pair(" + t1 + ", " + t2 + ")";
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2> Pair<T1, T2>[] createArray(int n, Class<T1> c1, Class<T2> c2) {
        return (Pair<T1, T2>[])Array.newInstance(Pair.class, n);
    }
}

