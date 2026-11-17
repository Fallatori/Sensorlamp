package com.sensorlamp;

import com.sensorlamp.defaults.DefaultSettings;
import com.sensorlamp.network.OpenDefaultsScreenPayload;
import com.sensorlamp.network.RemoveVirtualSensorPayload;
import com.sensorlamp.network.SetDefaultSettingsPayload;
import com.sensorlamp.network.SetSensorSettingsPayload;
import com.sensorlamp.network.SyncVirtualSensorsPayload;
import com.sensorlamp.virtual.VirtualSensors;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(SensorLamp.MODID)
public class SensorLamp {
    public static final String MODID = "sensorlamp";

    public SensorLamp(IEventBus modEventBus, ModContainer modContainer) {

        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        DefaultSettings.ATTACHMENT_TYPES.register(modEventBus);

        modEventBus.addListener(this::addCreative);

        modEventBus.addListener(this::registerPayloads);

        NeoForge.EVENT_BUS.addListener(VirtualSensors::onLevelTick);
        NeoForge.EVENT_BUS.addListener(VirtualSensors::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(VirtualSensors::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(VirtualSensors::onPlayerChangedDimension);
        NeoForge.EVENT_BUS.addListener(VirtualSensors::onPlayerRespawn);

        NeoForge.EVENT_BUS.addListener(DefaultSettings::onRightClickItem);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {

        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(SetSensorSettingsPayload.TYPE, SetSensorSettingsPayload.STREAM_CODEC,
                SetSensorSettingsPayload::handle);
        registrar.playToServer(RemoveVirtualSensorPayload.TYPE, RemoveVirtualSensorPayload.STREAM_CODEC,
                RemoveVirtualSensorPayload::handle);
        registrar.playToClient(SyncVirtualSensorsPayload.TYPE, SyncVirtualSensorsPayload.STREAM_CODEC,
                SyncVirtualSensorsPayload::handle);
        registrar.playToClient(OpenDefaultsScreenPayload.TYPE, OpenDefaultsScreenPayload.STREAM_CODEC,
                OpenDefaultsScreenPayload::handle);
        registrar.playToServer(SetDefaultSettingsPayload.TYPE, SetDefaultSettingsPayload.STREAM_CODEC,
                SetDefaultSettingsPayload::handle);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(ModBlocks.SENSOR_LAMP_ITEM);
            event.accept(ModBlocks.SENSOR_LANTERN_ITEM);
            event.accept(ModBlocks.SENSOR_SOUL_LANTERN_ITEM);
            event.accept(ModBlocks.PLAYER_SENSOR_ITEM);
        }
    }
}
