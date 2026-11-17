package com.sensorlamp.network;

import com.sensorlamp.SensorLamp;
import com.sensorlamp.client.SensorScreen;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenDefaultsScreenPayload(int range, int offDelaySeconds) implements CustomPacketPayload {
    public static final Type<OpenDefaultsScreenPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SensorLamp.MODID, "open_defaults_screen"));

    public static final StreamCodec<ByteBuf, OpenDefaultsScreenPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, OpenDefaultsScreenPayload::range,
            ByteBufCodecs.VAR_INT, OpenDefaultsScreenPayload::offDelaySeconds,
            OpenDefaultsScreenPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenDefaultsScreenPayload payload, IPayloadContext context) {
        SensorScreen.openDefaults(payload.range(), payload.offDelaySeconds());
    }
}
