package com.github.wolfiewaffle.reader;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import oshi.util.tuples.Pair;

import java.util.HashMap;

public class WeightReader {

    public static void applyWeights(HashMap<ResourceLocation, Float> hashMap) {
        for (String string : WeightConfig.stringList.get()) {
            Pair<String, Double> pair = pairFromString(string);

            if (pair != null) {

                // Handle tag entries
                if (pair.getA().charAt(0) == '#') { // Entry is Tag
                    TagKey key = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(pair.getA().substring(1)));
                    ITag<Item> itemTag = ForgeRegistries.ITEMS.tags().getTag(key);

                    if (itemTag != null) {
                        for (Item item : itemTag.stream().toList()) {
                            hashMap.put(item.getRegistryName(), pair.getB().floatValue());
                        }
                    }
                } else if (ResourceLocation.isValidResourceLocation(pair.getA())) { // Entry is Item
                    hashMap.put(new ResourceLocation(pair.getA()), pair.getB().floatValue());
                }
            }
        }
    }

    public static Pair<String, Double> pairFromString(String string) {
        String[] weightString = string.split("=");

        if (weightString.length != 2) return null;

        try {
            Double num = Double.parseDouble(weightString[1]);

            return new Pair<>(weightString[0], num);
        } catch(Exception e) {
            return null;
        }
    }
}
