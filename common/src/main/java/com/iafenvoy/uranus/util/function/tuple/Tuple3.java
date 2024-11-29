package com.iafenvoy.uranus.util.function.tuple;

public class Tuple3<T1, T2, T3> extends Tuple2<T1, T2> {
    private T3 t3;

    public Tuple3() {
        this(null, null, null);
    }

    public Tuple3(T1 t1, T2 t2, T3 t3) {
        this.set(t1, t2, t3);
    }

    public T3 getT3() {
        return t3;
    }

    public void setT3(T3 t3) {
        this.t3 = t3;
    }

    public void set(T1 t1, T2 t2, T3 t3) {
        this.set(t1, t2);
        this.t3 = t3;
    }
}

