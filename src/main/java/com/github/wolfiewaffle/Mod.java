package com.github.wolfiewaffle;

import com.github.wolfiewaffle.command.WeightCommands;
import com.github.wolfiewaffle.config.BaseConfig;
import com.github.wolfiewaffle.event.InventoryEvent;
import com.github.wolfiewaffle.network.ArmorCapacityUpgradePacket;
import com.github.wolfiewaffle.network.InventoryWeightPacket;
import com.github.wolfiewaffle.network.InventoryWeightsPacketHandler;
import com.github.wolfiewaffle.reader.CodecJsonDataManager;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;

// The value here should match an entry in the META-INF/mods.toml file
@net.minecraftforge.fml.common.Mod("inventory_weight")
public class Mod
{
    public static HashMap<ResourceLocation, Integer> client_weight_map = new HashMap<>();
    public static HashMap<ResourceLocation, Integer> client_armor_bonus_map = new HashMap<>();

    public static CodecJsonDataManager<Integer> weight_codec = new CodecJsonDataManager<>("inventory_weights", Codec.INT);
    public static CodecJsonDataManager<Integer> armor_upgrade_codec = new CodecJsonDataManager<>("armor_weight_bonus", Codec.INT);

    public Mod()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Config
        BaseConfig.init();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new InventoryEvent());

        // Networking
        InventoryWeightsPacketHandler.INSTANCE.registerMessage(1, InventoryWeightPacket.class, InventoryWeightPacket::toBytes, InventoryWeightPacket::fromBytes, InventoryWeightPacket::handlePacket);
        InventoryWeightsPacketHandler.INSTANCE.registerMessage(2, ArmorCapacityUpgradePacket.class, ArmorCapacityUpgradePacket::toBytes, ArmorCapacityUpgradePacket::fromBytes, ArmorCapacityUpgradePacket::handlePacket);
        weight_codec.subscribeAsSyncable(InventoryWeightsPacketHandler.INSTANCE, (map) -> new InventoryWeightPacket(map));
        armor_upgrade_codec.subscribeAsSyncable(InventoryWeightsPacketHandler.INSTANCE, (map) -> new ArmorCapacityUpgradePacket(map));
    }

    @SubscribeEvent
    public void tooltipEvent(final ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        // Armor only
        if (BaseConfig.onlyArmor.get() && !InventoryEvent.isArmor(stack, event.getEntity())) {
            return;
        }

        ResourceLocation itemResource = ForgeRegistries.ITEMS.getKey(stack.getItem());
        assert itemResource != null;

        // Weight capacity upgrades on armor
        double capacityUpgrade = 0;

        if (client_armor_bonus_map.containsKey(itemResource)) {
            capacityUpgrade = client_armor_bonus_map.get(itemResource);
            if (capacityUpgrade > 0) event.getToolTip().add(Component.literal("+" + (int) capacityUpgrade + " Max Carry Weight").withStyle(ChatFormatting.BLUE));
        }

        // Weight
        double weight = BaseConfig.defWeight.get();

        if (client_weight_map.containsKey(itemResource)) {
            weight = client_weight_map.get(itemResource);
        }

        int config = BaseConfig.tooltipMode.get();

        if (config == 0 || (config == 1 && weight > 0)) {
            event.getToolTip().add(Component.literal("Weight " + weight).withStyle(ChatFormatting.GOLD));
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(WeightCommands.class);
    }

    @SubscribeEvent
    public void reloadEvent(final AddReloadListenerEvent event) {
        event.addListener(weight_codec);
        event.addListener(armor_upgrade_codec);
    }
}
