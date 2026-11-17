package com.sensorlamp.block;

import javax.annotation.Nullable;

import com.sensorlamp.client.SensorScreen;
import com.sensorlamp.defaults.DefaultSettings;
import com.sensorlamp.defaults.SensorDefaults;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class PlayerSensorBlock extends Block implements EntityBlock {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private static final int CHECK_INTERVAL_TICKS = 10;

    public PlayerSensorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SensorBlockEntity(pos, state);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        startSensor(state, level, pos, oldState);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        sensorTick(state, level, pos);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        applyPlacerDefaults(level, pos, placer);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return useSensor(level, pos, player);
    }

    public static void applyPlacerDefaults(Level level, BlockPos pos, @Nullable LivingEntity placer) {
        if (!level.isClientSide && placer instanceof Player player
                && level.getBlockEntity(pos) instanceof SensorBlockEntity sensor) {
            SensorDefaults defaults = DefaultSettings.get(player);
            sensor.setSettings(defaults.range(), defaults.offDelaySeconds());
        }
    }

    public static void startSensor(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        if (!oldState.is(state.getBlock())) {
            level.scheduleTick(pos, state.getBlock(), 1);
        }
    }

    public static void sensorTick(BlockState state, ServerLevel level, BlockPos pos) {
        boolean on = level.getBlockEntity(pos) instanceof SensorBlockEntity sensor && sensor.shouldBeOn(level);

        if (on != state.getValue(POWERED)) {

            int flags = state.isSignalSource() ? Block.UPDATE_ALL : Block.UPDATE_CLIENTS;
            level.setBlock(pos, state.setValue(POWERED, on), flags);
        }

        level.scheduleTick(pos, state.getBlock(), CHECK_INTERVAL_TICKS);
    }

    public static InteractionResult useSensor(Level level, BlockPos pos, Player player) {

        if (!player.getMainHandItem().isEmpty()) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide && level.getBlockEntity(pos) instanceof SensorBlockEntity sensor) {
            SensorScreen.open(sensor);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }
}
