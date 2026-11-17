package com.sensorlamp.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sensorlamp.Config;
import com.sensorlamp.block.SensorSettings;

import net.minecraft.util.Mth;

public record SensorDefaults(int range, int offDelaySeconds) {

    public static final Codec<SensorDefaults> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("range").forGetter(SensorDefaults::range),
            Codec.INT.fieldOf("off_delay_seconds").forGetter(SensorDefaults::offDelaySeconds)
    ).apply(instance, SensorDefaults::of));

    public static SensorDefaults of(int range, int offDelaySeconds) {
        return new SensorDefaults(
                Mth.clamp(range, SensorSettings.MIN_RANGE, SensorSettings.MAX_RANGE),
                Mth.clamp(offDelaySeconds, SensorSettings.MIN_OFF_DELAY_SECONDS, SensorSettings.MAX_OFF_DELAY_SECONDS));
    }

    public static SensorDefaults fromConfig() {
        return of(Config.DETECTION_RADIUS.get(), Config.OFF_DELAY_SECONDS.get());
    }
}
