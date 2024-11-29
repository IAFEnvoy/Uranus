package com.iafenvoy.uranus.util.function.tuple;

public class Tuple4<T1, T2, T3, T4> extends Tuple3<T1, T2, T3> {
    private T4 t4;

    public Tuple4() {
        this(null, null, null, null);
    }

    public Tuple4(T1 t1, T2 t2, T3 t3, T4 t4) {
        this.set(t1, t2, t3, t4);
    }

    public T4 getT4() {
        return t4;
    }

    public void setT4(T4 t4) {
        this.t4 = t4;
    }

    public void set(T1 t1, T2 t2, T3 t3, T4 t4) {
        this.set(t1, t2, t3);
        this.t4 = t4;
    }
}

