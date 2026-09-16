package meteordevelopment.meteorclient.systems.modules.misc;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.text.RunnableClickEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_2558;
import net.minecraft.class_2561;
import net.minecraft.class_2568;
import net.minecraft.class_2596;
import net.minecraft.class_2720;
import net.minecraft.class_2817;
import net.minecraft.class_2856;
import net.minecraft.class_2960;
import net.minecraft.class_5250;
import net.minecraft.class_8709;
import net.minecraft.class_2856.class_2857;
import org.apache.commons.lang3.Strings;

public class ServerSpoof extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> spoofBrand;
   private final Setting<String> brand;
   private final Setting<Boolean> resourcePack;
   private final Setting<Boolean> blockChannels;
   private final Setting<List<String>> channels;
   private class_5250 msg;
   public boolean silentAcceptResourcePack;

   public ServerSpoof() {
      super(Categories.Misc, "server-spoof", "Spoof client brand, resource pack and channels.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.spoofBrand = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("spoof-brand")).description("Whether or not to spoof the brand.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      StringSetting.Builder var10002 = (StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("brand")).description("Specify the brand that will be send to the server.")).defaultValue("vanilla");
      Setting var10003 = this.spoofBrand;
      Objects.requireNonNull(var10003);
      this.brand = var10001.add(((StringSetting.Builder)var10002.visible(var10003::get)).build());
      this.resourcePack = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("resource-pack")).description("Spoof accepting server resource pack.")).defaultValue(false)).build());
      this.blockChannels = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("block-channels")).description("Whether or not to block some channels.")).defaultValue(true)).build());
      var10001 = this.sgGeneral;
      StringListSetting.Builder var2 = ((StringListSetting.Builder)((StringListSetting.Builder)(new StringListSetting.Builder()).name("channels")).description("If the channel contains the keyword, this outgoing channel will be blocked.")).defaultValue("fabric", "minecraft:register");
      var10003 = this.blockChannels;
      Objects.requireNonNull(var10003);
      this.channels = var10001.add(((StringListSetting.Builder)var2.visible(var10003::get)).build());
      this.silentAcceptResourcePack = false;
      this.runInMainMenu = true;
   }

   @EventHandler
   private void onPacketSend(PacketEvent.Send event) {
      if (this.isActive()) {
         if (event.packet instanceof class_2817) {
            class_2960 id = ((class_2817)event.packet).comp_1647().method_56479().comp_2242();
            if ((Boolean)this.blockChannels.get()) {
               for(String channel : this.channels.get()) {
                  if (Strings.CI.contains(id.toString(), channel)) {
                     event.cancel();
                     return;
                  }
               }
            }

            if ((Boolean)this.spoofBrand.get() && id.equals(class_8709.field_48655.comp_2242())) {
               class_2817 spoofedPacket = new class_2817(new class_8709(this.brand.get()));
               event.sendSilently(spoofedPacket);
               event.cancel();
            }
         }

         if (this.silentAcceptResourcePack && event.packet instanceof class_2856) {
            event.cancel();
         }

      }
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      if (this.isActive() && (Boolean)this.resourcePack.get()) {
         class_2596 var3 = event.packet;
         if (var3 instanceof class_2720) {
            class_2720 packet = (class_2720)var3;
            event.cancel();
            event.connection.method_10743(new class_2856(packet.comp_2158(), class_2857.field_13016));
            event.connection.method_10743(new class_2856(packet.comp_2158(), class_2857.field_47704));
            event.connection.method_10743(new class_2856(packet.comp_2158(), class_2857.field_13017));
            this.msg = class_2561.method_43470("This server has ");
            this.msg.method_27693(packet.comp_2161() ? "a required " : "an optional ").method_27693("resource pack. ");
            class_5250 link = class_2561.method_43470("[Open URL]");
            link.method_10862(link.method_10866().method_10977(class_124.field_1078).method_30938(true).method_10958(new class_2558.class_10608(URI.create(packet.comp_2159()))).method_10949(new class_2568.class_10613(class_2561.method_43470("Click to open the pack url"))));
            class_5250 acceptance = class_2561.method_43470("[Accept Pack]");
            acceptance.method_10862(acceptance.method_10866().method_10977(class_124.field_1077).method_30938(true).method_10958(new RunnableClickEvent(() -> {
               URL url = getParsedResourcePackUrl(packet.comp_2159());
               if (url == null) {
                  this.error("Invalid resource pack URL: " + packet.comp_2159(), new Object[0]);
               } else {
                  this.silentAcceptResourcePack = true;
                  this.mc.method_1516().method_55523(packet.comp_2158(), url, packet.comp_2160());
               }

            })).method_10949(new class_2568.class_10613(class_2561.method_43470("Click to accept and apply the pack."))));
            this.msg.method_10852(link).method_27693(" ");
            this.msg.method_10852(acceptance).method_27693(".");
         }
      }
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.isActive() && Utils.canUpdate() && this.msg != null) {
         this.info(this.msg);
         this.msg = null;
      }
   }

   private static URL getParsedResourcePackUrl(String url) {
      try {
         URL uRL = (new URI(url)).toURL();
         String string = uRL.getProtocol();
         return !"http".equals(string) && !"https".equals(string) ? null : uRL;
      } catch (URISyntaxException | MalformedURLException var3) {
         return null;
      }
   }
}
