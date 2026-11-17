package com.sensorlamp.virtual;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.sensorlamp.network.SyncVirtualSensorsPayload;

import net.minecraft.core.BlockPos;

public final class ClientVirtualSensors {
    private static Map<BlockPos, SyncVirtualSensorsPayload.Entry> sensors = new HashMap<>();

    private ClientVirtualSensors() {
    }

    public static void replace(List<SyncVirtualSensorsPayload.Entry> entries) {
        Map<BlockPos, SyncVirtualSensorsPayload.Entry> map = new HashMap<>();
        for (SyncVirtualSensorsPayload.Entry entry : entries) {
            map.put(entry.pos(), entry);
        }
        sensors = map;
    }

    @Nullable
    public static SyncVirtualSensorsPayload.Entry get(BlockPos pos) {
        return sensors.get(pos);
    }

    public static Collection<SyncVirtualSensorsPayload.Entry> all() {
        return sensors.values();
    }

    public static void clear() {
        sensors = new HashMap<>();
    }
}
