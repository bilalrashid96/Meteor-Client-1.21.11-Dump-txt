package meteordevelopment.meteorclient.systems.modules.combat;

import com.google.common.util.concurrent.AtomicDouble;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IBox;
import meteordevelopment.meteorclient.mixininterface.IRaycastContext;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.ModuleListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.DamageUtils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.Target;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2824;
import net.minecraft.class_2828;
import net.minecraft.class_2868;
import net.minecraft.class_2879;
import net.minecraft.class_2885;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_9274;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import org.joml.Vector3d;

public class CrystalAura extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgSwitch;
   private final SettingGroup sgPlace;
   private final SettingGroup sgFacePlace;
   private final SettingGroup sgBreak;
   private final SettingGroup sgPause;
   private final SettingGroup sgRender;
   private final Setting<Double> targetRange;
   private final Setting<Boolean> predictMovement;
   private final Setting<Double> minDamage;
   private final Setting<Double> maxDamage;
   private final Setting<Boolean> antiSuicide;
   private final Setting<Boolean> ignoreNakeds;
   private final Setting<Boolean> rotate;
   private final Setting<YawStepMode> yawStepMode;
   private final Setting<Double> yawSteps;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<AutoSwitchMode> autoSwitch;
   private final Setting<Integer> switchDelay;
   private final Setting<Boolean> noGapSwitch;
   private final Setting<Boolean> noBowSwitch;
   private final Setting<Boolean> antiWeakness;
   private final Setting<Boolean> doPlace;
   public final Setting<Integer> placeDelay;
   private final Setting<Double> placeRange;
   private final Setting<Double> placeWallsRange;
   private final Setting<Boolean> placement112;
   private final Setting<SupportMode> support;
   private final Setting<Integer> supportDelay;
   private final Setting<Boolean> facePlace;
   private final Setting<Double> facePlaceHealth;
   private final Setting<Double> facePlaceDurability;
   private final Setting<Boolean> facePlaceArmor;
   private final Setting<Keybind> forceFacePlace;
   private final Setting<Boolean> doBreak;
   private final Setting<Integer> breakDelay;
   private final Setting<Boolean> smartDelay;
   private final Setting<Double> breakRange;
   private final Setting<Double> breakWallsRange;
   private final Setting<Boolean> onlyBreakOwn;
   private final Setting<Integer> breakAttempts;
   private final Setting<Integer> ticksExisted;
   private final Setting<Integer> attackFrequency;
   private final Setting<Boolean> fastBreak;
   public final Setting<PauseMode> pauseOnUse;
   public final Setting<PauseMode> pauseOnMine;
   private final Setting<Boolean> pauseOnLag;
   public final Setting<List<Module>> pauseModules;
   public final Setting<Double> pauseHealth;
   public final Setting<SwingMode> swingMode;
   private final Setting<RenderMode> renderMode;
   private final Setting<Boolean> renderPlace;
   private final Setting<Integer> placeRenderTime;
   private final Setting<Boolean> renderBreak;
   private final Setting<Integer> breakRenderTime;
   private final Setting<Integer> smoothness;
   private final Setting<Double> height;
   private final Setting<Integer> renderTime;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<Boolean> renderDamageText;
   private final Setting<SettingColor> damageColor;
   private final Setting<Double> damageTextScale;
   private class_1792 mainItem;
   private class_1792 offItem;
   private int breakTimer;
   private int placeTimer;
   private int switchTimer;
   private int ticksPassed;
   private final List<class_1309> targets;
   private final class_243 vec3d;
   private final class_243 playerEyePos;
   private final Vector3d vec3;
   private final class_2338.class_2339 blockPos;
   private final class_238 box;
   private final class_243 vec3dRayTraceEnd;
   private class_3959 raycastContext;
   private final IntSet placedCrystals;
   private boolean placing;
   private int placingTimer;
   public int kaTimer;
   private final class_2338.class_2339 placingCrystalBlockPos;
   private final IntSet removed;
   private final Int2IntMap attemptedBreaks;
   private final Int2IntMap waitingToExplode;
   private int attacks;
   private double serverYaw;
   private class_1309 bestTarget;
   private double bestTargetDamage;
   private int bestTargetTimer;
   private boolean didRotateThisTick;
   private boolean isLastRotationPos;
   private final class_243 lastRotationPos;
   private double lastYaw;
   private double lastPitch;
   private int lastRotationTimer;
   private int placeRenderTimer;
   private int breakRenderTimer;
   private final class_2338.class_2339 placeRenderPos;
   private final class_2338.class_2339 breakRenderPos;
   private class_238 renderBoxOne;
   private class_238 renderBoxTwo;
   private double renderDamage;

   public CrystalAura() {
      super(Categories.Combat, "crystal-aura", "Automatically places and attacks crystals.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgSwitch = this.settings.createGroup("Switch");
      this.sgPlace = this.settings.createGroup("Place");
      this.sgFacePlace = this.settings.createGroup("Face Place");
      this.sgBreak = this.settings.createGroup("Break");
      this.sgPause = this.settings.createGroup("Pause");
      this.sgRender = this.settings.createGroup("Render");
      this.targetRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("target-range")).description("Range in which to target players.")).defaultValue((double)10.0F).min((double)0.0F).sliderMax((double)16.0F).build());
      this.predictMovement = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("predict-movement")).description("Predicts target movement.")).defaultValue(false)).build());
      this.minDamage = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("min-damage")).description("Minimum damage the crystal needs to deal to your target.")).defaultValue((double)6.0F).min((double)0.0F).build());
      this.maxDamage = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("max-damage")).description("Maximum damage crystals can deal to yourself.")).defaultValue((double)6.0F).range((double)0.0F, (double)36.0F).sliderMax((double)36.0F).build());
      this.antiSuicide = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-suicide")).description("Will not place and break crystals if they will kill you.")).defaultValue(true)).build());
      this.ignoreNakeds = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-nakeds")).description("Ignore players with no items.")).defaultValue(false)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Rotates server-side towards the crystals being hit/placed.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("yaw-steps-mode")).description("When to run the yaw steps check.")).defaultValue(CrystalAura.YawStepMode.Break);
      Setting var10003 = this.rotate;
      Objects.requireNonNull(var10003);
      this.yawStepMode = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      DoubleSetting.Builder var5 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("yaw-steps")).description("Maximum number of degrees its allowed to rotate in one tick.")).defaultValue((double)180.0F).range((double)1.0F, (double)180.0F);
      var10003 = this.rotate;
      Objects.requireNonNull(var10003);
      this.yawSteps = var10001.add(((DoubleSetting.Builder)var5.visible(var10003::get)).build());
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Entities to attack.")).onlyAttackable().defaultValue(class_1299.field_6097, class_1299.field_38095, class_1299.field_6119).build());
      this.autoSwitch = this.sgSwitch.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("auto-switch")).description("Switches to crystals in your hotbar once a target is found.")).defaultValue(CrystalAura.AutoSwitchMode.Normal)).build());
      this.switchDelay = this.sgSwitch.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("switch-delay")).description("The delay in ticks to wait to break a crystal after switching hotbar slot.")).defaultValue(0)).min(0).build());
      this.noGapSwitch = this.sgSwitch.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("no-gap-switch")).description("Won't auto switch if you're holding a gapple.")).defaultValue(true)).visible(() -> this.autoSwitch.get() == CrystalAura.AutoSwitchMode.Normal)).build());
      this.noBowSwitch = this.sgSwitch.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("no-bow-switch")).description("Won't auto switch if you're holding a bow.")).defaultValue(true)).build());
      this.antiWeakness = this.sgSwitch.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-weakness")).description("Switches to tools with so you can break crystals with the weakness effect.")).defaultValue(true)).build());
      this.doPlace = this.sgPlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("place")).description("If the CA should place crystals.")).defaultValue(true)).build());
      this.placeDelay = this.sgPlace.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-delay")).description("The delay in ticks to wait to place a crystal after it's exploded.")).defaultValue(0)).min(0).sliderMax(20).build());
      this.placeRange = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-range")).description("Range in which to place crystals.")).defaultValue((double)4.5F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.placeWallsRange = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("walls-range")).description("Range in which to place crystals when behind blocks.")).defaultValue((double)4.5F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.placement112 = this.sgPlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("1.12-placement")).description("Uses 1.12 crystal placement.")).defaultValue(false)).build());
      this.support = this.sgPlace.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("support")).description("Places a support block in air if no other position have been found.")).defaultValue(CrystalAura.SupportMode.Disabled)).build());
      this.supportDelay = this.sgPlace.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("support-delay")).description("Delay in ticks after placing support block.")).defaultValue(1)).min(0).visible(() -> this.support.get() != CrystalAura.SupportMode.Disabled)).build());
      this.facePlace = this.sgFacePlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("face-place")).description("Will face-place when target is below a certain health or armor durability threshold.")).defaultValue(true)).build());
      var10001 = this.sgFacePlace;
      var5 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("face-place-health")).description("The health the target has to be at to start face placing.")).defaultValue((double)8.0F).min((double)1.0F).sliderMin((double)1.0F).sliderMax((double)36.0F);
      var10003 = this.facePlace;
      Objects.requireNonNull(var10003);
      this.facePlaceHealth = var10001.add(((DoubleSetting.Builder)var5.visible(var10003::get)).build());
      var10001 = this.sgFacePlace;
      var5 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("face-place-durability")).description("The durability threshold percentage to be able to face-place.")).defaultValue((double)2.0F).min((double)1.0F).sliderMin((double)1.0F).sliderMax((double)100.0F);
      var10003 = this.facePlace;
      Objects.requireNonNull(var10003);
      this.facePlaceDurability = var10001.add(((DoubleSetting.Builder)var5.visible(var10003::get)).build());
      var10001 = this.sgFacePlace;
      BoolSetting.Builder var8 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("face-place-missing-armor")).description("Automatically starts face placing when a target misses a piece of armor.")).defaultValue(false);
      var10003 = this.facePlace;
      Objects.requireNonNull(var10003);
      this.facePlaceArmor = var10001.add(((BoolSetting.Builder)var8.visible(var10003::get)).build());
      this.forceFacePlace = this.sgFacePlace.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("force-face-place")).description("Starts face place when this button is pressed.")).defaultValue(Keybind.none())).build());
      this.doBreak = this.sgBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("break")).description("If the CA should break crystals.")).defaultValue(true)).build());
      this.breakDelay = this.sgBreak.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("break-delay")).description("The delay in ticks to wait to break a crystal after it's placed.")).defaultValue(0)).min(0).sliderMax(20).build());
      this.smartDelay = this.sgBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("smart-delay")).description("Only breaks crystals when the target can receive damage.")).defaultValue(false)).build());
      this.breakRange = this.sgBreak.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("break-range")).description("Range in which to break crystals.")).defaultValue((double)4.5F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.breakWallsRange = this.sgBreak.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("walls-range")).description("Range in which to break crystals when behind blocks.")).defaultValue((double)4.5F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.onlyBreakOwn = this.sgBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-own")).description("Only breaks own crystals.")).defaultValue(false)).build());
      this.breakAttempts = this.sgBreak.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("break-attempts")).description("How many times to hit a crystal before stopping to target it.")).defaultValue(2)).sliderMin(1).sliderMax(5).build());
      this.ticksExisted = this.sgBreak.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("ticks-existed")).description("Amount of ticks a crystal needs to have lived for it to be attacked by CrystalAura.")).defaultValue(0)).min(0).build());
      this.attackFrequency = this.sgBreak.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("attack-frequency")).description("Maximum hits to do per second.")).defaultValue(25)).min(1).sliderRange(1, 30).build());
      this.fastBreak = this.sgBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fast-break")).description("Ignores break delay and tries to break the crystal as soon as it's spawned in the world.")).defaultValue(true)).build());
      this.pauseOnUse = this.sgPause.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("pause-on-use")).description("Which processes should be paused while using an item.")).defaultValue(CrystalAura.PauseMode.Place)).build());
      this.pauseOnMine = this.sgPause.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("pause-on-mine")).description("Which processes should be paused while mining a block.")).defaultValue(CrystalAura.PauseMode.None)).build());
      this.pauseOnLag = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-lag")).description("Whether to pause if the server is not responding.")).defaultValue(true)).build());
      this.pauseModules = this.sgPause.add(((ModuleListSetting.Builder)((ModuleListSetting.Builder)(new ModuleListSetting.Builder()).name("pause-modules")).description("Pauses while any of the selected modules are active.")).defaultValue(BedAura.class).build());
      this.pauseHealth = this.sgPause.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("pause-health")).description("Pauses when you go below a certain health.")).defaultValue((double)5.0F).range((double)0.0F, (double)36.0F).sliderRange((double)0.0F, (double)36.0F).build());
      this.swingMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("swing-mode")).description("How to swing when placing.")).defaultValue(CrystalAura.SwingMode.Both)).build());
      this.renderMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("render-mode")).description("The mode to render in.")).defaultValue(CrystalAura.RenderMode.Normal)).build());
      this.renderPlace = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-place")).description("Renders a block overlay over the block the crystals are being placed on.")).defaultValue(true)).visible(() -> this.renderMode.get() == CrystalAura.RenderMode.Normal)).build());
      this.placeRenderTime = this.sgRender.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-time")).description("How long to render placements.")).defaultValue(10)).min(0).sliderMax(20).visible(() -> this.renderMode.get() == CrystalAura.RenderMode.Normal && (Boolean)this.renderPlace.get())).build());
      this.renderBreak = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-break")).description("Renders a block overlay over the block the crystals are broken on.")).defaultValue(false)).visible(() -> this.renderMode.get() == CrystalAura.RenderMode.Normal)).build());
      this.breakRenderTime = this.sgRender.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("break-time")).description("How long to render breaking for.")).defaultValue(13)).min(0).sliderMax(20).visible(() -> this.renderMode.get() == CrystalAura.RenderMode.Normal && (Boolean)this.renderBreak.get())).build());
      this.smoothness = this.sgRender.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("smoothness")).description("How smoothly the render should move around.")).defaultValue(10)).min(0).sliderMax(20).visible(() -> this.renderMode.get() == CrystalAura.RenderMode.Smooth)).build());
      this.height = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("height")).description("How tall the gradient should be.")).defaultValue(0.7).min((double)0.0F).sliderMax((double)1.0F).visible(() -> this.renderMode.get() == CrystalAura.RenderMode.Gradient)).build());
      this.renderTime = this.sgRender.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("render-time")).description("How long to render placements.")).defaultValue(10)).min(0).sliderMax(20).visible(() -> this.renderMode.get() == CrystalAura.RenderMode.Smooth || this.renderMode.get() == CrystalAura.RenderMode.Fading)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).visible(() -> this.renderMode.get() != CrystalAura.RenderMode.None)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the block overlay.")).defaultValue(new SettingColor(255, 255, 255, 45)).visible(() -> ((ShapeMode)this.shapeMode.get()).sides() && this.renderMode.get() != CrystalAura.RenderMode.None)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the block overlay.")).defaultValue(new SettingColor(255, 255, 255)).visible(() -> ((ShapeMode)this.shapeMode.get()).lines() && this.renderMode.get() != CrystalAura.RenderMode.None)).build());
      this.renderDamageText = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("damage")).description("Renders crystal damage text in the block overlay.")).defaultValue(true)).visible(() -> this.renderMode.get() != CrystalAura.RenderMode.None)).build());
      this.damageColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("damage-color")).description("The color of the damage text.")).defaultValue(new SettingColor(255, 255, 255)).visible(() -> this.renderMode.get() != CrystalAura.RenderMode.None && (Boolean)this.renderDamageText.get())).build());
      this.damageTextScale = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("damage-scale")).description("How big the damage text should be.")).defaultValue((double)1.25F).min((double)1.0F).sliderMax((double)4.0F).visible(() -> this.renderMode.get() != CrystalAura.RenderMode.None && (Boolean)this.renderDamageText.get())).build());
      this.targets = new ArrayList();
      this.vec3d = new class_243((double)0.0F, (double)0.0F, (double)0.0F);
      this.playerEyePos = new class_243((double)0.0F, (double)0.0F, (double)0.0F);
      this.vec3 = new Vector3d();
      this.blockPos = new class_2338.class_2339();
      this.box = new class_238((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F);
      this.vec3dRayTraceEnd = new class_243((double)0.0F, (double)0.0F, (double)0.0F);
      this.placedCrystals = new IntOpenHashSet();
      this.placingCrystalBlockPos = new class_2338.class_2339();
      this.removed = new IntOpenHashSet();
      this.attemptedBreaks = new Int2IntOpenHashMap();
      this.waitingToExplode = new Int2IntOpenHashMap();
      this.lastRotationPos = new class_243((double)0.0F, (double)0.0F, (double)0.0F);
      this.placeRenderPos = new class_2338.class_2339();
      this.breakRenderPos = new class_2338.class_2339();
   }

   public void onActivate() {
      this.breakTimer = 0;
      this.placeTimer = 0;
      this.ticksPassed = 0;
      this.raycastContext = new class_3959(new class_243((double)0.0F, (double)0.0F, (double)0.0F), new class_243((double)0.0F, (double)0.0F, (double)0.0F), class_3960.field_17558, class_242.field_1348, this.mc.field_1724);
      this.placing = false;
      this.placingTimer = 0;
      this.kaTimer = 0;
      this.attacks = 0;
      this.serverYaw = (double)this.mc.field_1724.method_36454();
      this.bestTargetDamage = (double)0.0F;
      this.bestTargetTimer = 0;
      this.lastRotationTimer = this.getLastRotationStopDelay();
      this.placeRenderTimer = 0;
      this.breakRenderTimer = 0;
   }

   public void onDeactivate() {
      this.targets.clear();
      this.placedCrystals.clear();
      this.attemptedBreaks.clear();
      this.waitingToExplode.clear();
      this.removed.clear();
      this.bestTarget = null;
   }

   private int getLastRotationStopDelay() {
      return Math.max(10, (Integer)this.placeDelay.get() / 2 + (Integer)this.breakDelay.get() / 2 + 10);
   }

   @EventHandler(
      priority = 100
   )
   private void onPreTick(TickEvent.Pre event) {
      this.didRotateThisTick = false;
      ++this.lastRotationTimer;
      if (this.placing) {
         if (this.placingTimer > 0) {
            --this.placingTimer;
         } else {
            this.placing = false;
         }
      }

      if (this.kaTimer > 0) {
         --this.kaTimer;
      }

      if (this.ticksPassed < 20) {
         ++this.ticksPassed;
      } else {
         this.ticksPassed = 0;
         this.attacks = 0;
      }

      if (this.bestTargetTimer > 0) {
         --this.bestTargetTimer;
      }

      this.bestTargetDamage = (double)0.0F;
      if (this.breakTimer > 0) {
         --this.breakTimer;
      }

      if (this.placeTimer > 0) {
         --this.placeTimer;
      }

      if (this.switchTimer > 0) {
         --this.switchTimer;
      }

      if (this.placeRenderTimer > 0) {
         --this.placeRenderTimer;
      }

      if (this.breakRenderTimer > 0) {
         --this.breakRenderTimer;
      }

      this.mainItem = this.mc.field_1724.method_6047().method_7909();
      this.offItem = this.mc.field_1724.method_6079().method_7909();
      IntIterator it = this.waitingToExplode.keySet().iterator();

      while(it.hasNext()) {
         int id = it.nextInt();
         int ticks = this.waitingToExplode.get(id);
         if (ticks > 3) {
            it.remove();
            this.removed.remove(id);
         } else {
            this.waitingToExplode.put(id, ticks + 1);
         }
      }

      ((IVec3d)this.playerEyePos).meteor$set(this.mc.field_1724.method_73189().field_1352, this.mc.field_1724.method_73189().field_1351 + (double)this.mc.field_1724.method_18381(this.mc.field_1724.method_18376()), this.mc.field_1724.method_73189().field_1350);
      this.findTargets();
      if (!this.targets.isEmpty()) {
         if (!this.didRotateThisTick) {
            this.doBreak();
         }

         if (!this.didRotateThisTick) {
            this.doPlace();
         }
      }

   }

   @EventHandler(
      priority = -866
   )
   private void onPreTickLast(TickEvent.Pre event) {
      if ((Boolean)this.rotate.get() && this.lastRotationTimer < this.getLastRotationStopDelay() && !this.didRotateThisTick) {
         Rotations.rotate(this.isLastRotationPos ? Rotations.getYaw(this.lastRotationPos) : this.lastYaw, this.isLastRotationPos ? Rotations.getPitch(this.lastRotationPos) : this.lastPitch, -100, (Runnable)null);
      }

   }

   @EventHandler
   private void onEntityAdded(EntityAddedEvent event) {
      if (event.entity instanceof class_1511) {
         if (this.placing && event.entity.method_24515().equals(this.placingCrystalBlockPos)) {
            this.placing = false;
            this.placingTimer = 0;
            this.placedCrystals.add(event.entity.method_5628());
         }

         if ((Boolean)this.fastBreak.get() && !this.didRotateThisTick && this.attacks < (Integer)this.attackFrequency.get()) {
            float damage = this.getBreakDamage(event.entity, true);
            if ((double)damage > (Double)this.minDamage.get()) {
               this.doBreak(event.entity);
            }
         }

      }
   }

   @EventHandler
   private void onEntityRemoved(EntityRemovedEvent event) {
      if (event.entity instanceof class_1511) {
         this.placedCrystals.remove(event.entity.method_5628());
         this.removed.remove(event.entity.method_5628());
         this.waitingToExplode.remove(event.entity.method_5628());
      }

   }

   private void setRotation(boolean isPos, class_243 pos, double yaw, double pitch) {
      this.didRotateThisTick = true;
      this.isLastRotationPos = isPos;
      if (isPos) {
         ((IVec3d)this.lastRotationPos).meteor$set(pos.field_1352, pos.field_1351, pos.field_1350);
      } else {
         this.lastYaw = yaw;
         this.lastPitch = pitch;
      }

      this.lastRotationTimer = 0;
   }

   private void doBreak() {
      if ((Boolean)this.doBreak.get() && this.breakTimer <= 0 && this.switchTimer <= 0 && this.attacks < (Integer)this.attackFrequency.get()) {
         if (!this.shouldPause(CrystalAura.PauseMode.Break)) {
            float bestDamage = 0.0F;
            class_1297 crystal = null;

            for(class_1297 entity : this.mc.field_1687.method_18112()) {
               float damage = this.getBreakDamage(entity, true);
               if (damage > bestDamage) {
                  bestDamage = damage;
                  crystal = entity;
               }
            }

            if (crystal != null) {
               this.doBreak(crystal);
            }

         }
      }
   }

   private float getBreakDamage(class_1297 entity, boolean checkCrystalAge) {
      if (!(entity instanceof class_1511)) {
         return 0.0F;
      } else if ((Boolean)this.onlyBreakOwn.get() && !this.placedCrystals.contains(entity.method_5628())) {
         return 0.0F;
      } else if (this.removed.contains(entity.method_5628())) {
         return 0.0F;
      } else if (this.attemptedBreaks.get(entity.method_5628()) > (Integer)this.breakAttempts.get()) {
         return 0.0F;
      } else if (checkCrystalAge && entity.field_6012 < (Integer)this.ticksExisted.get()) {
         return 0.0F;
      } else if (this.isOutOfRange(entity.method_73189(), entity.method_24515(), false)) {
         return 0.0F;
      } else {
         this.blockPos.method_10101(entity.method_24515()).method_10100(0, -1, 0);
         float selfDamage = DamageUtils.crystalDamage(this.mc.field_1724, entity.method_73189(), (Boolean)this.predictMovement.get(), this.blockPos);
         if (!((double)selfDamage > (Double)this.maxDamage.get()) && (!(Boolean)this.antiSuicide.get() || !(selfDamage >= EntityUtils.getTotalHealth(this.mc.field_1724)))) {
            float damage = this.getDamageToTargets(entity.method_73189(), this.blockPos, true, false);
            boolean shouldFacePlace = this.shouldFacePlace();
            double minimumDamage = shouldFacePlace ? Math.min((Double)this.minDamage.get(), (double)1.5F) : (Double)this.minDamage.get();
            return (double)damage < minimumDamage ? 0.0F : damage;
         } else {
            return 0.0F;
         }
      }
   }

   private void doBreak(class_1297 crystal) {
      if ((Boolean)this.antiWeakness.get()) {
         class_1293 weakness = this.mc.field_1724.method_6112(class_1294.field_5911);
         class_1293 strength = this.mc.field_1724.method_6112(class_1294.field_5910);
         if (weakness != null && (strength == null || strength.method_5578() <= weakness.method_5578()) && !this.isValidWeaknessItem(this.mc.field_1724.method_6047(), crystal)) {
            if (!InvUtils.swap(InvUtils.findInHotbar((Predicate)((stack) -> this.isValidWeaknessItem(stack, crystal))).slot(), false)) {
               return;
            }

            this.switchTimer = 1;
            return;
         }
      }

      boolean attacked = true;
      if ((Boolean)this.rotate.get()) {
         double yaw = Rotations.getYaw(crystal);
         double pitch = Rotations.getPitch(crystal, Target.Feet);
         if (this.doYawSteps(yaw, pitch)) {
            this.setRotation(true, crystal.method_73189(), (double)0.0F, (double)0.0F);
            Rotations.rotate(yaw, pitch, 50, () -> this.attackCrystal(crystal));
            this.breakTimer = (Integer)this.breakDelay.get();
         } else {
            attacked = false;
         }
      } else {
         this.attackCrystal(crystal);
         this.breakTimer = (Integer)this.breakDelay.get();
      }

      if (attacked) {
         this.removed.add(crystal.method_5628());
         this.attemptedBreaks.put(crystal.method_5628(), this.attemptedBreaks.get(crystal.method_5628()) + 1);
         this.waitingToExplode.put(crystal.method_5628(), 0);
         this.breakRenderPos.method_10101(crystal.method_24515().method_10074());
         this.breakRenderTimer = (Integer)this.breakRenderTime.get();
      }

   }

   private boolean isValidWeaknessItem(class_1799 itemStack, class_1297 crystal) {
      return DamageUtils.getAttackDamage(this.mc.field_1724, crystal, itemStack) > 0.0F;
   }

   private void attackCrystal(class_1297 entity) {
      this.mc.field_1724.field_3944.method_52787(class_2824.method_34206(entity, this.mc.field_1724.method_5715()));
      class_1268 hand = InvUtils.findInHotbar(class_1802.field_8301).getHand();
      if (hand == null) {
         hand = class_1268.field_5808;
      }

      if (((SwingMode)this.swingMode.get()).client()) {
         this.mc.field_1724.method_6104(hand);
      }

      if (((SwingMode)this.swingMode.get()).packet()) {
         this.mc.method_1562().method_52787(new class_2879(hand));
      }

      ++this.attacks;
   }

   @EventHandler
   private void onPacketSend(PacketEvent.Send event) {
      if (event.packet instanceof class_2868) {
         this.switchTimer = (Integer)this.switchDelay.get();
      }

   }

   private void doPlace() {
      if ((Boolean)this.doPlace.get() && this.placeTimer <= 0) {
         if (!this.shouldPause(CrystalAura.PauseMode.Place)) {
            if (InvUtils.testInHotbar(class_1802.field_8301)) {
               if (this.autoSwitch.get() != CrystalAura.AutoSwitchMode.None) {
                  if ((Boolean)this.noGapSwitch.get() && this.autoSwitch.get() == CrystalAura.AutoSwitchMode.Normal && this.offItem != class_1802.field_8301 && (this.mainItem == class_1802.field_8367 || this.offItem == class_1802.field_8367 || this.mainItem == class_1802.field_8463 || this.offItem == class_1802.field_8463)) {
                     return;
                  }

                  if ((Boolean)this.noBowSwitch.get() && (this.mainItem == class_1802.field_8102 || this.offItem == class_1802.field_8102)) {
                     return;
                  }
               } else if (this.mainItem != class_1802.field_8301 && this.offItem != class_1802.field_8301) {
                  return;
               }

               for(class_1297 entity : this.mc.field_1687.method_18112()) {
                  if (this.getBreakDamage(entity, false) > 0.0F) {
                     return;
                  }
               }

               AtomicDouble bestDamage = new AtomicDouble((double)0.0F);
               AtomicReference<class_2338.class_2339> bestBlockPos = new AtomicReference(new class_2338.class_2339());
               AtomicBoolean isSupport = new AtomicBoolean(this.support.get() != CrystalAura.SupportMode.Disabled);
               BlockIterator.register((int)Math.ceil((Double)this.placeRange.get()), (int)Math.ceil((Double)this.placeRange.get()), (bp, blockState) -> {
                  boolean hasBlock = blockState.method_27852(class_2246.field_9987) || blockState.method_27852(class_2246.field_10540);
                  if (hasBlock || isSupport.get() && blockState.method_45474()) {
                     this.blockPos.method_10103(bp.method_10263(), bp.method_10264() + 1, bp.method_10260());
                     if (this.mc.field_1687.method_8320(this.blockPos).method_26215()) {
                        if ((Boolean)this.placement112.get()) {
                           this.blockPos.method_10100(0, 1, 0);
                           if (!this.mc.field_1687.method_8320(this.blockPos).method_26215()) {
                              return;
                           }
                        }

                        ((IVec3d)this.vec3d).meteor$set((double)bp.method_10263() + (double)0.5F, (double)(bp.method_10264() + 1), (double)bp.method_10260() + (double)0.5F);
                        this.blockPos.method_10101(bp).method_10100(0, 1, 0);
                        if (!this.isOutOfRange(this.vec3d, this.blockPos, true)) {
                           float selfDamage = DamageUtils.crystalDamage(this.mc.field_1724, this.vec3d, (Boolean)this.predictMovement.get(), bp);
                           if (!((double)selfDamage > (Double)this.maxDamage.get()) && (!(Boolean)this.antiSuicide.get() || !(selfDamage >= EntityUtils.getTotalHealth(this.mc.field_1724)))) {
                              float damage = this.getDamageToTargets(this.vec3d, bp, false, !hasBlock && this.support.get() == CrystalAura.SupportMode.Fast);
                              boolean shouldFacePlace = this.shouldFacePlace();
                              double minimumDamage = Math.min((Double)this.minDamage.get(), shouldFacePlace ? (double)1.5F : (Double)this.minDamage.get());
                              if (!((double)damage < minimumDamage)) {
                                 double x = (double)bp.method_10263();
                                 double y = (double)(bp.method_10264() + 1);
                                 double z = (double)bp.method_10260();
                                 ((IBox)this.box).meteor$set(x, y, z, x + (double)1.0F, y + (double)((Boolean)this.placement112.get() ? 1 : 2), z + (double)1.0F);
                                 if (!this.intersectsWithEntities(this.box)) {
                                    if ((double)damage > bestDamage.get() || isSupport.get() && hasBlock) {
                                       bestDamage.set((double)damage);
                                       ((class_2338.class_2339)bestBlockPos.get()).method_10101(bp);
                                    }

                                    if (hasBlock) {
                                       isSupport.set(false);
                                    }

                                 }
                              }
                           }
                        }
                     }
                  }
               });
               BlockIterator.after(() -> {
                  if (bestDamage.get() != (double)0.0F) {
                     class_3965 result = this.getPlaceInfo((class_2338)bestBlockPos.get());
                     ((IVec3d)this.vec3d).meteor$set((double)result.method_17777().method_10263() + (double)0.5F + (double)result.method_17780().method_62675().method_10263() * (double)1.0F / (double)2.0F, (double)result.method_17777().method_10264() + (double)0.5F + (double)result.method_17780().method_62675().method_10264() * (double)1.0F / (double)2.0F, (double)result.method_17777().method_10260() + (double)0.5F + (double)result.method_17780().method_62675().method_10260() * (double)1.0F / (double)2.0F);
                     if ((Boolean)this.rotate.get()) {
                        double yaw = Rotations.getYaw(this.vec3d);
                        double pitch = Rotations.getPitch(this.vec3d);
                        if (this.yawStepMode.get() == CrystalAura.YawStepMode.Break || this.doYawSteps(yaw, pitch)) {
                           this.setRotation(true, this.vec3d, (double)0.0F, (double)0.0F);
                           Rotations.rotate(yaw, pitch, 50, () -> this.placeCrystal(result, bestDamage.get(), isSupport.get() ? (class_2338)bestBlockPos.get() : null));
                           this.placeTimer += (Integer)this.placeDelay.get();
                        }
                     } else {
                        this.placeCrystal(result, bestDamage.get(), isSupport.get() ? (class_2338)bestBlockPos.get() : null);
                        this.placeTimer += (Integer)this.placeDelay.get();
                     }

                  }
               });
            }
         }
      }
   }

   private class_3965 getPlaceInfo(class_2338 blockPos) {
      ((IVec3d)this.vec3d).meteor$set(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318() + (double)this.mc.field_1724.method_18381(this.mc.field_1724.method_18376()), this.mc.field_1724.method_23321());

      for(class_2350 side : class_2350.values()) {
         ((IVec3d)this.vec3dRayTraceEnd).meteor$set((double)blockPos.method_10263() + (double)0.5F + (double)side.method_62675().method_10263() * (double)0.5F, (double)blockPos.method_10264() + (double)0.5F + (double)side.method_62675().method_10264() * (double)0.5F, (double)blockPos.method_10260() + (double)0.5F + (double)side.method_62675().method_10260() * (double)0.5F);
         ((IRaycastContext)this.raycastContext).meteor$set(this.vec3d, this.vec3dRayTraceEnd, class_3960.field_17558, class_242.field_1348, this.mc.field_1724);
         class_3965 result = this.mc.field_1687.method_17742(this.raycastContext);
         if (result != null && result.method_17783() == class_240.field_1332 && result.method_17777().equals(blockPos)) {
            return result;
         }
      }

      class_2350 side = (double)blockPos.method_10264() > this.vec3d.field_1351 ? class_2350.field_11033 : class_2350.field_11036;
      return new class_3965(this.vec3d, side, blockPos, false);
   }

   private void placeCrystal(class_3965 result, double damage, class_2338 supportBlock) {
      class_1792 targetItem = supportBlock == null ? class_1802.field_8301 : class_1802.field_8281;
      FindItemResult item = InvUtils.findInHotbar(targetItem);
      if (item.found()) {
         int prevSlot = this.mc.field_1724.method_31548().method_67532();
         if (this.autoSwitch.get() != CrystalAura.AutoSwitchMode.None && !item.isOffhand()) {
            InvUtils.swap(item.slot(), false);
         }

         class_1268 hand = item.getHand();
         if (hand != null) {
            if (supportBlock == null) {
               this.mc.field_1761.method_41931(this.mc.field_1687, (sequence) -> new class_2885(hand, result, sequence));
               if (((SwingMode)this.swingMode.get()).client()) {
                  this.mc.field_1724.method_6104(hand);
               }

               if (((SwingMode)this.swingMode.get()).packet()) {
                  this.mc.method_1562().method_52787(new class_2879(hand));
               }

               this.placing = true;
               this.placingTimer = 4;
               this.kaTimer = 8;
               this.placingCrystalBlockPos.method_10101(result.method_17777()).method_10100(0, 1, 0);
               this.placeRenderPos.method_10101(result.method_17777());
               this.renderDamage = damage;
               if (this.renderMode.get() == CrystalAura.RenderMode.Normal) {
                  this.placeRenderTimer = (Integer)this.placeRenderTime.get();
               } else {
                  this.placeRenderTimer = (Integer)this.renderTime.get();
                  if (this.renderMode.get() == CrystalAura.RenderMode.Fading) {
                     RenderUtils.renderTickingBlock(this.placeRenderPos, this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), 0, (Integer)this.renderTime.get(), true, false);
                  }
               }
            } else {
               BlockUtils.place(supportBlock, item, false, 0, ((SwingMode)this.swingMode.get()).client(), true, false);
               this.placeTimer += (Integer)this.supportDelay.get();
               if ((Integer)this.supportDelay.get() == 0) {
                  this.placeCrystal(result, damage, (class_2338)null);
               }
            }

            if (this.autoSwitch.get() == CrystalAura.AutoSwitchMode.Silent) {
               InvUtils.swap(prevSlot, false);
            }

         }
      }
   }

   @EventHandler
   private void onPacketSent(PacketEvent.Sent event) {
      if (event.packet instanceof class_2828) {
         this.serverYaw = (double)((class_2828)event.packet).method_12271((float)this.serverYaw);
      }

   }

   public boolean doYawSteps(double targetYaw, double targetPitch) {
      targetYaw = class_3532.method_15338(targetYaw) + (double)180.0F;
      double serverYaw = class_3532.method_15338(this.serverYaw) + (double)180.0F;
      if (distanceBetweenAngles(serverYaw, targetYaw) <= (Double)this.yawSteps.get()) {
         return true;
      } else {
         double delta = Math.abs(targetYaw - serverYaw);
         double yaw = this.serverYaw;
         if (serverYaw < targetYaw) {
            if (delta < (double)180.0F) {
               yaw += (Double)this.yawSteps.get();
            } else {
               yaw -= (Double)this.yawSteps.get();
            }
         } else if (delta < (double)180.0F) {
            yaw -= (Double)this.yawSteps.get();
         } else {
            yaw += (Double)this.yawSteps.get();
         }

         this.setRotation(false, (class_243)null, yaw, targetPitch);
         Rotations.rotate(yaw, targetPitch, -100, (Runnable)null);
         return false;
      }
   }

   private static double distanceBetweenAngles(double alpha, double beta) {
      double phi = Math.abs(beta - alpha) % (double)360.0F;
      return phi > (double)180.0F ? (double)360.0F - phi : phi;
   }

   private boolean shouldFacePlace() {
      if (!(Boolean)this.facePlace.get()) {
         return false;
      } else if (((Keybind)this.forceFacePlace.get()).isPressed()) {
         return true;
      } else {
         for(class_1309 target : this.targets) {
            if ((double)EntityUtils.getTotalHealth(target) <= (Double)this.facePlaceHealth.get()) {
               return true;
            }

            for(class_1304 slot : class_9274.field_49224) {
               class_1799 itemStack = target.method_6118(slot);
               if (itemStack != null && !itemStack.method_7960()) {
                  if ((double)(itemStack.method_7936() - itemStack.method_7919()) / (double)itemStack.method_7936() * (double)100.0F <= (Double)this.facePlaceDurability.get()) {
                     return true;
                  }
               } else if ((Boolean)this.facePlaceArmor.get()) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean shouldPause(PauseMode process) {
      if ((this.mc.field_1724.method_6115() || this.mc.field_1690.field_1904.method_1434()) && ((PauseMode)this.pauseOnUse.get()).equals(process)) {
         return true;
      } else if ((Boolean)this.pauseOnLag.get() && TickRate.INSTANCE.getTimeSinceLastTick() >= 1.0F) {
         return true;
      } else {
         for(Module module : this.pauseModules.get()) {
            if (module.isActive()) {
               return true;
            }
         }

         if (((PauseMode)this.pauseOnMine.get()).equals(process) && this.mc.field_1761.method_2923()) {
            return true;
         } else {
            return (double)EntityUtils.getTotalHealth(this.mc.field_1724) <= (Double)this.pauseHealth.get();
         }
      }
   }

   private boolean isOutOfRange(class_243 vec3d, class_2338 blockPos, boolean place) {
      ((IRaycastContext)this.raycastContext).meteor$set(this.playerEyePos, vec3d, class_3960.field_17558, class_242.field_1348, this.mc.field_1724);
      class_3965 result = this.mc.field_1687.method_17742(this.raycastContext);
      if (result != null && result.method_17777().equals(blockPos)) {
         return !PlayerUtils.isWithin(vec3d, (Double)(place ? this.placeRange : this.breakRange).get());
      } else {
         return !PlayerUtils.isWithin(vec3d, (Double)(place ? this.placeWallsRange : this.breakWallsRange).get());
      }
   }

   private class_1309 getNearestTarget() {
      class_1309 nearestTarget = null;
      double nearestDistance = Double.MAX_VALUE;

      for(class_1309 target : this.targets) {
         double distance = PlayerUtils.squaredDistanceTo((class_1297)target);
         if (distance < nearestDistance) {
            nearestTarget = target;
            nearestDistance = distance;
         }
      }

      return nearestTarget;
   }

   private float getDamageToTargets(class_243 vec3d, class_2338 obsidianPos, boolean breaking, boolean fast) {
      float damage = 0.0F;
      if (fast) {
         class_1309 target = this.getNearestTarget();
         if (!(Boolean)this.smartDelay.get() || !breaking || target.field_6235 <= 0) {
            damage = DamageUtils.crystalDamage(target, vec3d, (Boolean)this.predictMovement.get(), obsidianPos);
         }
      } else {
         for(class_1309 target : this.targets) {
            if (!(Boolean)this.smartDelay.get() || !breaking || target.field_6235 <= 0) {
               float dmg = DamageUtils.crystalDamage(target, vec3d, (Boolean)this.predictMovement.get(), obsidianPos);
               if ((double)dmg > this.bestTargetDamage) {
                  this.bestTarget = target;
                  this.bestTargetDamage = (double)dmg;
                  this.bestTargetTimer = 10;
               }

               damage += dmg;
            }
         }
      }

      return damage;
   }

   public String getInfoString() {
      return this.bestTarget != null && this.bestTargetTimer > 0 ? EntityUtils.getName(this.bestTarget) : null;
   }

   private void findTargets() {
      this.targets.clear();

      for(class_1297 entity : this.mc.field_1687.method_18112()) {
         if (entity instanceof class_1309 livingEntity) {
            if (livingEntity instanceof class_1657 player) {
               if (player.method_31549().field_7477 || livingEntity == this.mc.field_1724 || !player.method_5805() || !Friends.get().shouldAttack(player) || (Boolean)this.ignoreNakeds.get() && player.method_6079().method_7960() && player.method_6047().method_7960() && player.method_6118(class_1304.field_6166).method_7960() && player.method_6118(class_1304.field_6172).method_7960() && player.method_6118(class_1304.field_6174).method_7960() && player.method_6118(class_1304.field_6169).method_7960()) {
                  continue;
               }
            }

            if (((Set)this.entities.get()).contains(livingEntity.method_5864()) && !(livingEntity.method_5858(this.mc.field_1724) > (Double)this.targetRange.get() * (Double)this.targetRange.get())) {
               this.targets.add(livingEntity);
            }
         }
      }

   }

   private boolean intersectsWithEntities(class_238 box) {
      return EntityUtils.intersectsWithEntity(box, (entity) -> !entity.method_7325() && !this.removed.contains(entity.method_5628()));
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (this.renderMode.get() != CrystalAura.RenderMode.None) {
         switch (((RenderMode)this.renderMode.get()).ordinal()) {
            case 0:
               if ((Boolean)this.renderPlace.get() && this.placeRenderTimer > 0) {
                  event.renderer.box((class_2338)this.placeRenderPos, this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), 0);
               }

               if ((Boolean)this.renderBreak.get() && this.breakRenderTimer > 0) {
                  event.renderer.box((class_2338)this.breakRenderPos, this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), 0);
               }
               break;
            case 1:
               if (this.placeRenderTimer <= 0) {
                  return;
               }

               if (this.renderBoxOne == null) {
                  this.renderBoxOne = new class_238(this.placeRenderPos);
               }

               if (this.renderBoxTwo == null) {
                  this.renderBoxTwo = new class_238(this.placeRenderPos);
               } else {
                  ((IBox)this.renderBoxTwo).meteor$set(this.placeRenderPos);
               }

               double offsetX = (this.renderBoxTwo.field_1323 - this.renderBoxOne.field_1323) / (double)(Integer)this.smoothness.get();
               double offsetY = (this.renderBoxTwo.field_1322 - this.renderBoxOne.field_1322) / (double)(Integer)this.smoothness.get();
               double offsetZ = (this.renderBoxTwo.field_1321 - this.renderBoxOne.field_1321) / (double)(Integer)this.smoothness.get();
               ((IBox)this.renderBoxOne).meteor$set(this.renderBoxOne.field_1323 + offsetX, this.renderBoxOne.field_1322 + offsetY, this.renderBoxOne.field_1321 + offsetZ, this.renderBoxOne.field_1320 + offsetX, this.renderBoxOne.field_1325 + offsetY, this.renderBoxOne.field_1324 + offsetZ);
               event.renderer.box((class_238)this.renderBoxOne, this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), 0);
            case 2:
            default:
               break;
            case 3:
               if (this.placeRenderTimer <= 0) {
                  return;
               }

               Color bottom = new Color(0, 0, 0, 0);
               int x = this.placeRenderPos.method_10263();
               int y = this.placeRenderPos.method_10264() + 1;
               int z = this.placeRenderPos.method_10260();
               if (((ShapeMode)this.shapeMode.get()).sides()) {
                  event.renderer.quadHorizontal((double)x, (double)y, (double)z, (double)(x + 1), (double)(z + 1), this.sideColor.get());
                  event.renderer.gradientQuadVertical((double)x, (double)y, (double)z, (double)(x + 1), (double)y - (Double)this.height.get(), (double)z, bottom, this.sideColor.get());
                  event.renderer.gradientQuadVertical((double)x, (double)y, (double)z, (double)x, (double)y - (Double)this.height.get(), (double)(z + 1), bottom, this.sideColor.get());
                  event.renderer.gradientQuadVertical((double)(x + 1), (double)y, (double)z, (double)(x + 1), (double)y - (Double)this.height.get(), (double)(z + 1), bottom, this.sideColor.get());
                  event.renderer.gradientQuadVertical((double)x, (double)y, (double)(z + 1), (double)(x + 1), (double)y - (Double)this.height.get(), (double)(z + 1), bottom, this.sideColor.get());
               }

               if (((ShapeMode)this.shapeMode.get()).lines()) {
                  event.renderer.line((double)x, (double)y, (double)z, (double)(x + 1), (double)y, (double)z, this.lineColor.get());
                  event.renderer.line((double)x, (double)y, (double)z, (double)x, (double)y, (double)(z + 1), this.lineColor.get());
                  event.renderer.line((double)(x + 1), (double)y, (double)z, (double)(x + 1), (double)y, (double)(z + 1), this.lineColor.get());
                  event.renderer.line((double)x, (double)y, (double)(z + 1), (double)(x + 1), (double)y, (double)(z + 1), this.lineColor.get());
                  event.renderer.line((double)x, (double)y, (double)z, (double)x, (double)y - (Double)this.height.get(), (double)z, this.lineColor.get(), bottom);
                  event.renderer.line((double)(x + 1), (double)y, (double)z, (double)(x + 1), (double)y - (Double)this.height.get(), (double)z, this.lineColor.get(), bottom);
                  event.renderer.line((double)x, (double)y, (double)(z + 1), (double)x, (double)y - (Double)this.height.get(), (double)(z + 1), this.lineColor.get(), bottom);
                  event.renderer.line((double)(x + 1), (double)y, (double)(z + 1), (double)(x + 1), (double)y - (Double)this.height.get(), (double)(z + 1), this.lineColor.get(), bottom);
               }
         }

      }
   }

   @EventHandler
   private void onRender2D(Render2DEvent event) {
      if (this.renderMode.get() != CrystalAura.RenderMode.None && (Boolean)this.renderDamageText.get()) {
         if (this.placeRenderTimer > 0 || this.breakRenderTimer > 0) {
            if (this.renderMode.get() == CrystalAura.RenderMode.Smooth) {
               if (this.renderBoxOne == null) {
                  return;
               }

               this.vec3.set(this.renderBoxOne.field_1323 + (double)0.5F, this.renderBoxOne.field_1322 + (double)0.5F, this.renderBoxOne.field_1321 + (double)0.5F);
            } else {
               this.vec3.set((double)this.placeRenderPos.method_10263() + (double)0.5F, (double)this.placeRenderPos.method_10264() + (double)0.5F, (double)this.placeRenderPos.method_10260() + (double)0.5F);
            }

            if (NametagUtils.to2D(this.vec3, (Double)this.damageTextScale.get())) {
               NametagUtils.begin(this.vec3);
               TextRenderer.get().begin((double)1.0F, false, true);
               String text = String.format("%.1f", this.renderDamage);
               double w = TextRenderer.get().getWidth(text) / (double)2.0F;
               TextRenderer.get().render(text, -w, (double)0.0F, this.damageColor.get(), true);
               TextRenderer.get().end();
               NametagUtils.end();
            }

         }
      }
   }

   public static enum YawStepMode {
      Break,
      All;

      // $FF: synthetic method
      private static YawStepMode[] $values() {
         return new YawStepMode[]{Break, All};
      }
   }

   public static enum AutoSwitchMode {
      Normal,
      Silent,
      None;

      // $FF: synthetic method
      private static AutoSwitchMode[] $values() {
         return new AutoSwitchMode[]{Normal, Silent, None};
      }
   }

   public static enum SupportMode {
      Disabled,
      Accurate,
      Fast;

      // $FF: synthetic method
      private static SupportMode[] $values() {
         return new SupportMode[]{Disabled, Accurate, Fast};
      }
   }

   public static enum PauseMode {
      Both,
      Place,
      Break,
      None;

      public boolean equals(PauseMode process) {
         return this == process || this == Both;
      }

      // $FF: synthetic method
      private static PauseMode[] $values() {
         return new PauseMode[]{Both, Place, Break, None};
      }
   }

   public static enum SwingMode {
      Both,
      Packet,
      Client,
      None;

      public boolean packet() {
         return this == Packet || this == Both;
      }

      public boolean client() {
         return this == Client || this == Both;
      }

      // $FF: synthetic method
      private static SwingMode[] $values() {
         return new SwingMode[]{Both, Packet, Client, None};
      }
   }

   public static enum RenderMode {
      Normal,
      Smooth,
      Fading,
      Gradient,
      None;

      // $FF: synthetic method
      private static RenderMode[] $values() {
         return new RenderMode[]{Normal, Smooth, Fading, Gradient, None};
      }
   }
}
