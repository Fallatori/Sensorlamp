package com.sensorlamp.network;

import com.sensorlamp.SensorLamp;
import com.sensorlamp.virtual.VirtualSensors;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RemoveVirtualSensorPayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<RemoveVirtualSensorPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SensorLamp.MODID, "remove_virtual_sensor"));

    public static final StreamCodec<ByteBuf, RemoveVirtualSensorPayload> STREAM_CODEC =
            BlockPos.STREAM_CODEC.map(RemoveVirtualSensorPayload::new, RemoveVirtualSensorPayload::pos);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RemoveVirtualSensorPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player && player.canInteractWithBlock(payload.pos(), 1.0)) {
            VirtualSensors.remove(player.serverLevel(), payload.pos(), player);
        }
    }
}
