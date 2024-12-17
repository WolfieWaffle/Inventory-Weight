package com.github.wolfiewaffle.network;

import com.github.wolfiewaffle.Mod;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ArmorCapacityUpgradePacket {

    protected Map<ResourceLocation, Integer> map;

    public ArmorCapacityUpgradePacket(final Map<ResourceLocation, Integer> map) {
        this.map = map;
    }

    public static ArmorCapacityUpgradePacket fromBytes(final FriendlyByteBuf buf) {
        final Map<ResourceLocation, Integer> pacMap = buf.readJsonWithCodec(Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT));
        return new ArmorCapacityUpgradePacket(pacMap);
    }

    public static void toBytes(final ArmorCapacityUpgradePacket msg, final FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT), msg.map);
    }

    public static void handlePacket(final ArmorCapacityUpgradePacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.enqueueWork(() -> {
                Mod.client_armor_bonus_map = new HashMap<>(message.map);
            });
        }
        context.setPacketHandled(true);
    }
}
