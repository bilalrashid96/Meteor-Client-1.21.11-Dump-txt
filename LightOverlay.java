package meteordevelopment.meteorclient.systems.modules.render;

import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2338;

public class LightOverlay extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgColors;
   private final Setting<Integer> horizontalRange;
   private final Setting<Integer> verticalRange;
   private final Setting<Boolean> seeThroughBlocks;
   private final Setting<Integer> lightLevel;
   private final Setting<SettingColor> color;
   private final Setting<SettingColor> potentialColor;
   private final Pool<Cross> crossPool;
   private final List<Cross> crosses;

   public LightOverlay() {
      super(Categories.Render, "light-overlay", "Shows blocks where mobs can spawn.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgColors = this.settings.createGroup("Colors");
      this.horizontalRange = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("horizontal-range")).description("Horizontal range in blocks.")).defaultValue(8)).min(0).build());
      this.verticalRange = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("vertical-range")).description("Vertical range in blocks.")).defaultValue(4)).min(0).build());
      this.seeThroughBlocks = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("see-through-blocks")).description("Allows you to see the lines through blocks.")).defaultValue(false)).build());
      this.lightLevel = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("light-level")).description("Which light levels to render. Old spawning light: 7.")).defaultValue(0)).min(0).sliderMax(15).build());
      this.color = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color")).description("Color of places where mobs can currently spawn.")).defaultValue(new SettingColor(225, 25, 25)).build());
      this.potentialColor = this.sgColors.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("potential-color")).description("Color of places where mobs can potentially spawn (eg at night).")).defaultValue(new SettingColor(225, 225, 25)).build());
      this.crossPool = new Pool<Cross>(() -> new Cross());
      this.crosses = new ArrayList();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      this.crossPool.freeAll(this.crosses);
      this.crosses.clear();
      BlockIterator.register((Integer)this.horizontalRange.get(), (Integer)this.verticalRange.get(), (blockPos, blockState) -> {
         switch (BlockUtils.isValidMobSpawn(blockPos, blockState, (Integer)this.lightLevel.get())) {
            case Potential -> this.crosses.add(((Cross)this.crossPool.get()).set(blockPos, true));
            case Always -> this.crosses.add(((Cross)this.crossPool.get()).set(blockPos, false));
         }

      });
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (!this.crosses.isEmpty()) {
         Renderer3D renderer = (Boolean)this.seeThroughBlocks.get() ? event.renderer : event.depthRenderer;

         for(Cross cross : this.crosses) {
            cross.render(renderer);
         }

      }
   }

   private class Cross {
      private double x;
      private double y;
      private double z;
      private boolean potential;

      public Cross set(class_2338 blockPos, boolean potential) {
         this.x = (double)blockPos.method_10263();
         this.y = (double)blockPos.method_10264() + 0.0075;
         this.z = (double)blockPos.method_10260();
         this.potential = potential;
         return this;
      }

      public void render(Renderer3D renderer) {
         Color c = this.potential ? (Color)LightOverlay.this.potentialColor.get() : (Color)LightOverlay.this.color.get();
         renderer.line(this.x, this.y, this.z, this.x + (double)1.0F, this.y, this.z + (double)1.0F, c);
         renderer.line(this.x + (double)1.0F, this.y, this.z, this.x, this.y, this.z + (double)1.0F, c);
      }
   }

   public static enum Spawn {
      Never,
      Potential,
      Always;

      // $FF: synthetic method
      private static Spawn[] $values() {
         return new Spawn[]{Never, Potential, Always};
      }
   }
}
