package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.Objects;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2846;
import net.minecraft.class_2879;
import net.minecraft.class_2846.class_2847;

public class AutoCity extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Double> targetRange;
   private final Setting<Double> breakRange;
   private final Setting<SwitchMode> switchMode;
   private final Setting<Boolean> support;
   private final Setting<Double> placeRange;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> chatInfo;
   private final Setting<Boolean> swingHand;
   private final Setting<Boolean> renderBlock;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private class_1657 target;
   private class_2338 targetPos;
   private FindItemResult pick;
   private float progress;

   public AutoCity() {
      super(Categories.Combat, "auto-city", "Automatically mine blocks next to someone's feet.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.targetRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("target-range")).description("The radius in which players get targeted.")).defaultValue((double)5.5F).min((double)0.0F).sliderMax((double)7.0F).build());
      this.breakRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("break-range")).description("How close a block must be to you to be considered.")).defaultValue((double)4.5F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.switchMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("switch-mode")).description("How to switch to a pickaxe.")).defaultValue(AutoCity.SwitchMode.Normal)).build());
      this.support = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("support")).description("If there is no block below a city block it will place one before mining.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      DoubleSetting.Builder var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-range")).description("How far away to try and place a block.")).defaultValue((double)4.5F).min((double)0.0F).sliderMax((double)6.0F);
      Setting var10003 = this.support;
      Objects.requireNonNull(var10003);
      this.placeRange = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Automatically rotates you towards the city block.")).defaultValue(true)).build());
      this.chatInfo = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("chat-info")).description("Whether the module should send messages in chat.")).defaultValue(true)).build());
      this.swingHand = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swing-hand")).description("Whether to render your hand swinging.")).defaultValue(false)).build());
      this.renderBlock = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-block")).description("Whether to render the block being broken.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      EnumSetting.Builder var2 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both);
      var10003 = this.renderBlock;
      Objects.requireNonNull(var10003);
      this.shapeMode = var10001.add(((EnumSetting.Builder)var2.visible(var10003::get)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the rendering.")).defaultValue(new SettingColor(225, 0, 0, 75)).visible(() -> (Boolean)this.renderBlock.get() && ((ShapeMode)this.shapeMode.get()).sides())).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the rendering.")).defaultValue(new SettingColor(225, 0, 0, 255)).visible(() -> (Boolean)this.renderBlock.get() && ((ShapeMode)this.shapeMode.get()).lines())).build());
   }

   public void onActivate() {
      this.target = TargetUtils.getPlayerTarget((Double)this.targetRange.get(), SortPriority.ClosestAngle);
      if (TargetUtils.isBadTarget(this.target, (Double)this.targetRange.get())) {
         if ((Boolean)this.chatInfo.get()) {
            this.error("Couldn't find a target, disabling.", new Object[0]);
         }

         this.toggle();
      } else {
         this.targetPos = EntityUtils.getCityBlock(this.target);
         if (this.targetPos != null && !(PlayerUtils.squaredDistanceTo(this.targetPos) > Math.pow((Double)this.breakRange.get(), (double)2.0F))) {
            if ((Boolean)this.support.get()) {
               class_2338 supportPos = this.targetPos.method_10074();
               if (!(PlayerUtils.squaredDistanceTo(supportPos) > Math.pow((Double)this.placeRange.get(), (double)2.0F))) {
                  BlockUtils.place(supportPos, InvUtils.findInHotbar(class_1802.field_8281), (Boolean)this.rotate.get(), 0, true);
               }
            }

            this.pick = InvUtils.find((Predicate)((itemStack) -> itemStack.method_7909() == class_1802.field_8377 || itemStack.method_7909() == class_1802.field_22024));
            if (!this.pick.isHotbar()) {
               this.error("No pickaxe found... disabling.", new Object[0]);
               this.toggle();
            } else {
               this.progress = 0.0F;
               this.mine(false);
            }
         } else {
            if ((Boolean)this.chatInfo.get()) {
               this.error("Couldn't find a good block, disabling.", new Object[0]);
            }

            this.toggle();
         }
      }
   }

   public void onDeactivate() {
      this.target = null;
      this.targetPos = null;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (TargetUtils.isBadTarget(this.target, (Double)this.targetRange.get())) {
         this.toggle();
      } else if (PlayerUtils.squaredDistanceTo(this.targetPos) > Math.pow((Double)this.breakRange.get(), (double)2.0F)) {
         if ((Boolean)this.chatInfo.get()) {
            this.error("Couldn't find a target, disabling.", new Object[0]);
         }

         this.toggle();
      } else {
         if (this.progress < 1.0F) {
            this.pick = InvUtils.find((Predicate)((itemStack) -> itemStack.method_7909() == class_1802.field_8377 || itemStack.method_7909() == class_1802.field_22024));
            if (!this.pick.isHotbar()) {
               this.error("No pickaxe found... disabling.", new Object[0]);
               this.toggle();
               return;
            }

            this.progress = (float)((double)this.progress + BlockUtils.getBreakDelta(this.pick.slot(), this.mc.field_1687.method_8320(this.targetPos)));
            if (this.progress < 1.0F) {
               return;
            }
         }

         this.mine(true);
         this.toggle();
      }
   }

   public void mine(boolean done) {
      InvUtils.swap(this.pick.slot(), this.switchMode.get() == AutoCity.SwitchMode.Silent);
      if ((Boolean)this.rotate.get()) {
         Rotations.rotate(Rotations.getYaw(this.targetPos), Rotations.getPitch(this.targetPos));
      }

      class_2350 direction = BlockUtils.getDirection(this.targetPos);
      if (!done) {
         this.mc.field_1761.method_41931(this.mc.field_1687, (sequence) -> new class_2846(class_2847.field_12968, this.targetPos, direction, sequence));
      }

      this.mc.field_1761.method_41931(this.mc.field_1687, (sequence) -> new class_2846(class_2847.field_12973, this.targetPos, direction, sequence));
      if ((Boolean)this.swingHand.get()) {
         this.mc.field_1724.method_6104(class_1268.field_5808);
      } else {
         this.mc.method_1562().method_52787(new class_2879(class_1268.field_5808));
      }

      if (this.switchMode.get() == AutoCity.SwitchMode.Silent) {
         InvUtils.swapBack();
      }

   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      if (this.targetPos != null && (Boolean)this.renderBlock.get()) {
         event.renderer.box((class_2338)this.targetPos, this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), 0);
      }
   }

   public String getInfoString() {
      return EntityUtils.getName(this.target);
   }

   public static enum SwitchMode {
      Normal,
      Silent;

      // $FF: synthetic method
      private static SwitchMode[] $values() {
         return new SwitchMode[]{Normal, Silent};
      }
   }
}
