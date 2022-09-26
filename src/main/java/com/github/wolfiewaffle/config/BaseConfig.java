package com.github.wolfiewaffle.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class BaseConfig {

    public static ForgeConfigSpec COMMON_CONFIG;

    public static ForgeConfigSpec.DoubleValue maxWeight;
    public static ForgeConfigSpec.DoubleValue defWeight;

    public static void init() {
        initCommon();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
    }

    private static void initCommon() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("General Settings").push("general");
        maxWeight = builder.comment("How much weight is required to achieve the maximum penalty.").defineInRange("maxWeight", 3000.0, 0, Double.MAX_VALUE);
        defWeight = builder.comment("The default weight of an item that does not have a custom weight defined.").defineInRange("defWeight", 1, 0, Double.MAX_VALUE);
        builder.pop();

        COMMON_CONFIG = builder.build();
    }
}
