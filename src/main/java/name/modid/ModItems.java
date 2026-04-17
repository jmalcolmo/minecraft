package name.modid;

import name.modid.item.StatueCoreItem;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class ModItems {
    private static final ResourceKey<Item> STATUE_CORE_KEY = ResourceKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "statue_core")
    );

    public static final StatueCoreItem STATUE_CORE = Registry.register(
            BuiltInRegistries.ITEM,
            STATUE_CORE_KEY,
            new StatueCoreItem(new Item.Properties().setId(STATUE_CORE_KEY).stacksTo(1))
    );

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(output -> {
            output.accept(STATUE_CORE);
        });
    }
}
