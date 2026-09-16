package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.NoFall;
import meteordevelopment.meteorclient.systems.modules.player.AntiHunger;
import net.minecraft.class_2172;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2828;

public class DamageCommand extends Command {
   private static final SimpleCommandExceptionType INVULNERABLE = new SimpleCommandExceptionType(class_2561.method_43470("You are invulnerable."));

   public DamageCommand() {
      super("damage", "Damages self", "dmg");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(argument("damage", IntegerArgumentType.integer(1, 7)).executes((context) -> {
         int amount = IntegerArgumentType.getInteger(context, "damage");
         if (mc.field_1724.method_31549().field_7480) {
            throw INVULNERABLE.create();
         } else {
            this.damagePlayer(amount);
            return 1;
         }
      }));
   }

   private void damagePlayer(int amount) {
      boolean noFall = Modules.get().isActive(NoFall.class);
      if (noFall) {
         ((NoFall)Modules.get().get(NoFall.class)).toggle();
      }

      boolean antiHunger = Modules.get().isActive(AntiHunger.class);
      if (antiHunger) {
         ((AntiHunger)Modules.get().get(AntiHunger.class)).toggle();
      }

      class_243 pos = mc.field_1724.method_73189();

      for(int i = 0; i < 80; ++i) {
         this.sendPositionPacket(pos.field_1352, pos.field_1351 + (double)amount + 2.1, pos.field_1350, false);
         this.sendPositionPacket(pos.field_1352, pos.field_1351 + 0.05, pos.field_1350, false);
      }

      this.sendPositionPacket(pos.field_1352, pos.field_1351, pos.field_1350, true);
      if (noFall) {
         ((NoFall)Modules.get().get(NoFall.class)).toggle();
      }

      if (antiHunger) {
         ((AntiHunger)Modules.get().get(AntiHunger.class)).toggle();
      }

   }

   private void sendPositionPacket(double x, double y, double z, boolean onGround) {
      mc.field_1724.field_3944.method_52787(new class_2828.class_2829(x, y, z, onGround, mc.field_1724.field_5976));
   }
}
