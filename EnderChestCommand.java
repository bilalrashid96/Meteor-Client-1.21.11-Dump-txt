package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2172;

public class EnderChestCommand extends Command {
   public EnderChestCommand() {
      super("ender-chest", "Allows you to preview memory of your ender chest.", "ec", "echest");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.executes((context) -> {
         Utils.openContainer(class_1802.field_8466.method_7854(), new class_1799[27], true);
         return 1;
      });
   }
}
