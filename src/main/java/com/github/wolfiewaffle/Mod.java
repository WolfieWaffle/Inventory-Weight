package com.github.wolfiewaffle;

import com.github.wolfiewaffle.command.WeightCommands;
import com.github.wolfiewaffle.config.BaseConfig;
import com.github.wolfiewaffle.event.InventoryEvent;
import com.github.wolfiewaffle.network.InventoryWeightPacket;
import com.github.wolfiewaffle.network.InventoryWeightsPacketHandler;
import com.github.wolfiewaffle.reader.CodecJsonDataManager;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.HashMap;

// The value here should match an entry in the META-INF/mods.toml file
@net.minecraftforge.fml.common.Mod("inventory_weight")
public class Mod
{
    public static final int DEFAULT_WEIGHT = 1;

    public static HashMap<ResourceLocation, Integer> client_weight_map = new HashMap<>();

    public static CodecJsonDataManager<Integer> weight_codec = new CodecJsonDataManager<>("inventory_weights", Codec.INT);

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
        weight_codec.subscribeAsSyncable(InventoryWeightsPacketHandler.INSTANCE, (map) -> new InventoryWeightPacket(map));
    }

    @SubscribeEvent
    public void tooltipEvent(final ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        // Armor only
        if (BaseConfig.onlyArmor.get() && !InventoryEvent.isArmor(stack, event.getPlayer())) {
            return;
        }

        ResourceLocation item = stack.getItem().getRegistryName();
        int weight = Mod.DEFAULT_WEIGHT;

        if (client_weight_map.containsKey(item)) {
            weight = client_weight_map.get(item);
        }

        int config = BaseConfig.tooltipMode.get();

        if (config == 0 || (config == 1 && weight > 0)) {
            event.getToolTip().add(new TextComponent("Weight " + weight).withStyle(ChatFormatting.GOLD));
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(WeightCommands.class);
    }

    @SubscribeEvent
    public void reloadEvent(final AddReloadListenerEvent event) {
        event.addListener(weight_codec);
    }
}
