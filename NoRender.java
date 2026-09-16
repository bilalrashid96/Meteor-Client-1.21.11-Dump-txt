package meteordevelopment.meteorclient.systems.modules.render;

import java.util.List;
import java.util.Set;
import meteordevelopment.meteorclient.events.render.RenderBlockEntityEvent;
import meteordevelopment.meteorclient.events.world.ChunkOcclusionEvent;
import meteordevelopment.meteorclient.events.world.ParticleEvent;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.ParticleTypeListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_2185;
import net.minecraft.class_2248;
import net.minecraft.class_2343;
import net.minecraft.class_2396;
import net.minecraft.class_2398;

public class NoRender extends Module {
   private final SettingGroup sgOverlay;
   private final SettingGroup sgHUD;
   private final SettingGroup sgWorld;
   private final SettingGroup sgEntity;
   private final Setting<Boolean> noPortalOverlay;
   private final Setting<Boolean> noSpyglassOverlay;
   private final Setting<Boolean> noNausea;
   private final Setting<Boolean> noPumpkinOverlay;
   private final Setting<Boolean> noPowderedSnowOverlay;
   private final Setting<Boolean> noFireOverlay;
   private final Setting<Boolean> noLiquidOverlay;
   private final Setting<Boolean> noInWallOverlay;
   private final Setting<Boolean> noVignette;
   private final Setting<Boolean> noGuiBackground;
   private final Setting<Boolean> noTotemAnimation;
   private final Setting<Boolean> noEatParticles;
   private final Setting<Boolean> noEnchantGlint;
   private final Setting<Boolean> noBossBar;
   private final Setting<Boolean> noScoreboard;
   private final Setting<Boolean> noCrosshair;
   private final Setting<Boolean> noTitle;
   private final Setting<Boolean> noHeldItemName;
   private final Setting<Boolean> noObfuscation;
   private final Setting<Boolean> noPotionIcons;
   private final Setting<Boolean> noMessageSignatureIndicator;
   private final Setting<Boolean> noWeather;
   private final Setting<Boolean> noWorldBorder;
   private final Setting<Boolean> noBlindness;
   private final Setting<Boolean> noDarkness;
   private final Setting<Boolean> noFog;
   private final Setting<Boolean> noEnchTableBook;
   private final Setting<Boolean> noSignText;
   private final Setting<Boolean> noBlockBreakParticles;
   private final Setting<Boolean> noBlockBreakOverlay;
   private final Setting<Boolean> noBeaconBeams;
   private final Setting<Boolean> noFallingBlocks;
   private final Setting<Boolean> noCaveCulling;
   private final Setting<Boolean> noMapMarkers;
   private final Setting<Boolean> noMapContents;
   private final Setting<BannerRenderMode> bannerRender;
   private final Setting<Boolean> noFireworkExplosions;
   private final Setting<List<class_2396<?>>> particles;
   private final Setting<Boolean> noBarrierInvis;
   private final Setting<Boolean> noTextureRotations;
   private final Setting<List<class_2248>> blockEntities;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<Boolean> dropSpawnPacket;
   private final Setting<Boolean> noArmor;
   private final Setting<Boolean> noInvisibility;
   private final Setting<Boolean> noGlowing;
   private final Setting<Boolean> noMobInSpawner;
   private final Setting<Boolean> noDeadEntities;
   private final Setting<Boolean> noNametags;

