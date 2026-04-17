package name.modid.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.HashMap;
import java.util.Map;

public class StatueCoreItem extends Item {
    public static final int MAX_ENERGY = 100;
    public static final String[] MOB_TYPES = {"cow", "sheep", "pig", "chicken"};

    public StatueCoreItem(Properties properties) {
        super(properties);
    }

    public static String getMobType(LivingEntity entity) {
        String path = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE
                .getKey(entity.getType()).getPath();
        for (String type : MOB_TYPES) {
            if (type.equals(path)) return type;
        }
        return null;
    }

    public static void addEnergy(ItemStack stack, String mobType, int amount) {
        CompoundTag nbt = getNbt(stack);
        int current = nbt.getInt("energy_" + mobType).orElse(0);
        nbt.putInt("energy_" + mobType, Math.min(current + amount, MAX_ENERGY));
        setNbt(stack, nbt);
    }

    public static Map<String, Integer> getAllEnergy(ItemStack stack) {
        CompoundTag nbt = getNbt(stack);
        Map<String, Integer> energy = new HashMap<>();
        for (String type : MOB_TYPES) {
            int e = nbt.getInt("energy_" + type).orElse(0);
            if (e > 0) energy.put(type, e);
        }
        return energy;
    }

    private static CompoundTag getNbt(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return data.copyTag();
    }

    private static void setNbt(ItemStack stack, CompoundTag nbt) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
    }


}
