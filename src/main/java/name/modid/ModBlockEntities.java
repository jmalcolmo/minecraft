package name.modid;

import name.modid.block.MobStatueBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static BlockEntityType<MobStatueBlockEntity> MOB_STATUE_ENTITY;

    public static void initialize() {
        MOB_STATUE_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "mob_statue"),
                FabricBlockEntityTypeBuilder.create(MobStatueBlockEntity::new, ModBlocks.MOB_STATUE).build()
        );
    }
}
