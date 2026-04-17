package name.modid.block;

import name.modid.ModBlockEntities;
import name.modid.item.StatueCoreItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
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
    private static final int MAX_MOBS_PER_TYPE = 5;
    private static final double TETHER_RADIUS = 16.0;
    private static final double SPAWN_RADIUS = 6.0;

    private ItemStack coreStack = ItemStack.EMPTY;
    private int ticker = 0;

    public MobStatueBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOB_STATUE_ENTITY, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MobStatueBlockEntity be) {
        if (level.isClientSide()) return;
        be.ticker++;
        if (be.ticker % TICK_INTERVAL != 0) return;
        be.doTick((ServerLevel) level, pos);
    }

    private void doTick(ServerLevel level, BlockPos pos) {
        if (coreStack.isEmpty()) return;
        Map<String, Integer> energyMap = StatueCoreItem.getAllEnergy(coreStack);
        if (energyMap.isEmpty()) return;

        for (Map.Entry<String, Integer> entry : energyMap.entrySet()) {
            String mobType = entry.getKey();
            int energy = entry.getValue();
            int maxMobs = Math.min(energy / 20, MAX_MOBS_PER_TYPE);
            if (maxMobs <= 0) continue;

            if (countNearbyMobs(level, pos, mobType) < maxMobs) {
                spawnMob(level, pos, mobType);
            }
        }

        tetherMobs(level, pos, energyMap);
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
        if (type == null) return;

        double angle = level.getRandom().nextDouble() * Math.PI * 2;
        double dist = 2 + level.getRandom().nextDouble() * (SPAWN_RADIUS - 2);
        double x = pos.getX() + 0.5 + Math.cos(angle) * dist;
        double z = pos.getZ() + 0.5 + Math.sin(angle) * dist;
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z);

        if (type.create(level, EntitySpawnReason.SPAWNER) instanceof Mob mob) {
            mob.setPos(x, Math.max(y, pos.getY() + 1), z);
            mob.setYRot(level.getRandom().nextFloat() * 360f);
            level.addFreshEntity(mob);
        }
    }

    private void tetherMobs(ServerLevel level, BlockPos pos, Map<String, Integer> energyMap) {
        Vec3 center = Vec3.atCenterOf(pos);
        AABB box = AABB.ofSize(center, TETHER_RADIUS * 2 + 8, TETHER_RADIUS, TETHER_RADIUS * 2 + 8);

        List<Animal> animals = level.getEntitiesOfClass(Animal.class, box, animal -> {
            String typePath = BuiltInRegistries.ENTITY_TYPE.getKey(animal.getType()).getPath();
            return energyMap.containsKey(typePath);
        });

        for (Animal animal : animals) {
            if (animal.distanceToSqr(center.x, center.y, center.z) > TETHER_RADIUS * TETHER_RADIUS) {
                Vec3 dir = center.subtract(animal.position()).normalize();
                double nx = animal.getX() + dir.x * 3;
                double nz = animal.getZ() + dir.z * 3;
                int ny = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) nx, (int) nz);
                animal.teleportTo(nx, Math.max(ny, pos.getY() + 1), nz);
            }
        }
    }

    public ItemStack getCoreStack() { return coreStack; }

    public void setCoreStack(ItemStack stack) {
        this.coreStack = stack;
        setChanged();
    }
}
