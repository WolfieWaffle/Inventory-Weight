package com.github.wolfiewaffle.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

public class CommandSetArmorBonus {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command
            = Commands.literal("inventoryweight")
            .then(
                Commands.literal("set_armor_carry_bonus")
                .requires((commandSource) -> commandSource.hasPermission(2))
                .then(Commands.argument("value", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(CommandSetArmorBonus::set)
                )
            );

        dispatcher.register(command);
    }

    static int set(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        float value = IntegerArgumentType.getInteger(commandContext, "value");

        ServerPlayer player = commandContext.getSource().getPlayerOrException();

        Item item = player.getMainHandItem().getItem();

        WeightCommandTools.attemptWriteToDatapack(commandContext, "armor_weight_bonus", item, value);

        //player.sendMessage(new TextComponent("SET WEIGHT " + value + " (/reload to update)"), player.getUUID());
        player.displayClientMessage(Component.literal("SET CARRY BONUS TO " + value + " (/reload to update)"), true);

        return Command.SINGLE_SUCCESS;
    }

//    private static void makeSpecificFile(CommandContext<CommandSourceStack> context, ResourceLocation loc, int weight) throws CommandSyntaxException {
//        Path path = context.getSource().getServer().getWorldPath(LevelResource.ROOT);
//        path = Path.of(path + "/datapacks/inventory_weights_datapack");
//
//        // Write json
//        String itemPath = path + "/data/" + loc.getNamespace() + "/inventory_weights/";
//        File jsonFile = new File(itemPath + loc.getPath() + ".json");
//        FMLPaths.getOrCreateGameRelativePath(Path.of(itemPath));
//
//
//        try {
//            // This throws if the file path is invalid
//            File canonFile = jsonFile.getCanonicalFile();
//
//            // Write new entries
//            try (FileWriter fileWriter = new FileWriter(canonFile)) {
//                canonFile.createNewFile();
//                fileWriter.write("" + weight);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
