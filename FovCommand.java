package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.mixininterface.ISimpleOption;
import net.minecraft.class_2172;

public class FovCommand extends Command {
   public FovCommand() {
      super("fov", "Changes your fov.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(argument("fov", IntegerArgumentType.integer(1, 180)).executes((context) -> {
         ((ISimpleOption)mc.field_1690.method_41808()).meteor$set(context.getArgument("fov", Integer.class));
         return 1;
      }));
   }
}
