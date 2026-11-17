package com.sensorlamp.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sensorlamp.ModBlocks;
import com.sensorlamp.SensorLamp;
import com.sensorlamp.block.SensorBlockEntity;
import com.sensorlamp.network.SyncVirtualSensorsPayload;
import com.sensorlamp.virtual.ClientVirtualSensors;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = SensorLamp.MODID, dist = Dist.CLIENT)
public class SensorLampClient {

    private static final double HIGHLIGHT_DISTANCE = 48;

    public SensorLampClient(IEventBus modEventBus, ModContainer container) {

        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        modEventBus.addListener(SensorLampClient::registerGuiLayers);
        NeoForge.EVENT_BUS.addListener(SensorLampClient::renderHighlights);

        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> ClientVirtualSensors.clear());
    }

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR,
                ResourceLocation.fromNamespaceAndPath(SensorLamp.MODID, "sensor_info"),
                SensorLampClient::renderSensorInfo);
    }

    private static boolean isHoldingPlayerSensor(LocalPlayer player) {
        return player != null && (player.getMainHandItem().is(ModBlocks.PLAYER_SENSOR_ITEM.get())
                || player.getOffhandItem().is(ModBlocks.PLAYER_SENSOR_ITEM.get()));
    }

    private static boolean isHoldingAnySensorItem(LocalPlayer player) {
        return player != null && (ModBlocks.isSensorItem(player.getMainHandItem())
                || ModBlocks.isSensorItem(player.getOffhandItem()));
    }

    private static void renderSensorInfo(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || minecraft.level == null || minecraft.screen != null
                || !isHoldingAnySensorItem(minecraft.player)) {
            return;
        }

        if (!(minecraft.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockPos pos = hit.getBlockPos();
        Component text = null;
        if (minecraft.level.getBlockEntity(pos) instanceof SensorBlockEntity sensor) {
            text = Component.translatable("hud.sensorlamp.info", sensor.getRange(), sensor.getOffDelaySeconds());
        } else if (ClientVirtualSensors.get(pos) instanceof SyncVirtualSensorsPayload.Entry virtual) {
            text = Component.translatable("hud.sensorlamp.info.virtual", virtual.range(), virtual.offDelaySeconds());
        }
        if (text != null) {
            graphics.drawCenteredString(minecraft.font, text,
                    graphics.guiWidth() / 2, graphics.guiHeight() / 2 + 12, 0xFFFFFF);
        }
    }

    private static void renderHighlights(RenderLevelStageEvent event) {

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (!isHoldingPlayerSensor(minecraft.player)) {
            return;
        }

        Vec3 camera = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        VertexConsumer lines = buffers.getBuffer(RenderType.lines());

        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);
        for (SyncVirtualSensorsPayload.Entry sensor : ClientVirtualSensors.all()) {
            if (sensor.pos().distToCenterSqr(camera) > HIGHLIGHT_DISTANCE * HIGHLIGHT_DISTANCE) {
                continue;
            }

            AABB box = new AABB(sensor.pos()).inflate(0.005);
            LevelRenderer.renderLineBox(poseStack, lines, box, 0.25F, 0.9F, 1.0F, 1.0F);
        }
        poseStack.popPose();
        buffers.endBatch(RenderType.lines());
    }
}
