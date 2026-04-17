# CLAUDE.md — testMod

## Repo
https://github.com/jmalcolmo/minecraft

## Mod
- **ID:** testmod
- **Name:** testMod
- **Purpose:** Early-game mob farming using Statue Cores and Mob Statues
- **Loader:** Fabric
- **Minecraft:** 26.1.2
- **Java:** JDK 25 (build target)
- **Mappings:** Mojang official (not Yarn — use `net.minecraft.world.*` package paths)

## Mechanic Overview

### Statue Core (item, non-stackable)
- Crafted from iron ingots + gold nugget (cross pattern)
- Stores energy per passive mob type: cow, sheep, pig, chicken
- Energy gained when the player holding the core kills a passive mob (+10 per kill, max 100)
- Energy visible in item tooltip

### Mob Statue (block)
- Crafted from cobblestone + carved pumpkin
- Right-click with a Statue Core to insert it → block becomes **active**
- Right-click empty-handed when active → retrieves the core
- While active: spawns mobs based on stored energy (1 mob per 20 energy, up to 5 per type)
- Tethers nearby animals within 16 blocks — nudges wanderers back toward the statue every 5s

## Rules
- Short answers. No filler.
- MVP first. No feature creep.
- Get it working, then stop.
- Use Mojang mappings throughout (not Yarn). Key differences:
  - `Level` not `World`, `ServerLevel` not `ServerWorld`
  - `Animal` not `AnimalEntity`, `ServerPlayer` not `ServerPlayerEntity`
  - `CompoundTag` not `NbtCompound`, `BlockBehaviour.Properties` not `AbstractBlock.Settings`
  - `BlockEntity.loadAdditional(ValueInput)` / `saveAdditional(ValueOutput)` for persistence
  - `nbt.getInt(key)` returns `Optional<Integer>` — always `.orElse(0)`

## Git Workflow
- `main` = production
- `release` = stable releases
- `dev` = active development
- Branch off `dev` for features: `feature/your-feature`
- Open a PR before large changes
- Commit often, keep messages short and descriptive

## File Structure
```
src/main/java/name/modid/
  TestMod.java               — init + kill event listener
  ModItems.java              — statue_core registration
  ModBlocks.java             — mob_statue block registration
  ModBlockEntities.java      — block entity type registration
  item/
    StatueCoreItem.java      — energy storage via DataComponents
  block/
    MobStatueBlock.java      — block with ACTIVE state, useItemOn/useWithoutItem
    MobStatueBlockEntity.java — tick: spawn + tether mobs

src/main/resources/
  assets/testmod/
    blockstates/mob_statue.json
    lang/en_us.json
    models/block/mob_statue.json
    models/block/mob_statue_active.json
    models/item/statue_core.json
    models/item/mob_statue.json
    textures/item/statue_core.png
    textures/block/mob_statue.png
    textures/block/mob_statue_active.png
  data/testmod/recipe/
    statue_core.json
    mob_statue.json
```

## Priorities
1. ✅ Statue Core item with energy storage
2. ✅ Mob Statue block with core insertion
3. ✅ Kill event → energy tracking
4. ✅ Block entity: spawn mobs + tether
5. ⬜ Block entity persistence (ValueInput/ValueOutput API — core lost on chunk reload for now)
6. ⬜ Item tooltip showing energy levels
