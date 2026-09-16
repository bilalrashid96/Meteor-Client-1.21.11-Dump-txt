package meteordevelopment.meteorclient.systems.modules.render;

import java.util.List;
import meteordevelopment.meteorclient.MixinPlugin;
import meteordevelopment.meteorclient.events.render.RenderBlockEntityEvent;
import meteordevelopment.meteorclient.events.world.AmbientOcclusionEvent;
import meteordevelopment.meteorclient.events.world.ChunkOcclusionEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.class_1922;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_259;
import net.minecraft.class_2680;

public class Xray extends Module {
   private final SettingGroup sgGeneral;
   public static final List<class_2248> ORES;
   private final Setting<List<class_2248>> blocks;
   public final Setting<Integer> opacity;
   private final Setting<Boolean> exposedOnly;

   public Xray() {
      super(Categories.Render, "xray", "Only renders specified blocks. Good for mining.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("whitelist")).description("Which blocks to show x-rayed.")).defaultValue(ORES)).onChanged((v) -> {
         if (this.isActive()) {
            this.mc.field_1769.method_3279();
         }

      })).build());
      this.opacity = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("opacity")).description("The opacity for all other blocks.")).defaultValue(25)).range(0, 255).sliderMax(255).onChanged((onChanged) -> {
         if (this.isActive()) {
            this.mc.field_1769.method_3279();
         }

      })).build());
      this.exposedOnly = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("exposed-only")).description("Show only exposed ores.")).defaultValue(false)).onChanged((onChanged) -> {
         if (this.isActive()) {
            this.mc.field_1769.method_3279();
         }

      })).build());
   }

   public void onActivate() {
      this.mc.field_1769.method_3279();
   }

   public void onDeactivate() {
      this.mc.field_1769.method_3279();
   }

   public WWidget getWidget(GuiTheme theme) {
      return MixinPlugin.isIrisPresent && IrisApi.getInstance().isShaderPackInUse() ? theme.label("Warning: Due to shaders in use, opacity is overridden to 0.") : null;
   }

   @EventHandler
   private void onRenderBlockEntity(RenderBlockEntityEvent event) {
      if (this.isBlocked(event.blockEntityState.field_62674.method_26204(), event.blockEntityState.field_62673)) {
         event.cancel();
      }

   }

   @EventHandler
   private void onChunkOcclusion(ChunkOcclusionEvent event) {
      event.cancel();
   }

   @EventHandler
   private void onAmbientOcclusion(AmbientOcclusionEvent event) {
      event.lightLevel = 1.0F;
   }

   public boolean modifyDrawSide(class_2680 state, class_1922 view, class_2338 pos, class_2350 facing, boolean returns) {
      if (!returns && !this.isBlocked(state.method_26204(), pos)) {
         class_2338 adjPos = pos.method_10093(facing);
         class_2680 adjState = view.method_8320(adjPos);
         return adjState.method_26173(facing.method_10153()) != class_259.method_1077() || adjState.method_26204() != state.method_26204() || !adjState.method_26216() || this.isBlocked(adjState.method_26204(), adjPos);
      } else {
         return returns;
      }
   }

   public boolean isBlocked(class_2248 block, class_2338 blockPos) {
      return !((List)this.blocks.get()).contains(block) || (Boolean)this.exposedOnly.get() && blockPos != null && !BlockUtils.isExposed(blockPos);
   }

   public static int getAlpha(class_2680 state, class_2338 pos) {
      WallHack wallHack = (WallHack)Modules.get().get(WallHack.class);
      Xray xray = (Xray)Modules.get().get(Xray.class);
      if (wallHack.isActive() && ((List)wallHack.blocks.get()).contains(state.method_26204())) {
         if (MixinPlugin.isIrisPresent && IrisApi.getInstance().isShaderPackInUse()) {
            return 0;
         } else {
            int alpha;
            if (xray.isActive()) {
               alpha = (Integer)xray.opacity.get();
            } else {
               alpha = (Integer)wallHack.opacity.get();
            }

            return alpha;
         }
      } else if (xray.isActive() && !wallHack.isActive() && xray.isBlocked(state.method_26204(), pos)) {
         return MixinPlugin.isIrisPresent && IrisApi.getInstance().isShaderPackInUse() ? 0 : (Integer)xray.opacity.get();
      } else {
         return -1;
      }
   }

   static {
      ORES = List.of(class_2246.field_10418, class_2246.field_29219, class_2246.field_10212, class_2246.field_29027, class_2246.field_10571, class_2246.field_29026, class_2246.field_10090, class_2246.field_29028, class_2246.field_10080, class_2246.field_29030, class_2246.field_10442, class_2246.field_29029, class_2246.field_10013, class_2246.field_29220, class_2246.field_27120, class_2246.field_29221, class_2246.field_23077, class_2246.field_10213, class_2246.field_22109);
   }
}
