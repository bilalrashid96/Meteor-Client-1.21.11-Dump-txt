package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.network.Capes;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.class_2172;

public class ReloadCommand extends Command {
   public ReloadCommand() {
      super("reload", "Reloads many systems.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.executes((context) -> {
         this.warning("Reloading systems, this may take a while.", new Object[0]);
         Systems.load();
         Capes.init();
         Fonts.refresh();
         MeteorExecutor.execute(() -> Friends.get().forEach(Friend::updateInfo));
         return 1;
      });
   }
}
