package com.sensorlamp.network;

import com.sensorlamp.SensorLamp;
import com.sensorlamp.block.SensorBlockEntity;
import com.sensorlamp.virtual.VirtualSensors;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetSensorSettingsPayload(BlockPos pos, int range, int offDelaySeconds) implements CustomPacketPayload {
    public static final Type<SetSensorSettingsPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SensorLamp.MODID, "set_sensor_settings"));

    public static final StreamCodec<ByteBuf, SetSensorSettingsPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SetSensorSettingsPayload::pos,
            ByteBufCodecs.VAR_INT, SetSensorSettingsPayload::range,
            ByteBufCodecs.VAR_INT, SetSensorSettingsPayload::offDelaySeconds,
            SetSensorSettingsPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetSensorSettingsPayload payload, IPayloadContext context) {
        Player player = context.player();

        if (!player.canInteractWithBlock(payload.pos(), 1.0)) {
            return;
        }
        if (player.level().getBlockEntity(payload.pos()) instanceof SensorBlockEntity sensor) {

            sensor.setSettings(payload.range(), payload.offDelaySeconds());
        } else if (player.level() instanceof ServerLevel level) {

            VirtualSensors.setSettings(level, payload.pos(), payload.range(), payload.offDelaySeconds());
        }
    }
}
