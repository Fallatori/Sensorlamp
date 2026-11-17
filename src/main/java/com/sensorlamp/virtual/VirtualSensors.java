package com.sensorlamp.virtual;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sensorlamp.ModBlocks;
import com.sensorlamp.ModTags;
import com.sensorlamp.client.SensorScreen;
import com.sensorlamp.defaults.DefaultSettings;
import com.sensorlamp.defaults.SensorDefaults;
import com.sensorlamp.network.SyncVirtualSensorsPayload;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class VirtualSensors {

    private static final int CHECK_INTERVAL_TICKS = 10;

    private static void attach(ServerLevel level, BlockPos pos, Player player, ItemStack stack) {
        VirtualSensorData data = VirtualSensorData.get(level);
        if (!data.add(pos)) {
            player.displayClientMessage(Component.translatable("message.sensorlamp.virtual.already"), true);
            return;
        }

        SensorDefaults defaults = DefaultSettings.get(player);
        data.get(pos).settings.set(defaults.range(), defaults.offDelaySeconds());
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        level.playSound(null, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.6F, 1.4F);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 15, 0.35, 0.35, 0.35, 0.05);
        player.displayClientMessage(Component.translatable("message.sensorlamp.virtual.attached"), true);
        sync(level);
    }

    public static void remove(ServerLevel level, BlockPos pos, ServerPlayer player) {
        VirtualSensorData.VirtualSensor removed = VirtualSensorData.get(level).remove(pos);
        if (removed == null) {
            return;
        }
        if (removed.powered) {
            notifyBlock(level, pos);
        }
        ItemStack sensor = new ItemStack(ModBlocks.PLAYER_SENSOR_ITEM.get());
        if (!player.addItem(sensor)) {
            player.drop(sensor, false);
        }
        level.playSound(null, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.6F, 0.8F);
        sync(level);
    }

    public static void setSettings(ServerLevel level, BlockPos pos, int range, int offDelaySeconds) {
        VirtualSensorData data = VirtualSensorData.get(level);
        VirtualSensorData.VirtualSensor sensor = data.get(pos);
        if (sensor != null) {
            sensor.settings.set(range, offDelaySeconds);
            data.setDirty();
            sync(level);
        }
    }

    public static boolean isPowered(ServerLevel level, BlockPos pos) {
        return VirtualSensorData.get(level).isPowered(pos);
    }

    private static void notifyBlock(ServerLevel level, BlockPos pos) {
        level.neighborChanged(pos, ModBlocks.PLAYER_SENSOR.get(), pos);
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.getGameTime() % CHECK_INTERVAL_TICKS != 0) {
            return;
        }
        VirtualSensorData data = VirtualSensorData.get(level);
        boolean listChanged = false;

        Iterator<Map.Entry<BlockPos, VirtualSensorData.VirtualSensor>> it = data.mutableSensors().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, VirtualSensorData.VirtualSensor> entry = it.next();
            BlockPos pos = entry.getKey();
            VirtualSensorData.VirtualSensor sensor = entry.getValue();

            if (!level.isLoaded(pos)) {
                continue;
            }

            if (!canAttachTo(level.getBlockState(pos))) {
                data.setPowered(pos, sensor, false);
                it.remove();
                listChanged = true;
                Block.popResource(level, pos, new ItemStack(ModBlocks.PLAYER_SENSOR_ITEM.get()));
                continue;
            }

            boolean on = sensor.settings.shouldBeOn(level, pos);
            if (on != sensor.powered) {
                data.setPowered(pos, sensor, on);
                notifyBlock(level, pos);
            }
        }

        if (listChanged) {
            data.setDirty();
            sync(level);
        }
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!player.isShiftKeyDown() || event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        ItemStack stack = event.getItemStack();

        if (stack.is(ModBlocks.PLAYER_SENSOR_ITEM.get()) && canAttachTo(level.getBlockState(pos))) {

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
            if (level instanceof ServerLevel serverLevel) {
                attach(serverLevel, pos, player, stack);
            }
        } else if (stack.isEmpty() && hasSensor(level, pos)) {

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
            if (level.isClientSide) {
                SensorScreen.openVirtual(pos);
            }
        }
    }

    private static boolean canAttachTo(BlockState state) {
        return !state.isAir() && !state.is(ModTags.VIRTUAL_SENSOR_BLACKLIST);
    }

    private static boolean hasSensor(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            return VirtualSensorData.get(serverLevel).get(pos) != null;
        }
        return ClientVirtualSensors.get(pos) != null;
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        syncTo(event.getEntity());
    }

    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        syncTo(event.getEntity());
    }

    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        syncTo(event.getEntity());
    }

    private static void syncTo(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, createSyncPayload(serverPlayer.serverLevel()));
        }
    }

    private static void sync(ServerLevel level) {
        PacketDistributor.sendToPlayersInDimension(level, createSyncPayload(level));
    }

    private static SyncVirtualSensorsPayload createSyncPayload(ServerLevel level) {
        List<SyncVirtualSensorsPayload.Entry> entries = new ArrayList<>();
        VirtualSensorData.get(level).all().forEach((pos, sensor) -> entries.add(new SyncVirtualSensorsPayload.Entry(
                pos, sensor.settings.getRange(), sensor.settings.getOffDelaySeconds())));
        return new SyncVirtualSensorsPayload(entries);
    }
}
