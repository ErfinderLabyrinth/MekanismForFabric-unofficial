package mekanism.common.storage.util;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public interface TransactionBiPredicate<T, U> extends BiPredicate<T, U> {
    @Override
    boolean test(T t, U u);

    default boolean test(T t, U u, @Nullable TransactionContext transaction) {
        return test(t, u);
    }

    static <T, U> TransactionBiPredicate<T, U> of(BiPredicate<T, U> predicate) {
        return predicate::test;
    }

    //Helper for lambdas
    static <T, U> WithTransaction<T, U> withTransaction(WithTransaction<T, U> predicate) {
        return predicate;
    }

    interface WithTransaction<T, U> extends TransactionBiPredicate<T, U> {
        @Override
        default boolean test(T t, U u) {
            return test(t, u, null);
        }

        @Override
        boolean test(T t, U u, @Nullable TransactionContext transaction);
    }
}
