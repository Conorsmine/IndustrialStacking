package com.conorsmine.net.industrialstacking;

public class Tuple<T, E> {

    private final T t;
    private final E e;

    public Tuple(T t, E e) {
        this.t = t;
        this.e = e;
    }

    public T getT() {
        return t;
    }

    public E getE() {
        return e;
    }
}
