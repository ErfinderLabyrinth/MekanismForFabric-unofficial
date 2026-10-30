package mekanism.api.providers;

public interface INameProvider {

    /**
     * Used for retrieving the path/name of a registry object before the registry object has been fully initialized
     */
    String getInternalRegistryName();
}