package meteordevelopment.meteorclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.class_2170;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_7157;
import net.minecraft.class_7887;

public abstract class Command {
   protected static class_7157 REGISTRY_ACCESS = class_2170.method_46732(class_7887.method_46817());
   protected static final int SINGLE_SUCCESS = 1;
   protected static final class_310 mc;
   private final String name;
   private final String title;
   private final String description;
   private final List<String> aliases;

   public Command(String name, String description, String... aliases) {
      this.name = name;
      this.title = Utils.nameToTitle(name);
      this.description = description;
      this.aliases = List.of(aliases);
   }

   protected static <T> RequiredArgumentBuilder<class_2172, T> argument(String name, ArgumentType<T> type) {
      return RequiredArgumentBuilder.argument(name, type);
   }

   protected static LiteralArgumentBuilder<class_2172> literal(String name) {
      return LiteralArgumentBuilder.literal(name);
   }

   public final void registerTo(CommandDispatcher<class_2172> dispatcher) {
      this.register(dispatcher, this.name);

      for(String alias : this.aliases) {
         this.register(dispatcher, alias);
      }

   }

   public void register(CommandDispatcher<class_2172> dispatcher, String name) {
      LiteralArgumentBuilder<class_2172> builder = LiteralArgumentBuilder.literal(name);
      this.build(builder);
      dispatcher.register(builder);
   }

   public abstract void build(LiteralArgumentBuilder<class_2172> var1);

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public List<String> getAliases() {
      return this.aliases;
   }

   public String toString() {
      String var10000 = Config.get().prefix.get();
      return var10000 + this.name;
   }

   public String toString(String... args) {
      StringBuilder base = new StringBuilder(this.toString());

      for(String arg : args) {
         base.append(' ').append(arg);
      }

      return base.toString();
   }

   public void info(class_2561 message) {
      ChatUtils.forceNextPrefixClass(this.getClass());
      ChatUtils.sendMsg(this.title, message);
   }

   public void info(String message, Object... args) {
      ChatUtils.forceNextPrefixClass(this.getClass());
      ChatUtils.infoPrefix(this.title, message, args);
   }

   public void warning(String message, Object... args) {
      ChatUtils.forceNextPrefixClass(this.getClass());
      ChatUtils.warningPrefix(this.title, message, args);
   }

   public void error(String message, Object... args) {
      ChatUtils.forceNextPrefixClass(this.getClass());
      ChatUtils.errorPrefix(this.title, message, args);
   }

   static {
      mc = MeteorClient.mc;
   }
}
