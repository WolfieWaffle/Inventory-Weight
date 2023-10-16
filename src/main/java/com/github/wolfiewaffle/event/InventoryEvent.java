package com.github.wolfiewaffle.event;

import com.github.wolfiewaffle.Mod;
import com.github.wolfiewaffle.config.BaseConfig;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;


public class InventoryEvent {
    private static final UUID WEIGHT_UUID = UUID.fromString("23cd1056-2967-4c57-902a-d3ead6358272");

    @SubscribeEvent
    public void inventoryEvent(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;
        if (event.side == LogicalSide.CLIENT) return;

        Inventory inventory = event.player.getInventory();

        float weight = getAllWeight(inventory);


        float speedPenalty = (float) (Math.max(0.1f, 1.0f - (weight / BaseConfig.maxWeight.get())) - 1);
        boolean hasChanged = true;

        AttributeInstance instance = event.player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeModifier modifier = instance.getModifier(WEIGHT_UUID);

        // Add modifier if it does not exist already
        if (modifier == null) {
            modifier = new AttributeModifier(WEIGHT_UUID, "wolfiewaffle.weight", speedPenalty, AttributeModifier.Operation.MULTIPLY_TOTAL);
            instance.addTransientModifier(modifier);
            hasChanged = false;
        }

        // Update the modifier
        if (hasChanged) {
            instance.removeModifier(WEIGHT_UUID);
            instance.addTransientModifier(new AttributeModifier(WEIGHT_UUID, "wolfiewaffle.weight", speedPenalty, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    public static float getAllWeight(Inventory inventory) {
        float weight = 0;

        weight += getListWeight(inventory.armor, null);
        if (BaseConfig.onlyArmor.get()) {
            weight += checkForShield(inventory.offhand.get(0), inventory.getSelected());
        } else {
            weight += getListWeight(inventory.items, inventory.getSelected());
            weight += getListWeight(inventory.offhand, null);
            weight += getWeightStack(inventory.getSelected());
        }

        return weight;
    }

    private static float getListWeight(NonNullList<ItemStack> list, ItemStack selected) {
        float weight = 0;

        for (ItemStack stack : list) {
            if (selected != null && stack == selected) continue; // Held should be counted separately cuz of shield rules

            weight += getWeightStack(stack);
        }

        return weight;
    }

    private static float checkForShield(ItemStack offhand, ItemStack selected) {
        float weight = 0;
        if (offhand.getItem() instanceof ShieldItem) {
            weight += getWeightStack(offhand);
        }
        if (selected.getItem() instanceof ShieldItem) {
            weight += getWeightStack(selected);
        }
        return weight;
    }

    private static float getWeightStack(ItemStack stack) {
        ResourceLocation loc = ForgeRegistries.ITEMS.getKey(stack.getItem());
        assert loc != null;

        if (stack != ItemStack.EMPTY) {
            float amount = 0;

            if (Mod.weight_codec.getData().containsKey(loc)) {
                amount += Mod.weight_codec.getData().get(loc);
            } else {
                amount += Mod.DEFAULT_WEIGHT;
            }

            return amount * stack.getCount();
        }
        return 0;
    }

    public static boolean isArmor(ItemStack stack, Entity player) {
        if (stack.canEquip(EquipmentSlot.HEAD, player)) return true;
        if (stack.canEquip(EquipmentSlot.CHEST, player)) return true;
        if (stack.canEquip(EquipmentSlot.LEGS, player)) return true;
        if (stack.canEquip(EquipmentSlot.FEET, player)) return true;
        if (stack.getItem() instanceof ShieldItem) return true;

        return false;
    }
}
