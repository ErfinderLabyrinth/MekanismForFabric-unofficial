package mekanism.api.chemical.slurry;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public final class EmptySlurry extends Slurry {

    public EmptySlurry() {
        super(SlurryBuilder.clean().hidden());
    }

//    @NotNull
//    @Override
//    protected Optional<IReverseTag<Slurry>> getReverseTag() {
//        //Empty slurry is in no tags
//        return Optional.empty();
//    }
}