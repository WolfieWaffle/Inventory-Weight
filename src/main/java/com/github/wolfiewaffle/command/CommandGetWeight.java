package com.github.wolfiewaffle.command;

import com.github.wolfiewaffle.event.InventoryEvent;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.loading.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class CommandGetWeight {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command
                = Commands.literal("getweight")
                .requires((commandSource) -> commandSource.hasPermission(2))
                .executes(CommandGetWeight::get);

        dispatcher.register(command);
    }

    static int get(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        ServerPlayer player = commandContext.getSource().getPlayerOrException();

        float weight = InventoryEvent.getAllWeight(player.getInventory());

        player.sendMessage(new TextComponent("CURRENT WEIGHT " + weight), player.getUUID());

        return Command.SINGLE_SUCCESS;
    }

    private static void makeSpecificFile(CommandContext<CommandSourceStack> context, ResourceLocation loc, int weight) throws CommandSyntaxException {
        Path path = context.getSource().getServer().getWorldPath(LevelResource.ROOT);
        path = Path.of(path + "/datapacks/inventory_weights_datapack");

        // Write json
        String itemPath = path + "/data/" + loc.getNamespace() + "/inventory_weights/";
        File jsonFile = new File(itemPath + loc.getPath() + ".json");
        FileUtils.getOrCreateDirectory(Path.of(itemPath), loc.getPath() + " file");

        try {
            // This throws if the file path is invalid
            File canonFile = jsonFile.getCanonicalFile();

            // Write new entries
            try (FileWriter fileWriter = new FileWriter(canonFile)) {
                canonFile.createNewFile();
                fileWriter.write("" + weight);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
