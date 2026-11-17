package com.sensorlamp.network;

import java.util.List;

import com.sensorlamp.SensorLamp;
import com.sensorlamp.virtual.ClientVirtualSensors;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncVirtualSensorsPayload(List<Entry> sensors) implements CustomPacketPayload {
    public static final Type<SyncVirtualSensorsPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SensorLamp.MODID, "sync_virtual_sensors"));

    public record Entry(BlockPos pos, int range, int offDelaySeconds) {
        public static final StreamCodec<ByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, Entry::pos,
                ByteBufCodecs.VAR_INT, Entry::range,
                ByteBufCodecs.VAR_INT, Entry::offDelaySeconds,
                Entry::new);
    }

    public static final StreamCodec<ByteBuf, SyncVirtualSensorsPayload> STREAM_CODEC = StreamCodec.composite(
            Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), SyncVirtualSensorsPayload::sensors,
            SyncVirtualSensorsPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncVirtualSensorsPayload payload, IPayloadContext context) {
        ClientVirtualSensors.replace(payload.sensors());
    }
}
