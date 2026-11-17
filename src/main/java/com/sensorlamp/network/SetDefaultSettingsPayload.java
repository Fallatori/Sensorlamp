package com.sensorlamp.network;

import com.sensorlamp.SensorLamp;
import com.sensorlamp.defaults.DefaultSettings;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetDefaultSettingsPayload(int range, int offDelaySeconds) implements CustomPacketPayload {
    public static final Type<SetDefaultSettingsPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SensorLamp.MODID, "set_default_settings"));

    public static final StreamCodec<ByteBuf, SetDefaultSettingsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetDefaultSettingsPayload::range,
            ByteBufCodecs.VAR_INT, SetDefaultSettingsPayload::offDelaySeconds,
            SetDefaultSettingsPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetDefaultSettingsPayload payload, IPayloadContext context) {
        DefaultSettings.set(context.player(), payload.range(), payload.offDelaySeconds());
    }
}
