package com.github.wolfiewaffle.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class WeightCommandTools {

    public static void attemptWriteToDatapack(CommandContext<CommandSourceStack> commandContext, String folderName, Item item, float value) {

        // Make the path for the datapack
        Path path = commandContext.getSource().getServer().getWorldPath(LevelResource.ROOT);
        path = Path.of(path + "/datapacks/inventory_weights_datapack");

        FMLPaths.getOrCreateGameRelativePath(path);
        File packFile = new File(path + "/pack.mcmeta");

        //Write pack.mcmeta
        try {
            // This throws if the file path is invalid
            File canonFile = packFile.getCanonicalFile();

            // Write new entries
            try (FileWriter fileWriter = new FileWriter(canonFile)) {
                canonFile.createNewFile();
                String mcmeta = """
                        {
                            "pack": {
                                "pack_format": 9,
                                "description": "Inventory Weights"
                            }
                        }""";
                fileWriter.write(mcmeta);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write json
        ResourceLocation itemResource = ForgeRegistries.ITEMS.getKey(item); // 1.20
        assert itemResource != null;

        String itemPath = path + "/data/" + itemResource.getNamespace() + "/" + folderName + "/";
        File jsonFile = new File(itemPath + itemResource.getPath() + ".json");
        FMLPaths.getOrCreateGameRelativePath(Path.of(itemPath));

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
    }
}
