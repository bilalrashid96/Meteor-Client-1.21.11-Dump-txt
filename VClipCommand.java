package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.class_2172;
import net.minecraft.class_2828;
import net.minecraft.class_2833;

public class VClipCommand extends Command {
   public VClipCommand() {
      super("vclip", "Lets you clip through blocks vertically.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(argument("blocks", DoubleArgumentType.doubleArg()).executes((context) -> {
         double blocks = (Double)context.getArgument("blocks", Double.class);
         int packetsRequired = (int)Math.ceil(Math.abs(blocks / (double)10.0F));
         if (packetsRequired > 20) {
            packetsRequired = 1;
         }

         if (mc.field_1724.method_5765()) {
            for(int packetNumber = 0; packetNumber < packetsRequired - 1; ++packetNumber) {
               mc.field_1724.field_3944.method_52787(class_2833.method_65307(mc.field_1724.method_5854()));
            }

            mc.field_1724.method_5854().method_5814(mc.field_1724.method_5854().method_23317(), mc.field_1724.method_5854().method_23318() + blocks, mc.field_1724.method_5854().method_23321());
            mc.field_1724.field_3944.method_52787(class_2833.method_65307(mc.field_1724.method_5854()));
         } else {
            for(int packetNumber = 0; packetNumber < packetsRequired - 1; ++packetNumber) {
               mc.field_1724.field_3944.method_52787(new class_2828.class_5911(true, mc.field_1724.field_5976));
            }

            mc.field_1724.field_3944.method_52787(new class_2828.class_2829(mc.field_1724.method_23317(), mc.field_1724.method_23318() + blocks, mc.field_1724.method_23321(), true, mc.field_1724.field_5976));
            mc.field_1724.method_5814(mc.field_1724.method_23317(), mc.field_1724.method_23318() + blocks, mc.field_1724.method_23321());
         }

         return 1;
      }));
   }
}
