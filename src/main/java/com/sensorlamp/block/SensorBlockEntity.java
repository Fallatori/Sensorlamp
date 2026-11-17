package com.sensorlamp.block;

import com.sensorlamp.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SensorBlockEntity extends BlockEntity {
    private final SensorSettings settings = new SensorSettings();

    public SensorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SENSOR.get(), pos, state);
    }

    public boolean shouldBeOn(ServerLevel level) {
        return settings.shouldBeOn(level, getBlockPos());
    }

    public int getRange() {
        return settings.getRange();
    }

    public int getOffDelaySeconds() {
        return settings.getOffDelaySeconds();
    }

    public void setSettings(int range, int offDelaySeconds) {
        settings.set(range, offDelaySeconds);
        setChanged();

        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        settings.save(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        settings.load(tag);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
