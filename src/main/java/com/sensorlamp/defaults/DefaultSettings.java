package com.sensorlamp.defaults;

import java.util.function.Supplier;

import com.sensorlamp.ModBlocks;
import com.sensorlamp.SensorLamp;
import com.sensorlamp.network.OpenDefaultsScreenPayload;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class DefaultSettings {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SensorLamp.MODID);

    private static final Supplier<AttachmentType<SensorDefaults>> SENSOR_DEFAULTS =
            ATTACHMENT_TYPES.register("sensor_defaults", () -> AttachmentType.builder(() -> SensorDefaults.fromConfig())
                    .serialize(SensorDefaults.CODEC)
                    .copyOnDeath()
                    .build());

    public static SensorDefaults get(Player player) {
        return player.hasData(SENSOR_DEFAULTS) ? player.getData(SENSOR_DEFAULTS) : SensorDefaults.fromConfig();
    }

    public static void set(Player player, int range, int offDelaySeconds) {
        player.setData(SENSOR_DEFAULTS, SensorDefaults.of(range, offDelaySeconds));
    }

    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (!player.isShiftKeyDown() || !ModBlocks.isSensorItem(event.getItemStack())) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));

        if (player instanceof ServerPlayer serverPlayer) {
            SensorDefaults defaults = get(serverPlayer);
            PacketDistributor.sendToPlayer(serverPlayer,
                    new OpenDefaultsScreenPayload(defaults.range(), defaults.offDelaySeconds()));
        }
    }
}
