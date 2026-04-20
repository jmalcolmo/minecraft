package name.modid.block;

import name.modid.ModBlockEntities;
import name.modid.item.StatueCoreItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;

public class MobStatueBlockEntity extends BlockEntity {
    private static final int TICK_INTERVAL = 100;
    private static final int TETHER_TICK_INTERVAL = 10;
    private static final int MAX_MOBS_PER_TYPE = 5;
    private static final double TETHER_RADIUS = 5.0;
    private static final double SPAWN_RADIUS = 4.0;

    private ItemStack coreStack = ItemStack.EMPTY;
    private int ticker = 0;

    public MobStatueBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOB_STATUE_ENTITY, pos, state);
    }

    public static void broadcast(ServerLevel level, String msg) {
        Component text = Component.literal("[Statue] " + msg);
        for (ServerPlayer p : level.players()) {
            p.sendSystemMessage(text);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MobStatueBlockEntity be) {
        if (level.isClientSide()) return;
        be.ticker++;

        ServerLevel serverLevel = (ServerLevel) level;

        if (be.ticker % TETHER_TICK_INTERVAL == 0 && !be.coreStack.isEmpty()) {
            Map<String, Integer> energyMap = StatueCoreItem.getAllEnergy(be.coreStack);
            if (!energyMap.isEmpty()) {
                be.tetherMobs(serverLevel, pos, energyMap);
            }
        }

        if (be.ticker % TICK_INTERVAL == 0) {
            be.doTick(serverLevel, pos);
        }
    }

    private void doTick(ServerLevel level, BlockPos pos) {
        if (coreStack.isEmpty()) return;
        Map<String, Integer> energyMap = StatueCoreItem.getAllEnergy(coreStack);
        if (energyMap.isEmpty()) return;

        broadcast(level, "Statue tick at " + pos.toShortString() + " | energy: " + energyMap);

        for (Map.Entry<String, Integer> entry : energyMap.entrySet()) {
            String mobType = entry.getKey();
            int energy = entry.getValue();
            int maxMobs = Math.min(energy / 20, MAX_MOBS_PER_TYPE);
            if (maxMobs <= 0) {
                broadcast(level, mobType + ": not enough energy to spawn (energy=" + energy + ")");
                continue;
            }

            int current = countNearbyMobs(level, pos, mobType);
            broadcast(level, mobType + ": " + current + "/" + maxMobs + " present (energy=" + energy + ")");

            if (current < maxMobs) {
                spawnMob(level, pos, mobType);
            }
        }
    }

    private int countNearbyMobs(ServerLevel level, BlockPos pos, String mobType) {
        AABB box = AABB.ofSize(Vec3.atCenterOf(pos), TETHER_RADIUS * 2, TETHER_RADIUS, TETHER_RADIUS * 2);
        return level.getEntitiesOfClass(Animal.class, box, animal ->
                BuiltInRegistries.ENTITY_TYPE.getKey(animal.getType()).getPath().equals(mobType)
        ).size();
    }

    private void spawnMob(ServerLevel level, BlockPos pos, String mobType) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(
                Identifier.fromNamespaceAndPath("minecraft", mobType));
        double angle = level.getRandom().nextDouble() * Math.PI * 2;
        double dist = 2 + level.getRandom().nextDouble() * (SPAWN_RADIUS - 2);
        double x = pos.getX() + 0.5 + Math.cos(angle) * dist;
        double z = pos.getZ() + 0.5 + Math.sin(angle) * dist;
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z);

        if (type.create(level, EntitySpawnReason.SPAWNER) instanceof Mob mob) {
            mob.setPos(x, Math.max(y, pos.getY() + 1), z);
            mob.setYRot(level.getRandom().nextFloat() * 360f);
            level.addFreshEntity(mob);
            broadcast(level, "Spawned " + mobType + " at (" + String.format("%.1f,%.1f,%.1f", x, (double) y, z) + ")");
        }
    }

    private void tetherMobs(ServerLevel level, BlockPos pos, Map<String, Integer> energyMap) {
        Vec3 center = Vec3.atCenterOf(pos);
        AABB box = AABB.ofSize(center, (TETHER_RADIUS + 8) * 2, TETHER_RADIUS * 2, (TETHER_RADIUS + 8) * 2);

        List<Animal> animals = level.getEntitiesOfClass(Animal.class, box, animal -> {
            String typePath = BuiltInRegistries.ENTITY_TYPE.getKey(animal.getType()).getPath();
            return energyMap.containsKey(typePath);
        });

        for (Animal animal : animals) {
            double dist = Math.sqrt(animal.distanceToSqr(center.x, center.y, center.z));
            if (dist > TETHER_RADIUS) {
                Vec3 dir = center.subtract(animal.position()).normalize();
                double excess = dist - TETHER_RADIUS;
                double speed = Math.min(excess * 0.15 + 0.1, 0.8);
                animal.setDeltaMovement(dir.x * speed, 0.2, dir.z * speed);
                animal.hurtMarked = true;
                broadcast(level, "Tethering " + BuiltInRegistries.ENTITY_TYPE.getKey(animal.getType()).getPath()
                        + " (dist=" + String.format("%.1f", dist) + ", pull=" + String.format("%.2f", speed) + ")");
            }
        }
    }

    public ItemStack getCoreStack() { return coreStack; }

    public void setCoreStack(ItemStack stack) {
        this.coreStack = stack;
        setChanged();
    }
}
