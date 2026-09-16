package meteordevelopment.meteorclient.systems.modules.misc;

import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;

public class MessageAura extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<String> message;
   private final Setting<Boolean> ignoreFriends;

   public MessageAura() {
      super(Categories.Misc, "message-aura", "Sends a specified message to any player that enters render distance.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.message = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("message")).description("The specified message sent to the player.")).defaultValue("Meteor on Crack!")).build());
      this.ignoreFriends = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-friends")).description("Will not send any messages to people friended.")).defaultValue(false)).build());
   }

   @EventHandler
   private void onEntityAdded(EntityAddedEvent event) {
      if (event.entity instanceof class_1657 && !event.entity.method_5667().equals(this.mc.field_1724.method_5667())) {
         if (!(Boolean)this.ignoreFriends.get() || (Boolean)this.ignoreFriends.get() && !Friends.get().isFriend((class_1657)event.entity)) {
            String var10000 = event.entity.method_5477().getString();
            ChatUtils.sendPlayerMsg("/msg " + var10000 + " " + (String)this.message.get());
         }

      }
   }
}
