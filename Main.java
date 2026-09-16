package meteordevelopment.meteorclient;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
   public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      int option = JOptionPane.showOptionDialog((Component)null, "To install Meteor Client you need to put it in your mods folder and run Fabric for latest Minecraft version.", "Meteor Client", 0, 0, (Icon)null, new String[]{"Open Wiki", "Open Mods Folder"}, (Object)null);
      switch (option) {
         case 0:
            getOS().open("https://meteorclient.com/faq/installation");
            break;
         case 1:
            File mods = new File(getModsFolder());
            if (!mods.exists()) {
               mods.mkdirs();
            }

            getOS().open(mods);
      }

   }

   private static String getModsFolder() {
      String userHome = System.getProperty("user.home");
      switch (getOS().ordinal()) {
         case 1:
            return System.getenv("AppData") + "/.minecraft/mods";
         case 2:
            return userHome + "/Library/Application Support/minecraft/mods";
         default:
            return userHome + "/.minecraft/mods";
      }
   }

   private static OperatingSystem getOS() {
      String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
      if (!os.contains("linux") && !os.contains("unix")) {
         if (os.contains("mac")) {
            return Main.OperatingSystem.OSX;
         } else {
            return os.contains("win") ? Main.OperatingSystem.WINDOWS : Main.OperatingSystem.UNKNOWN;
         }
      } else {
         return Main.OperatingSystem.LINUX;
      }
   }

   private static enum OperatingSystem {
      LINUX,
      WINDOWS {
         protected String[] getURLOpenCommand(URL url) {
            return new String[]{"rundll32", "url.dll,FileProtocolHandler", url.toString()};
         }
      },
      OSX {
         protected String[] getURLOpenCommand(URL url) {
            return new String[]{"open", url.toString()};
         }
      },
      UNKNOWN;

      private OperatingSystem() {
      }

      public void open(URL url) {
         try {
            Runtime.getRuntime().exec(this.getURLOpenCommand(url));
         } catch (IOException e) {
            e.printStackTrace();
         }

      }

      public void open(String url) {
         try {
            this.open((new URI(url)).toURL());
         } catch (MalformedURLException | URISyntaxException e) {
            ((Exception)e).printStackTrace();
         }

      }

      public void open(File file) {
         try {
            this.open(file.toURI().toURL());
         } catch (MalformedURLException e) {
            e.printStackTrace();
         }

      }

      protected String[] getURLOpenCommand(URL url) {
         String string = url.toString();
         if ("file".equals(url.getProtocol())) {
            string = string.replace("file:", "file://");
         }

         return new String[]{"xdg-open", string};
      }

      // $FF: synthetic method
      private static OperatingSystem[] $values() {
         return new OperatingSystem[]{LINUX, WINDOWS, OSX, UNKNOWN};
      }
   }
}
