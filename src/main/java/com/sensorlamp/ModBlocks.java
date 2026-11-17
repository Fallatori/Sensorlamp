package com.sensorlamp;

import com.sensorlamp.block.PlayerSensorBlock;
import com.sensorlamp.block.SensorLampBlock;
import com.sensorlamp.block.SensorLanternBlock;
import com.sensorlamp.block.SensorWallLanternBlock;
import com.sensorlamp.item.SensorLanternItem;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SensorLamp.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SensorLamp.MODID);

    public static final DeferredBlock<SensorLampBlock> SENSOR_LAMP = BLOCKS.register("sensor_lamp",
            () -> new SensorLampBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_ORANGE)
                    .strength(0.3F)
                    .sound(SoundType.GLASS)
                    .lightLevel(state -> state.getValue(SensorLampBlock.POWERED) ? 15 : 0)));

    public static final DeferredBlock<SensorLanternBlock> SENSOR_LANTERN = BLOCKS.register("sensor_lantern",
            () -> new SensorLanternBlock(lanternProperties(15)));
    public static final DeferredBlock<SensorWallLanternBlock> SENSOR_WALL_LANTERN = BLOCKS.register("sensor_wall_lantern",
            () -> new SensorWallLanternBlock(lanternProperties(15).lootFrom(SENSOR_LANTERN)));

    public static final DeferredBlock<SensorLanternBlock> SENSOR_SOUL_LANTERN = BLOCKS.register("sensor_soul_lantern",
            () -> new SensorLanternBlock(lanternProperties(10)));
    public static final DeferredBlock<SensorWallLanternBlock> SENSOR_SOUL_WALL_LANTERN = BLOCKS.register("sensor_soul_wall_lantern",
            () -> new SensorWallLanternBlock(lanternProperties(10).lootFrom(SENSOR_SOUL_LANTERN)));

    public static final DeferredBlock<PlayerSensorBlock> PLAYER_SENSOR = BLOCKS.register("player_sensor",
            () -> new PlayerSensorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(1.5F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final DeferredItem<BlockItem> SENSOR_LAMP_ITEM = ITEMS.registerSimpleBlockItem("sensor_lamp", SENSOR_LAMP);
    public static final DeferredItem<SensorLanternItem> SENSOR_LANTERN_ITEM = ITEMS.register("sensor_lantern",
            () -> new SensorLanternItem(SENSOR_LANTERN.get(), SENSOR_WALL_LANTERN.get(), new Item.Properties()));
    public static final DeferredItem<SensorLanternItem> SENSOR_SOUL_LANTERN_ITEM = ITEMS.register("sensor_soul_lantern",
            () -> new SensorLanternItem(SENSOR_SOUL_LANTERN.get(), SENSOR_SOUL_WALL_LANTERN.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> PLAYER_SENSOR_ITEM = ITEMS.registerSimpleBlockItem("player_sensor", PLAYER_SENSOR);

    public static boolean isSensorItem(ItemStack stack) {
        return stack.is(PLAYER_SENSOR_ITEM.get())
                || stack.is(SENSOR_LAMP_ITEM.get())
                || stack.is(SENSOR_LANTERN_ITEM.get())
                || stack.is(SENSOR_SOUL_LANTERN_ITEM.get());
    }

    private static BlockBehaviour.Properties lanternProperties(int lightWhenOn) {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .forceSolidOn()
                .requiresCorrectToolForDrops()
                .strength(3.5F)
                .sound(SoundType.LANTERN)
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY)
                .lightLevel(state -> state.getValue(PlayerSensorBlock.POWERED) ? lightWhenOn : 0);
    }
}
