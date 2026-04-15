package name.modid;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class ModItems {

    private static final ResourceKey<Item> TEST_FOOD_KEY = ResourceKey.create(
        Registries.ITEM,
        Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "test_food")
    );

    public static final Item TEST_FOOD = Registry.register(
        BuiltInRegistries.ITEM,
        TEST_FOOD_KEY,
        new Item(new Item.Properties().setId(TEST_FOOD_KEY))
    );

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(output -> {
            output.prepend(TEST_FOOD);
        });
    }
}
