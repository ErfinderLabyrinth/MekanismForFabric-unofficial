package mekanism.common.storage.util;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public interface TransactionPredicate<T> extends Predicate<T> {
    @Override
    boolean test(T t);

    default boolean test(T t, @Nullable TransactionContext transaction) {
        return test(t);
    }

    static <T> TransactionPredicate<T> of(Predicate<T> predicate) {
        return predicate::test;
    }

    //Helper for lambdas
    static <T> WithTransaction<T> withTransaction(WithTransaction<T> predicate) {
        return predicate;
    }

    interface WithTransaction<T> extends TransactionPredicate<T> {
        @Override
        default boolean test(T t) {
            return test(t, null);
        }

        @Override
        boolean test(T t, @Nullable TransactionContext transaction);
    }
}
