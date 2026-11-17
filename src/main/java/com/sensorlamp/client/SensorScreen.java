package com.sensorlamp.client;

import javax.annotation.Nullable;

import com.sensorlamp.block.SensorBlockEntity;
import com.sensorlamp.block.SensorSettings;
import com.sensorlamp.network.RemoveVirtualSensorPayload;
import com.sensorlamp.network.SetDefaultSettingsPayload;
import com.sensorlamp.network.SetSensorSettingsPayload;
import com.sensorlamp.network.SyncVirtualSensorsPayload;
import com.sensorlamp.virtual.ClientVirtualSensors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import net.neoforged.neoforge.network.PacketDistributor;

public class SensorScreen extends Screen {
    private static final int WIDGET_WIDTH = 200;

    private enum Mode {
        BLOCK,
        VIRTUAL,
        DEFAULTS
    }

    @Nullable
    private final BlockPos pos;
    private final Mode mode;
    private final int originalRange;
    private final int originalOffDelay;
    private int range;
    private int offDelay;
    private boolean removed;

    private ExtendedSlider rangeSlider;
    private ExtendedSlider offDelaySlider;
    private int top;

    private SensorScreen(@Nullable BlockPos pos, Mode mode, int range, int offDelay) {
        super(Component.translatable(switch (mode) {
            case BLOCK -> "screen.sensorlamp.title";
            case VIRTUAL -> "screen.sensorlamp.title.virtual";
            case DEFAULTS -> "screen.sensorlamp.title.defaults";
        }));
        this.pos = pos;
        this.mode = mode;
        this.originalRange = this.range = range;
        this.originalOffDelay = this.offDelay = offDelay;
    }

    public static void open(SensorBlockEntity sensor) {
        Minecraft.getInstance().setScreen(
                new SensorScreen(sensor.getBlockPos(), Mode.BLOCK, sensor.getRange(), sensor.getOffDelaySeconds()));
    }

    public static void openVirtual(BlockPos pos) {
        SyncVirtualSensorsPayload.Entry sensor = ClientVirtualSensors.get(pos);
        if (sensor != null) {
            Minecraft.getInstance().setScreen(
                    new SensorScreen(pos, Mode.VIRTUAL, sensor.range(), sensor.offDelaySeconds()));
        }
    }

    public static void openDefaults(int range, int offDelay) {
        Minecraft.getInstance().setScreen(new SensorScreen(null, Mode.DEFAULTS, range, offDelay));
    }

    @Override
    protected void init() {

        readSliders();

        int left = (width - WIDGET_WIDTH) / 2;
        top = height / 2 - 40;

        rangeSlider = addRenderableWidget(new ExtendedSlider(left, top, WIDGET_WIDTH, 20,
                Component.translatable("screen.sensorlamp.range"), Component.translatable("screen.sensorlamp.blocks"),
                SensorSettings.MIN_RANGE, SensorSettings.MAX_RANGE, range, 1, 0, true));

        offDelaySlider = addRenderableWidget(new ExtendedSlider(left, top + 24, WIDGET_WIDTH, 20,
                Component.translatable("screen.sensorlamp.off_delay"), Component.translatable("screen.sensorlamp.seconds"),
                SensorSettings.MIN_OFF_DELAY_SECONDS, SensorSettings.MAX_OFF_DELAY_SECONDS, offDelay, 1, 0, true));

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .bounds(left, top + 56, WIDGET_WIDTH, 20)
                .build());

        if (mode == Mode.VIRTUAL) {
            addRenderableWidget(Button.builder(Component.translatable("screen.sensorlamp.remove"), button -> {
                        PacketDistributor.sendToServer(new RemoveVirtualSensorPayload(pos));
                        removed = true;
                        onClose();
                    })
                    .bounds(left, top + 80, WIDGET_WIDTH, 20)
                    .build());
        }
    }

    private void readSliders() {
        if (rangeSlider != null) {
            range = rangeSlider.getValueInt();
            offDelay = offDelaySlider.getValueInt();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, top - 16, 0xFFFFFF);
        if (mode == Mode.DEFAULTS) {
            graphics.drawCenteredString(font, Component.translatable("screen.sensorlamp.defaults.hint"),
                    width / 2, top + 84, 0xA0A0A0);
        }
    }

    @Override
    public void onClose() {
        readSliders();
        if (!removed && (range != originalRange || offDelay != originalOffDelay)) {
            PacketDistributor.sendToServer(mode == Mode.DEFAULTS
                    ? new SetDefaultSettingsPayload(range, offDelay)
                    : new SetSensorSettingsPayload(pos, range, offDelay));
        }
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
