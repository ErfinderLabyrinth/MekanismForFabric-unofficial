package mekanism.common.capabilities.holder;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public abstract class ProxiedHolder<T> implements IHolder<T> {

    private final Predicate<Direction> insertPredicate;
    private final Predicate<Direction> extractPredicate;

    protected ProxiedHolder(Predicate<Direction> insertPredicate, Predicate<Direction> extractPredicate) {
        this.insertPredicate = insertPredicate;
        this.extractPredicate = extractPredicate;
    }

    @Override
    public boolean canInsert(@Nullable Direction side) {
        return insertPredicate.test(side);
    }

    @Override
    public boolean canExtract(@Nullable Direction side) {
        return extractPredicate.test(side);
    }
}