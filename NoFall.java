package meteordevelopment.meteorclient.systems.modules.movement;

import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.mixininterface.IPlayerMoveC2SPacket;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_12206;
import net.minecraft.class_1268;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2828;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_9362;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class NoFall extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Mode> mode;
   private final Setting<PlacedItem> placedItem;
   private final Setting<PlaceMode> airPlaceMode;
   private final Setting<Boolean> anchor;
   private final Setting<Boolean> antiBounce;
   private final Setting<Boolean> pauseOnMace;
   private boolean placedWater;
   private class_2338 targetPos;
   private int timer;
   private boolean prePathManagerNoFall;

   public NoFall() {
      super(Categories.Movement, "no-fall", "Attempts to prevent you from taking fall damage.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("The way you are saved from fall damage.")).defaultValue(NoFall.Mode.Packet)).build());
      this.placedItem = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("placed-item")).description("Which block to place.")).defaultValue(NoFall.PlacedItem.Bucket)).visible(() -> this.mode.get() == NoFall.Mode.Place)).build());
      this.airPlaceMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("air-place-mode")).description("Whether place mode places before you die or before you take damage.")).defaultValue(NoFall.PlaceMode.BeforeDeath)).visible(() -> this.mode.get() == NoFall.Mode.AirPlace)).build());
      this.anchor = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anchor")).description("Centers the player and reduces movement when using bucket or air place mode.")).defaultValue(true)).visible(() -> this.mode.get() != NoFall.Mode.Packet)).build());
      this.antiBounce = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-bounce")).description("Disables bouncing on slime-block and bed upon landing.")).defaultValue(true)).build());
      this.pauseOnMace = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-mace")).description("Pauses NoFall when using a mace.")).defaultValue(true)).build());
   }

   public void onActivate() {
      this.prePathManagerNoFall = (Boolean)PathManagers.get().getSettings().getNoFall().get();
      if (this.mode.get() == NoFall.Mode.Packet) {
         PathManagers.get().getSettings().getNoFall().set(true);
      }

      this.placedWater = false;
   }

   public void onDeactivate() {
      PathManagers.get().getSettings().getNoFall().set(this.prePathManagerNoFall);
   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      if (this.mc.field_1724 != null) {
         if (!(Boolean)this.pauseOnMace.get() || !(this.mc.field_1724.method_6047().method_7909() instanceof class_9362)) {
            if (!this.mc.field_1724.method_31549().field_7477 && event.packet instanceof class_2828 && this.mode.get() == NoFall.Mode.Packet && ((IPlayerMoveC2SPacket)event.packet).meteor$getTag() != 1337) {
               if (!Modules.get().isActive(Flight.class)) {
                  if (this.mc.field_1724.method_6128()) {
                     return;
                  }

                  if (this.mc.field_1724.method_18798().field_1351 > (double)-0.5F) {
                     return;
                  }

                  ((PlayerMoveC2SPacketAccessor)event.packet).meteor$setOnGround(true);
               } else {
                  ((PlayerMoveC2SPacketAccessor)event.packet).meteor$setOnGround(true);
               }

            }
         }
      }
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (Utils.canUpdate()) {
         if (this.timer > 20) {
            this.placedWater = false;
            this.timer = 0;
         }

         if (!this.mc.field_1724.method_31549().field_7477) {
            if (!(Boolean)this.pauseOnMace.get() || !(this.mc.field_1724.method_6047().method_7909() instanceof class_9362)) {
               if (this.mode.get() == NoFall.Mode.AirPlace) {
                  if (!((PlaceMode)this.airPlaceMode.get()).test((float)this.mc.field_1724.field_6017)) {
                     return;
                  }

                  if ((Boolean)this.anchor.get()) {
                     PlayerUtils.centerPlayer();
                  }

                  Rotations.rotate((double)this.mc.field_1724.method_36454(), (double)90.0F, Integer.MAX_VALUE, () -> {
                     double preY = this.mc.field_1724.method_18798().field_1351;
                     ((IVec3d)this.mc.field_1724.method_18798()).meteor$setY((double)0.0F);
                     BlockUtils.place(this.mc.field_1724.method_24515().method_10074(), InvUtils.findInHotbar((Predicate)((itemStack) -> itemStack.method_7909() instanceof class_1747)), false, 0, true);
                     ((IVec3d)this.mc.field_1724.method_18798()).meteor$setY(preY);
                  });
               } else if (this.mode.get() == NoFall.Mode.Place) {
                  PlacedItem placedItem1 = (Boolean)this.mc.field_1687.method_75728().method_75694(class_12206.field_63755) && this.placedItem.get() == NoFall.PlacedItem.Bucket ? NoFall.PlacedItem.PowderSnow : (PlacedItem)this.placedItem.get();
                  if (this.mc.field_1724.field_6017 > (double)3.0F && !EntityUtils.isAboveWater(this.mc.field_1724)) {
                     class_1792 item = placedItem1.item;
                     FindItemResult findItemResult = InvUtils.findInHotbar(item);
                     if (!findItemResult.found()) {
                        return;
                     }

                     if ((Boolean)this.anchor.get()) {
                        PlayerUtils.centerPlayer();
                     }

                     class_3965 result = this.mc.field_1687.method_17742(new class_3959(this.mc.field_1724.method_73189(), this.mc.field_1724.method_73189().method_1023((double)0.0F, (double)5.0F, (double)0.0F), class_3960.field_17559, class_242.field_1348, this.mc.field_1724));
                     if (result != null && result.method_17783() == class_240.field_1332) {
                        this.targetPos = result.method_17777().method_10084();
                        if (placedItem1 == NoFall.PlacedItem.Bucket) {
                           this.useItem(findItemResult, true, this.targetPos, true);
                        } else {
                           this.useItem(findItemResult, placedItem1 == NoFall.PlacedItem.PowderSnow, this.targetPos, false);
                        }
                     }
                  }

                  if (this.placedWater) {
                     ++this.timer;
                     if (this.mc.field_1724.method_55667().method_26204() == placedItem1.block) {
                        this.useItem(InvUtils.findInHotbar(class_1802.field_8550), false, this.targetPos, true);
                     } else if (this.mc.field_1687.method_8320(this.mc.field_1724.method_24515().method_10074()).method_26204() == class_2246.field_27879 && this.mc.field_1724.field_6017 == (double)0.0F && placedItem1.block == class_2246.field_27879) {
                        this.useItem(InvUtils.findInHotbar(class_1802.field_8550), false, this.targetPos.method_10074(), true);
                     }
                  }
               }

            }
         }
      }
   }

   public boolean cancelBounce() {
      return this.isActive() && (Boolean)this.antiBounce.get();
   }

   private void useItem(FindItemResult item, boolean placedWater, class_2338 blockPos, boolean interactItem) {
      if (item.found()) {
         if (interactItem) {
            Rotations.rotate(Rotations.getYaw(blockPos), Rotations.getPitch(blockPos), 10, true, () -> {
               if (item.isOffhand()) {
                  this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5810);
               } else {
                  InvUtils.swap(item.slot(), true);
                  this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
                  InvUtils.swapBack();
               }

            });
         } else {
            BlockUtils.place(blockPos, item, true, 10, true);
         }

         this.placedWater = placedWater;
      }
   }

   public String getInfoString() {
      return ((Mode)this.mode.get()).toString();
   }

   public static enum Mode {
      Packet,
      AirPlace,
      Place;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Packet, AirPlace, Place};
      }
   }

   public static enum PlacedItem {
      Bucket(class_1802.field_8705, class_2246.field_10382),
      PowderSnow(class_1802.field_27876, class_2246.field_27879),
      HayBale(class_1802.field_17528, class_2246.field_10359),
      Cobweb(class_1802.field_8786, class_2246.field_10343),
      SlimeBlock(class_1802.field_8828, class_2246.field_10030);

      private final class_1792 item;
      private final class_2248 block;

      private PlacedItem(class_1792 item, class_2248 block) {
         this.item = item;
         this.block = block;
      }

      // $FF: synthetic method
      private static PlacedItem[] $values() {
         return new PlacedItem[]{Bucket, PowderSnow, HayBale, Cobweb, SlimeBlock};
      }
   }

   public static enum PlaceMode {
      BeforeDamage((height) -> height > 2.0F),
      BeforeDeath((height) -> height > Math.max(PlayerUtils.getTotalHealth(), 2.0F));

      private final Predicate<Float> fallHeight;

      private PlaceMode(Predicate<Float> fallHeight) {
         this.fallHeight = fallHeight;
      }

      public boolean test(float fallheight) {
         return this.fallHeight.test(fallheight);
      }

      // $FF: synthetic method
      private static PlaceMode[] $values() {
         return new PlaceMode[]{BeforeDamage, BeforeDeath};
      }
   }
}
