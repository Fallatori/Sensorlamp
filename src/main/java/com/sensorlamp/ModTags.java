package com.sensorlamp;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static final TagKey<Block> VIRTUAL_SENSOR_BLACKLIST = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(SensorLamp.MODID, "virtual_sensor_blacklist"));
}
