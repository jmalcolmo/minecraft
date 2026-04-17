package name.modid.block;

import com.mojang.serialization.MapCodec;
import name.modid.item.StatueCoreItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MobStatueBlock extends BaseEntityBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final MapCodec<MobStatueBlock> CODEC = simpleCodec(MobStatueBlock::new);

    public MobStatueBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    public MapCodec<MobStatueBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MobStatueBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        MobStatueBlockEntity be = (MobStatueBlockEntity) level.getBlockEntity(pos);
        if (be == null) return InteractionResult.PASS;

        if (stack.getItem() instanceof StatueCoreItem && be.getCoreStack().isEmpty()) {
            be.setCoreStack(stack.copyWithCount(1));
            stack.shrink(1);
            level.setBlockAndUpdate(pos, state.setValue(ACTIVE, true));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        MobStatueBlockEntity be = (MobStatueBlockEntity) level.getBlockEntity(pos);
        if (be == null) return InteractionResult.PASS;

        if (!be.getCoreStack().isEmpty()) {
            player.getInventory().add(be.getCoreStack().copy());
            be.setCoreStack(ItemStack.EMPTY);
            level.setBlockAndUpdate(pos, state.setValue(ACTIVE, false));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, name.modid.ModBlockEntities.MOB_STATUE_ENTITY, MobStatueBlockEntity::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
