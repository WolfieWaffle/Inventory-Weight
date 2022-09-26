package com.github.wolfiewaffle.reader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class JsonWeightReader {

    public static void addEntries(File file, HashMap<ResourceLocation, Float> hashMap) {
        File files[] = file.listFiles();

        for (int i = 0; i < files.length; i++) {
            try {
                readFile(files[i], hashMap);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private static void readFile(File file, HashMap<ResourceLocation, Float> hashMap) throws IOException {
        float defWeight = 1.0f;
        Gson gson = new Gson();
        Type type = new TypeToken<JsonObject>(){}.getType();
        JsonObject jsonObject = gson.fromJson(Files.readString(file.toPath()), type);

        if (jsonObject.has("default")) {
            defWeight = jsonObject.get("default").getAsFloat();
        }

        if (jsonObject.has("entries")) {
            JsonObject entries = jsonObject.get("entries").getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : entries.entrySet()) {
                float weight = entry.getValue().getAsFloat();

                // Negatives are default weight
                if (weight < 0) weight = defWeight;

                //System.out.println("defWeights.add(\"" + entry.getKey() + "=" + weight + "\");");

                if (entry.getKey().charAt(0) == '#') { // Entry is Tag
                    TagKey key = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(entry.getKey().substring(1)));
                    ITag<Item> itemTag = ForgeRegistries.ITEMS.tags().getTag(key);

                    if (itemTag != null) {
                        for (Item item : itemTag.stream().toList()) {
                            hashMap.put(item.getRegistryName(), weight);
                        }
                    }
                } else { // Entry is Item
                    hashMap.put(new ResourceLocation(entry.getKey()), weight);
                }
            }
        }
    }
}
