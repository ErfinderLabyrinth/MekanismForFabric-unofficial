package mekanism.additions.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mekanism.additions.common.world.modifier.BabyEntitySpawnStructureModifier;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Structure.class)
public class StructureMixin {
    @WrapOperation(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/levelgen/structure/Structure;settings:Lnet/minecraft/world/level/levelgen/structure/Structure$StructureSettings;", opcode = Opcodes.PUTFIELD))
    private static void modifyStructureSettings(Structure instance, Structure.StructureSettings value, Operation<Void> original) {
        Structure.StructureSettings modified = BabyEntitySpawnStructureModifier.modifyStructure(instance, value);
        original.call(instance, modified);
    }
}
