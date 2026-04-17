package name.modid;

import name.modid.block.MobStatueBlock;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ModBlocks {
    private static final ResourceKey<Block> MOB_STATUE_KEY = ResourceKey.create(
            Registries.BLOCK, Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "mob_statue")
    );
    private static final ResourceKey<Item> MOB_STATUE_ITEM_KEY = ResourceKey.create(
            Registries.ITEM, Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "mob_statue")
    );

    public static final MobStatueBlock MOB_STATUE = Registry.register(
            BuiltInRegistries.BLOCK, MOB_STATUE_KEY,
            new MobStatueBlock(BlockBehaviour.Properties.of()
                    .setId(MOB_STATUE_KEY)
                    .mapColor(MapColor.STONE)
                    .strength(2.5f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE))
    );

    public static void initialize() {
        Registry.register(BuiltInRegistries.ITEM, MOB_STATUE_ITEM_KEY,
                new BlockItem(MOB_STATUE, new Item.Properties().setId(MOB_STATUE_ITEM_KEY))
        );

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(output -> {
            output.accept(MOB_STATUE.asItem());
        });
    }
}
