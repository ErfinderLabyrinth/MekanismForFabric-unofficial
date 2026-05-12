package mekanism.common.capabilities.chemical;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.slurry.ISlurryHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@ParametersAreNotNullByDefault
public class BoxedChemicalHandler {

    private final Map<ChemicalType, Optional<? extends IChemicalHandler<?, ?, ?>>> handlers = new EnumMap<>(ChemicalType.class);

    @Nullable
    @SuppressWarnings("unchecked")
    public <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> IChemicalHandler<CHEMICAL, STACK, ?> getHandlerFor(ChemicalType chemicalType) {
        if (handlers.containsKey(chemicalType)) {
            Optional<? extends IChemicalHandler<?, ?, ?>> handler = handlers.get(chemicalType);
            if (handler.isPresent()) {
                return (IChemicalHandler<CHEMICAL, STACK, ?>) handler.get();
            }
        }
        return null;
    }

    public void addGasHandler(Optional<IGasHandler> handler) {
        handlers.put(ChemicalType.GAS, handler);
    }

    public void addInfusionHandler(Optional<IInfusionHandler> handler) {
        handlers.put(ChemicalType.INFUSION, handler);
    }

    public void addPigmentHandler(Optional<IPigmentHandler> handler) {
        handlers.put(ChemicalType.PIGMENT, handler);
    }

    public void addSlurryHandler(Optional<ISlurryHandler> handler) {
        handlers.put(ChemicalType.SLURRY, handler);
    }

    public boolean sameHandlers(BoxedChemicalHandler other) {
        return this == other || handlers.size() == other.handlers.size() && handlers.entrySet().stream().noneMatch(entry -> entry.getValue() != other.handlers.get(entry.getKey()));
    }

    public void addRefreshListeners(Consumer<Optional<BoxedChemicalHandler>> refreshListener) {
        //TODO - V11: Make the listener only have to invalidate specific sub pieces
        for (Optional<? extends IChemicalHandler<?, ?, ?>> sourceAcceptor : handlers.values()) {
            //Use unchecked generics to add the listener to the source acceptor
//            CapabilityUtils.addListener(sourceAcceptor, refreshListener);
        }
    }
}