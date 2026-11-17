package com.sensorlamp.virtual;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.sensorlamp.SensorLamp;
import com.sensorlamp.block.SensorSettings;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class VirtualSensorData extends SavedData {
    private static final String NAME = SensorLamp.MODID + "_virtual_sensors";

    public static class VirtualSensor {
        public final SensorSettings settings = new SensorSettings();

        public boolean powered;
    }

    private final Map<BlockPos, VirtualSensor> sensors = new HashMap<>();

    private final LongSet poweredPositions = new LongOpenHashSet();

    public static VirtualSensorData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(VirtualSensorData::new, VirtualSensorData::load), NAME);
    }

    @Nullable
    public VirtualSensor get(BlockPos pos) {
        return sensors.get(pos);
    }

    public boolean isPowered(BlockPos pos) {
        return isPoweredAt(pos.asLong());
    }

    public boolean isPoweredAt(long pos) {
        return !poweredPositions.isEmpty() && poweredPositions.contains(pos);
    }

    public void setPowered(BlockPos pos, VirtualSensor sensor, boolean on) {
        sensor.powered = on;
        if (on) {
            poweredPositions.add(pos.asLong());
        } else {
            poweredPositions.remove(pos.asLong());
        }
        setDirty();
    }

    public boolean add(BlockPos pos) {
        if (sensors.containsKey(pos)) {
            return false;
        }

        sensors.put(pos.immutable(), new VirtualSensor());
        setDirty();
        return true;
    }

    @Nullable
    public VirtualSensor remove(BlockPos pos) {
        VirtualSensor removed = sensors.remove(pos);
        if (removed != null) {
            poweredPositions.remove(pos.asLong());
            setDirty();
        }
        return removed;
    }

    public Map<BlockPos, VirtualSensor> all() {
        return Collections.unmodifiableMap(sensors);
    }

    Map<BlockPos, VirtualSensor> mutableSensors() {
        return sensors;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        sensors.forEach((pos, sensor) -> {
            CompoundTag entry = new CompoundTag();
            entry.putLong("Pos", pos.asLong());
            entry.putBoolean("Powered", sensor.powered);
            sensor.settings.save(entry);
            list.add(entry);
        });
        tag.put("Sensors", list);
        return tag;
    }

    private static VirtualSensorData load(CompoundTag tag, HolderLookup.Provider registries) {
        VirtualSensorData data = new VirtualSensorData();
        ListTag list = tag.getList("Sensors", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            VirtualSensor sensor = new VirtualSensor();
            sensor.powered = entry.getBoolean("Powered");
            sensor.settings.load(entry);
            long pos = entry.getLong("Pos");
            data.sensors.put(BlockPos.of(pos), sensor);
            if (sensor.powered) {
                data.poweredPositions.add(pos);
            }
        }
        return data;
    }
}
