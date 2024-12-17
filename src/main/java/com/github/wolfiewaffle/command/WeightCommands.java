package com.github.wolfiewaffle.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WeightCommands {

    @SubscribeEvent
    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();

        CommandSetHandWeight.register(commandDispatcher);
        CommandSetArmorBonus.register(commandDispatcher);
        CommandGetWeight.register(commandDispatcher);
    }
}
