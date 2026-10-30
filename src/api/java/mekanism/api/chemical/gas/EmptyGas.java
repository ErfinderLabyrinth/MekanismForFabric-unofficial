package mekanism.api.chemical.gas;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public final class EmptyGas extends Gas {

    public EmptyGas() {
        super(GasBuilder.builder().hidden());
    }

//    @NotNull
//    @Override
//    protected Optional<IReverseTag<Gas>> getReverseTag() {
//        //Empty gas is in no tags
//        return Optional.empty();
//    }
}