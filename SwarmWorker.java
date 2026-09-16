package meteordevelopment.meteorclient.systems.modules.misc.swarm;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.class_2248;

public class SwarmWorker extends Thread {
   private Socket socket;
   public class_2248 target;

   public SwarmWorker(String ip, int port) {
      try {
         this.socket = new Socket(ip, port);
      } catch (Exception e) {
         this.socket = null;
         ChatUtils.warningPrefix("Swarm", "Server not found at %s on port %s.", ip, port);
         e.printStackTrace();
      }

      if (this.socket != null) {
         this.start();
      }

   }

   public void run() {
      ChatUtils.infoPrefix("Swarm", "Connected to Swarm host on at %s on port %s.", this.getIp(this.socket.getInetAddress().getHostAddress()), this.socket.getPort());

      try {
         DataInputStream in = new DataInputStream(this.socket.getInputStream());

         while(!this.isInterrupted()) {
            String read = in.readUTF();
            if (read.startsWith("swarm")) {
               ChatUtils.infoPrefix("Swarm", "Received command: (highlight)%s", read);

               try {
                  Commands.dispatch(read);
               } catch (Exception e) {
                  ChatUtils.error("Error fetching command.");
                  e.printStackTrace();
               }
            }
         }

         in.close();
      } catch (IOException e) {
         ChatUtils.errorPrefix("Swarm", "Error in connection to host.");
         e.printStackTrace();
         this.disconnect();
      }

   }

   public void disconnect() {
      try {
         this.socket.close();
      } catch (IOException e) {
         e.printStackTrace();
      }

      PathManagers.get().stop();
      ChatUtils.infoPrefix("Swarm", "Disconnected from host.");
      this.interrupt();
   }

   public void tick() {
      if (this.target != null) {
         PathManagers.get().stop();
         PathManagers.get().mine(this.target);
         this.target = null;
      }
   }

   public String getConnection() {
      String var10000 = this.getIp(this.socket.getInetAddress().getHostAddress());
      return var10000 + ":" + this.socket.getPort();
   }

   private String getIp(String ip) {
      return ip.equals("127.0.0.1") ? "localhost" : ip;
   }
}
