package name.modid.client;

import name.modid.ModItems;
import name.modid.item.StatueCoreItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.network.chat.Component;

import java.util.Map;

public class TestModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
            if (!(stack.getItem() instanceof StatueCoreItem)) return;

            Map<String, Integer> energy = StatueCoreItem.getAllEnergy(stack);
            if (energy.isEmpty()) {
                lines.add(Component.literal("No energy stored. Kill passive mobs to charge."));
            } else {
                for (Map.Entry<String, Integer> entry : energy.entrySet()) {
                    String name = entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1);
                    lines.add(Component.literal(name + ": " + entry.getValue() + "/" + StatueCoreItem.MAX_ENERGY));
                }
            }
        });
    }
}
