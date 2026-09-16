package meteordevelopment.meteorclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.commands.BindCommand;
import meteordevelopment.meteorclient.commands.commands.BindsCommand;
import meteordevelopment.meteorclient.commands.commands.CommandsCommand;
import meteordevelopment.meteorclient.commands.commands.DamageCommand;
import meteordevelopment.meteorclient.commands.commands.DisconnectCommand;
import meteordevelopment.meteorclient.commands.commands.DismountCommand;
import meteordevelopment.meteorclient.commands.commands.DropCommand;
import meteordevelopment.meteorclient.commands.commands.EnchantCommand;
import meteordevelopment.meteorclient.commands.commands.EnderChestCommand;
import meteordevelopment.meteorclient.commands.commands.FakePlayerCommand;
import meteordevelopment.meteorclient.commands.commands.FovCommand;
import meteordevelopment.meteorclient.commands.commands.FriendsCommand;
import meteordevelopment.meteorclient.commands.commands.GamemodeCommand;
import meteordevelopment.meteorclient.commands.commands.GiveCommand;
import meteordevelopment.meteorclient.commands.commands.HClipCommand;
import meteordevelopment.meteorclient.commands.commands.HelpCommand;
import meteordevelopment.meteorclient.commands.commands.InputCommand;
import meteordevelopment.meteorclient.commands.commands.InventoryCommand;
import meteordevelopment.meteorclient.commands.commands.LocateCommand;
import meteordevelopment.meteorclient.commands.commands.MacroCommand;
import meteordevelopment.meteorclient.commands.commands.ModulesCommand;
import meteordevelopment.meteorclient.commands.commands.NameHistoryCommand;
import meteordevelopment.meteorclient.commands.commands.NbtCommand;
import meteordevelopment.meteorclient.commands.commands.NotebotCommand;
import meteordevelopment.meteorclient.commands.commands.PeekCommand;
import meteordevelopment.meteorclient.commands.commands.ProfilesCommand;
import meteordevelopment.meteorclient.commands.commands.ReloadCommand;
import meteordevelopment.meteorclient.commands.commands.ResetCommand;
import meteordevelopment.meteorclient.commands.commands.RotationCommand;
import meteordevelopment.meteorclient.commands.commands.SaveMapCommand;
import meteordevelopment.meteorclient.commands.commands.SayCommand;
import meteordevelopment.meteorclient.commands.commands.ServerCommand;
import meteordevelopment.meteorclient.commands.commands.SettingCommand;
import meteordevelopment.meteorclient.commands.commands.SpectateCommand;
import meteordevelopment.meteorclient.commands.commands.SwarmCommand;
import meteordevelopment.meteorclient.commands.commands.ToggleCommand;
import meteordevelopment.meteorclient.commands.commands.VClipCommand;
import meteordevelopment.meteorclient.commands.commands.WaspCommand;
import meteordevelopment.meteorclient.commands.commands.WaypointCommand;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2172;
import net.minecraft.class_634;
import net.minecraft.class_7157;

public class Commands {
   public static final List<Command> COMMANDS = new ArrayList();
   public static CommandDispatcher<class_2172> DISPATCHER = new CommandDispatcher();

   @PostInit(
      dependencies = {PathManagers.class}
   )
   public static void init() {
      add(new VClipCommand());
      add(new HClipCommand());
      add(new DismountCommand());
      add(new DisconnectCommand());
      add(new DamageCommand());
      add(new DropCommand());
      add(new EnchantCommand());
      add(new FakePlayerCommand());
      add(new FriendsCommand());
      add(new CommandsCommand());
      add(new InventoryCommand());
      add(new NbtCommand());
      add(new NotebotCommand());
      add(new PeekCommand());
      add(new EnderChestCommand());
      add(new ProfilesCommand());
      add(new ReloadCommand());
      add(new ResetCommand());
      add(new SayCommand());
      add(new ServerCommand());
      add(new SwarmCommand());
      add(new ToggleCommand());
      add(new SettingCommand());
      add(new SpectateCommand());
      add(new GamemodeCommand());
      add(new SaveMapCommand());
      add(new MacroCommand());
      add(new ModulesCommand());
      add(new BindsCommand());
      add(new GiveCommand());
      add(new NameHistoryCommand());
      add(new BindCommand());
      add(new FovCommand());
      add(new RotationCommand());
      add(new WaypointCommand());
      add(new InputCommand());
      add(new WaspCommand());
      add(new LocateCommand());
      add(new HelpCommand());
      COMMANDS.sort(Comparator.comparing(Command::getName));
      MeteorClient.EVENT_BUS.subscribe(Commands.class);
   }

   public static void add(Command command) {
      COMMANDS.removeIf((existing) -> existing.getName().equals(command.getName()));
      COMMANDS.add(command);
   }

   public static void dispatch(String message) throws CommandSyntaxException {
      DISPATCHER.execute(message, MeteorClient.mc.method_1562().method_2875());
   }

   public static Command get(String name) {
      for(Command command : COMMANDS) {
         if (command.getName().equals(name)) {
            return command;
         }
      }

      return null;
   }

   @EventHandler
   private static void onJoin(GameJoinedEvent event) {
      class_634 networkHandler = MeteorClient.mc.method_1562();
      Command.REGISTRY_ACCESS = class_7157.method_46722(networkHandler.method_29091(), networkHandler.method_45735());
      DISPATCHER = new CommandDispatcher();

      for(Command command : COMMANDS) {
         command.registerTo(DISPATCHER);
      }

   }
}
