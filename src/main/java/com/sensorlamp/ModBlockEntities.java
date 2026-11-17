package com.sensorlamp;

import com.sensorlamp.block.SensorBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SensorLamp.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SensorBlockEntity>> SENSOR =
            BLOCK_ENTITY_TYPES.register("sensor", () -> BlockEntityType.Builder.of(SensorBlockEntity::new,
                    ModBlocks.SENSOR_LAMP.get(),
                    ModBlocks.SENSOR_LANTERN.get(),
                    ModBlocks.SENSOR_WALL_LANTERN.get(),
                    ModBlocks.SENSOR_SOUL_LANTERN.get(),
                    ModBlocks.SENSOR_SOUL_WALL_LANTERN.get(),
                    ModBlocks.PLAYER_SENSOR.get()
            ).build(null));
}
