package meteordevelopment.meteorclient.systems.hud.elements;

import java.util.Objects;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.hud.screens.HudEditorScreen;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_10090;
import net.minecraft.class_1799;
import net.minecraft.class_1806;
import net.minecraft.class_22;
import net.minecraft.class_9209;
import net.minecraft.class_9334;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

public class MapHud extends HudElement {
   public static final HudElementInfo<MapHud> INFO;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgVisual;
   private final Setting<Mode> mode;
   private final Setting<Integer> slotIndex;
   private final Setting<Integer> mapId;
   private final Setting<Double> scale;
   private final Setting<Boolean> background;
   private final Setting<SettingColor> backgroundColor;
   private final class_10090 renderState;
   private @Nullable class_9209 mapComponent;
   private @Nullable class_22 mapState;

   public MapHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgVisual = this.settings.createGroup("Visual");
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("How to determine which map to render.")).defaultValue(MapHud.Mode.Simple)).build());
      this.slotIndex = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("slot-index")).description("Which slot to grab the map from.")).visible(() -> this.mode.get() == MapHud.Mode.SlotIndex)).defaultValue(0)).sliderRange(0, 40).build());
      this.mapId = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("map-id")).description("Which map id to render from. Must be in your inventory!")).visible(() -> this.mode.get() == MapHud.Mode.MapId)).defaultValue(0)).noSlider().build());
      this.scale = this.sgVisual.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("How big to render the map.")).defaultValue((double)1.0F).min((double)0.5F).sliderRange((double)0.5F, (double)3.0F).build());
      this.background = this.sgVisual.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("background")).description("Displays background.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgVisual;
      ColorSetting.Builder var10002 = (ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("Color used for the background.");
      Setting var10003 = this.background;
      Objects.requireNonNull(var10003);
      this.backgroundColor = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).defaultValue(new SettingColor(25, 25, 25, 50)).build());
      this.renderState = new class_10090();
   }

   public void tick(HudRenderer renderer) {
      double scale = (Double)this.scale.get();
      this.setSize((double)128.0F * scale, (double)128.0F * scale);
      if (Utils.canUpdate()) {
         class_1799 mapStack = class_1799.field_8037;
         switch (((Mode)this.mode.get()).ordinal()) {
            case 0:
               mapStack = MeteorClient.mc.field_1724.method_31548().method_5438((Integer)this.slotIndex.get());
               break;
            case 1:
               FindItemResult mapResult = InvUtils.find((Predicate)((stack) -> {
                  class_9209 mapIdComponent = (class_9209)stack.method_58694(class_9334.field_49646);
                  return mapIdComponent != null && mapIdComponent.comp_2315() == (Integer)this.mapId.get();
               }));
               if (mapResult.found()) {
                  mapStack = MeteorClient.mc.field_1724.method_31548().method_5438(mapResult.slot());
               }
               break;
            case 2:
               FindItemResult mapResult = InvUtils.find((Predicate)((stack) -> {
                  class_9209 mapIdComponent = (class_9209)stack.method_58694(class_9334.field_49646);
                  return mapIdComponent != null;
               }));
               if (mapResult.found()) {
                  mapStack = MeteorClient.mc.field_1724.method_31548().method_5438(mapResult.slot());
               }
         }

         if (!mapStack.method_7960() && mapStack.method_57826(class_9334.field_49646)) {
            this.mapComponent = (class_9209)mapStack.method_58694(class_9334.field_49646);
            this.mapState = class_1806.method_7997(this.mapComponent, MeteorClient.mc.field_1687);
         } else {
            this.mapComponent = null;
            this.mapState = null;
         }

      }
   }

   public void render(HudRenderer renderer) {
      if (this.mapComponent != null && this.mapState != null) {
         if ((Boolean)this.background.get()) {
            renderer.quad((double)this.x, (double)this.y, (double)this.getWidth(), (double)this.getHeight(), this.backgroundColor.get());
         }

         renderer.post(() -> {
            MeteorClient.mc.method_61965().method_62230(this.mapComponent, this.mapState, this.renderState);
            Matrix3x2fStack matrices = renderer.drawContext.method_51448();
            matrices.pushMatrix();
            matrices.scale(1.0F / (float)MeteorClient.mc.method_22683().method_4495());
            matrices.translate((float)this.x, (float)this.y);
            matrices.scale(((Double)this.scale.get()).floatValue());
            renderer.drawContext.method_70857(this.renderState);
            matrices.popMatrix();
         });
      } else {
         if (HudEditorScreen.isOpen()) {
            renderer.quad((double)this.x, (double)this.y, (double)this.getWidth(), (double)this.getHeight(), this.backgroundColor.get());
            renderer.line((double)this.x, (double)this.y, (double)(this.x + this.getWidth()), (double)(this.y + this.getHeight()), SettingColor.GRAY);
            renderer.line((double)(this.x + this.getWidth()), (double)this.y, (double)this.x, (double)(this.y + this.getHeight()), SettingColor.GRAY);
         }

      }
   }

   static {
      INFO = new HudElementInfo<MapHud>(Hud.GROUP, "map", "Displays the contents of a map on your Hud.", MapHud::new);
   }

   private static enum Mode {
      SlotIndex,
      MapId,
      Simple;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{SlotIndex, MapId, Simple};
      }
   }
}
