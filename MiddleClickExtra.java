package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.events.entity.player.FinishUsingItemEvent;
import meteordevelopment.meteorclient.events.entity.player.StoppedUsingItemEvent;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1753;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_1934;
import net.minecraft.class_2868;

public class MiddleClickExtra extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Mode> mode;
   private final Setting<Boolean> message;
   private final Setting<String> friendMessage;
   private final Setting<Boolean> quickSwap;
   private final Setting<Boolean> swapBack;
   private final Setting<Boolean> disableInCreative;
   private final Setting<Boolean> notify;
   private boolean isUsing;
   private boolean wasHeld;
   private int itemSlot;
   private int selectedSlot;

   public MiddleClickExtra() {
      super(Categories.Player, "middle-click-extra", "Perform various actions when you middle click.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Which item to use when you middle click.")).defaultValue(MiddleClickExtra.Mode.Pearl)).build());
      this.message = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("send-message")).description("Sends a message when you add a player as a friend.")).defaultValue(false)).visible(() -> this.mode.get() == MiddleClickExtra.Mode.AddFriend)).build());
      this.friendMessage = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("message-to-send")).description("Message to send when you add a player as a friend (use %player for the player's name)")).defaultValue("/msg %player I just friended you on Meteor.")).visible(() -> this.mode.get() == MiddleClickExtra.Mode.AddFriend)).build());
      this.quickSwap = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("quick-swap")).description("Allows you to use items in your inventory by simulating hotbar key presses. May get flagged by anticheats.")).defaultValue(false)).visible(() -> this.mode.get() != MiddleClickExtra.Mode.AddFriend)).build());
      this.swapBack = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swap-back")).description("Swap back to your original slot when you finish using an item.")).defaultValue(false)).visible(() -> this.mode.get() != MiddleClickExtra.Mode.AddFriend && !(Boolean)this.quickSwap.get())).build());
      this.disableInCreative = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-in-creative")).description("Middle click action is disabled in Creative mode.")).defaultValue(true)).build());
      this.notify = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("notify")).description("Notifies you when you do not have the specified item in your hotbar.")).defaultValue(true)).visible(() -> this.mode.get() != MiddleClickExtra.Mode.AddFriend)).build());
   }

   public void onDeactivate() {
      this.stopIfUsing(false);
   }

   @EventHandler
   private void onMouseClick(MouseClickEvent event) {
      if (event.action == KeyAction.Press && event.button() == 2 && this.mc.field_1755 == null) {
         if (!this.disabledByCreative()) {
            if (this.mode.get() == MiddleClickExtra.Mode.AddFriend) {
               if (this.mc.field_1692 != null) {
                  class_1297 var3 = this.mc.field_1692;
                  if (var3 instanceof class_1657) {
                     class_1657 player = (class_1657)var3;
                     if (!Friends.get().isFriend(player)) {
                        Friends.get().add(new Friend(player));
                        this.info("Added %s to friends", new Object[]{player.method_5477().getString()});
                        if ((Boolean)this.message.get()) {
                           String messageNotify = ((String)this.friendMessage.get()).replace("%player", player.method_5477().getString());
                           ChatUtils.sendPlayerMsg(messageNotify);
                        }
                     } else {
                        Friends.get().remove(Friends.get().get(player));
                        this.info("Removed %s from friends", new Object[]{player.method_5477().getString()});
                     }

                  }
               }
            } else {
               FindItemResult result = InvUtils.find((this.mode.get()).item);
               if (!result.found() || !result.isHotbar() && !(Boolean)this.quickSwap.get()) {
                  if ((Boolean)this.notify.get()) {
                     this.warning("Unable to find specified item.", new Object[0]);
                  }

               } else {
                  this.selectedSlot = this.mc.field_1724.method_31548().method_67532();
                  this.itemSlot = result.slot();
                  this.wasHeld = result.isMainHand();
                  if (!this.wasHeld) {
                     if (!(Boolean)this.quickSwap.get()) {
                        InvUtils.swap(result.slot(), (Boolean)this.swapBack.get());
                     } else {
                        InvUtils.quickSwap().fromId(this.selectedSlot).to(this.itemSlot);
                     }
                  }

                  if ((this.mode.get()).immediate) {
                     this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
                     this.swapBack(false);
                  } else {
                     this.mc.field_1690.field_1904.method_23481(true);
                     this.isUsing = true;
                  }

               }
            }
         }
      }
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.isUsing) {
         boolean pressed = true;
         if (this.mc.field_1724.method_6047().method_7909() instanceof class_1753) {
            pressed = class_1753.method_7722(this.mc.field_1724.method_6048()) < 1.0F;
         }

         this.mc.field_1690.field_1904.method_23481(pressed);
      }
   }

   @EventHandler
   private void onPacketSendEvent(PacketEvent.Send event) {
      if (event.packet instanceof class_2868) {
         this.stopIfUsing(true);
      }

   }

   @EventHandler
   private void onStoppedUsingItem(StoppedUsingItemEvent event) {
      this.stopIfUsing(false);
   }

   @EventHandler
   private void onFinishUsingItem(FinishUsingItemEvent event) {
      this.stopIfUsing(false);
   }

   private void stopIfUsing(boolean wasCancelled) {
      if (this.isUsing) {
         this.swapBack(wasCancelled);
         this.mc.field_1690.field_1904.method_23481(false);
         this.isUsing = false;
      }

   }

   void swapBack(boolean wasCancelled) {
      if (!this.wasHeld) {
         if ((Boolean)this.quickSwap.get()) {
            InvUtils.quickSwap().fromId(this.selectedSlot).to(this.itemSlot);
         } else {
            if (!(Boolean)this.swapBack.get() || wasCancelled) {
               return;
            }

            InvUtils.swapBack();
         }

      }
   }

   private boolean disabledByCreative() {
      if (this.mc.field_1724 == null) {
         return false;
      } else {
         return (Boolean)this.disableInCreative.get() && this.mc.field_1724.method_68876() == class_1934.field_9220;
      }
   }

   public static enum Mode {
      Pearl(class_1802.field_8634, true),
      XP(class_1802.field_8287, true),
      Rocket(class_1802.field_8639, true),
      WindCharge(class_1802.field_49098, true),
      Bow(class_1802.field_8102, false),
      Gap(class_1802.field_8463, false),
      EGap(class_1802.field_8367, false),
      Chorus(class_1802.field_8233, false),
      AddFriend((class_1792)null, true);

      private final class_1792 item;
      private final boolean immediate;

      private Mode(class_1792 item, boolean immediate) {
         this.item = item;
         this.immediate = immediate;
      }

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Pearl, XP, Rocket, WindCharge, Bow, Gap, EGap, Chorus, AddFriend};
      }
   }
}
