package com.github.wolfiewaffle.event;

import com.github.wolfiewaffle.Mod;
import com.github.wolfiewaffle.config.BaseConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.UUID;


public class InventoryEvent {
    private static final UUID WEIGHT_UUID = UUID.fromString("23cd1056-2967-4c57-902a-d3ead6358272");

    @SubscribeEvent
    public void inventoryEvent(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;
        if (event.side == LogicalSide.CLIENT) return;

        float weight = 0;
        Inventory inventory = event.player.getInventory();

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            ResourceLocation loc = stack.getItem().getRegistryName();

            if (stack != ItemStack.EMPTY) {
                float amount = 0;

                if (Mod.weight_codec.getData().containsKey(loc)) {
                    amount += Mod.weight_codec.getData().get(loc);
                } else {
                    amount += Mod.DEFAULT_WEIGHT;
                }

                weight += amount * stack.getCount();
            }
        }

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
}
