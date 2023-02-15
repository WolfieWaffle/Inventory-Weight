package com.github.wolfiewaffle.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class BaseConfig {

    public static ForgeConfigSpec COMMON_CONFIG;

    public static ForgeConfigSpec.DoubleValue maxWeight;
    public static ForgeConfigSpec.DoubleValue defWeight;
    public static ForgeConfigSpec.BooleanValue onlyArmor;
    public static ForgeConfigSpec.IntValue tooltipMode;

    public static void init() {
        initCommon();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
    }

    private static void initCommon() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("General Settings").push("general");
        maxWeight = builder.comment("How much weight is required to achieve the maximum penalty.").defineInRange("maxWeight", 3000.0, 0, Double.MAX_VALUE);
        defWeight = builder.comment("The default weight of an item that does not have a custom weight defined.").defineInRange("defWeight", 1, 0, Double.MAX_VALUE);
        onlyArmor = builder.comment("Only care about the weight of equipped armor and held shields.").define("onlyArmor", false);
        tooltipMode = builder.comment("0 - always show. 1 - show when nonzero. (onlyArmor also hides anything that isn't armor or shield)").defineInRange("tooltipMode", 0, 0, 1);
        builder.pop();

        COMMON_CONFIG = builder.build();
    }
}
