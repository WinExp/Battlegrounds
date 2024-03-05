package com.github.winexp.battlegrounds.util.function;

@FunctionalInterface
public interface TripleConsumer<A, B, C> {
    void accept(A a, B b, C c);
}
