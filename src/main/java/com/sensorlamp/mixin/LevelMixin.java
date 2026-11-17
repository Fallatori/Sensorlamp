package com.sensorlamp.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.sensorlamp.virtual.VirtualSensorData;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Level.class)
public abstract class LevelMixin implements SignalGetter {

    @Unique
    private VirtualSensorData sensorlamp$data;

    @Override
    public int getSignal(BlockPos pos, Direction direction) {

        if ((Object) this instanceof ServerLevel serverLevel) {
            if (sensorlamp$data == null) {
                sensorlamp$data = VirtualSensorData.get(serverLevel);
            }

            long receiver = BlockPos.offset(pos.asLong(), direction.getOpposite());
            if (sensorlamp$data.isPoweredAt(receiver)) {
                return 15;
            }
        }

        BlockState state = getBlockState(pos);
        int signal = state.getSignal(this, pos, direction);
        return state.shouldCheckWeakPower(this, pos, direction) ? Math.max(signal, getDirectSignalTo(pos)) : signal;
    }
}
