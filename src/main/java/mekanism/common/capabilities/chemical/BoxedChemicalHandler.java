package mekanism.common.capabilities.chemical;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.Slurry;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@ParametersAreNotNullByDefault
public class BoxedChemicalHandler {

    private final Map<ChemicalType, Optional<? extends Storage<?>>> handlers = new EnumMap<>(ChemicalType.class);

    @Nullable
    @SuppressWarnings("unchecked")
    public <CHEMICAL extends Chemical<CHEMICAL>> Storage<CHEMICAL> getHandlerFor(ChemicalType chemicalType) {
        if (handlers.containsKey(chemicalType)) {
            Optional<? extends Storage<?>> handler = handlers.get(chemicalType);
            if (handler.isPresent()) {
                return (Storage<CHEMICAL>) handler.get();
            }
        }
        return null;
    }

    public void addGasHandler(Optional<Storage<Gas>> handler) {
        handlers.put(ChemicalType.GAS, handler);
    }

    public void addInfusionHandler(Optional<Storage<InfuseType>> handler) {
        handlers.put(ChemicalType.INFUSION, handler);
    }

    public void addPigmentHandler(Optional<Storage<Pigment>> handler) {
        handlers.put(ChemicalType.PIGMENT, handler);
    }

    public void addSlurryHandler(Optional<Storage<Slurry>> handler) {
        handlers.put(ChemicalType.SLURRY, handler);
    }

    public boolean sameHandlers(BoxedChemicalHandler other) {
        return this == other || handlers.size() == other.handlers.size() && handlers.entrySet().stream().noneMatch(entry -> entry.getValue() != other.handlers.get(entry.getKey()));
    }

    public void addRefreshListeners(Consumer<Optional<BoxedChemicalHandler>> refreshListener) {
        //TODO - V11: Make the listener only have to invalidate specific sub pieces
        for (Optional<? extends Storage<?>> sourceAcceptor : handlers.values()) {
            //Use unchecked generics to add the listener to the source acceptor
//            CapabilityUtils.addListener(sourceAcceptor, refreshListener);
        }
    }
}