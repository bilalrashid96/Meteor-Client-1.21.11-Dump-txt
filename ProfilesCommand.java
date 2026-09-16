package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.ProfileArgumentType;
import meteordevelopment.meteorclient.systems.profiles.Profile;
import meteordevelopment.meteorclient.systems.profiles.Profiles;
import net.minecraft.class_2172;

public class ProfilesCommand extends Command {
   public ProfilesCommand() {
      super("profiles", "Loads and saves profiles.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(literal("load").then(argument("profile", ProfileArgumentType.create()).executes((context) -> {
         Profile profile = ProfileArgumentType.get(context);
         if (profile != null) {
            profile.load();
            this.info("Loaded profile (highlight)%s(default).", new Object[]{profile.name.get()});
         }

         return 1;
      })));
      builder.then(literal("save").then(argument("profile", ProfileArgumentType.create()).executes((context) -> {
         Profile profile = ProfileArgumentType.get(context);
         if (profile != null) {
            profile.save();
            this.info("Saved profile (highlight)%s(default).", new Object[]{profile.name.get()});
         }

         return 1;
      })));
      builder.then(literal("delete").then(argument("profile", ProfileArgumentType.create()).executes((context) -> {
         Profile profile = ProfileArgumentType.get(context);
         if (profile != null) {
            Profiles.get().remove(profile);
            this.info("Deleted profile (highlight)%s(default).", new Object[]{profile.name.get()});
         }

         return 1;
      })));
   }
}
