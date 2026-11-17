package com.sensorlamp;

import com.sensorlamp.block.SensorSettings;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue DETECTION_RADIUS = BUILDER
            .comment("Standard: hvor mange blokker unna en spiller kan være før sensoren reagerer")
            .defineInRange("detectionRadius", 6, SensorSettings.MIN_RANGE, SensorSettings.MAX_RANGE);

    public static final ModConfigSpec.IntValue OFF_DELAY_SECONDS = BUILDER
            .comment("Standard: hvor mange sekunder lyset holder seg på etter at spilleren har gått")
            .defineInRange("offDelaySeconds", 2,
                    SensorSettings.MIN_OFF_DELAY_SECONDS, SensorSettings.MAX_OFF_DELAY_SECONDS);

    static final ModConfigSpec SPEC = BUILDER.build();
}
