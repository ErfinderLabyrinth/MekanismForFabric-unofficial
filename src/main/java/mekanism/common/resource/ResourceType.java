package mekanism.common.resource;

public enum ResourceType {
    SHARD("shard"),
    CRYSTAL("crystal"),
    DUST("dust"),
    DIRTY_DUST("dirty_dust"),
    CLUMP("clump"),
    INGOT("ingot"),
    RAW("raw", "raw", true),
    NUGGET("nugget"),
    ENRICHED("enriched", "enriched", true);

    private final String registryName;
    private final String baseTagPath;
    private final boolean prefix;

    ResourceType(String registryName) {
        this(registryName, registryName + "s", false);
    }

    ResourceType(String registryName, String baseTagPath, boolean prefix) {
        this.registryName = registryName;
        this.baseTagPath = baseTagPath;
        this.prefix = prefix;
    }

    public String getRegistryName() {
        return registryName;
    }

    public String getBaseTagPath() {
        return baseTagPath;
    }

    public boolean isPrefix() {
        return prefix;
    }

    public boolean usedByPrimary(PrimaryResource resource) {
        //Copper doesn't have nuggets
        return this != ENRICHED && (resource != PrimaryResource.COPPER || this != NUGGET);
    }

    public boolean isVanilla() {
        return this == INGOT || this == RAW || this == NUGGET;
    }
}