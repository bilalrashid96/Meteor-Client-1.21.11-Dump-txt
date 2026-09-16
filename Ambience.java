package meteordevelopment.meteorclient.systems.modules.world;

import java.util.Objects;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class Ambience extends Module {
   private final SettingGroup sgSky;
   private final SettingGroup sgWorld;
   public final Setting<Boolean> endSky;
   public final Setting<Boolean> customSkyColor;
   public final Setting<SettingColor> overworldSkyColor;
   public final Setting<SettingColor> netherSkyColor;
   public final Setting<SettingColor> endSkyColor;
   public final Setting<Boolean> customCloudColor;
   public final Setting<SettingColor> cloudColor;
   public final Setting<Boolean> changeLightningColor;
   public final Setting<SettingColor> lightningColor;
   public final Setting<Boolean> customGrassColor;
   public final Setting<SettingColor> grassColor;
   public final Setting<Boolean> customFoliageColor;
   public final Setting<SettingColor> foliageColor;
   public final Setting<Boolean> customWaterColor;
   public final Setting<SettingColor> waterColor;
   public final Setting<Boolean> customLavaColor;
   public final Setting<SettingColor> lavaColor;
   public final Setting<Boolean> customFogColor;
   public final Setting<SettingColor> fogColor;

   public Ambience() {
      super(Categories.World, "ambience", "Change the color of various pieces of the environment.");
      this.sgSky = this.settings.createGroup("Sky");
      this.sgWorld = this.settings.createGroup("World");
      this.endSky = this.sgSky.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("end-sky")).description("Makes the sky like the end.")).defaultValue(false)).build());
      this.customSkyColor = this.sgSky.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-sky-color")).description("Whether the sky color should be changed.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgSky;
      ColorSetting.Builder var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("overworld-sky-color")).description("The color of the overworld sky.")).defaultValue(new SettingColor(0, 125, 255));
      Setting var10003 = this.customSkyColor;
      Objects.requireNonNull(var10003);
      this.overworldSkyColor = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgSky;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("nether-sky-color")).description("The color of the nether sky.")).defaultValue(new SettingColor(102, 0, 0));
      var10003 = this.customSkyColor;
      Objects.requireNonNull(var10003);
      this.netherSkyColor = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgSky;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("end-sky-color")).description("The color of the end sky.")).defaultValue(new SettingColor(65, 30, 90));
      var10003 = this.customSkyColor;
      Objects.requireNonNull(var10003);
      this.endSkyColor = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      this.customCloudColor = this.sgSky.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-cloud-color")).description("Whether the clouds color should be changed.")).defaultValue(false)).build());
      var10001 = this.sgSky;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("cloud-color")).description("The color of the clouds.")).defaultValue(new SettingColor(102, 0, 0));
      var10003 = this.customCloudColor;
      Objects.requireNonNull(var10003);
      this.cloudColor = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      this.changeLightningColor = this.sgSky.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-lightning-color")).description("Whether the lightning color should be changed.")).defaultValue(false)).build());
      var10001 = this.sgSky;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("lightning-color")).description("The color of the lightning.")).defaultValue(new SettingColor(102, 0, 0));
      var10003 = this.changeLightningColor;
      Objects.requireNonNull(var10003);
      this.lightningColor = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      this.customGrassColor = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-grass-color")).description("Whether the grass color should be changed.")).defaultValue(false)).onChanged((val) -> this.reload())).build());
      var10001 = this.sgWorld;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("grass-color")).description("The color of the grass.")).defaultValue(new SettingColor(102, 0, 0));
      var10003 = this.customGrassColor;
      Objects.requireNonNull(var10003);
      this.grassColor = var10001.add(((ColorSetting.Builder)((ColorSetting.Builder)var10002.visible(var10003::get)).onChanged((val) -> this.reload())).build());
      this.customFoliageColor = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-foliage-color")).description("Whether the foliage color should be changed.")).defaultValue(false)).onChanged((val) -> this.reload())).build());
      var10001 = this.sgWorld;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("foliage-color")).description("The color of the foliage.")).defaultValue(new SettingColor(102, 0, 0));
      var10003 = this.customFoliageColor;
      Objects.requireNonNull(var10003);
      this.foliageColor = var10001.add(((ColorSetting.Builder)((ColorSetting.Builder)var10002.visible(var10003::get)).onChanged((val) -> this.reload())).build());
      this.customWaterColor = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-water-color")).description("Whether the water color should be changed.")).defaultValue(false)).onChanged((val) -> this.reload())).build());
      var10001 = this.sgWorld;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("water-color")).description("The color of the water.")).defaultValue(new SettingColor(102, 0, 0));
      var10003 = this.customWaterColor;
      Objects.requireNonNull(var10003);
      this.waterColor = var10001.add(((ColorSetting.Builder)((ColorSetting.Builder)var10002.visible(var10003::get)).onChanged((val) -> this.reload())).build());
      this.customLavaColor = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-lava-color")).description("Whether the lava color should be changed.")).defaultValue(false)).onChanged((val) -> this.reload())).build());
      var10001 = this.sgWorld;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("lava-color")).description("The color of the lava.")).defaultValue(new SettingColor(102, 0, 0));
      var10003 = this.customLavaColor;
      Objects.requireNonNull(var10003);
      this.lavaColor = var10001.add(((ColorSetting.Builder)((ColorSetting.Builder)var10002.visible(var10003::get)).onChanged((val) -> this.reload())).build());
      this.customFogColor = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-fog-color")).description("Whether the fog color should be changed.")).defaultValue(false)).build());
      var10001 = this.sgWorld;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("fog-color")).description("The color of the fog.")).defaultValue(new SettingColor(102, 0, 0));
      var10003 = this.customFogColor;
      Objects.requireNonNull(var10003);
      this.fogColor = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
   }

   public void onActivate() {
      this.reload();
   }

   public void onDeactivate() {
      this.reload();
   }

   private void reload() {
      if (this.mc.field_1769 != null && this.isActive()) {
         this.mc.field_1769.method_3279();
      }

   }

   public SettingColor skyColor() {
      switch (PlayerUtils.getDimension()) {
         case Overworld -> {
            return this.overworldSkyColor.get();
         }
         case Nether -> {
            return this.netherSkyColor.get();
         }
         case End -> {
            return this.endSkyColor.get();
         }
         default -> {
            return null;
         }
      }
   }
}
