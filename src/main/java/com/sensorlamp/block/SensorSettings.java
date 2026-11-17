package com.sensorlamp.block;

import com.sensorlamp.Config;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;

public class SensorSettings {

    public static final int MIN_RANGE = 1;
    public static final int MAX_RANGE = 32;
    public static final int MIN_OFF_DELAY_SECONDS = 0;
    public static final int MAX_OFF_DELAY_SECONDS = 120;

    private int range = Config.DETECTION_RADIUS.get();
    private int offDelaySeconds = Config.OFF_DELAY_SECONDS.get();

    private long lastSeenTime = -1;

    public boolean shouldBeOn(ServerLevel level, BlockPos pos) {
        long now = level.getGameTime();

        if (level.hasNearbyAlivePlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, range)) {
            lastSeenTime = now;
            return true;
        }

        return lastSeenTime >= 0 && now - lastSeenTime < offDelaySeconds * 20L;
    }

    public int getRange() {
        return range;
    }

    public int getOffDelaySeconds() {
        return offDelaySeconds;
    }

    public void set(int range, int offDelaySeconds) {

        this.range = Mth.clamp(range, MIN_RANGE, MAX_RANGE);
        this.offDelaySeconds = Mth.clamp(offDelaySeconds, MIN_OFF_DELAY_SECONDS, MAX_OFF_DELAY_SECONDS);
    }

    public void save(CompoundTag tag) {
        tag.putInt("Range", range);
        tag.putInt("OffDelaySeconds", offDelaySeconds);
    }

    public void load(CompoundTag tag) {
        if (tag.contains("Range")) {
            range = tag.getInt("Range");
        }
        if (tag.contains("OffDelaySeconds")) {
            offDelaySeconds = tag.getInt("OffDelaySeconds");
        }
    }
}
