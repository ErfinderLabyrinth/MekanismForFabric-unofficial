package mekanism.common.inventory.container.sync.dynamic;

import mekanism.common.inventory.container.sync.ISyncableData;

import java.util.function.Consumer;

public interface IContainerSyncable {
    void addSyncables(Consumer<ISyncableData> acceptor, String tag);
}
