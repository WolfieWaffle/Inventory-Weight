package com.github.wolfiewaffle.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.loading.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class CommandSetHandWeight {
    private static String mcmeta = "{\n" +
            "    \"pack\": {\n" +
            "        \"pack_format\": 9,\n" +
            "        \"description\": \"Inventory Weights\"\n" +
            "    }\n" +
            "}";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command
                = Commands.literal("setweight")
                .requires((commandSource) -> commandSource.hasPermission(2))
                .then(Commands.argument("value", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(CommandSetHandWeight::set)
                );

        dispatcher.register(command);
    }

    static int set(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        float value = IntegerArgumentType.getInteger(commandContext, "value");

        ServerPlayer player = commandContext.getSource().getPlayerOrException();

        Item item = player.getMainHandItem().getItem();
        Path path = commandContext.getSource().getServer().getWorldPath(LevelResource.ROOT);
        path = Path.of(path + "/datapacks/inventory_weights_datapack");

        FileUtils.getOrCreateDirectory(path, "datapack root");
        File packFile = new File(path + "/pack.mcmeta");

        //Write pack.mcmeta
        try {
            // This throws if the file path is invalid
            File canonFile = packFile.getCanonicalFile();

            // Write new entries
            try (FileWriter fileWriter = new FileWriter(canonFile)) {
                canonFile.createNewFile();
                fileWriter.write("" + mcmeta);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write json
        String itemPath = path + "/data/" + item.getRegistryName().getNamespace() + "/inventory_weights/";
        File jsonFile = new File(itemPath + item.getRegistryName().getPath() + ".json");
        FileUtils.getOrCreateDirectory(Path.of(itemPath), item.getRegistryName().getPath() + " file");

        try {
            // This throws if the file path is invalid
            File canonFile = jsonFile.getCanonicalFile();

            // Write new entries
            try (FileWriter fileWriter = new FileWriter(canonFile)) {
                canonFile.createNewFile();
                fileWriter.write("" + value);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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
