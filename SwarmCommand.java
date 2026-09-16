package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import java.util.List;
import java.util.Random;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.ModuleArgumentType;
import meteordevelopment.meteorclient.commands.arguments.PlayerArgumentType;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.swarm.Swarm;
import meteordevelopment.meteorclient.systems.modules.misc.swarm.SwarmConnection;
import meteordevelopment.meteorclient.systems.modules.misc.swarm.SwarmHost;
import meteordevelopment.meteorclient.systems.modules.misc.swarm.SwarmWorker;
import meteordevelopment.meteorclient.systems.modules.world.InfinityMiner;
import meteordevelopment.meteorclient.utils.misc.text.MeteorClickEvent;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.class_124;
import net.minecraft.class_1657;
import net.minecraft.class_2172;
import net.minecraft.class_2247;
import net.minecraft.class_2257;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import org.jetbrains.annotations.Nullable;

public class SwarmCommand extends Command {
   private static final SimpleCommandExceptionType SWARM_NOT_ACTIVE = new SimpleCommandExceptionType(class_2561.method_43470("The swarm module must be active to use this command."));
   private @Nullable ObjectIntPair<String> pendingConnection;

   public SwarmCommand() {
      super("swarm", "Sends commands to connected swarm workers.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(literal("disconnect").executes((context) -> {
         Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
         if (swarm.isActive()) {
            swarm.close();
            return 1;
         } else {
            throw SWARM_NOT_ACTIVE.create();
         }
      }));
      builder.then(((LiteralArgumentBuilder)literal("join").then(argument("ip", StringArgumentType.string()).then(argument("port", IntegerArgumentType.integer(0, 65535)).executes((context) -> {
         String ip = StringArgumentType.getString(context, "ip");
         int port = IntegerArgumentType.getInteger(context, "port");
         this.pendingConnection = new ObjectIntImmutablePair(ip, port);
         this.info("Are you sure you want to connect to '%s:%s'?", new Object[]{ip, port});
         this.info(class_2561.method_43470("Click here to confirm").method_10862(class_2583.field_24360.method_27705(new class_124[]{class_124.field_1073, class_124.field_1060}).method_10958(new MeteorClickEvent(".swarm join confirm"))));
         return 1;
      })))).then(literal("confirm").executes((ctx) -> {
         if (this.pendingConnection == null) {
            this.error("No pending swarm connections.", new Object[0]);
            return 1;
         } else {
            Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
            swarm.enable();
            swarm.close();
            swarm.mode.set(Swarm.Mode.Worker);
            swarm.worker = new SwarmWorker((String)this.pendingConnection.left(), this.pendingConnection.rightInt());
            this.pendingConnection = null;

            try {
               this.info("Connected to (highlight)%s.", new Object[]{swarm.worker.getConnection()});
            } catch (NullPointerException var4) {
               this.error("Error connecting to swarm host.", new Object[0]);
               swarm.close();
               swarm.toggle();
            }

            return 1;
         }
      })));
      builder.then(literal("connections").executes((context) -> {
         Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
         if (!swarm.isActive()) {
            throw SWARM_NOT_ACTIVE.create();
         } else {
            if (swarm.isHost()) {
               if (swarm.host.getConnectionCount() > 0) {
                  ChatUtils.info("--- Swarm Connections (highlight)(%s/%s)(default) ---", swarm.host.getConnectionCount(), swarm.host.getConnections().length);

                  for(int i = 0; i < swarm.host.getConnections().length; ++i) {
                     SwarmConnection connection = swarm.host.getConnections()[i];
                     if (connection != null) {
                        ChatUtils.info("(highlight)Worker %s(default): %s.", i, connection.getConnection());
                     }
                  }
               } else {
                  this.warning("No active connections", new Object[0]);
               }
            } else if (swarm.isWorker()) {
               this.info("Connected to (highlight)%s", new Object[]{swarm.worker.getConnection()});
            }

            return 1;
         }
      }));
      builder.then(((LiteralArgumentBuilder)literal("follow").executes((context) -> {
         Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
         if (swarm.isActive()) {
            if (swarm.isHost()) {
               SwarmHost var10000 = swarm.host;
               String var10001 = context.getInput();
               var10000.sendMessage(var10001 + " " + mc.field_1724.method_5477().getString());
            } else if (swarm.isWorker()) {
               this.error("The follow host command must be used by the host.", new Object[0]);
            }

            return 1;
         } else {
            throw SWARM_NOT_ACTIVE.create();
         }
      })).then(argument("player", PlayerArgumentType.create()).executes((context) -> {
         class_1657 playerEntity = PlayerArgumentType.get(context);
         Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
         if (swarm.isActive()) {
            if (swarm.isHost()) {
               swarm.host.sendMessage(context.getInput());
            } else if (swarm.isWorker() && playerEntity != null) {
               PathManagers.get().follow((entity) -> entity.method_5477().getString().equalsIgnoreCase(playerEntity.method_5477().getString()));
            }

            return 1;
         } else {
            throw SWARM_NOT_ACTIVE.create();
         }
      })));
      builder.then(literal("goto").then(argument("x", IntegerArgumentType.integer()).then(argument("z", IntegerArgumentType.integer()).executes((context) -> {
         Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
         if (swarm.isActive()) {
            if (swarm.isHost()) {
               swarm.host.sendMessage(context.getInput());
            } else if (swarm.isWorker()) {
               int x = IntegerArgumentType.getInteger(context, "x");
               int z = IntegerArgumentType.getInteger(context, "z");
               PathManagers.get().moveTo(new class_2338(x, 0, z), true);
            }

            return 1;
         } else {
            throw SWARM_NOT_ACTIVE.create();
         }
      }))));
      builder.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)literal("infinity-miner").executes((context) -> {
         Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
         if (swarm.isActive()) {
            if (swarm.isHost()) {
               swarm.host.sendMessage(context.getInput());
            } else if (swarm.isWorker()) {
               this.runInfinityMiner();
            }

            return 1;
         } else {
            throw SWARM_NOT_ACTIVE.create();
         }
      })).then(((RequiredArgumentBuilder)argument("target", class_2257.method_9653(REGISTRY_ACCESS)).executes((context) -> {
         Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
         if (swarm.isActive()) {
            if (swarm.isHost()) {
               swarm.host.sendMessage(context.getInput());
            } else if (swarm.isWorker()) {
               ((InfinityMiner)Modules.get().get(InfinityMiner.class)).targetBlocks.set(List.of(((class_2247)context.getArgument("target", class_2247.class)).method_9494().method_26204()));
               this.runInfinityMiner();
            }

            return 1;
         } else {
            throw SWARM_NOT_ACTIVE.create();
         }
      })).then(argument("repair", class_2257.method_9653(REGISTRY_ACCESS)).executes((context) -> {
         Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
         if (swarm.isActive()) {
            if (swarm.isHost()) {
               swarm.host.sendMessage(context.getInput());
            } else if (swarm.isWorker()) {
               ((InfinityMiner)Modules.get().get(InfinityMiner.class)).targetBlocks.set(List.of(((class_2247)context.getArgument("target", class_2247.class)).method_9494().method_26204()));
               ((InfinityMiner)Modules.get().get(InfinityMiner.class)).repairBlocks.set(List.of(((class_2247)context.getArgument("repair", class_2247.class)).method_9494().method_26204()));
               this.runInfinityMiner();
            }

            return 1;
         } else {
            throw SWARM_NOT_ACTIVE.create();
         }
      })))).then(literal("logout").then(argument("logout", BoolArgumentType.bool()).executes((context) -> {
         Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
         if (swarm.isActive()) {
            if (swarm.isHost()) {
               swarm.host.sendMessage(context.getInput());
            } else if (swarm.isWorker()) {
               ((InfinityMiner)Modules.get().get(InfinityMiner.class)).logOut.set(BoolArgumentType.getBool(context, "logout"));
            }

            return 1;
         } else {
            throw SWARM_NOT_ACTIVE.create();
         }
      })))).then(literal("walkhome").then(argument("walkhome", BoolArgumentType.bool()).executes((context) -> {
         Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
         if (swarm.isActive()) {
            if (swarm.isHost()) {
               swarm.host.sendMessage(context.getInput());
            } else if (swarm.isWorker()) {
               ((InfinityMiner)Modules.get().get(InfinityMiner.class)).walkHome.set(BoolArgumentType.getBool(context, "walkhome"));
            }

            return 1;
         } else {
            throw SWARM_NOT_ACTIVE.create();
         }
      }))));
      builder.then(literal("mine").then(argument("block", class_2257.method_9653(REGISTRY_ACCESS)).executes((context) -> {
         Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
         if (swarm.isActive()) {
            if (swarm.isHost()) {
               swarm.host.sendMessage(context.getInput());
            } else if (swarm.isWorker()) {
               swarm.worker.target = ((class_2247)context.getArgument("block", class_2247.class)).method_9494().method_26204();
            }

            return 1;
         } else {
            throw SWARM_NOT_ACTIVE.create();
         }
      })));
      builder.then(literal("toggle").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)argument("module", ModuleArgumentType.create()).executes((context) -> {
         Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
         if (swarm.isActive()) {
            if (swarm.isHost()) {
               swarm.host.sendMessage(context.getInput());
            } else if (swarm.isWorker()) {
               Module module = ModuleArgumentType.get(context);
               module.toggle();
            }

            return 1;
         } else {
            throw SWARM_NOT_ACTIVE.create();
         }
      })).then(literal("on").executes((context) -> {
         Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
         if (swarm.isActive()) {
            if (swarm.isHost()) {
               swarm.host.sendMessage(context.getInput());
            } else if (swarm.isWorker()) {
               Module m = ModuleArgumentType.get(context);
               m.enable();
            }

            return 1;
         } else {
            throw SWARM_NOT_ACTIVE.create();
         }
      }))).then(literal("off").executes((context) -> {
         Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
         if (swarm.isActive()) {
            if (swarm.isHost()) {
               swarm.host.sendMessage(context.getInput());
            } else if (swarm.isWorker()) {
               Module m = ModuleArgumentType.get(context);
               m.disable();
            }

            return 1;
         } else {
            throw SWARM_NOT_ACTIVE.create();
         }
      }))));
      builder.then(((LiteralArgumentBuilder)literal("scatter").executes((context) -> {
         Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
         if (swarm.isActive()) {
            if (swarm.isHost()) {
               swarm.host.sendMessage(context.getInput());
            } else if (swarm.isWorker()) {
               this.scatter(100);
            }

            return 1;
         } else {
            throw SWARM_NOT_ACTIVE.create();
         }
      })).then(argument("radius", IntegerArgumentType.integer()).executes((context) -> {
         Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
         if (swarm.isActive()) {
            if (swarm.isHost()) {
               swarm.host.sendMessage(context.getInput());
            } else if (swarm.isWorker()) {
               this.scatter(IntegerArgumentType.getInteger(context, "radius"));
            }

            return 1;
         } else {
            throw SWARM_NOT_ACTIVE.create();
         }
      })));
      builder.then(literal("stop").executes((context) -> {
         Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
         if (swarm.isActive()) {
            if (swarm.isHost()) {
               swarm.host.sendMessage(context.getInput());
            } else if (swarm.isWorker()) {
               PathManagers.get().stop();
            }

            return 1;
         } else {
            throw SWARM_NOT_ACTIVE.create();
         }
      }));
      builder.then(literal("exec").then(argument("command", StringArgumentType.greedyString()).executes((context) -> {
         Swarm swarm = (Swarm)Modules.get().get(Swarm.class);
         if (swarm.isActive()) {
            if (swarm.isHost()) {
               swarm.host.sendMessage(context.getInput());
            } else if (swarm.isWorker()) {
               ChatUtils.sendPlayerMsg(StringArgumentType.getString(context, "command"));
            }

            return 1;
         } else {
            throw SWARM_NOT_ACTIVE.create();
         }
      })));
   }

   private void runInfinityMiner() {
      InfinityMiner infinityMiner = (InfinityMiner)Modules.get().get(InfinityMiner.class);
      infinityMiner.disable();
      infinityMiner.enable();
   }

   private void scatter(int radius) {
      Random random = new Random();
      double a = random.nextDouble() * (double)2.0F * Math.PI;
      double r = (double)radius * Math.sqrt(random.nextDouble());
      double x = mc.field_1724.method_23317() + r * Math.cos(a);
      double z = mc.field_1724.method_23321() + r * Math.sin(a);
      PathManagers.get().stop();
      PathManagers.get().moveTo(new class_2338((int)x, 0, (int)z), true);
   }
}
