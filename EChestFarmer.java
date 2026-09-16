package meteordevelopment.meteorclient.systems.modules.world;

import java.util.Objects;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1893;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;

public class EChestFarmer extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Boolean> selfToggle;
   private final Setting<Boolean> ignoreExisting;
   private final Setting<Integer> amount;
   private final Setting<Boolean> swingHand;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final class_265 SHAPE;
   private class_2338 target;
   private int startCount;

   public EChestFarmer() {
      super(Categories.World, "echest-farmer", "Places and breaks EChests to farm obsidian.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.selfToggle = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("self-toggle")).description("Disables when you reach the desired amount of obsidian.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-existing")).description("Ignores existing obsidian in your inventory and mines the total target amount.")).defaultValue(true);
      Setting var10003 = this.selfToggle;
      Objects.requireNonNull(var10003);
      this.ignoreExisting = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      IntSetting.Builder var2 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("amount")).description("The amount of obsidian to farm.")).defaultValue(64)).sliderMax(128).range(8, 512).sliderRange(8, 128);
      var10003 = this.selfToggle;
      Objects.requireNonNull(var10003);
      this.amount = var10001.add(((IntSetting.Builder)var2.visible(var10003::get)).build());
      this.swingHand = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swing-hand")).description("Swing hand client-side.")).defaultValue(true)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders a block overlay where the obsidian will be placed.")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The color of the sides of the blocks being rendered.")).defaultValue(new SettingColor(204, 0, 0, 50)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The color of the lines of the blocks being rendered.")).defaultValue(new SettingColor(204, 0, 0, 255)).build());
      this.SHAPE = class_2248.method_9541((double)1.0F, (double)0.0F, (double)1.0F, (double)15.0F, (double)14.0F, (double)15.0F);
   }

   public void onActivate() {
      this.target = null;
      this.startCount = InvUtils.find(class_1802.field_8281).count();
   }

   public void onDeactivate() {
      InvUtils.swapBack();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.target == null) {
         if (this.mc.field_1765 == null || this.mc.field_1765.method_17783() != class_240.field_1332) {
            return;
         }

         class_2338 pos = ((class_3965)this.mc.field_1765).method_17777().method_10084();
         class_2680 state = this.mc.field_1687.method_8320(pos);
         if (!state.method_45474() && state.method_26204() != class_2246.field_10443) {
            return;
         }

         this.target = ((class_3965)this.mc.field_1765).method_17777().method_10084();
      }

      if (!PlayerUtils.isWithinReach(this.target)) {
         this.error("Target block pos out of reach.", new Object[0]);
         this.target = null;
      } else if ((Boolean)this.selfToggle.get() && InvUtils.find(class_1802.field_8281).count() - ((Boolean)this.ignoreExisting.get() ? this.startCount : 0) >= (Integer)this.amount.get()) {
         InvUtils.swapBack();
         this.toggle();
      } else {
         if (this.mc.field_1687.method_8320(this.target).method_26204() == class_2246.field_10443) {
            double bestScore = (double)-1.0F;
            int bestSlot = -1;

            for(int i = 0; i < 9; ++i) {
               class_1799 itemStack = this.mc.field_1724.method_31548().method_5438(i);
               if (!Utils.hasEnchantment(itemStack, class_1893.field_9099)) {
                  double score = (double)itemStack.method_7924(class_2246.field_10443.method_9564());
                  if (score > bestScore) {
                     bestScore = score;
                     bestSlot = i;
                  }
               }
            }

            if (bestSlot == -1) {
               return;
            }

            InvUtils.swap(bestSlot, true);
            BlockUtils.breakBlock(this.target, (Boolean)this.swingHand.get());
         }

         if (this.mc.field_1687.method_8320(this.target).method_45474()) {
            FindItemResult echest = InvUtils.findInHotbar(class_1802.field_8466);
            if (!echest.found()) {
               this.error("No Echests in hotbar, disabling", new Object[0]);
               this.toggle();
               return;
            }

            BlockUtils.place(this.target, echest, true, 0, true);
         }

      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (this.target != null && (Boolean)this.render.get() && !((PacketMine)Modules.get().get(PacketMine.class)).isMiningBlock(this.target)) {
         class_238 box = (class_238)this.SHAPE.method_1090().getFirst();
         event.renderer.box((double)this.target.method_10263() + box.field_1323, (double)this.target.method_10264() + box.field_1322, (double)this.target.method_10260() + box.field_1321, (double)this.target.method_10263() + box.field_1320, (double)this.target.method_10264() + box.field_1325, (double)this.target.method_10260() + box.field_1324, this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), 0);
      }
   }
}