   public NoRender() {
      super(Categories.Render, "no-render", "Disables certain animations or overlays from rendering.");
      this.sgOverlay = this.settings.createGroup("Overlay");
      this.sgHUD = this.settings.createGroup("HUD");
      this.sgWorld = this.settings.createGroup("World");
      this.sgEntity = this.settings.createGroup("Entity");
      this.noPortalOverlay = this.sgOverlay.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("portal-overlay")).description("Disables rendering of the nether portal overlay.")).defaultValue(false)).build());
      this.noSpyglassOverlay = this.sgOverlay.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("spyglass-overlay")).description("Disables rendering of the spyglass overlay.")).defaultValue(false)).build());
      this.noNausea = this.sgOverlay.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("nausea")).description("Disables rendering of the nausea overlay.")).defaultValue(false)).build());
      this.noPumpkinOverlay = this.sgOverlay.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pumpkin-overlay")).description("Disables rendering of the pumpkin head overlay")).defaultValue(false)).build());
      this.noPowderedSnowOverlay = this.sgOverlay.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("powdered-snow-overlay")).description("Disables rendering of the powdered snow overlay.")).defaultValue(false)).build());
      this.noFireOverlay = this.sgOverlay.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fire-overlay")).description("Disables rendering of the fire overlay.")).defaultValue(false)).build());
      this.noLiquidOverlay = this.sgOverlay.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("liquid-overlay")).description("Disables rendering of the liquid overlay.")).defaultValue(false)).build());
      this.noInWallOverlay = this.sgOverlay.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("in-wall-overlay")).description("Disables rendering of the overlay when inside blocks.")).defaultValue(false)).build());
      this.noVignette = this.sgOverlay.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("vignette")).description("Disables rendering of the vignette overlay.")).defaultValue(false)).build());
      this.noGuiBackground = this.sgOverlay.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("gui-background")).description("Disables rendering of the GUI background overlay.")).defaultValue(false)).build());
      this.noTotemAnimation = this.sgOverlay.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("totem-animation")).description("Disables rendering of the totem animation when you pop a totem.")).defaultValue(false)).build());
      this.noEatParticles = this.sgOverlay.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("eating-particles")).description("Disables rendering of eating particles.")).defaultValue(false)).build());
      this.noEnchantGlint = this.sgOverlay.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("enchantment-glint")).description("Disables rending of the enchantment glint.")).defaultValue(false)).build());
      this.noBossBar = this.sgHUD.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("boss-bar")).description("Disable rendering of boss bars.")).defaultValue(false)).build());
      this.noScoreboard = this.sgHUD.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("scoreboard")).description("Disable rendering of the scoreboard.")).defaultValue(false)).build());
      this.noCrosshair = this.sgHUD.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("crosshair")).description("Disables rendering of the crosshair.")).defaultValue(false)).build());
      this.noTitle = this.sgHUD.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("title")).description("Disables rendering of the title.")).defaultValue(false)).build());
      this.noHeldItemName = this.sgHUD.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("held-item-name")).description("Disables rendering of the held item name.")).defaultValue(false)).build());
      this.noObfuscation = this.sgHUD.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("obfuscation")).description("Disables obfuscation styling of characters.")).defaultValue(false)).build());
      this.noPotionIcons = this.sgHUD.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("potion-icons")).description("Disables rendering of status effect icons.")).defaultValue(false)).build());
      this.noMessageSignatureIndicator = this.sgHUD.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("message-signature-indicator")).description("Disables chat message signature indicator on the left of the message.")).defaultValue(false)).build());
      this.noWeather = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("weather")).description("Disables rendering of weather.")).defaultValue(false)).build());
      this.noWorldBorder = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("world-border")).description("Disables rendering of the world border.")).defaultValue(false)).build());
      this.noBlindness = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("blindness")).description("Disables rendering of blindness.")).defaultValue(false)).build());
      this.noDarkness = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("darkness")).description("Disables rendering of darkness.")).defaultValue(false)).build());
      this.noFog = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fog")).description("Disables rendering of fog.")).defaultValue(false)).build());
      this.noEnchTableBook = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("enchantment-table-book")).description("Disables rendering of books above enchanting tables.")).defaultValue(false)).build());
      this.noSignText = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sign-text")).description("Disables rendering of text on signs.")).defaultValue(false)).build());
      this.noBlockBreakParticles = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("block-break-particles")).description("Disables rendering of block-break particles.")).defaultValue(false)).build());
      this.noBlockBreakOverlay = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("block-break-overlay")).description("Disables rendering of block-break overlay.")).defaultValue(false)).build());
      this.noBeaconBeams = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("beacon-beams")).description("Disables rendering of beacon beams.")).defaultValue(false)).build());
      this.noFallingBlocks = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("falling-blocks")).description("Disables rendering of falling blocks.")).defaultValue(false)).build());
      this.noCaveCulling = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("cave-culling")).description("Disables Minecraft's cave culling algorithm.")).defaultValue(false)).onChanged((b) -> this.mc.field_1769.method_3279())).build());
      this.noMapMarkers = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("map-markers")).description("Disables markers on maps.")).defaultValue(false)).build());
      this.noMapContents = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("map-contents")).description("Disable rendering of maps.")).defaultValue(false)).build());
      this.bannerRender = this.sgWorld.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("banners")).description("Changes rendering of banners.")).defaultValue(NoRender.BannerRenderMode.Everything)).build());
      this.noFireworkExplosions = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("firework-explosions")).description("Disables rendering of firework explosions.")).defaultValue(false)).build());
      this.particles = this.sgWorld.add(((ParticleTypeListSetting.Builder)((ParticleTypeListSetting.Builder)(new ParticleTypeListSetting.Builder()).name("particles")).description("Particles to not render.")).build());
      this.noBarrierInvis = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("barrier-invisibility")).description("Disables barriers being invisible when not holding one.")).defaultValue(false)).build());
      this.noTextureRotations = this.sgWorld.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("texture-rotations")).description("Changes texture rotations and model offsets to use a constant value instead of the block position.")).defaultValue(false)).onChanged((b) -> this.mc.field_1769.method_3279())).build());
      this.blockEntities = this.sgWorld.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("block-entities")).description("Block entities (chest, shulker block, etc.) to not render.")).filter((block) -> block instanceof class_2343 && !(block instanceof class_2185)).build());
      this.entities = this.sgEntity.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Disables rendering of selected entities.")).build());
      this.dropSpawnPacket = this.sgEntity.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("drop-spawn-packets")).description("WARNING! Drops all spawn packets of entities selected in the above list.")).defaultValue(false)).build());
      this.noArmor = this.sgEntity.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("armor")).description("Disables rendering of armor on entities.")).defaultValue(false)).build());
      this.noInvisibility = this.sgEntity.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("invisibility")).description("Shows invisible entities.")).defaultValue(false)).build());
      this.noGlowing = this.sgEntity.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("glowing")).description("Disables rendering of the glowing effect")).defaultValue(false)).build());
      this.noMobInSpawner = this.sgEntity.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("spawner-entities")).description("Disables rendering of spinning mobs inside of mob spawners")).defaultValue(false)).build());
      this.noDeadEntities = this.sgEntity.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("dead-entities")).description("Disables rendering of dead entities")).defaultValue(false)).build());
      this.noNametags = this.sgEntity.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("nametags")).description("Disables rendering of entity nametags")).defaultValue(false)).build());
   }

   public void onActivate() {
      if ((Boolean)this.noCaveCulling.get() || (Boolean)this.noTextureRotations.get()) {
         this.mc.field_1769.method_3279();
      }

   }

   public void onDeactivate() {
      if ((Boolean)this.noCaveCulling.get() || (Boolean)this.noTextureRotations.get()) {
         this.mc.field_1769.method_3279();
      }

   }

   public boolean noPortalOverlay() {
      return this.isActive() && (Boolean)this.noPortalOverlay.get();
   }

   public boolean noSpyglassOverlay() {
      return this.isActive() && (Boolean)this.noSpyglassOverlay.get();
   }

   public boolean noNausea() {
      return this.isActive() && (Boolean)this.noNausea.get();
   }

   public boolean noPumpkinOverlay() {
      return this.isActive() && (Boolean)this.noPumpkinOverlay.get();
   }

   public boolean noFireOverlay() {
      return this.isActive() && (Boolean)this.noFireOverlay.get();
   }

   public boolean noLiquidOverlay() {
      return this.isActive() && (Boolean)this.noLiquidOverlay.get();
   }

   public boolean noPowderedSnowOverlay() {
      return this.isActive() && (Boolean)this.noPowderedSnowOverlay.get();
   }

   public boolean noInWallOverlay() {
      return this.isActive() && (Boolean)this.noInWallOverlay.get();
   }

   public boolean noVignette() {
      return this.isActive() && (Boolean)this.noVignette.get();
   }

   public boolean noGuiBackground() {
      return this.isActive() && (Boolean)this.noGuiBackground.get();
   }

   public boolean noTotemAnimation() {
      return this.isActive() && (Boolean)this.noTotemAnimation.get();
   }

   public boolean noEatParticles() {
      return this.isActive() && (Boolean)this.noEatParticles.get();
   }

   public boolean noEnchantGlint() {
      return this.isActive() && (Boolean)this.noEnchantGlint.get();
   }

   public boolean noBossBar() {
      return this.isActive() && (Boolean)this.noBossBar.get();
   }

   public boolean noScoreboard() {
      return this.isActive() && (Boolean)this.noScoreboard.get();
   }

   public boolean noCrosshair() {
      return this.isActive() && (Boolean)this.noCrosshair.get();
   }

   public boolean noTitle() {
      return this.isActive() && (Boolean)this.noTitle.get();
   }

   public boolean noHeldItemName() {
      return this.isActive() && (Boolean)this.noHeldItemName.get();
   }

   public boolean noObfuscation() {
      return this.isActive() && (Boolean)this.noObfuscation.get();
   }

   public boolean noPotionIcons() {
      return this.isActive() && (Boolean)this.noPotionIcons.get();
   }

   public boolean noMessageSignatureIndicator() {
      return this.isActive() && (Boolean)this.noMessageSignatureIndicator.get();
   }

   public boolean noWeather() {
      return this.isActive() && (Boolean)this.noWeather.get();
   }

   public boolean noWorldBorder() {
      return this.isActive() && (Boolean)this.noWorldBorder.get();
   }

   public boolean noBlindness() {
      return this.isActive() && (Boolean)this.noBlindness.get();
   }

   public boolean noDarkness() {
      return this.isActive() && (Boolean)this.noDarkness.get();
   }

   public boolean noFog() {
      return this.isActive() && (Boolean)this.noFog.get();
   }

   public boolean noEnchTableBook() {
      return this.isActive() && (Boolean)this.noEnchTableBook.get();
   }

   public boolean noSignText() {
      return this.isActive() && (Boolean)this.noSignText.get();
   }

   public boolean noBlockBreakParticles() {
      return this.isActive() && (Boolean)this.noBlockBreakParticles.get();
   }

   public boolean noBlockBreakOverlay() {
      return this.isActive() && (Boolean)this.noBlockBreakOverlay.get();
   }

   public boolean noBeaconBeams() {
      return this.isActive() && (Boolean)this.noBeaconBeams.get();
   }

   public boolean noFallingBlocks() {
      return this.isActive() && (Boolean)this.noFallingBlocks.get();
   }

   @EventHandler
   private void onChunkOcclusion(ChunkOcclusionEvent event) {
      if ((Boolean)this.noCaveCulling.get()) {
         event.cancel();
      }

   }

   public boolean noMapMarkers() {
      return this.isActive() && (Boolean)this.noMapMarkers.get();
   }

   public boolean noMapContents() {
      return this.isActive() && (Boolean)this.noMapContents.get();
   }

   public BannerRenderMode getBannerRenderMode() {
      return !this.isActive() ? NoRender.BannerRenderMode.Everything : (BannerRenderMode)this.bannerRender.get();
   }

   public boolean noFireworkExplosions() {
      return this.isActive() && (Boolean)this.noFireworkExplosions.get();
   }

   @EventHandler
   private void onAddParticle(ParticleEvent event) {
      if ((Boolean)this.noWeather.get() && event.particle.method_10295() == class_2398.field_11242) {
         event.cancel();
      } else if ((Boolean)this.noFireworkExplosions.get() && event.particle.method_10295() == class_2398.field_11248) {
         event.cancel();
      } else if (((List)this.particles.get()).contains(event.particle.method_10295())) {
         event.cancel();
      }

   }

   public boolean noBarrierInvis() {
      return this.isActive() && (Boolean)this.noBarrierInvis.get();
   }

   public boolean noTextureRotations() {
      return this.isActive() && (Boolean)this.noTextureRotations.get();
   }

   @EventHandler
   private void onRenderBlockEntity(RenderBlockEntityEvent event) {
      if (((List)this.blockEntities.get()).contains(event.blockEntityState.field_62674.method_26204())) {
         event.cancel();
      }

   }

   public boolean noEntity(class_1297 entity) {
      return this.isActive() && ((Set)this.entities.get()).contains(entity.method_5864());
   }

   public boolean noEntity(class_1299<?> entity) {
      return this.isActive() && ((Set)this.entities.get()).contains(entity);
   }

   public boolean getDropSpawnPacket() {
      return this.isActive() && (Boolean)this.dropSpawnPacket.get();
   }

   public boolean noArmor() {
      return this.isActive() && (Boolean)this.noArmor.get();
   }

   public boolean noInvisibility() {
      return this.isActive() && (Boolean)this.noInvisibility.get();
   }

   public boolean noGlowing() {
      return this.isActive() && (Boolean)this.noGlowing.get();
   }

   public boolean noMobInSpawner() {
      return this.isActive() && (Boolean)this.noMobInSpawner.get();
   }

   public boolean noDeadEntities() {
      return this.isActive() && (Boolean)this.noDeadEntities.get();
   }

   public boolean noNametags() {
      return this.isActive() && (Boolean)this.noNametags.get();
   }

   public static enum BannerRenderMode {
      Everything,
      Pillar,
      None;

      // $FF: synthetic method
      private static BannerRenderMode[] $values() {
         return new BannerRenderMode[]{Everything, Pillar, None};
      }
   }
}
