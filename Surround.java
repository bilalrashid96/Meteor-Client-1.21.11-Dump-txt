package meteordevelopment.meteorclient.systems.modules.combat;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.DirectionAccessor;
import meteordevelopment.meteorclient.mixin.WorldRendererAccessor;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ModuleListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.DamageUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2596;
import net.minecraft.class_2680;
import net.minecraft.class_2824;
import net.minecraft.class_2879;
import net.minecraft.class_3191;
import net.minecraft.class_5892;

public class Surround extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgToggles;
   private final SettingGroup sgRender;
   private final Setting<List<class_2248>> blocks;
   private final Setting<Integer> delay;
   private final Setting<Integer> blocksPerTick;
   private final Setting<Center> center;
   private final Setting<Boolean> doubleHeight;
   private final Setting<Boolean> onlyOnGround;
   private final Setting<Boolean> airPlace;
   private final Setting<Boolean> toggleModules;
   private final Setting<Boolean> toggleBack;
   private final Setting<List<Module>> modules;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> protect;
   private final Setting<Boolean> toggleOnYChange;
   private final Setting<Boolean> toggleOnComplete;
   private final Setting<Boolean> toggleOnDeath;
   private final Setting<Boolean> swing;
   private final Setting<Boolean> render;
   private final Setting<Boolean> renderBelow;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> safeSideColor;
   private final Setting<SettingColor> safeLineColor;
   private final Setting<SettingColor> normalSideColor;
   private final Setting<SettingColor> normalLineColor;
   private final Setting<SettingColor> unsafeSideColor;
   private final Setting<SettingColor> unsafeLineColor;
   public ArrayList<Module> toActivate;
   private int timer;

   public Surround() {
      super(Categories.Combat, "surround", "Surrounds you in blocks to prevent massive crystal damage.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgToggles = this.settings.createGroup("Toggles");
      this.sgRender = this.settings.createGroup("Render");
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("blocks")).description("What blocks to use for surround.")).defaultValue(class_2246.field_10540, class_2246.field_22423, class_2246.field_22108).filter(this::blockFilter).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("Delay, in ticks, between block placements.")).min(0).defaultValue(0)).build());
      this.blocksPerTick = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("blocks-per-tick")).description("How many blocks to place in one tick.")).defaultValue(1)).min(1).build());
      this.center = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("center")).description("Teleports you to the center of the block.")).defaultValue(Surround.Center.Incomplete)).build());
      this.doubleHeight = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("double-height")).description("Places obsidian on top of the original surround blocks to prevent people from face-placing you.")).defaultValue(false)).build());
      this.onlyOnGround = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-ground")).description("Works only when you are standing on blocks.")).defaultValue(true)).build());
      this.airPlace = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("air-place")).description("Allows Surround to place blocks in the air.")).defaultValue(true)).build());
      this.toggleModules = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-modules")).description("Turn off other modules when surround is activated.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-back-on")).description("Turn the other modules back on when surround is deactivated.")).defaultValue(false);
      Setting var10003 = this.toggleModules;
      Objects.requireNonNull(var10003);
      this.toggleBack = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      ModuleListSetting.Builder var2 = (ModuleListSetting.Builder)((ModuleListSetting.Builder)(new ModuleListSetting.Builder()).name("modules")).description("Which modules to disable on activation.");
      var10003 = this.toggleModules;
      Objects.requireNonNull(var10003);
      this.modules = var10001.add(((ModuleListSetting.Builder)var2.visible(var10003::get)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Automatically faces towards the obsidian being placed.")).defaultValue(true)).build());
      this.protect = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("protect")).description("Attempts to break crystals around surround positions to prevent surround break.")).defaultValue(true)).build());
      this.toggleOnYChange = this.sgToggles.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-on-y-change")).description("Automatically disables when your y level changes (step, jumping, etc).")).defaultValue(true)).build());
      this.toggleOnComplete = this.sgToggles.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-on-complete")).description("Toggles off when all blocks are placed.")).defaultValue(false)).build());
      this.toggleOnDeath = this.sgToggles.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-on-death")).description("Toggles off when you die.")).defaultValue(true)).build());
      this.swing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swing")).description("Render your hand swinging when placing surround blocks.")).defaultValue(true)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders a block overlay where the obsidian will be placed.")).defaultValue(true)).build());
      this.renderBelow = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("below")).description("Renders the block below you.")).defaultValue(false)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.safeSideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("safe-side-color")).description("The side color for safe blocks.")).defaultValue(new SettingColor(13, 255, 0, 0)).visible(() -> (Boolean)this.render.get() && this.shapeMode.get() != ShapeMode.Lines)).build());
      this.safeLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("safe-line-color")).description("The line color for safe blocks.")).defaultValue(new SettingColor(13, 255, 0, 0)).visible(() -> (Boolean)this.render.get() && this.shapeMode.get() != ShapeMode.Sides)).build());
      this.normalSideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("normal-side-color")).description("The side color for normal blocks.")).defaultValue(new SettingColor(0, 255, 238, 12)).visible(() -> (Boolean)this.render.get() && this.shapeMode.get() != ShapeMode.Lines)).build());
      this.normalLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("normal-line-color")).description("The line color for normal blocks.")).defaultValue(new SettingColor(0, 255, 238, 100)).visible(() -> (Boolean)this.render.get() && this.shapeMode.get() != ShapeMode.Sides)).build());
      this.unsafeSideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("unsafe-side-color")).description("The side color for unsafe blocks.")).defaultValue(new SettingColor(204, 0, 0, 12)).visible(() -> (Boolean)this.render.get() && this.shapeMode.get() != ShapeMode.Lines)).build());
      this.unsafeLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("unsafe-line-color")).description("The line color for unsafe blocks.")).defaultValue(new SettingColor(204, 0, 0, 100)).visible(() -> (Boolean)this.render.get() && this.shapeMode.get() != ShapeMode.Sides)).build());
      this.toActivate = new ArrayList();
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      if ((Boolean)this.render.get()) {
         class_2338 playerPos = this.mc.field_1724.method_24515();
         if ((Boolean)this.renderBelow.get()) {
            this.draw(playerPos.method_10074(), event, 0);
         }

         for(class_2350 direction : DirectionAccessor.meteor$getHorizontal()) {
            class_2338 renderPos = playerPos.method_10093(direction);
            this.draw(renderPos, event, (Boolean)this.doubleHeight.get() ? 2 : 0);
            if ((Boolean)this.doubleHeight.get()) {
               this.draw(renderPos.method_10084(), event, 4);
            }
         }

      }
   }

   private void draw(class_2338 renderPos, Render3DEvent event, int exclude) {
      Color sideColor = this.getSideColor(renderPos);
      Color lineColor = this.getLineColor(renderPos);
      event.renderer.box(renderPos, sideColor, lineColor, this.shapeMode.get(), exclude);
   }

   public void onActivate() {
      if (this.center.get() == Surround.Center.OnActivate) {
         PlayerUtils.centerPlayer();
      }

      this.timer = (Integer)this.delay.get();
      if ((Boolean)this.toggleModules.get() && !((List)this.modules.get()).isEmpty() && this.mc.field_1687 != null && this.mc.field_1724 != null) {
         for(Module module : this.modules.get()) {
            if (module.isActive()) {
               module.toggle();
               this.toActivate.add(module);
            }
         }
      }

   }

   public void onDeactivate() {
      if ((Boolean)this.toggleBack.get() && !this.toActivate.isEmpty() && this.mc.field_1687 != null && this.mc.field_1724 != null) {
         for(Module module : this.toActivate) {
            module.enable();
         }
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.timer++ >= (Integer)this.delay.get()) {
         if ((Boolean)this.toggleOnYChange.get() && this.mc.field_1724.field_6036 != this.mc.field_1724.method_23318()) {
            this.toggle();
         } else if (!(Boolean)this.onlyOnGround.get() || this.mc.field_1724.method_24828()) {
            FindItemResult block = InvUtils.findInHotbar((Predicate)((itemStack) -> ((List)this.blocks.get()).contains(class_2248.method_9503(itemStack.method_7909()))));
            if (block.found()) {
               if (this.center.get() == Surround.Center.Always) {
                  PlayerUtils.centerPlayer();
               }

               int placedCount = 0;
               boolean complete = true;
               class_2338 playerPos = this.mc.field_1724.method_24515();

               for(class_2350 direction : DirectionAccessor.meteor$getHorizontal()) {
                  class_2338 placePos = playerPos.method_10093(direction);
                  if (!(Boolean)this.airPlace.get() && this.isAirPlace(placePos) && this.mc.field_1687.method_8320(placePos).method_45474()) {
                     if (this.place(placePos.method_10074(), block)) {
                        ++placedCount;
                        if (placedCount >= (Integer)this.blocksPerTick.get()) {
                           break;
                        }
                     }

                     if (this.mc.field_1687.method_8320(placePos.method_10074()).method_45474()) {
                        complete = false;
                     }
                  }

                  if (this.place(placePos, block)) {
                     ++placedCount;
                     if (placedCount >= (Integer)this.blocksPerTick.get()) {
                        break;
                     }
                  }

                  if (this.mc.field_1687.method_8320(placePos).method_45474()) {
                     complete = false;
                  }
               }

               if ((Boolean)this.doubleHeight.get() && complete) {
                  for(class_2350 direction : DirectionAccessor.meteor$getHorizontal()) {
                     class_2338 placePos = playerPos.method_10093(direction).method_10084();
                     if (this.place(placePos, block)) {
                        ++placedCount;
                        if (placedCount >= (Integer)this.blocksPerTick.get()) {
                           break;
                        }
                     }

                     if (this.mc.field_1687.method_8320(placePos).method_45474()) {
                        complete = false;
                     }
                  }
               }

               this.timer = 0;
               if (complete && (Boolean)this.toggleOnComplete.get()) {
                  this.toggle();
               } else {
                  if (!complete && this.center.get() == Surround.Center.Incomplete) {
                     PlayerUtils.centerPlayer();
                  }

               }
            }
         }
      }
   }

   private boolean place(class_2338 placePos, FindItemResult block) {
      boolean placed = BlockUtils.place(placePos, block, (Boolean)this.rotate.get(), 100, (Boolean)this.swing.get(), true);
      boolean beingMined = false;
      ObjectIterator var5 = ((WorldRendererAccessor)this.mc.field_1769).meteor$getBlockBreakingInfos().values().iterator();

      while(var5.hasNext()) {
         class_3191 value = (class_3191)var5.next();
         if (value.method_13991().equals(placePos)) {
            beingMined = true;
            break;
         }
      }

      boolean isThreat = this.mc.field_1687.method_8320(placePos).method_45474() || beingMined;
      if ((Boolean)this.protect.get() && !placed && isThreat) {
         class_238 box = new class_238((double)(placePos.method_10263() - 1), (double)(placePos.method_10264() - 1), (double)(placePos.method_10260() - 1), (double)(placePos.method_10263() + 1), (double)(placePos.method_10264() + 1), (double)(placePos.method_10260() + 1));
         Predicate<class_1297> entityPredicate = (entity) -> entity instanceof class_1511 && DamageUtils.crystalDamage(this.mc.field_1724, entity.method_73189()) < PlayerUtils.getTotalHealth();

         for(class_1297 crystal : this.mc.field_1687.method_8333((class_1297)null, box, entityPredicate)) {
            if ((Boolean)this.rotate.get()) {
               Rotations.rotate(Rotations.getPitch(crystal), Rotations.getYaw(crystal), () -> this.mc.field_1724.field_3944.method_52787(class_2824.method_34206(crystal, this.mc.field_1724.method_5715())));
            } else {
               this.mc.field_1724.field_3944.method_52787(class_2824.method_34206(crystal, this.mc.field_1724.method_5715()));
            }

            this.mc.method_1562().method_52787(new class_2879(class_1268.field_5808));
         }
      }

      return placed;
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_5892 packet) {
         class_1297 entity = this.mc.field_1687.method_8469(packet.comp_2275());
         if (entity == this.mc.field_1724 && (Boolean)this.toggleOnDeath.get()) {
            this.toggle();
            this.info("Toggled off because you died.", new Object[0]);
         }
      }

   }

   private BlockType getBlockType(class_2338 pos) {
      class_2680 blockState = this.mc.field_1687.method_8320(pos);
      if (blockState.method_26204().method_36555() < 0.0F) {
         return Surround.BlockType.Safe;
      } else {
         return blockState.method_26204().method_9520() >= 600.0F ? Surround.BlockType.Normal : Surround.BlockType.Unsafe;
      }
   }

   private Color getSideColor(class_2338 pos) {
      SettingColor var10000;
      switch (this.getBlockType(pos).ordinal()) {
         case 0 -> var10000 = this.safeSideColor.get();
         case 1 -> var10000 = this.normalSideColor.get();
         case 2 -> var10000 = this.unsafeSideColor.get();
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private Color getLineColor(class_2338 pos) {
      SettingColor var10000;
      switch (this.getBlockType(pos).ordinal()) {
         case 0 -> var10000 = this.safeLineColor.get();
         case 1 -> var10000 = this.normalLineColor.get();
         case 2 -> var10000 = this.unsafeLineColor.get();
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private boolean isAirPlace(class_2338 blockPos) {
      for(class_2350 direction : class_2350.values()) {
         if (!this.mc.field_1687.method_8320(blockPos.method_10093(direction)).method_45474()) {
            return false;
         }
      }

      return true;
   }

   private boolean blockFilter(class_2248 block) {
      return block.method_9520() >= 600.0F && block.method_36555() >= 0.0F && block != class_2246.field_38420;
   }

   public static enum Center {
      Never,
      OnActivate,
      Incomplete,
      Always;

      // $FF: synthetic method
      private static Center[] $values() {
         return new Center[]{Never, OnActivate, Incomplete, Always};
      }
   }

   public static enum BlockType {
      Safe,
      Normal,
      Unsafe;

      // $FF: synthetic method
      private static BlockType[] $values() {
         return new BlockType[]{Safe, Normal, Unsafe};
      }
   }
}
