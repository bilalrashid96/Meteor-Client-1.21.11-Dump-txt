package meteordevelopment.meteorclient.systems.modules.misc.swarm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.player.ChatUtils;

public class SwarmHost extends Thread {
   private ServerSocket socket;
   private final SwarmConnection[] clientConnections = new SwarmConnection[50];

   public SwarmHost(int port) {
      try {
         this.socket = new ServerSocket(port);
      } catch (IOException e) {
         this.socket = null;
         ChatUtils.errorPrefix("Swarm", "Couldn't start a server on port %s.", port);
         e.printStackTrace();
      }

      if (this.socket != null) {
         this.start();
      }

   }

   public void run() {
      ChatUtils.infoPrefix("Swarm", "Listening for incoming connections on port %s.", this.socket.getLocalPort());

      while(!this.isInterrupted()) {
         try {
            Socket connection = this.socket.accept();
            this.assignConnectionToSubServer(connection);
         } catch (IOException e) {
            ChatUtils.errorPrefix("Swarm", "Error making a connection to worker.");
            e.printStackTrace();
         }
      }

   }

   public void assignConnectionToSubServer(Socket connection) {
      for(int i = 0; i < this.clientConnections.length; ++i) {
         if (this.clientConnections[i] == null) {
            this.clientConnections[i] = new SwarmConnection(connection);
            break;
         }
      }

   }

   public void disconnect() {
      for(SwarmConnection connection : this.clientConnections) {
         if (connection != null) {
            connection.disconnect();
         }
      }

      try {
         this.socket.close();
      } catch (IOException e) {
         e.printStackTrace();
      }

      ChatUtils.infoPrefix("Swarm", "Server closed on port %s.", this.socket.getLocalPort());
      this.interrupt();
   }

   public void sendMessage(String s) {
      MeteorExecutor.execute(() -> {
         for(SwarmConnection connection : this.clientConnections) {
            if (connection != null) {
               connection.messageToSend = s;
            }
         }

      });
   }

   public SwarmConnection[] getConnections() {
      return this.clientConnections;
   }

   public int getConnectionCount() {
      int count = 0;

      for(SwarmConnection clientConnection : this.clientConnections) {
         if (clientConnection != null) {
            ++count;
         }
      }

      return count;
   }
}
