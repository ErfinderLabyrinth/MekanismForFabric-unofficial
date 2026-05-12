package mekanism.api.functions;

public interface TriPredicate<T, T1, T2> {
    boolean test(T t, T1 t1, T2 t2);
}
