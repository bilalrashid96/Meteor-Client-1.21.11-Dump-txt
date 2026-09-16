package meteordevelopment.meteorclient.gui.screens;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Matcher;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.proxies.Proxies;
import meteordevelopment.meteorclient.systems.proxies.Proxy;
import meteordevelopment.meteorclient.systems.proxies.ProxyType;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_437;

public class ProxiesImportScreen extends WindowScreen {
   private final File file;

   public ProxiesImportScreen(GuiTheme theme, File file) {
      super(theme, "Import Proxies");
      this.file = file;
      this.onClosed(() -> {
         class_437 patt0$temp = this.parent;
         if (patt0$temp instanceof ProxiesScreen screen) {
            screen.reload();
         }

      });
   }

   public void initWidgets() {
      if (this.file.exists() && this.file.isFile()) {
         this.add(this.theme.label("Importing proxies from " + this.file.getName() + "...").color(Color.GREEN));
         WVerticalList list = (WVerticalList)((WSection)this.add(this.theme.section("Log", false)).widget()).add(this.theme.verticalList()).expandX().widget();
         Proxies proxies = Proxies.get();

         try {
            int success = 0;
            int fail = 0;

            for(String line : Files.readAllLines(this.file.toPath())) {
               Proxy proxy = null;
               Matcher matcher = Proxies.PROXY_PATTERN.matcher(line);
               if (matcher.matches()) {
                  String address = matcher.group(2).replaceAll("\\b0+\\B", "");
                  int port = Integer.parseInt(matcher.group(3));
                  proxy = (new Proxy.Builder()).address(address).port(port).name(matcher.group(1) != null ? matcher.group(1) : address + ":" + port).type(matcher.group(4) != null ? ProxyType.parse(matcher.group(4)) : ProxyType.Socks4).build();
               }

               matcher = Proxies.PROXY_PATTERN_WEBSHARE.matcher(line);
               if (proxy == null && matcher.matches()) {
                  String address = matcher.group(1).replaceAll("\\b0+\\B", "");
                  int port = Integer.parseInt(matcher.group(2));
                  proxy = (new Proxy.Builder()).address(address).port(port).name(address + ":" + port).username(matcher.group(3) != null ? matcher.group(3) : "").password(matcher.group(4) != null ? matcher.group(4) : "").type(ProxyType.Socks5).build();
               }

               matcher = Proxies.PROXY_PATTERN_URI.matcher(line);
               if (proxy == null && matcher.matches()) {
                  String address = matcher.group("addr").replaceAll("\\b0+\\B", "");
                  int port = Integer.parseInt(matcher.group("port"));
                  ProxyType type = ProxyType.parse(matcher.group(1));
                  if (type == null) {
                     if (matcher.group(1) != null && matcher.group(1).equals("socks")) {
                        type = ProxyType.Socks5;
                     } else if (matcher.group("pass") != null) {
                        type = ProxyType.Socks5;
                     } else {
                        type = ProxyType.Socks4;
                     }
                  }

                  proxy = (new Proxy.Builder()).address(address).port(port).name(address + ":" + port).username(matcher.group("user") != null ? matcher.group("user") : "").password(matcher.group("pass") != null ? matcher.group("pass") : "").type(type).build();
               }

               if (proxy == null) {
                  list.add(this.theme.label("Unrecognised proxy format: " + line).color(Color.RED));
                  ++fail;
               } else if (proxies.add(proxy)) {
                  list.add(this.theme.label("Imported proxy: " + (String)proxy.name.get()).color(Color.GREEN));
                  ++success;
               } else {
                  list.add(this.theme.label("Proxy already exists: " + (String)proxy.name.get()).color(Color.ORANGE));
                  ++fail;
               }
            }

            this.add(this.theme.label("Successfully imported " + success + "/" + (fail + success) + " proxies.").color(Utils.lerp(Color.RED, Color.GREEN, (float)success / (float)(success + fail))));
         } catch (IOException e) {
            MeteorClient.LOG.error("An error occurred while importing the proxy file", e);
         }
      } else {
         this.add(this.theme.label("Invalid File!"));
      }

      this.add(this.theme.horizontalSeparator()).expandX();
      WButton refresh = (WButton)this.add(this.theme.button("Check proxies")).expandX().widget();
      refresh.action = () -> {
         Proxies.get().checkProxies(false);
         this.method_25419();
      };
      WButton btnBack = (WButton)this.add(this.theme.button("Back")).expandX().widget();
      btnBack.action = this::method_25419;
   }
}
