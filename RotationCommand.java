package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.DirectionArgumentType;
import net.minecraft.class_2172;
import net.minecraft.class_2350;
import net.minecraft.class_3532;

public class RotationCommand extends Command {
   public RotationCommand() {
      super("rotation", "Modifies your rotation.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      ((LiteralArgumentBuilder)builder.then(((LiteralArgumentBuilder)literal("set").then(argument("direction", DirectionArgumentType.create()).executes((context) -> {
         mc.field_1724.method_36457((float)(((class_2350)context.getArgument("direction", class_2350.class)).method_62675().method_10264() * -90));
         mc.field_1724.method_36456(((class_2350)context.getArgument("direction", class_2350.class)).method_10144());
         return 1;
      }))).then(((RequiredArgumentBuilder)argument("pitch", FloatArgumentType.floatArg(-90.0F, 90.0F)).executes((context) -> {
         mc.field_1724.method_36457((Float)context.getArgument("pitch", Float.class));
         return 1;
      })).then(argument("yaw", FloatArgumentType.floatArg(-180.0F, 180.0F)).executes((context) -> {
         mc.field_1724.method_36457((Float)context.getArgument("pitch", Float.class));
         mc.field_1724.method_36456((Float)context.getArgument("yaw", Float.class));
         return 1;
      }))))).then(literal("add").then(((RequiredArgumentBuilder)argument("pitch", FloatArgumentType.floatArg(-90.0F, 90.0F)).executes((context) -> {
         float pitch = mc.field_1724.method_36455() + (Float)context.getArgument("pitch", Float.class);
         mc.field_1724.method_36457(pitch >= 0.0F ? Math.min(pitch, 90.0F) : Math.max(pitch, -90.0F));
         return 1;
      })).then(argument("yaw", FloatArgumentType.floatArg(-180.0F, 180.0F)).executes((context) -> {
         float pitch = mc.field_1724.method_36455() + (Float)context.getArgument("pitch", Float.class);
         mc.field_1724.method_36457(pitch >= 0.0F ? Math.min(pitch, 90.0F) : Math.max(pitch, -90.0F));
         float yaw = mc.field_1724.method_36454() + (Float)context.getArgument("yaw", Float.class);
         mc.field_1724.method_36456(class_3532.method_15393(yaw));
         return 1;
      }))));
   }
}
