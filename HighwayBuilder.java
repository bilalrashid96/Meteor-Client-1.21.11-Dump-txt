package meteordevelopment.meteorclient.systems.modules.world;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.ShulkerBoxScreenHandlerAccessor;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.systems.modules.movement.Velocity;
import meteordevelopment.meteorclient.systems.modules.movement.speed.Speed;
import meteordevelopment.meteorclient.systems.modules.player.AutoEat;
import meteordevelopment.meteorclient.systems.modules.player.AutoGap;
import meteordevelopment.meteorclient.systems.modules.player.AutoTool;
import meteordevelopment.meteorclient.systems.modules.player.InstantRebreak;
import meteordevelopment.meteorclient.systems.modules.player.SpeedMine;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.misc.HorizontalDirection;
import meteordevelopment.meteorclient.utils.misc.MBlockPos;
import meteordevelopment.meteorclient.utils.player.CustomPlayerInput;
import meteordevelopment.meteorclient.utils.player.EChestMemory;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.Dir;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_1263;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1542;
import net.minecraft.class_1707;
import net.minecraft.class_1733;
import net.minecraft.class_1744;
import net.minecraft.class_1747;
import net.minecraft.class_1753;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1893;
import net.minecraft.class_2189;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2336;
import net.minecraft.class_2338;
import net.minecraft.class_2346;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2480;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2649;
import net.minecraft.class_2680;
import net.minecraft.class_2682;
import net.minecraft.class_2846;
import net.minecraft.class_310;
import net.minecraft.class_3489;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_437;
import net.minecraft.class_476;
import net.minecraft.class_495;
import net.minecraft.class_5250;
import net.minecraft.class_744;
import net.minecraft.class_9334;
import net.minecraft.class_239.class_240;
import net.minecraft.class_2846.class_2847;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.joml.Vector3d;

public class HighwayBuilder extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgDigging;
   private final SettingGroup sgPaving;
   private final SettingGroup sgInventory;
   private final SettingGroup sgRenderDigging;
   private final SettingGroup sgRenderPaving;
   private final Setting<Integer> width;
   private final Setting<Integer> height;
   private final Setting<Floor> floor;
   private final Setting<Boolean> railings;
   private final Setting<Boolean> cornerBlock;
   private final Setting<Boolean> mineAboveRailings;
   private final Setting<Rotation> rotation;
   private final Setting<Boolean> disconnectOnToggle;
   private final Setting<Boolean> pauseOnLag;
   private final Setting<Boolean> destroyCrystalTraps;
   private final Setting<Boolean> doubleMine;
   private final Setting<Boolean> fastBreak;
   private final Setting<Boolean> dontBreakTools;
   private final Setting<Integer> breakDurability;
   private final Setting<Integer> savePickaxes;
   private final Setting<Integer> breakDelay;
   private final Setting<Integer> blocksPerTick;
   public final Setting<List<class_2248>> blocksToPlace;
   private final Setting<Double> placeRange;
   private final Setting<Integer> placeDelay;
   private final Setting<Integer> placementsPerTick;
   private final Setting<List<class_1792>> trashItems;
   private final Setting<Integer> inventoryDelay;
   private final Setting<Boolean> ejectUselessShulkers;
   private final Setting<Boolean> searchEnderChest;
   private final Setting<Boolean> searchShulkers;
   private final Setting<Integer> minEmpty;
   private final Setting<Boolean> mineEnderChests;
   private final Setting<BlockadeType> blockadeType;
   public final Setting<Integer> saveEchests;
   private final Setting<Boolean> rebreakEchests;
   private final Setting<Integer> rebreakTimer;
   private final Setting<Boolean> renderMine;
   private final Setting<ShapeMode> renderMineShape;
   private final Setting<SettingColor> renderMineSideColor;
   private final Setting<SettingColor> renderMineLineColor;
   private final Setting<Boolean> renderPlace;
   private final Setting<ShapeMode> renderPlaceShape;
   private final Setting<SettingColor> renderPlaceSideColor;
   private final Setting<SettingColor> renderPlaceLineColor;
   private HorizontalDirection dir;
   private HorizontalDirection leftDir;
   private HorizontalDirection rightDir;
   private class_744 prevInput;
   private CustomPlayerInput input;
   private State state;
   private State lastState;
   private IBlockPosProvider blockPosProvider;
   public class_243 start;
   public int blocksBroken;
   public int blocksPlaced;
   private final MBlockPos lastBreakingPos;
   private boolean displayInfo;
   private boolean warned;
   private boolean suspended;
   private boolean inventory;
   private int placeTimer;
   private int breakTimer;
   private int count;
   private int syncId;
   private final RestockTask restockTask;
   private final ArrayList<class_1511> ignoreCrystals;
   public boolean drawingBow;
   public DoubleMineBlock normalMining;
   public DoubleMineBlock packetMining;
   private final MBlockPos posRender2;
   private final MBlockPos posRender3;

   public HighwayBuilder() {
      super(Categories.World, "highway-builder", "Automatically builds highways.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgDigging = this.settings.createGroup("Digging");
      this.sgPaving = this.settings.createGroup("Paving");
      this.sgInventory = this.settings.createGroup("Inventory");
      this.sgRenderDigging = this.settings.createGroup("Render Digging");
      this.sgRenderPaving = this.settings.createGroup("Render Paving");
      this.width = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("width")).description("Width of the highway.")).defaultValue(4)).range(1, 5).sliderRange(1, 5).build());
      this.height = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("height")).description("Height of the highway.")).defaultValue(3)).range(2, 5).sliderRange(2, 5).build());
      this.floor = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("floor")).description("What floor placement mode to use.")).defaultValue(HighwayBuilder.Floor.Replace)).build());
      this.railings = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("railings")).description("Builds railings next to the highway.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("corner-support-block")).description("Places a support block underneath the railings, to prevent air placing.")).defaultValue(true);
      Setting var10003 = this.railings;
      Objects.requireNonNull(var10003);
      this.cornerBlock = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.mineAboveRailings = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("mine-above-railings")).description("Mines blocks above railings.")).defaultValue(true)).build());
      this.rotation = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("rotation")).description("Mode of rotation.")).defaultValue(HighwayBuilder.Rotation.Both)).build());
      this.disconnectOnToggle = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disconnect-on-toggle")).description("Automatically disconnects when the module is turned off, for example for not having enough blocks.")).defaultValue(false)).build());
      this.pauseOnLag = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-lag")).description("Pauses the current process while the server stops responding.")).defaultValue(true)).build());
      this.destroyCrystalTraps = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("destroy-crystal-traps")).description("Use a bow to defuse crystal traps safely from a distance. An infinity bow is recommended.")).defaultValue(true)).build());
      this.doubleMine = this.sgDigging.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("double-mine")).description("Whether to double mine blocks when applicable (normal mine and packet mine simultaneously).")).defaultValue(true)).build());
      var10001 = this.sgDigging;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fast-break")).description("Whether to finish breaking blocks faster than normal while double mining.")).defaultValue(true);
      var10003 = this.doubleMine;
      Objects.requireNonNull(var10003);
      this.fastBreak = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.dontBreakTools = this.sgDigging.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("dont-break-tools")).description("Don't break tools.")).defaultValue(false)).build());
      var10001 = this.sgDigging;
      IntSetting.Builder var7 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("durability-percentage")).description("The durability percentage at which to stop using a tool.")).defaultValue(2)).range(1, 100).sliderRange(1, 100);
      var10003 = this.dontBreakTools;
      Objects.requireNonNull(var10003);
      this.breakDurability = var10001.add(((IntSetting.Builder)var7.visible(var10003::get)).build());
      this.savePickaxes = this.sgDigging.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("save-pickaxes")).description("How many pickaxes to ensure are saved. Hitting this number in your inventory will trigger a restock or the module toggling off.")).defaultValue(1)).range(0, 36).sliderRange(0, 36).visible(() -> !(Boolean)this.dontBreakTools.get())).build());
      this.breakDelay = this.sgDigging.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("break-delay")).description("The delay between breaking blocks.")).defaultValue(0)).min(0).build());
      this.blocksPerTick = this.sgDigging.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("blocks-per-tick")).description("The maximum amount of blocks that can be mined in a tick. Only applies to blocks instantly breakable.")).defaultValue(1)).range(1, 100).sliderRange(1, 25).build());
      this.blocksToPlace = this.sgPaving.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("blocks-to-place")).description("Blocks it is allowed to place.")).defaultValue(class_2246.field_10540).filter((block) -> class_2248.method_9614(block.method_9564().method_26220(class_2682.field_12294, class_2338.field_10980))).build());
      this.placeRange = this.sgPaving.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-range")).description("The maximum distance at which you can place blocks.")).defaultValue((double)4.5F).sliderMax((double)5.5F).build());
      this.placeDelay = this.sgPaving.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-delay")).description("The delay between placing blocks.")).defaultValue(0)).min(0).build());
      this.placementsPerTick = this.sgPaving.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("placements-per-tick")).description("The maximum amount of blocks that can be placed in a tick.")).defaultValue(1)).min(1).build());
      this.trashItems = this.sgInventory.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("trash-items")).description("Items that are considered trash and can be thrown out.")).defaultValue(class_1802.field_8328, class_1802.field_8155, class_1802.field_8397, class_1802.field_8845, class_1802.field_8601, class_1802.field_8801, class_1802.field_23843, class_1802.field_22000, class_1802.field_8070, class_1802.field_8067, class_1802.field_21999, class_1802.field_8511, class_1802.field_8354).build());
      this.inventoryDelay = this.sgInventory.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("inventory-delay")).description("Delay in ticks on inventory interactions.")).defaultValue(3)).min(0).build());
      this.ejectUselessShulkers = this.sgInventory.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("eject-useless-shulkers")).description("Whether you should eject useless shulkers. Warning - will throw out any shulkers that don't contain blocks to place, pickaxes, or food. Be careful with your kits.")).defaultValue(true)).build());
      this.searchEnderChest = this.sgInventory.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("search-ender-chest")).description("Searches your ender chest to find items to use. Be careful with this one, especially if you let it search through shulkers.")).defaultValue(false)).build());
      this.searchShulkers = this.sgInventory.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("search-shulkers")).description("Searches through shulkers to find items to use.")).defaultValue(true)).build());
      this.minEmpty = this.sgInventory.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("minimum-empty-slots")).description("The minimum amount of empty slots you want left after mining obsidian.")).defaultValue(3)).sliderRange(0, 9).min(0).build());
      this.mineEnderChests = this.sgInventory.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("mine-ender-chests")).description("Mines ender chests for obsidian.")).defaultValue(true)).build());
      var10001 = this.sgInventory;
      EnumSetting.Builder var8 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("echest-blockade-type")).description("What blockade type to use (the structure placed when mining echests).")).defaultValue(HighwayBuilder.BlockadeType.Full);
      var10003 = this.mineEnderChests;
      Objects.requireNonNull(var10003);
      this.blockadeType = var10001.add(((EnumSetting.Builder)var8.visible(var10003::get)).build());
      var10001 = this.sgInventory;
      IntSetting.Builder var9 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("save-ender-chests")).description("How many ender chests to ensure are saved. Hitting this number in your inventory will trigger a restock or the module toggling off.")).defaultValue(2)).range(0, 64).sliderRange(0, 64);
      var10003 = this.mineEnderChests;
      Objects.requireNonNull(var10003);
      this.saveEchests = var10001.add(((IntSetting.Builder)var9.visible(var10003::get)).build());
      var10001 = this.sgInventory;
      BoolSetting.Builder var10 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("instantly-rebreak-echests")).description("Whether or not to use the instant rebreak exploit to break echests.")).defaultValue(false);
      var10003 = this.mineEnderChests;
      Objects.requireNonNull(var10003);
      this.rebreakEchests = var10001.add(((BoolSetting.Builder)var10.visible(var10003::get)).build());
      this.rebreakTimer = this.sgInventory.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("rebreak-delay")).description("Delay between rebreak attempts.")).defaultValue(0)).sliderMax(20).visible(() -> (Boolean)this.mineEnderChests.get() && (Boolean)this.rebreakEchests.get())).build());
      this.renderMine = this.sgRenderDigging.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-blocks-to-mine")).description("Render blocks to be mined.")).defaultValue(true)).build());
      this.renderMineShape = this.sgRenderDigging.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("blocks-to-mine-shape-mode")).description("How the blocks to be mined are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.renderMineSideColor = this.sgRenderDigging.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("blocks-to-mine-side-color")).description("Color of blocks to be mined.")).defaultValue(new SettingColor(225, 25, 25, 25)).build());
      this.renderMineLineColor = this.sgRenderDigging.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("blocks-to-mine-line-color")).description("Color of blocks to be mined.")).defaultValue(new SettingColor(225, 25, 25)).build());
      this.renderPlace = this.sgRenderPaving.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-blocks-to-place")).description("Render blocks to be placed.")).defaultValue(true)).build());
      this.renderPlaceShape = this.sgRenderPaving.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("blocks-to-place-shape-mode")).description("How the blocks to be placed are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.renderPlaceSideColor = this.sgRenderPaving.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("blocks-to-place-side-color")).description("Color of blocks to be placed.")).defaultValue(new SettingColor(25, 25, 225, 25)).build());
      this.renderPlaceLineColor = this.sgRenderPaving.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("blocks-to-place-line-color")).description("Color of blocks to be placed.")).defaultValue(new SettingColor(25, 25, 225)).build());
      this.lastBreakingPos = new MBlockPos();
      this.suspended = true;
      this.inventory = true;
      this.restockTask = new RestockTask(this);
      this.ignoreCrystals = new ArrayList();
      this.posRender2 = new MBlockPos();
      this.posRender3 = new MBlockPos();
      this.runInMainMenu = true;
   }

   public void onActivate() {
      if (Utils.canUpdate()) {
         this.updateVariables();
         this.dir = HorizontalDirection.get(this.mc.field_1724.method_36454());
         this.leftDir = this.dir.rotateLeftSkipOne();
         this.rightDir = this.leftDir.opposite();
         this.blockPosProvider = (IBlockPosProvider)(this.dir.diagonal ? new DiagonalBlockPosProvider() : new StraightBlockPosProvider());
         this.state = HighwayBuilder.State.Forward;
         this.setState(HighwayBuilder.State.Center);
         this.lastBreakingPos.set(0, 0, 0);
         this.start = this.mc.field_1724.method_73189();
         this.blocksBroken = this.blocksPlaced = 0;
         this.displayInfo = true;
         this.suspended = false;
         this.restockTask.complete();
         if ((Integer)this.blocksPerTick.get() > 1 && (this.rotation.get()).mine) {
            this.warning("With rotations enabled, you can break at most 1 block per tick.", new Object[0]);
         }

         if ((Integer)this.placementsPerTick.get() > 1 && (this.rotation.get()).place) {
            this.warning("With rotations enabled, you can place at most 1 block per tick.", new Object[0]);
         }

         if (((InstantRebreak)Modules.get().get(InstantRebreak.class)).isActive()) {
            this.warning("It's recommended to disable the Instant Rebreak module and instead use the 'instantly-rebreak-echests' setting to avoid errors.", new Object[0]);
         }

         if (((Speed)Modules.get().get(Speed.class)).isActive() && this.dir.diagonal) {
            this.warning("It's recommended to disable the Speed module to avoid misalignment on diagonals.", new Object[0]);
         }

         if (!((Velocity)Modules.get().get(Velocity.class)).isActive()) {
            this.warning("It's recommended to enable the Velocity module to avoid misalignment (entity pushing, liquid movement).", new Object[0]);
         }

         if (!this.warned && ((NoGhostBlocks)Modules.get().get(NoGhostBlocks.class)).isActive()) {
            this.info("The No Ghost Blocks module is useful to prevent desyncs on laggy servers. However, it will also slow Highway Builder down, and comes with the risks of incorrect statistics and packet kicks.", new Object[0]);
            this.warned = true;
         }

      }
   }

   public void onDeactivate() {
      if (Utils.canUpdate()) {
         this.mc.field_1724.field_3913 = this.prevInput;
         this.mc.field_1724.method_36456(this.dir.yaw);
         this.mc.field_1690.field_1904.method_23481(false);
         if (this.displayInfo) {
            this.info("Distance: (highlight)%.0f", new Object[]{PlayerUtils.distanceTo(this.start)});
            this.info("Blocks broken: (highlight)%d", new Object[]{this.blocksBroken});
            this.info("Blocks placed: (highlight)%d", new Object[]{this.blocksPlaced});
         }

      }
   }

   public void error(String message, Object... args) {
      super.error(message, args);
      this.toggle();
      if ((Boolean)this.disconnectOnToggle.get()) {
         this.disconnect(message, args);
      }

   }

   private void errorEarly(String message, Object... args) {
      super.error(message, args);
      this.displayInfo = false;
      this.toggle();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.dir == null) {
         this.onActivate();
      } else {
         if (this.suspended) {
            if (!this.inventory || !Utils.canUpdate()) {
               return;
            }

            this.updateVariables();
            this.suspended = false;
         }

         if ((Integer)this.width.get() < 3 && this.dir.diagonal) {
            this.errorEarly("Diagonal highways with width less than 3 are not supported.");
         } else if (!((AutoEat)Modules.get().get(AutoEat.class)).eating && !((AutoGap)Modules.get().get(AutoGap.class)).isEating() && !((KillAura)Modules.get().get(KillAura.class)).attacking) {
            if ((Boolean)this.pauseOnLag.get() && TickRate.INSTANCE.getTimeSinceLastTick() >= 1.5F) {
               this.input.stop();
            } else {
               this.count = 0;
               if (this.mc.field_1724.method_23318() < this.start.field_1351 - (double)0.5F) {
                  this.setState(HighwayBuilder.State.ReLevel);
               }

               this.tickDoubleMine();
               this.state.tick(this);
               if (this.breakTimer > 0) {
                  --this.breakTimer;
               }

               if (this.placeTimer > 0) {
                  --this.placeTimer;
               }

            }
         } else {
            this.input.stop();
         }
      }
   }

   @EventHandler
   private void onPacket(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2649 p) {
         if (p.comp_3837() == 0 && this.suspended) {
            this.inventory = true;
         } else {
            this.syncId = p.comp_3837();
         }
      }

   }

   @EventHandler
   private void onGameLeave(GameLeftEvent event) {
      this.suspended = true;
      this.inventory = false;
   }

   @EventHandler
   private void onRender2d(Render2DEvent event) {
      if (!this.suspended && (Boolean)this.renderMine.get()) {
         if (this.normalMining != null) {
            this.normalMining.renderLetter();
         }

         if (this.packetMining != null) {
            this.packetMining.renderLetter();
         }

      }
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      if (!this.suspended && this.blockPosProvider != null) {
         if ((Boolean)this.renderMine.get()) {
            this.render(event, this.blockPosProvider.getFront(), (mBlockPos) -> this.canMine(mBlockPos, true), true);
            if (this.floor.get() == HighwayBuilder.Floor.Replace) {
               this.render(event, this.blockPosProvider.getFloor(), (mBlockPos) -> this.canMine(mBlockPos, false), true);
            }

            if ((Boolean)this.railings.get()) {
               this.render(event, this.blockPosProvider.getRailings(0), (mBlockPos) -> this.canMine(mBlockPos, false), true);
            }

            if ((Boolean)this.mineAboveRailings.get()) {
               this.render(event, this.blockPosProvider.getRailings(1), (mBlockPos) -> this.canMine(mBlockPos, true), true);
            }

            if (this.state == HighwayBuilder.State.MineEChestBlockade) {
               this.render(event, this.blockPosProvider.getBlockade(true, this.blockadeType.get()), (mBlockPos) -> this.canMine(mBlockPos, true), true);
            }
         }

         if ((Boolean)this.renderPlace.get()) {
            this.render(event, this.blockPosProvider.getLiquids(), (mBlockPos) -> this.canPlace(mBlockPos, true), false);
            if ((Boolean)this.railings.get()) {
               this.render(event, this.blockPosProvider.getRailings(0), (mBlockPos) -> this.canPlace(mBlockPos, false), false);
               if ((Boolean)this.cornerBlock.get()) {
                  this.render(event, this.blockPosProvider.getRailings(-1), (mBlockPos) -> {
                     boolean valid = false;

                     for(MBlockPos pos : this.blockPosProvider.getRailings(0)) {
                        if (!((List)this.blocksToPlace.get()).contains(pos.getState().method_26204()) && pos.add(0, -1, 0).equals(mBlockPos)) {
                           valid = true;
                           break;
                        }
                     }

                     return valid && this.canPlace(mBlockPos, false);
                  }, false);
               }
            }

            this.render(event, this.blockPosProvider.getFloor(), (mBlockPos) -> this.canPlace(mBlockPos, false), false);
            if (this.state == HighwayBuilder.State.PlaceEChestBlockade) {
               this.render(event, this.blockPosProvider.getBlockade(false, this.blockadeType.get()), (mBlockPos) -> this.canPlace(mBlockPos, false), false);
            }
         }

      }
   }

   private void render(Render3DEvent event, MBPIterator it, Predicate<MBlockPos> predicate, boolean mine) {
      Color sideColor = mine ? (Color)this.renderMineSideColor.get() : (Color)this.renderPlaceSideColor.get();
      Color lineColor = mine ? (Color)this.renderMineLineColor.get() : (Color)this.renderPlaceLineColor.get();
      ShapeMode shapeMode = mine ? (ShapeMode)this.renderMineShape.get() : (ShapeMode)this.renderPlaceShape.get();

      for(MBlockPos pos : it) {
         this.posRender2.set(pos);
         if (predicate.test(this.posRender2)) {
            int excludeDir = 0;

            for(class_2350 side : class_2350.values()) {
               this.posRender3.set(this.posRender2).add(side.method_10148(), side.method_10164(), side.method_10165());
               it.save();

               for(MBlockPos p : it) {
                  if (p.equals(this.posRender3) && predicate.test(p)) {
                     excludeDir |= Dir.get(side);
                  }
               }

               it.restore();
            }

            event.renderer.box(this.posRender2.getBlockPos(), sideColor, lineColor, shapeMode, excludeDir);
         }
      }

   }

   private void updateVariables() {
      this.prevInput = this.mc.field_1724.field_3913;
      this.mc.field_1724.field_3913 = this.input = new CustomPlayerInput();
      this.placeTimer = this.breakTimer = this.count = this.syncId = 0;
      this.ignoreCrystals.clear();
      this.normalMining = null;
      this.packetMining = null;
   }

   private void setState(State state) {
      this.setState(state, this.state);
   }

   private void setState(State state, State lastState) {
      this.lastState = lastState;
      this.state = state;
      this.input.stop();
      state.start(this);
   }

   private int getWidthLeft() {
      byte var10000;
      switch ((Integer)this.width.get()) {
         case 2:
         case 3:
            var10000 = 1;
            break;
         case 4:
         case 5:
            var10000 = 2;
            break;
         default:
            var10000 = 0;
      }

      return var10000;
   }

   private int getWidthRight() {
      byte var10000;
      switch ((Integer)this.width.get()) {
         case 3:
         case 4:
            var10000 = 1;
            break;
         case 5:
            var10000 = 2;
            break;
         default:
            var10000 = 0;
      }

      return var10000;
   }

   private boolean canMine(MBlockPos pos, boolean mineBlocksToPlace) {
      class_2680 state = pos.getState();
      return BlockUtils.canBreak(pos.getBlockPos(), state) && (mineBlocksToPlace || !((List)this.blocksToPlace.get()).contains(state.method_26204()));
   }

   private boolean canPlace(MBlockPos pos, boolean liquids) {
      if (pos.getBlockPos().method_19770(this.mc.field_1724.method_33571()) > (Double)this.placeRange.get() * (Double)this.placeRange.get()) {
         return false;
      } else {
         return liquids ? !pos.getState().method_26227().method_15769() : BlockUtils.canPlace(pos.getBlockPos());
      }
   }

   private void disconnect(String message, Object... args) {
      String var10000 = String.format("%s[%s%s%s] %s", class_124.field_1080, class_124.field_1078, this.title, class_124.field_1080, class_124.field_1061);
      class_5250 text = class_2561.method_43470(var10000 + String.format(message, args)).method_27693("\n");
      text.method_10852(this.getStatsText());
      this.mc.method_1562().method_48296().method_10747(text);
   }

   public class_5250 getStatsText() {
      class_5250 text = class_2561.method_43470(String.format("%sDistance: %s%.0f\n", class_124.field_1080, class_124.field_1068, this.mc.field_1724 == null ? (double)0.0F : PlayerUtils.distanceTo(this.start)));
      text.method_27693(String.format("%sBlocks broken: %s%d\n", class_124.field_1080, class_124.field_1068, this.blocksBroken));
      text.method_27693(String.format("%sBlocks placed: %s%d", class_124.field_1080, class_124.field_1068, this.blocksPlaced));
      return text;
   }

   private void tickDoubleMine() {
      if (this.normalMining != null) {
         if (this.normalMining.shouldRemove()) {
            this.mc.method_1562().method_52787(new class_2846(class_2847.field_12971, this.normalMining.blockPos, this.normalMining.direction));
            this.normalMining = null;
            HighwayBuilder.DoubleMineBlock.rateLimited = true;
         } else if (this.mc.field_1687.method_8320(this.normalMining.blockPos).method_26204() != this.normalMining.block) {
            this.normalMining = null;
            ++this.blocksBroken;
            ++this.count;
            HighwayBuilder.DoubleMineBlock.rateLimited = false;
         } else if (this.normalMining.isReady()) {
            this.normalMining.stopDestroying();
         }

         this.mc.field_1724.method_6104(class_1268.field_5808);
      }

      if (this.packetMining != null) {
         if (this.packetMining.shouldRemove()) {
            this.packetMining = null;
         } else if (this.mc.field_1687.method_8320(this.packetMining.blockPos).method_26204() != this.packetMining.block) {
            this.packetMining = null;
            ++this.blocksBroken;
            ++this.count;
         }
      }

   }

   // $FF: synthetic method
   static class_310 access$000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$100(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$200(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$300(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$400(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$500(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$600(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$700(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$800(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$900(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1100(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1200(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1300(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1400(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1500(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1600(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1700(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1800(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1900(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2100(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2200(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2300(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2400(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2500(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2600(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2700(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2800(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2900(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$3000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$3100(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$3200(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$3300(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$3400(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$3500(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$3600(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$3700(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$3800(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$3900(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$4000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$4100(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$4200(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$4300(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$4400(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$4500(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$4600(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$4700(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$4800(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$4900(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$5000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$5100(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$5200(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$5300(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$5400(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$5500(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$5600(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$5700(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$5800(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$5900(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$6000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$6100(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$6200(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$6300(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$6400(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$6500(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$6600(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$6700(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$6800(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$6900(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$7000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$7100(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$7200(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$7300(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$7400(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$7500(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$7600(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$7700(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$7800(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$7900(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$8000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$8100(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$8200(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$8300(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$8400(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$8500(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$8600(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$8700(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$8800(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$8900(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$9000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$9100(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$9200(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$9300(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$9400(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$9500(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$9600(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$9700(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$9800(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$9900(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$10000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$10100(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$10200(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$12400(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$12600(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$12700(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$12800(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$13000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$13100(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$13200(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$13400(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$13500(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$13600(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$13800(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$13900(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$14000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$14100(HighwayBuilder x0) {
      return x0.mc;
   }

   public static enum Floor {
      Replace,
      PlaceMissing;

      // $FF: synthetic method
      private static Floor[] $values() {
         return new Floor[]{Replace, PlaceMissing};
      }
   }

   public static enum Rotation {
      None(false, false),
      Mine(true, false),
      Place(false, true),
      Both(true, true);

      public final boolean mine;
      public final boolean place;

      private Rotation(boolean mine, boolean place) {
         this.mine = mine;
         this.place = place;
      }

      // $FF: synthetic method
      private static Rotation[] $values() {
         return new Rotation[]{None, Mine, Place, Both};
      }
   }

   public static enum BlockadeType {
      Full(6),
      Partial(4),
      Shulker(3);

      public final int columns;

      private BlockadeType(int columns) {
         this.columns = columns;
      }

      // $FF: synthetic method
      private static BlockadeType[] $values() {
         return new BlockadeType[]{Full, Partial, Shulker};
      }
   }

   private static enum State permits null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null {
      Center {
         protected void start(HighwayBuilder b) {
            if (HighwayBuilder.access$100(b).field_1724.method_73189().method_24802(class_243.method_24955(HighwayBuilder.access$000(b).field_1724.method_24515()), 0.1)) {
               this.stop(b);
            }

         }

         protected void tick(HighwayBuilder b) {
            double x = Math.abs(HighwayBuilder.access$200(b).field_1724.method_23317() - (double)((int)HighwayBuilder.access$300(b).field_1724.method_23317())) - (double)0.5F;
            double z = Math.abs(HighwayBuilder.access$400(b).field_1724.method_23321() - (double)((int)HighwayBuilder.access$500(b).field_1724.method_23321())) - (double)0.5F;
            boolean isX = Math.abs(x) <= 0.1;
            boolean isZ = Math.abs(z) <= 0.1;
            if (isX && isZ) {
               this.stop(b);
            } else {
               HighwayBuilder.access$600(b).field_1724.method_36456(0.0F);
               if (!isZ) {
                  b.input.forward(z < (double)0.0F);
                  b.input.backward(z > (double)0.0F);
                  if (HighwayBuilder.access$700(b).field_1724.method_23321() < (double)0.0F) {
                     boolean forward = b.input.field_54155.comp_3159();
                     b.input.forward(b.input.field_54155.comp_3160());
                     b.input.backward(forward);
                  }
               }

               if (!isX) {
                  b.input.right(x > (double)0.0F);
                  b.input.left(x < (double)0.0F);
                  if (HighwayBuilder.access$800(b).field_1724.method_23317() < (double)0.0F) {
                     boolean right = b.input.field_54155.comp_3162();
                     b.input.right(b.input.field_54155.comp_3161());
                     b.input.left(right);
                  }
               }

               b.input.sneak(true);
            }

         }

         private void stop(HighwayBuilder b) {
            b.input.stop();
            HighwayBuilder.access$900(b).field_1724.method_18800((double)0.0F, (double)0.0F, (double)0.0F);
            HighwayBuilder.access$1500(b).field_1724.method_5814((double)((int)HighwayBuilder.access$1000(b).field_1724.method_23317()) + (HighwayBuilder.access$1100(b).field_1724.method_23317() < (double)0.0F ? (double)-0.5F : (double)0.5F), HighwayBuilder.access$1200(b).field_1724.method_23318(), (double)((int)HighwayBuilder.access$1300(b).field_1724.method_23321()) + (HighwayBuilder.access$1400(b).field_1724.method_23321() < (double)0.0F ? (double)-0.5F : (double)0.5F));
            b.setState(b.lastState);
         }
      },
      Forward {
         protected void start(HighwayBuilder b) {
            this.checkTasks(b);
            if (b.state == Forward) {
               HighwayBuilder.access$1600(b).field_1724.method_36456(b.dir.yaw);
            }

         }

         protected void tick(HighwayBuilder b) {
            this.checkTasks(b);
            if (b.state == Forward) {
               b.input.forward(true);
            }

         }

         private void checkTasks(HighwayBuilder b) {
            if ((Boolean)b.destroyCrystalTraps.get() && this.isCrystalTrap(b)) {
               b.setState(DefuseCrystalTraps);
            } else if (this.needsToPlace(b, b.blockPosProvider.getLiquids(), true)) {
               b.setState(FillLiquids);
            } else if (this.needsToMine(b, b.blockPosProvider.getFront(), true)) {
               b.setState(MineFront);
            } else if (b.floor.get() == HighwayBuilder.Floor.Replace && this.needsToMine(b, b.blockPosProvider.getFloor(), false)) {
               b.setState(MineFloor);
            } else if ((Boolean)b.railings.get() && this.needsToMine(b, b.blockPosProvider.getRailings(0), false)) {
               b.setState(MineRailings);
            } else if ((Boolean)b.mineAboveRailings.get() && this.needsToMine(b, b.blockPosProvider.getRailings(1), true)) {
               b.setState(MineAboveRailings);
            } else if ((Boolean)b.railings.get() && this.needsToPlace(b, b.blockPosProvider.getRailings(0), false)) {
               if ((Boolean)b.cornerBlock.get() && this.needsToPlace(b, b.blockPosProvider.getRailings(-1), false)) {
                  b.setState(PlaceCornerBlock);
               } else {
                  b.setState(PlaceRailings);
               }
            } else if (this.needsToPlace(b, b.blockPosProvider.getFloor(), false)) {
               b.setState(PlaceFloor);
            }

         }

         private boolean needsToMine(HighwayBuilder b, HighwayBuilder.MBPIterator it, boolean mineBlocksToPlace) {
            for(MBlockPos pos : it) {
               if (b.canMine(pos, mineBlocksToPlace)) {
                  return true;
               }
            }

            return false;
         }

         private boolean needsToPlace(HighwayBuilder b, HighwayBuilder.MBPIterator it, boolean liquids) {
            for(MBlockPos pos : it) {
               if (b.canPlace(pos, liquids)) {
                  return true;
               }
            }

            return false;
         }

         private boolean isCrystalTrap(HighwayBuilder b) {
            for(class_1297 entity : HighwayBuilder.access$1700(b).field_1687.method_18112()) {
               if (entity instanceof class_1511 endCrystal) {
                  if (!PlayerUtils.isWithin((class_1297)endCrystal, (double)12.0F) && PlayerUtils.isWithin((class_1297)endCrystal, (double)24.0F) && !b.ignoreCrystals.contains(endCrystal)) {
                     class_243 vec1 = new class_243((double)0.0F, (double)0.0F, (double)0.0F);
                     class_243 vec2 = new class_243((double)0.0F, (double)0.0F, (double)0.0F);
                     ((IVec3d)vec1).meteor$set(HighwayBuilder.access$1800(b).field_1724.method_23317(), HighwayBuilder.access$1900(b).field_1724.method_23318() + (double)HighwayBuilder.access$2000(b).field_1724.method_5751(), HighwayBuilder.access$2100(b).field_1724.method_23321());
                     ((IVec3d)vec2).meteor$set(entity.method_23317(), entity.method_23318() + (double)0.5F, entity.method_23321());
                     return HighwayBuilder.access$2300(b).field_1687.method_17742(new class_3959(vec1, vec2, class_3960.field_17558, class_242.field_1348, HighwayBuilder.access$2200(b).field_1724)).method_17783() == class_240.field_1333;
                  }
               }
            }

            return false;
         }
      },
      ReLevel {
         private final class_2338.class_2339 pos;
         private class_2338 startPos;
         private int timer;

         private {
            this.pos = new class_2338.class_2339();
            this.timer = 30;
         }

         protected void start(HighwayBuilder b) {
            this.startPos = class_2338.method_49638(b.start);
         }

         protected void tick(HighwayBuilder b) {
            class_243 vec = HighwayBuilder.access$2500(b).field_1724.method_73189().method_1019(HighwayBuilder.access$2400(b).field_1724.method_18798()).method_1031((double)0.0F, (double)-0.75F, (double)0.0F);
            this.pos.method_10102((double)HighwayBuilder.access$2600(b).field_1724.method_31477(), vec.field_1351, (double)HighwayBuilder.access$2700(b).field_1724.method_31479());
            if (this.pos.method_10264() >= HighwayBuilder.access$2800(b).field_1724.method_24515().method_10264()) {
               this.pos.method_33098(HighwayBuilder.access$2900(b).field_1724.method_24515().method_10264() - 1);
            }

            if (this.pos.method_10264() >= this.startPos.method_10264()) {
               this.pos.method_33098(this.startPos.method_10264() - 1);
            }

            if (HighwayBuilder.access$3000(b).field_1724.method_23318() > b.start.field_1351 - (double)0.5F && !HighwayBuilder.access$3100(b).field_1687.method_8320(this.pos).method_45474()) {
               b.input.jump(false);
               if (this.timer > 0) {
                  --this.timer;
               } else {
                  b.setState(Forward);
                  this.timer = 30;
               }

            } else if (b.placeTimer <= 0) {
               if (this.timer < 30) {
                  this.timer = 30;
               }

               b.input.jump(true);
               int slot = -1;
               if (this.pos.method_10264() == this.startPos.method_10074().method_10264()) {
                  slot = this.findAndMoveToHotbar(b, (itemStack) -> {
                     class_1792 patt0$temp = itemStack.method_7909();
                     boolean var10000;
                     if (patt0$temp instanceof class_1747 blockItem) {
                        if (((List)b.blocksToPlace.get()).contains(blockItem.method_7711())) {
                           var10000 = true;
                           return var10000;
                        }
                     }

                     var10000 = false;
                     return var10000;
                  });
               }

               if (slot == -1) {
                  slot = this.findAcceptablePlacementBlock(b);
                  if (slot == -1) {
                     return;
                  }
               }

               if (BlockUtils.place(this.pos.method_10062(), class_1268.field_5808, slot, (b.rotation.get()).place, 100, true, true, false)) {
                  if ((Boolean)b.renderPlace.get()) {
                     RenderUtils.renderTickingBlock(this.pos.method_10062(), b.renderPlaceSideColor.get(), b.renderPlaceLineColor.get(), b.renderPlaceShape.get(), 0, 5, true, false);
                  }

                  b.placeTimer = (Integer)b.placeDelay.get();
               }

            }
         }

         private int findAcceptablePlacementBlock(HighwayBuilder b) {
            int slot = this.findAndMoveToHotbar(b, (itemStack) -> !(itemStack.method_7909() instanceof class_1747) ? false : ((List)b.trashItems.get()).contains(itemStack.method_7909()));
            if (slot == -1) {
               slot = this.findAndMoveToHotbar(b, (itemStack) -> {
                  class_1792 patt0$temp = itemStack.method_7909();
                  if (patt0$temp instanceof class_1747 bi) {
                     return ((List)b.blocksToPlace.get()).contains(bi.method_7711());
                  } else {
                     return false;
                  }
               });
            }

            return slot != -1 ? slot : this.findAndMoveToHotbar(b, (itemStack) -> {
               class_1792 patt0$temp = itemStack.method_7909();
               if (!(patt0$temp instanceof class_1747 bi)) {
                  return false;
               } else if (Utils.isShulker(bi)) {
                  return false;
               } else {
                  class_2248 block = bi.method_7711();
                  if (!class_2248.method_9614(block.method_9564().method_26220(HighwayBuilder.access$3200(b).field_1687, this.pos))) {
                     return false;
                  } else {
                     return !(block instanceof class_2346) || !class_2346.method_10128(HighwayBuilder.access$3300(b).field_1687.method_8320(this.pos));
                  }
               }
            });
         }

         // $FF: synthetic method
         private boolean lambda$findAcceptablePlacementBlock$3(HighwayBuilder b, class_1799 itemStack) {
            class_1792 patt0$temp = itemStack.method_7909();
            if (!(patt0$temp instanceof class_1747 bi)) {
               return false;
            } else if (Utils.isShulker(bi)) {
               return false;
            } else {
               class_2248 block = bi.method_7711();
               if (!class_2248.method_9614(block.method_9564().method_26220(HighwayBuilder.access$3200(b).field_1687, this.pos))) {
                  return false;
               } else {
                  return !(block instanceof class_2346) || !class_2346.method_10128(HighwayBuilder.access$3300(b).field_1687.method_8320(this.pos));
               }
            }
         }

         // $FF: synthetic method
         private static boolean lambda$findAcceptablePlacementBlock$2(HighwayBuilder b, class_1799 itemStack) {
            class_1792 patt0$temp = itemStack.method_7909();
            if (patt0$temp instanceof class_1747 bi) {
               return ((List)b.blocksToPlace.get()).contains(bi.method_7711());
            } else {
               return false;
            }
         }

         // $FF: synthetic method
         private static boolean lambda$findAcceptablePlacementBlock$1(HighwayBuilder b, class_1799 itemStack) {
            return !(itemStack.method_7909() instanceof class_1747) ? false : ((List)b.trashItems.get()).contains(itemStack.method_7909());
         }

         // $FF: synthetic method
         private static boolean lambda$tick$0(HighwayBuilder b, class_1799 itemStack) {
            class_1792 patt0$temp = itemStack.method_7909();
            boolean var10000;
            if (patt0$temp instanceof class_1747 blockItem) {
               if (((List)b.blocksToPlace.get()).contains(blockItem.method_7711())) {
                  var10000 = true;
                  return var10000;
               }
            }

            var10000 = false;
            return var10000;
         }
      },
      FillLiquids {
         protected void tick(HighwayBuilder b) {
            int slot = this.findBlocksToPlacePrioritizeTrash(b);
            if (slot != -1) {
               this.place(b, new HighwayBuilder.MBPIteratorFilter(b.blockPosProvider.getLiquids(), (pos) -> !pos.getState().method_26227().method_15769()), slot, Forward);
            }
         }

         // $FF: synthetic method
         private static boolean lambda$tick$0(MBlockPos pos) {
            return !pos.getState().method_26227().method_15769();
         }
      },
      MineFront {
         protected void start(HighwayBuilder b) {
            this.mine(b, b.blockPosProvider.getFront(), true, Forward, this);
         }

         protected void tick(HighwayBuilder b) {
            this.mine(b, b.blockPosProvider.getFront(), true, Forward, this);
         }
      },
      MineFloor {
         protected void start(HighwayBuilder b) {
            this.mine(b, b.blockPosProvider.getFloor(), false, Forward, this);
         }

         protected void tick(HighwayBuilder b) {
            this.mine(b, b.blockPosProvider.getFloor(), false, Forward, this);
         }
      },
      MineRailings {
         protected void start(HighwayBuilder b) {
            this.mine(b, b.blockPosProvider.getRailings(0), false, Forward, this);
         }

         protected void tick(HighwayBuilder b) {
            this.mine(b, b.blockPosProvider.getRailings(0), false, Forward, this);
         }
      },
      MineAboveRailings {
         protected void start(HighwayBuilder b) {
            this.mine(b, b.blockPosProvider.getRailings(1), true, Forward, this);
         }

         protected void tick(HighwayBuilder b) {
            this.mine(b, b.blockPosProvider.getRailings(1), true, Forward, this);
         }
      },
      PlaceCornerBlock {
         protected void start(HighwayBuilder b) {
            int slot = this.findBlocksToPlacePrioritizeTrash(b);
            if (slot != -1) {
               this.place(b, b.blockPosProvider.getRailings(-1), slot, Forward);
            }
         }

         protected void tick(HighwayBuilder b) {
            int slot = this.findBlocksToPlacePrioritizeTrash(b);
            if (slot != -1) {
               this.place(b, b.blockPosProvider.getRailings(-1), slot, Forward);
            }
         }
      },
      PlaceRailings {
         protected void start(HighwayBuilder b) {
            int slot = this.findBlocksToPlace(b);
            if (slot != -1) {
               this.place(b, b.blockPosProvider.getRailings(0), slot, Forward);
            }
         }

         protected void tick(HighwayBuilder b) {
            int slot = this.findBlocksToPlace(b);
            if (slot != -1) {
               this.place(b, b.blockPosProvider.getRailings(0), slot, Forward);
            }
         }
      },
      PlaceFloor {
         protected void start(HighwayBuilder b) {
            int slot = this.findBlocksToPlace(b);
            if (slot != -1) {
               this.place(b, b.blockPosProvider.getFloor(), slot, Forward);
            }
         }

         protected void tick(HighwayBuilder b) {
            int slot = this.findBlocksToPlace(b);
            if (slot != -1) {
               this.place(b, b.blockPosProvider.getFloor(), slot, Forward);
            }
         }
      },
      ThrowOutTrash {
         private int skipSlot;
         private boolean timerEnabled;
         private boolean firstTick;
         private boolean threwItems;
         private int timer;
         private static final class_1799[] ITEMS = new class_1799[27];

         protected void start(HighwayBuilder b) {
            int biggestCount = 0;

            for(int i = 0; i < HighwayBuilder.access$3400(b).field_1724.method_31548().method_67533().size(); ++i) {
               class_1799 itemStack = HighwayBuilder.access$3500(b).field_1724.method_31548().method_5438(i);
               if (itemStack.method_7909() instanceof class_1747 && ((List)b.trashItems.get()).contains(itemStack.method_7909()) && itemStack.method_7947() > biggestCount) {
                  biggestCount = itemStack.method_7947();
                  this.skipSlot = i;
                  if (biggestCount >= 64) {
                     break;
                  }
               }
            }

            if (biggestCount == 0) {
               this.skipSlot = -1;
            }

            this.timerEnabled = false;
            this.firstTick = true;
            this.threwItems = false;
         }

         protected void tick(HighwayBuilder b) {
            if (this.timerEnabled) {
               if (this.timer > 0) {
                  --this.timer;
               } else {
                  b.setState(b.lastState);
               }

            } else {
               HighwayBuilder.access$3600(b).field_1724.method_36456(b.dir.opposite().yaw);
               HighwayBuilder.access$3700(b).field_1724.method_36457(-25.0F);
               if (this.firstTick) {
                  this.firstTick = false;
               } else if (!HighwayBuilder.access$3800(b).field_1724.field_7512.method_34255().method_7960()) {
                  InvUtils.dropHand();
               } else {
                  for(int i = 0; i < HighwayBuilder.access$3900(b).field_1724.method_31548().method_67533().size(); ++i) {
                     if (i != this.skipSlot) {
                        class_1799 itemStack = HighwayBuilder.access$4000(b).field_1724.method_31548().method_5438(i);
                        if (((List)b.trashItems.get()).contains(itemStack.method_7909())) {
                           InvUtils.drop().slot(i);
                           this.threwItems = true;
                           return;
                        }

                        if ((Boolean)b.ejectUselessShulkers.get() && Utils.isShulker(itemStack.method_7909())) {
                           Utils.getItemsInContainerItem(itemStack, ITEMS);
                           boolean eject = true;

                           for(class_1799 stack : ITEMS) {
                              class_1792 var10 = stack.method_7909();
                              if (var10 instanceof class_1747) {
                                 class_1747 bi = (class_1747)var10;
                                 if (((List)b.blocksToPlace.get()).contains(bi.method_7711()) || ((List)b.blocksToPlace.get()).contains(class_2246.field_10540) && bi == class_1802.field_8466) {
                                    eject = false;
                                    break;
                                 }
                              }

                              if (stack.method_31573(class_3489.field_42614)) {
                                 eject = false;
                                 break;
                              }

                              if (stack.method_57826(class_9334.field_50075) && !((List)((AutoEat)Modules.get().get(AutoEat.class)).blacklist.get()).contains(stack.method_7909())) {
                                 eject = false;
                                 break;
                              }
                           }

                           if (eject) {
                              InvUtils.drop().slot(i);
                              this.threwItems = true;
                              return;
                           }
                        }
                     }
                  }

                  this.timerEnabled = true;
                  this.timer = this.threwItems ? 10 : 1;
               }
            }
         }
      },
      PlaceEChestBlockade {
         protected void tick(HighwayBuilder b) {
            int slot = this.findBlocksToPlacePrioritizeTrash(b);
            if (slot != -1) {
               this.place(b, b.blockPosProvider.getBlockade(false, b.blockadeType.get()), slot, MineEnderChests);
            }
         }
      },
      MineEChestBlockade {
         protected void tick(HighwayBuilder b) {
            this.mine(b, b.blockPosProvider.getBlockade(true, b.blockadeType.get()), true, Center, Forward);
         }
      },
      MineEnderChests {
         private static final MBlockPos pos = new MBlockPos();
         private int minimumObsidian;
         private boolean first;
         private boolean primed;
         private boolean stopTimerEnabled;
         private int stopTimer;
         private int moveTimer;
         private int rebreakTimer;
         private int timeout;

         protected void start(HighwayBuilder b) {
            if (b.lastState != Center && b.lastState != ThrowOutTrash && b.lastState != PlaceEChestBlockade) {
               b.setState(Center);
            } else if (b.lastState == Center) {
               b.setState(ThrowOutTrash);
            } else if (b.lastState == ThrowOutTrash) {
               b.setState(PlaceEChestBlockade);
            } else {
               int emptySlots = 0;

               for(int i = 0; i < HighwayBuilder.access$4100(b).field_1724.method_31548().method_67533().size(); ++i) {
                  if (HighwayBuilder.access$4200(b).field_1724.method_31548().method_5438(i).method_7960()) {
                     ++emptySlots;
                  }
               }

               if (emptySlots == 0) {
                  b.error("No empty slots.");
               } else {
                  int minimumSlots = Math.max(emptySlots - (Integer)b.minEmpty.get(), 1);
                  this.minimumObsidian = minimumSlots * 64;
                  this.first = true;
                  this.moveTimer = this.timeout = 0;
                  this.stopTimerEnabled = false;
                  this.primed = false;
               }
            }
         }

         protected void tick(HighwayBuilder b) {
            if (this.stopTimerEnabled) {
               if (this.stopTimer > 0) {
                  --this.stopTimer;
               } else {
                  b.setState(MineEChestBlockade);
               }

            } else {
               HorizontalDirection dir = b.dir.diagonal ? b.dir.rotateLeft().rotateLeftSkipOne() : b.dir.opposite();
               pos.set((class_1297)HighwayBuilder.access$4300(b).field_1724).offset(dir);
               if (this.moveTimer > 0) {
                  HighwayBuilder.access$4400(b).field_1724.method_36456(dir.yaw);
                  b.input.forward(this.moveTimer > 2);
                  --this.moveTimer;
               } else {
                  int obsidianCount = 0;
                  double var10004 = (double)pos.x;
                  double var10005 = (double)pos.y;
                  double var10006 = (double)pos.z;

                  for(class_1297 entity : HighwayBuilder.access$4600(b).field_1687.method_8335(HighwayBuilder.access$4500(b).field_1724, new class_238(var10004, var10005, var10006, (double)(pos.x + 1), (double)(pos.y + 2), (double)(pos.z + 1)))) {
                     if (entity instanceof class_1542) {
                        class_1542 itemEntity = (class_1542)entity;
                        if (itemEntity.method_6983().method_7909() == class_1802.field_8281) {
                           obsidianCount += itemEntity.method_6983().method_7947();
                        }
                     }
                  }

                  for(int i = 0; i < HighwayBuilder.access$4700(b).field_1724.method_31548().method_67533().size(); ++i) {
                     class_1799 itemStack = HighwayBuilder.access$4800(b).field_1724.method_31548().method_5438(i);
                     if (itemStack.method_7909() == class_1802.field_8281) {
                        obsidianCount += itemStack.method_7947();
                     }
                  }

                  if (obsidianCount >= this.minimumObsidian) {
                     this.stopTimerEnabled = true;
                     this.stopTimer = 12;
                  } else {
                     class_2338 bp = pos.getBlockPos();
                     class_2680 blockState = HighwayBuilder.access$4900(b).field_1687.method_8320(bp);
                     if (blockState.method_26204() == class_2246.field_10443) {
                        class_437 var7 = HighwayBuilder.access$5000(b).field_1755;
                        if (var7 instanceof class_476) {
                           class_476 screen = (class_476)var7;
                           if (((class_1707)screen.method_17577()).field_7763 != b.syncId) {
                              return;
                           }

                           HighwayBuilder.access$5100(b).field_1755.method_25419();
                        }

                        if (!EChestMemory.isKnown()) {
                           if ((b.rotation.get()).place) {
                              Rotations.rotate(Rotations.getYaw(bp), Rotations.getPitch(bp), () -> HighwayBuilder.access$5900(b).field_1761.method_2896(HighwayBuilder.access$5800(b).field_1724, class_1268.field_5808, new class_3965(class_243.method_24953(bp), class_2350.field_11036, bp, false)));
                           } else {
                              HighwayBuilder.access$5300(b).field_1761.method_2896(HighwayBuilder.access$5200(b).field_1724, class_1268.field_5808, new class_3965(class_243.method_24953(bp), class_2350.field_11036, bp, false));
                           }

                           return;
                        }

                        if (this.first) {
                           this.moveTimer = 8;
                           this.first = false;
                           return;
                        }

                        int slot = this.findAndMoveBestToolToHotbar(b, blockState, true);
                        if (slot == -1) {
                           b.error("Cannot find pickaxe without silk touch to mine ender chests.");
                           return;
                        }

                        InvUtils.swap(slot, false);
                        if ((Boolean)b.rebreakEchests.get() && this.primed) {
                           ++this.timeout;
                           if (this.timeout > 60) {
                              this.primed = false;
                              this.timeout = 0;
                              return;
                           }

                           if (this.rebreakTimer > 0) {
                              --this.rebreakTimer;
                              return;
                           }

                           this.rebreakTimer = (Integer)b.rebreakTimer.get();
                           if ((b.rotation.get()).mine) {
                              Rotations.rotate(Rotations.getYaw(bp), Rotations.getPitch(bp), () -> HighwayBuilder.access$5700(b).field_1761.method_41931(HighwayBuilder.access$5600(b).field_1687, (sequence) -> new class_2846(class_2847.field_12973, bp, BlockUtils.getDirection(bp), sequence)));
                           } else {
                              HighwayBuilder.access$5500(b).field_1761.method_41931(HighwayBuilder.access$5400(b).field_1687, (sequence) -> new class_2846(class_2847.field_12973, bp, BlockUtils.getDirection(bp), sequence));
                           }
                        } else if ((b.rotation.get()).mine) {
                           Rotations.rotate(Rotations.getYaw(bp), Rotations.getPitch(bp), () -> BlockUtils.breakBlock(bp, true));
                        } else {
                           BlockUtils.breakBlock(bp, true);
                        }
                     } else {
                        int slot = this.findAndMoveToHotbar(b, (itemStack) -> itemStack.method_7909() == class_1802.field_8466);
                        if (slot == -1 || this.countItem(b, (stack) -> stack.method_7909().equals(class_1802.field_8466)) <= (Integer)b.saveEchests.get()) {
                           this.stopTimerEnabled = true;
                           this.stopTimer = 12;
                           return;
                        }

                        if (this.countItem(b, (stack) -> stack.method_31573(class_3489.field_42614)) <= (Integer)b.savePickaxes.get() && ((Boolean)b.searchEnderChest.get() || (Boolean)b.searchShulkers.get())) {
                           b.restockTask.setPickaxes();
                        }

                        if (!this.first) {
                           this.primed = true;
                        }

                        BlockUtils.place(bp, class_1268.field_5808, slot, (b.rotation.get()).place, 0, true, true, false);
                        this.timeout = 0;
                     }

                  }
               }
            }
         }

         // $FF: synthetic method
         private static boolean lambda$tick$7(class_1799 stack) {
            return stack.method_31573(class_3489.field_42614);
         }

         // $FF: synthetic method
         private static boolean lambda$tick$6(class_1799 stack) {
            return stack.method_7909().equals(class_1802.field_8466);
         }

         // $FF: synthetic method
         private static boolean lambda$tick$5(class_1799 itemStack) {
            return itemStack.method_7909() == class_1802.field_8466;
         }

         // $FF: synthetic method
         private static void lambda$tick$4(class_2338 bp) {
            BlockUtils.breakBlock(bp, true);
         }

         // $FF: synthetic method
         private static class_2596 lambda$tick$3(class_2338 bp, int sequence) {
            return new class_2846(class_2847.field_12973, bp, BlockUtils.getDirection(bp), sequence);
         }

         // $FF: synthetic method
         private static void lambda$tick$2(HighwayBuilder b, class_2338 bp) {
            HighwayBuilder.access$5700(b).field_1761.method_41931(HighwayBuilder.access$5600(b).field_1687, (sequence) -> new class_2846(class_2847.field_12973, bp, BlockUtils.getDirection(bp), sequence));
         }

         // $FF: synthetic method
         private static class_2596 lambda$tick$1(class_2338 bp, int sequence) {
            return new class_2846(class_2847.field_12973, bp, BlockUtils.getDirection(bp), sequence);
         }

         // $FF: synthetic method
         private static void lambda$tick$0(HighwayBuilder b, class_2338 bp) {
            HighwayBuilder.access$5900(b).field_1761.method_2896(HighwayBuilder.access$5800(b).field_1724, class_1268.field_5808, new class_3965(class_243.method_24953(bp), class_2350.field_11036, bp, false));
         }
      },
      Restock {
         private static final MBlockPos pos = new MBlockPos();
         private static final class_1799[] ITEMS = new class_1799[27];
         private int minimumSlots;
         private int stopTimer;
         private int delayTimer;
         private boolean breakContainer;
         private boolean indicateStopping;
         private Predicate<class_1799> shulkerPredicate;
         private int slot;

         private {
            this.slot = -1;
         }

         protected void start(HighwayBuilder b) {
            this.slot = -1;
            if (this.shulkerPredicate == null) {
               this.setShulkerPredicate(b);
            }

            if (b.restockTask.tasksInactive()) {
               b.setState(Forward);
            } else if (b.lastState != Center && b.lastState != ThrowOutTrash && b.lastState != PlaceShulkerBlockade && b.lastState != this) {
               b.setState(Center);
            } else if (b.lastState == Center) {
               b.setState(ThrowOutTrash);
            } else {
               if (this.slot == -1 && (Boolean)b.searchShulkers.get()) {
                  this.slot = this.findAndMoveToHotbar(b, this.shulkerPredicate);
                  if (this.slot != -1 && b.lastState != PlaceShulkerBlockade) {
                     b.setState(PlaceShulkerBlockade);
                  }
               }

               if (this.slot == -1 && (Boolean)b.searchEnderChest.get() && this.countItem(b, (stack) -> stack.method_7909().equals(class_1802.field_8466)) > 0) {
                  boolean stop = EChestMemory.isKnown();
                  if (EChestMemory.isKnown()) {
                     for(class_1799 stack : EChestMemory.ITEMS) {
                        if (b.restockTask.materials) {
                           class_1792 var6 = stack.method_7909();
                           if (var6 instanceof class_1747) {
                              class_1747 bi = (class_1747)var6;
                              if (((List)b.blocksToPlace.get()).contains(bi.method_7711()) || ((List)b.blocksToPlace.get()).contains(class_2246.field_10540) && bi == class_1802.field_8466) {
                                 stop = false;
                                 break;
                              }
                           }
                        }

                        if (b.restockTask.pickaxes && stack.method_31573(class_3489.field_42614)) {
                           stop = false;
                           break;
                        }

                        if (b.restockTask.food && stack.method_57826(class_9334.field_50075) && !((List)((AutoEat)Modules.get().get(AutoEat.class)).blacklist.get()).contains(stack.method_7909())) {
                           stop = false;
                           break;
                        }

                        if ((Boolean)b.searchShulkers.get() && this.shulkerPredicate.test(stack)) {
                           stop = false;
                           break;
                        }
                     }
                  }

                  if (!stop) {
                     this.slot = this.findAndMoveToHotbar(b, (itemStack) -> itemStack.method_7909() == class_1802.field_8466);
                  }
               }

               if (this.slot != -1) {
                  int restockSlots = -(Integer)b.minEmpty.get();

                  for(int i = 0; i < HighwayBuilder.access$6000(b).field_1724.method_31548().method_67533().size(); ++i) {
                     if (HighwayBuilder.access$6100(b).field_1724.method_31548().method_5438(i).method_7960()) {
                        ++restockSlots;
                     }
                  }

                  if (restockSlots <= 0) {
                     b.error("No empty slots for restocking items.");
                  } else {
                     this.minimumSlots = b.restockTask.materials ? restockSlots : 1;
                     HorizontalDirection dir = b.dir.diagonal ? b.dir.rotateLeft().rotateLeftSkipOne() : b.dir.opposite();
                     pos.set((class_1297)HighwayBuilder.access$6200(b).field_1724).offset(dir);
                     this.breakContainer = HighwayBuilder.access$6300(b).field_1687.method_8320(pos.getBlockPos()).method_26204() == class_2246.field_10443;
                     this.indicateStopping = false;
                     this.delayTimer = (Integer)b.inventoryDelay.get();
                  }
               } else {
                  boolean restockOccurred = b.restockTask.materials && (this.hasItem(b, (stack) -> {
                     class_1792 patt0$temp = stack.method_7909();
                     boolean var10000;
                     if (patt0$temp instanceof class_1747 bi) {
                        if (((List)b.blocksToPlace.get()).contains(bi.method_7711())) {
                           var10000 = true;
                           return var10000;
                        }
                     }

                     var10000 = false;
                     return var10000;
                  }) || ((List)b.blocksToPlace.get()).contains(class_2246.field_10540) && this.countItem(b, (itemStack) -> itemStack.method_7909() == class_1802.field_8466) > (Integer)b.saveEchests.get()) || b.restockTask.pickaxes && this.countItem(b, (itemStack) -> itemStack.method_31573(class_3489.field_42614)) > (Integer)b.savePickaxes.get() || b.restockTask.food && this.hasItem(b, (itemStack) -> itemStack.method_57826(class_9334.field_50075) && !((List)((AutoEat)Modules.get().get(AutoEat.class)).blacklist.get()).contains(itemStack.method_7909()));
                  if (restockOccurred) {
                     b.setState(ThrowOutTrash, Forward);
                  } else {
                     b.error("Unable to perform restock for '" + b.restockTask.item() + "'.");
                  }

               }
            }
         }

         protected void tick(HighwayBuilder b) {
            if (this.slot == -1) {
               b.error("Invalid restocking action.");
            } else if (this.indicateStopping && !this.breakContainer) {
               if (this.stopTimer > 0) {
                  --this.stopTimer;
               } else if (b.lastState == PlaceShulkerBlockade) {
                  b.setState(MineShulkerBlockade);
               } else {
                  b.setState(ThrowOutTrash, Forward);
               }

            } else if (b.restockTask.tasksInactive()) {
               b.setState(Forward);
            } else if (this.delayTimer > 0) {
               --this.delayTimer;
            } else {
               int slotsPulled = 0;
               if (b.restockTask.materials) {
                  slotsPulled += this.countSlots(b, (itemStack) -> {
                     class_1792 patt0$temp = itemStack.method_7909();
                     boolean var10000;
                     if (patt0$temp instanceof class_1747 bi) {
                        if (((List)b.blocksToPlace.get()).contains(bi.method_7711())) {
                           var10000 = true;
                           return var10000;
                        }
                     }

                     var10000 = false;
                     return var10000;
                  });
                  if (((List)b.blocksToPlace.get()).contains(class_2246.field_10540)) {
                     slotsPulled += (this.countItem(b, (itemStack) -> itemStack.method_7909() == class_1802.field_8466) - (Integer)b.saveEchests.get()) * 8 / 64;
                  }
               }

               if (b.restockTask.pickaxes) {
                  slotsPulled += this.countSlots(b, (itemStack) -> itemStack.method_31573(class_3489.field_42614)) - (Integer)b.savePickaxes.get();
               }

               if (b.restockTask.food) {
                  slotsPulled += this.countSlots(b, (itemStack) -> itemStack.method_57826(class_9334.field_50075) && !((List)((AutoEat)Modules.get().get(AutoEat.class)).blacklist.get()).contains(itemStack.method_7909()));
               }

               if (slotsPulled >= this.minimumSlots && !this.indicateStopping) {
                  this.indicateStopping = true;
                  this.breakContainer = true;
                  this.stopTimer = 12;
                  if (HighwayBuilder.access$6400(b).field_1755 != null) {
                     HighwayBuilder.access$6500(b).field_1755.method_25419();
                  }

               } else {
                  class_2338 blockPos = pos.getBlockPos();
                  class_2680 blockState = HighwayBuilder.access$6600(b).field_1687.method_8320(blockPos);
                  class_2248 var10000 = blockState.method_26204();
                  Objects.requireNonNull(var10000);
                  class_2248 var5 = var10000;
                  byte var6 = 0;
                  //$FF: var6->value
                  //0->net/minecraft/class_2480
                  //1->net/minecraft/class_2336
                  //2->net/minecraft/class_2189
                  switch (var5.typeSwitch<invokedynamic>(var5, var6)) {
                     case 0:
                        class_2480 ignored = (class_2480)var5;
                        class_437 var15 = HighwayBuilder.access$6700(b).field_1755;
                        if (var15 instanceof class_495) {
                           class_495 screen = (class_495)var15;
                           if (((class_1733)screen.method_17577()).field_7763 != b.syncId) {
                              return;
                           }

                           class_1263 inv = ((ShulkerBoxScreenHandlerAccessor)screen.method_17577()).meteor$getInventory();
                           if (this.restockItems(b, inv)) {
                              this.delayTimer = (Integer)b.inventoryDelay.get();
                              return;
                           }

                           HighwayBuilder.access$6800(b).field_1755.method_25419();
                           this.breakContainer = true;
                        } else {
                           if (!(Boolean)b.searchShulkers.get()) {
                              this.breakContainer = true;
                           }

                           this.handleContainerBlock(b, blockPos);
                        }
                        break;
                     case 1:
                        class_2336 ignored = (class_2336)var5;
                        class_437 var10 = HighwayBuilder.access$6900(b).field_1755;
                        if (var10 instanceof class_476) {
                           class_476 screen = (class_476)var10;
                           if (((class_1707)screen.method_17577()).field_7763 != b.syncId) {
                              return;
                           }

                           class_1263 inv = ((class_1707)screen.method_17577()).method_7629();
                           if (this.restockItems(b, inv)) {
                              this.delayTimer = (Integer)b.inventoryDelay.get();
                              return;
                           }

                           if ((Boolean)b.searchShulkers.get()) {
                              int moveTo = InvUtils.findEmpty().slot();
                              if (moveTo != -1) {
                                 for(int i = 0; i < inv.method_5439(); ++i) {
                                    if (this.shulkerPredicate.test(inv.method_5438(i))) {
                                       InvUtils.move().fromId(i).to(moveTo);
                                       this.delayTimer = (Integer)b.inventoryDelay.get();
                                       break;
                                    }
                                 }
                              }
                           }

                           HighwayBuilder.access$7000(b).field_1755.method_25419();
                           this.breakContainer = true;
                        } else {
                           if (!(Boolean)b.searchEnderChest.get()) {
                              this.breakContainer = true;
                           }

                           this.handleContainerBlock(b, blockPos);
                        }
                        break;
                     case 2:
                        class_2189 ignored = (class_2189)var5;
                        if (this.breakContainer) {
                           this.breakContainer = false;
                           if (this.indicateStopping) {
                              b.restockTask.complete();
                           } else {
                              this.start(b);
                           }

                           return;
                        }

                        BlockUtils.place(blockPos, class_1268.field_5808, this.slot, (b.rotation.get()).place, 0, true, true, false);
                        break;
                     default:
                        b.error("Invalid block at container restocking position?");
                  }

               }
            }
         }

         private boolean restockItems(HighwayBuilder b, class_1263 inv) {
            if (b.restockTask.materials) {
               if (this.grabFromInventory(inv, (itemStack) -> {
                  class_1792 patt0$temp = itemStack.method_7909();
                  boolean var10000;
                  if (patt0$temp instanceof class_1747 bi) {
                     if (((List)b.blocksToPlace.get()).contains(bi.method_7711())) {
                        var10000 = true;
                        return var10000;
                     }
                  }

                  var10000 = false;
                  return var10000;
               })) {
                  return true;
               }

               if (((List)b.blocksToPlace.get()).contains(class_2246.field_10540) && this.grabFromInventory(inv, (itemStack) -> itemStack.method_7909() == class_1802.field_8466)) {
                  return true;
               }
            }

            if (b.restockTask.pickaxes && this.grabFromInventory(inv, (itemStack) -> itemStack.method_31573(class_3489.field_42614))) {
               return true;
            } else {
               return b.restockTask.food ? this.grabFromInventory(inv, (itemStack) -> itemStack.method_57826(class_9334.field_50075) && !((List)((AutoEat)Modules.get().get(AutoEat.class)).blacklist.get()).contains(itemStack.method_7909())) : false;
            }
         }

         private boolean grabFromInventory(class_1263 inv, Predicate<class_1799> filterItem) {
            for(int i = 0; i < inv.method_5439(); ++i) {
               if (filterItem.test(inv.method_5438(i))) {
                  InvUtils.shiftClick().slotId(i);
                  return true;
               }
            }

            return false;
         }

         private void setShulkerPredicate(HighwayBuilder b) {
            this.shulkerPredicate = (itemStack) -> {
               if (!Utils.isShulker(itemStack.method_7909())) {
                  return false;
               } else {
                  Utils.getItemsInContainerItem(itemStack, ITEMS);

                  for(class_1799 stack : ITEMS) {
                     if (b.restockTask.materials) {
                        class_1792 patt0$temp = stack.method_7909();
                        if (patt0$temp instanceof class_1747) {
                           class_1747 bi = (class_1747)patt0$temp;
                           if (((List)b.blocksToPlace.get()).contains(bi.method_7711()) || ((List)b.blocksToPlace.get()).contains(class_2246.field_10540) && bi == class_1802.field_8466) {
                              return true;
                           }
                        }
                     }

                     if (b.restockTask.pickaxes && stack.method_31573(class_3489.field_42614)) {
                        return true;
                     }

                     if (b.restockTask.food && stack.method_57826(class_9334.field_50075) && !((List)((AutoEat)Modules.get().get(AutoEat.class)).blacklist.get()).contains(stack.method_7909())) {
                        return true;
                     }
                  }

                  return false;
               }
            };
         }

         private void handleContainerBlock(HighwayBuilder b, class_2338 bp) {
            if (this.breakContainer) {
               class_2680 state = HighwayBuilder.access$7100(b).field_1687.method_8320(bp);
               int toolSlot = this.findAndMoveBestToolToHotbar(b, state, false);
               InvUtils.swap(toolSlot, false);
               if ((b.rotation.get()).mine) {
                  Rotations.rotate(Rotations.getYaw(bp), Rotations.getPitch(bp), () -> BlockUtils.breakBlock(bp, true));
               } else {
                  BlockUtils.breakBlock(bp, true);
               }
            } else {
               if ((b.rotation.get()).place) {
                  Rotations.rotate(Rotations.getYaw(bp), Rotations.getPitch(bp), () -> HighwayBuilder.access$7700(b).field_1761.method_2896(HighwayBuilder.access$7600(b).field_1724, class_1268.field_5808, new class_3965(class_243.method_24953(bp), class_2350.field_11036, bp, false)));
               } else {
                  HighwayBuilder.access$7300(b).field_1761.method_2896(HighwayBuilder.access$7200(b).field_1724, class_1268.field_5808, new class_3965(class_243.method_24953(bp), class_2350.field_11036, bp, false));
               }

               this.delayTimer = (Integer)b.inventoryDelay.get();
            }

         }

         private int countSlots(HighwayBuilder b, Predicate<class_1799> predicate) {
            int count = 0;

            for(int i = 0; i < HighwayBuilder.access$7400(b).field_1724.method_31548().method_67533().size(); ++i) {
               class_1799 stack = HighwayBuilder.access$7500(b).field_1724.method_31548().method_5438(i);
               if (predicate.test(stack)) {
                  ++count;
               }
            }

            return count;
         }

         // $FF: synthetic method
         private static void lambda$handleContainerBlock$16(HighwayBuilder b, class_2338 bp) {
            HighwayBuilder.access$7700(b).field_1761.method_2896(HighwayBuilder.access$7600(b).field_1724, class_1268.field_5808, new class_3965(class_243.method_24953(bp), class_2350.field_11036, bp, false));
         }

         // $FF: synthetic method
         private static void lambda$handleContainerBlock$15(class_2338 bp) {
            BlockUtils.breakBlock(bp, true);
         }

         // $FF: synthetic method
         private static boolean lambda$setShulkerPredicate$14(HighwayBuilder b, class_1799 itemStack) {
            if (!Utils.isShulker(itemStack.method_7909())) {
               return false;
            } else {
               Utils.getItemsInContainerItem(itemStack, ITEMS);

               for(class_1799 stack : ITEMS) {
                  if (b.restockTask.materials) {
                     class_1792 patt0$temp = stack.method_7909();
                     if (patt0$temp instanceof class_1747) {
                        class_1747 bi = (class_1747)patt0$temp;
                        if (((List)b.blocksToPlace.get()).contains(bi.method_7711()) || ((List)b.blocksToPlace.get()).contains(class_2246.field_10540) && bi == class_1802.field_8466) {
                           return true;
                        }
                     }
                  }

                  if (b.restockTask.pickaxes && stack.method_31573(class_3489.field_42614)) {
                     return true;
                  }

                  if (b.restockTask.food && stack.method_57826(class_9334.field_50075) && !((List)((AutoEat)Modules.get().get(AutoEat.class)).blacklist.get()).contains(stack.method_7909())) {
                     return true;
                  }
               }

               return false;
            }
         }

         // $FF: synthetic method
         private static boolean lambda$restockItems$13(class_1799 itemStack) {
            return itemStack.method_57826(class_9334.field_50075) && !((List)((AutoEat)Modules.get().get(AutoEat.class)).blacklist.get()).contains(itemStack.method_7909());
         }

         // $FF: synthetic method
         private static boolean lambda$restockItems$12(class_1799 itemStack) {
            return itemStack.method_31573(class_3489.field_42614);
         }

         // $FF: synthetic method
         private static boolean lambda$restockItems$11(class_1799 itemStack) {
            return itemStack.method_7909() == class_1802.field_8466;
         }

         // $FF: synthetic method
         private static boolean lambda$restockItems$10(HighwayBuilder b, class_1799 itemStack) {
            class_1792 patt0$temp = itemStack.method_7909();
            boolean var10000;
            if (patt0$temp instanceof class_1747 bi) {
               if (((List)b.blocksToPlace.get()).contains(bi.method_7711())) {
                  var10000 = true;
                  return var10000;
               }
            }

            var10000 = false;
            return var10000;
         }

         // $FF: synthetic method
         private static boolean lambda$tick$9(class_1799 itemStack) {
            return itemStack.method_57826(class_9334.field_50075) && !((List)((AutoEat)Modules.get().get(AutoEat.class)).blacklist.get()).contains(itemStack.method_7909());
         }

         // $FF: synthetic method
         private static boolean lambda$tick$8(class_1799 itemStack) {
            return itemStack.method_31573(class_3489.field_42614);
         }

         // $FF: synthetic method
         private static boolean lambda$tick$7(class_1799 itemStack) {
            return itemStack.method_7909() == class_1802.field_8466;
         }

         // $FF: synthetic method
         private static boolean lambda$tick$6(HighwayBuilder b, class_1799 itemStack) {
            class_1792 patt0$temp = itemStack.method_7909();
            boolean var10000;
            if (patt0$temp instanceof class_1747 bi) {
               if (((List)b.blocksToPlace.get()).contains(bi.method_7711())) {
                  var10000 = true;
                  return var10000;
               }
            }

            var10000 = false;
            return var10000;
         }

         // $FF: synthetic method
         private static boolean lambda$start$5(class_1799 itemStack) {
            return itemStack.method_57826(class_9334.field_50075) && !((List)((AutoEat)Modules.get().get(AutoEat.class)).blacklist.get()).contains(itemStack.method_7909());
         }

         // $FF: synthetic method
         private static boolean lambda$start$4(class_1799 itemStack) {
            return itemStack.method_31573(class_3489.field_42614);
         }

         // $FF: synthetic method
         private static boolean lambda$start$3(class_1799 itemStack) {
            return itemStack.method_7909() == class_1802.field_8466;
         }

         // $FF: synthetic method
         private static boolean lambda$start$2(HighwayBuilder b, class_1799 stack) {
            class_1792 patt0$temp = stack.method_7909();
            boolean var10000;
            if (patt0$temp instanceof class_1747 bi) {
               if (((List)b.blocksToPlace.get()).contains(bi.method_7711())) {
                  var10000 = true;
                  return var10000;
               }
            }

            var10000 = false;
            return var10000;
         }

         // $FF: synthetic method
         private static boolean lambda$start$1(class_1799 itemStack) {
            return itemStack.method_7909() == class_1802.field_8466;
         }

         // $FF: synthetic method
         private static boolean lambda$start$0(class_1799 stack) {
            return stack.method_7909().equals(class_1802.field_8466);
         }
      },
      PlaceShulkerBlockade {
         protected void tick(HighwayBuilder b) {
            int slot = this.findBlocksToPlacePrioritizeTrash(b);
            if (slot != -1) {
               this.place(b, b.blockPosProvider.getBlockade(false, HighwayBuilder.BlockadeType.Shulker), slot, Restock);
            }
         }
      },
      MineShulkerBlockade {
         private boolean stopTimerEnabled;
         private int stopTimer;

         protected void start(HighwayBuilder b) {
            this.stopTimerEnabled = false;
            if (b.lastState == this) {
               this.stopTimerEnabled = true;
               this.stopTimer = 12;
            }

         }

         protected void tick(HighwayBuilder b) {
            if (!this.stopTimerEnabled) {
               this.mine(b, b.blockPosProvider.getBlockade(true, b.blockadeType.get()), true, this, this);
            } else {
               --this.stopTimer;
               if (this.stopTimer <= 0) {
                  b.setState(ThrowOutTrash, Forward);
               }
            }

         }
      },
      DefuseCrystalTraps {
         private int cooldown;
         private int shots;
         private class_1511 target;

         protected void start(HighwayBuilder b) {
            if (!InvUtils.find(class_1802.field_8102).found() || !InvUtils.find((Predicate)((itemStack) -> itemStack.method_7909() instanceof class_1744)).found() && !HighwayBuilder.access$7800(b).field_1724.method_31549().field_7477) {
               b.destroyCrystalTraps.set(false);
               b.warning("No bow found to destroy crystal traps with. Toggling the setting off.", new Object[0]);
               b.setState(Forward);
            }

            this.shots = this.cooldown = 0;
            this.target = null;
         }

         protected void tick(HighwayBuilder b) {
            if (this.cooldown > 0) {
               --this.cooldown;
            } else {
               if (!InvUtils.testInMainHand(class_1802.field_8102)) {
                  int slot = this.findAndMoveToHotbar(b, (itemStack) -> itemStack.method_7909() instanceof class_1753);
                  if (slot == -1) {
                     b.destroyCrystalTraps.set(false);
                     b.warning("No bow found to destroy crystal traps with. Toggling the setting off.", new Object[0]);
                     b.setState(Forward);
                     HighwayBuilder.access$8000(b).field_1761.method_2897(HighwayBuilder.access$7900(b).field_1724);
                     b.drawingBow = false;
                     return;
                  }

                  InvUtils.swap(slot, false);
               }

               class_1511 potentialTarget = (class_1511)TargetUtils.get((entity) -> {
                  if (entity instanceof class_1511 endCrystal) {
                     if (!PlayerUtils.isWithin((class_1297)endCrystal, (double)12.0F) && PlayerUtils.isWithin((class_1297)endCrystal, (double)24.0F)) {
                        if (b.ignoreCrystals.contains(endCrystal)) {
                           return false;
                        } else {
                           class_243 vec1 = new class_243((double)0.0F, (double)0.0F, (double)0.0F);
                           class_243 vec2 = new class_243((double)0.0F, (double)0.0F, (double)0.0F);
                           ((IVec3d)vec1).meteor$set(HighwayBuilder.access$9700(b).field_1724.method_23317(), HighwayBuilder.access$9800(b).field_1724.method_23318() + (double)HighwayBuilder.access$9900(b).field_1724.method_5751(), HighwayBuilder.access$10000(b).field_1724.method_23321());
                           ((IVec3d)vec2).meteor$set(entity.method_23317(), entity.method_23318() + (double)0.5F, entity.method_23321());
                           return HighwayBuilder.access$10200(b).field_1687.method_17742(new class_3959(vec1, vec2, class_3960.field_17558, class_242.field_1348, HighwayBuilder.access$10100(b).field_1724)).method_17783() == class_240.field_1333;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               }, SortPriority.LowestDistance);
               if (this.target == null || this.target.method_31481()) {
                  if (potentialTarget == null) {
                     b.setState(Forward);
                     HighwayBuilder.access$8200(b).field_1761.method_2897(HighwayBuilder.access$8100(b).field_1724);
                     b.drawingBow = false;
                     return;
                  }

                  this.target = potentialTarget;
                  this.shots = 0;
               }

               if (this.shots >= 3) {
                  b.ignoreCrystals.add(this.target);
                  b.warning("Detected potential hangup on a crystal. Adding it to ignore list and continuing forward.", new Object[0]);
                  b.setState(Forward);
                  HighwayBuilder.access$8400(b).field_1761.method_2897(HighwayBuilder.access$8300(b).field_1724);
                  b.drawingBow = false;
               } else {
                  HighwayBuilder.access$8500(b).field_1724.method_36456((float)Rotations.getYaw((class_1297)this.target));
                  float pitch = this.aim(b, this.target);
                  if (Float.isNaN(pitch)) {
                     HighwayBuilder.access$8600(b).field_1724.method_36457((float)Rotations.getPitch((class_1297)this.target));
                  } else {
                     HighwayBuilder.access$8700(b).field_1724.method_36457(pitch);
                  }

                  if (class_1753.method_7722(HighwayBuilder.access$8800(b).field_1724.method_6048() - 3) >= 1.0F) {
                     HighwayBuilder.access$9000(b).field_1761.method_2897(HighwayBuilder.access$8900(b).field_1724);
                     b.drawingBow = false;
                     this.cooldown = 20;
                     ++this.shots;
                  } else {
                     b.drawingBow = true;
                     HighwayBuilder.access$9200(b).field_1761.method_2919(HighwayBuilder.access$9100(b).field_1724, class_1268.field_5808);
                  }

               }
            }
         }

         private float aim(HighwayBuilder b, class_1297 target) {
            float velocity = class_1753.method_7722(HighwayBuilder.access$9300(b).field_1724.method_6048());
            class_243 pos = target.method_73189();
            double relativeX = pos.field_1352 - HighwayBuilder.access$9400(b).field_1724.method_23317();
            double relativeY = pos.field_1351 + (double)0.5F - HighwayBuilder.access$9500(b).field_1724.method_23320();
            double relativeZ = pos.field_1350 - HighwayBuilder.access$9600(b).field_1724.method_23321();
            double hDistance = Math.sqrt(relativeX * relativeX + relativeZ * relativeZ);
            double hDistanceSq = hDistance * hDistance;
            float g = 0.006F;
            float velocitySq = velocity * velocity;
            return (float)(-Math.toDegrees(Math.atan(((double)velocitySq - Math.sqrt((double)(velocitySq * velocitySq) - (double)g * ((double)g * hDistanceSq + (double)2.0F * relativeY * (double)velocitySq))) / ((double)g * hDistance))));
         }

         // $FF: synthetic method
         private static boolean lambda$tick$2(HighwayBuilder b, class_1297 entity) {
            if (entity instanceof class_1511 endCrystal) {
               if (!PlayerUtils.isWithin((class_1297)endCrystal, (double)12.0F) && PlayerUtils.isWithin((class_1297)endCrystal, (double)24.0F)) {
                  if (b.ignoreCrystals.contains(endCrystal)) {
                     return false;
                  } else {
                     class_243 vec1 = new class_243((double)0.0F, (double)0.0F, (double)0.0F);
                     class_243 vec2 = new class_243((double)0.0F, (double)0.0F, (double)0.0F);
                     ((IVec3d)vec1).meteor$set(HighwayBuilder.access$9700(b).field_1724.method_23317(), HighwayBuilder.access$9800(b).field_1724.method_23318() + (double)HighwayBuilder.access$9900(b).field_1724.method_5751(), HighwayBuilder.access$10000(b).field_1724.method_23321());
                     ((IVec3d)vec2).meteor$set(entity.method_23317(), entity.method_23318() + (double)0.5F, entity.method_23321());
                     return HighwayBuilder.access$10200(b).field_1687.method_17742(new class_3959(vec1, vec2, class_3960.field_17558, class_242.field_1348, HighwayBuilder.access$10100(b).field_1724)).method_17783() == class_240.field_1333;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }

         // $FF: synthetic method
         private static boolean lambda$tick$1(class_1799 itemStack) {
            return itemStack.method_7909() instanceof class_1753;
         }

         // $FF: synthetic method
         private static boolean lambda$start$0(class_1799 itemStack) {
            return itemStack.method_7909() instanceof class_1744;
         }
      };

      protected void start(HighwayBuilder b) {
      }

      protected abstract void tick(HighwayBuilder var1);

      protected void mine(HighwayBuilder b, MBPIterator it, boolean mineBlocksToPlace, State nextState, State lastState) {
         boolean breaking = false;
         boolean finishedBreaking = false;
         if ((Boolean)b.doubleMine.get()) {
            ArrayDeque<class_2338> toDoubleMine = new ArrayDeque();
            it.save();
            it.forEach((posx) -> {
               if (BlockUtils.canBreak(posx.getBlockPos(), posx.getState()) && (mineBlocksToPlace || !((List)b.blocksToPlace.get()).contains(posx.getState().method_26204())) && !BlockUtils.canInstaBreak(posx.getBlockPos()) && (!((SpeedMine)Modules.get().get(SpeedMine.class)).instamine() || (double)posx.getState().method_26165(b.mc.field_1724, b.mc.field_1687, posx.getBlockPos()) <= (double)0.5F) && (b.normalMining == null || !posx.getBlockPos().equals(b.normalMining.blockPos)) && (b.packetMining == null || !posx.getBlockPos().equals(b.packetMining.blockPos))) {
                  toDoubleMine.add(posx.getBlockPos().method_25503());
               }

            });
            it.restore();
            if (!toDoubleMine.isEmpty()) {
               int slot = this.findAndMoveBestToolToHotbar(b, b.mc.field_1687.method_8320((class_2338)toDoubleMine.peek()), false);
               if (slot == -1) {
                  return;
               }

               InvUtils.swap(slot, false);
               this.doubleMine(b, toDoubleMine);
            }

            if (b.normalMining != null || b.packetMining != null) {
               int slot = this.findAndMoveBestToolToHotbar(b, b.normalMining != null ? b.normalMining.blockState : b.packetMining.blockState, false);
               if (slot == -1) {
                  return;
               } else {
                  InvUtils.swap(slot, false);
                  return;
               }
            }
         }

         Iterator var14 = it.iterator();

         while(true) {
            class_2338 mcPos;
            label119: {
               while(var14.hasNext()) {
                  MBlockPos pos = (MBlockPos)var14.next();
                  if (b.count >= (Integer)b.blocksPerTick.get()) {
                     return;
                  }

                  if (b.breakTimer > 0) {
                     return;
                  }

                  class_2680 state = pos.getState();
                  if (!state.method_26215() && (mineBlocksToPlace || !((List)b.blocksToPlace.get()).contains(state.method_26204()))) {
                     int slot = this.findAndMoveBestToolToHotbar(b, state, false);
                     if (slot == -1) {
                        return;
                     }

                     InvUtils.swap(slot, false);
                     mcPos = pos.getBlockPos();
                     boolean multiBreak = (Integer)b.blocksPerTick.get() > 1 && BlockUtils.canInstaBreak(mcPos) && !(b.rotation.get()).mine;
                     if (!BlockUtils.canBreak(mcPos)) {
                        break label119;
                     }

                     if ((b.rotation.get()).mine) {
                        Rotations.rotate(Rotations.getYaw(mcPos), Rotations.getPitch(mcPos), () -> BlockUtils.breakBlock(mcPos, true));
                     } else {
                        BlockUtils.breakBlock(mcPos, true);
                     }

                     breaking = true;
                     b.breakTimer = (Integer)b.breakDelay.get();
                     if (!b.lastBreakingPos.equals(pos)) {
                        b.lastBreakingPos.set(pos);
                        ++b.blocksBroken;
                     }

                     ++b.count;
                     if (multiBreak) {
                        break label119;
                     }
                     break;
                  }
               }

               if (finishedBreaking || !breaking) {
                  b.setState(nextState, lastState);
               }

               return;
            }

            if (!it.hasNext() && BlockUtils.canInstaBreak(mcPos)) {
               finishedBreaking = true;
            }
         }
      }

      private void doubleMine(HighwayBuilder b, ArrayDeque<class_2338> blocks) {
         if (b.breakTimer <= 0) {
            if (b.normalMining == null) {
               DoubleMineBlock block = new DoubleMineBlock(b, (class_2338)blocks.pop());
               b.normalMining = block.startDestroying();
               b.breakTimer = (Integer)b.breakDelay.get();
               if (b.breakTimer > 0) {
                  return;
               }
            }

            if (!HighwayBuilder.DoubleMineBlock.rateLimited) {
               if (b.packetMining == null && !blocks.isEmpty()) {
                  DoubleMineBlock block = new DoubleMineBlock(b, (class_2338)blocks.pop());
                  if (block != null) {
                     b.packetMining = b.normalMining.packetMine();
                     b.normalMining = block.startDestroying();
                     b.breakTimer = (Integer)b.breakDelay.get();
                  }
               }

            }
         }
      }

      protected void place(HighwayBuilder b, MBPIterator it, int slot, State nextState) {
         boolean placed = false;
         boolean finishedPlacing = false;
         Iterator var7 = it.iterator();

         while(true) {
            while(true) {
               if (var7.hasNext()) {
                  MBlockPos pos = (MBlockPos)var7.next();
                  if (b.count >= it.placementsPerTick(b)) {
                     return;
                  }

                  if (b.placeTimer > 0) {
                     return;
                  }

                  if (pos.getBlockPos().method_19770(b.mc.field_1724.method_33571()) > (Double)b.placeRange.get() * (Double)b.placeRange.get()) {
                     continue;
                  }

                  if (!BlockUtils.place(pos.getBlockPos(), class_1268.field_5808, slot, (b.rotation.get()).place, 0, true, true, true)) {
                     break;
                  }

                  placed = true;
                  ++b.blocksPlaced;
                  b.placeTimer = (Integer)b.placeDelay.get();
                  ++b.count;
                  if ((Integer)b.placementsPerTick.get() != 1) {
                     break;
                  }
               }

               if (finishedPlacing || !placed) {
                  b.setState(nextState);
               }

               return;
            }

            if (!it.hasNext()) {
               finishedPlacing = true;
            }
         }
      }

      private int findSlot(HighwayBuilder b, Predicate<class_1799> predicate, boolean hotbar) {
         for(int i = hotbar ? 0 : 9; i < (hotbar ? 9 : b.mc.field_1724.method_31548().method_67533().size()); ++i) {
            if (predicate.test(b.mc.field_1724.method_31548().method_5438(i))) {
               return i;
            }
         }

         return -1;
      }

      protected int findHotbarSlot(HighwayBuilder b, boolean replaceTools) {
         int thrashSlot = -1;
         int slotsWithBlocks = 0;
         int slotWithLeastBlocks = -1;
         int slotWithLeastBlocksCount = Integer.MAX_VALUE;

         for(int i = 0; i < 9; ++i) {
            class_1799 itemStack = b.mc.field_1724.method_31548().method_5438(i);
            if (itemStack.method_7960()) {
               return i;
            }

            if (replaceTools && AutoTool.isTool(itemStack)) {
               return i;
            }

            if (((List)b.trashItems.get()).contains(itemStack.method_7909())) {
               thrashSlot = i;
            }

            class_1792 var10 = itemStack.method_7909();
            if (var10 instanceof class_1747 blockItem) {
               if (((List)b.blocksToPlace.get()).contains(blockItem.method_7711()) || ((List)b.blocksToPlace.get()).contains(class_2246.field_10540) && blockItem == class_1802.field_8466) {
                  ++slotsWithBlocks;
                  if (itemStack.method_7947() < slotWithLeastBlocksCount) {
                     slotWithLeastBlocksCount = itemStack.method_7947();
                     slotWithLeastBlocks = i;
                  }
               }
            }
         }

         if (thrashSlot != -1) {
            return thrashSlot;
         } else if (slotsWithBlocks > 0) {
            return slotWithLeastBlocks;
         } else {
            b.error("No empty space in hotbar.");
            return -1;
         }
      }

      protected boolean hasItem(HighwayBuilder b, Predicate<class_1799> predicate) {
         for(int i = 0; i < b.mc.field_1724.method_31548().method_67533().size(); ++i) {
            if (predicate.test(b.mc.field_1724.method_31548().method_5438(i))) {
               return true;
            }
         }

         return false;
      }

      protected int countItem(HighwayBuilder b, Predicate<class_1799> predicate) {
         int count = 0;

         for(int i = 0; i < b.mc.field_1724.method_31548().method_67533().size(); ++i) {
            class_1799 stack = b.mc.field_1724.method_31548().method_5438(i);
            if (predicate.test(stack)) {
               count += stack.method_7947();
            }
         }

         return count;
      }

      protected int findAndMoveToHotbar(HighwayBuilder b, Predicate<class_1799> predicate) {
         int slot = this.findSlot(b, predicate, true);
         if (slot != -1) {
            return slot;
         } else {
            int hotbarSlot = this.findHotbarSlot(b, false);
            if (hotbarSlot == -1) {
               return -1;
            } else {
               slot = this.findSlot(b, predicate, false);
               if (slot == -1) {
                  return -1;
               } else {
                  InvUtils.move().from(slot).toHotbar(hotbarSlot);
                  InvUtils.dropHand();
                  return hotbarSlot;
               }
            }
         }
      }

      protected int findAndMoveBestToolToHotbar(HighwayBuilder b, class_2680 blockState, boolean noSilkTouch) {
         if (b.mc.field_1724.method_68878()) {
            return b.mc.field_1724.method_31548().method_67532();
         } else {
            double bestScore = (double)-1.0F;
            int bestSlot = -1;

            for(int i = 0; i < b.mc.field_1724.method_31548().method_67533().size(); ++i) {
               double score = AutoTool.getScore(b.mc.field_1724.method_31548().method_5438(i), blockState, false, false, AutoTool.EnchantPreference.None, (itemStack) -> {
                  if (noSilkTouch && Utils.hasEnchantment(itemStack, class_1893.field_9099)) {
                     return false;
                  } else {
                     return !(Boolean)b.dontBreakTools.get() || itemStack.method_7936() - itemStack.method_7919() > itemStack.method_7936() * ((Integer)b.breakDurability.get() / 100);
                  }
               });
               if (score > bestScore) {
                  bestScore = score;
                  bestSlot = i;
               }
            }

            if (bestSlot == -1) {
               return b.mc.field_1724.method_31548().method_67532();
            } else {
               class_1799 bestStack = b.mc.field_1724.method_31548().method_5438(bestSlot);
               if (bestStack.method_31573(class_3489.field_42614)) {
                  int count = this.countItem(b, (stack) -> stack.method_31573(class_3489.field_42614));
                  if (count <= (Integer)b.savePickaxes.get() && (!b.restockTask.pickaxes || bestStack.method_7936() - bestStack.method_7919() <= bestStack.method_7936() * ((Integer)b.breakDurability.get() / 100))) {
                     if (b.restockTask.pickaxes || !(Boolean)b.searchEnderChest.get() && !(Boolean)b.searchShulkers.get()) {
                        b.error("Found less than the minimum amount of pickaxes required: " + count + "/" + ((Integer)b.savePickaxes.get() + 1));
                     } else {
                        b.restockTask.setPickaxes();
                     }

                     return -1;
                  }
               }

               if (bestSlot < 9) {
                  return bestSlot;
               } else {
                  int hotbarSlot = this.findHotbarSlot(b, true);
                  if (hotbarSlot == -1) {
                     return -1;
                  } else {
                     InvUtils.move().from(bestSlot).toHotbar(hotbarSlot);
                     InvUtils.dropHand();
                     return hotbarSlot;
                  }
               }
            }
         }
      }

      protected int findBlocksToPlace(HighwayBuilder b) {
         int slot = this.findAndMoveToHotbar(b, (itemStack) -> {
            class_1792 patt0$temp = itemStack.method_7909();
            boolean var10000;
            if (patt0$temp instanceof class_1747 blockItem) {
               if (((List)b.blocksToPlace.get()).contains(blockItem.method_7711())) {
                  var10000 = true;
                  return var10000;
               }
            }

            var10000 = false;
            return var10000;
         });
         if (slot != -1) {
            return slot;
         } else {
            if ((Boolean)b.mineEnderChests.get() && ((List)b.blocksToPlace.get()).contains(class_2246.field_10540) && this.countItem(b, (stack) -> stack.method_7909().equals(class_1802.field_8466)) > (Integer)b.saveEchests.get()) {
               b.setState(MineEnderChests);
            } else if (!(Boolean)b.searchEnderChest.get() && !(Boolean)b.searchShulkers.get()) {
               b.error("Out of blocks to place.");
            } else {
               b.restockTask.setMaterials();
            }

            return -1;
         }
      }

      protected int findBlocksToPlacePrioritizeTrash(HighwayBuilder b) {
         int slot = this.findAndMoveToHotbar(b, (itemStack) -> !(itemStack.method_7909() instanceof class_1747) ? false : ((List)b.trashItems.get()).contains(itemStack.method_7909()));
         return slot != -1 ? slot : this.findBlocksToPlace(b);
      }

      // $FF: synthetic method
      private static State[] $values() {
         return new State[]{Center, Forward, ReLevel, FillLiquids, MineFront, MineFloor, MineRailings, MineAboveRailings, PlaceCornerBlock, PlaceRailings, PlaceFloor, ThrowOutTrash, PlaceEChestBlockade, MineEChestBlockade, MineEnderChests, Restock, PlaceShulkerBlockade, MineShulkerBlockade, DefuseCrystalTraps};
      }
   }

   private interface MBPIterator extends Iterator<MBlockPos>, Iterable<MBlockPos> {
      void save();

      void restore();

      default @NotNull Iterator<MBlockPos> iterator() {
         return this;
      }

      default int placementsPerTick(HighwayBuilder b) {
         return (Integer)b.placementsPerTick.get();
      }
   }

   private static class MBPIteratorFilter implements MBPIterator {
      private final MBPIterator it;
      private final Predicate<MBlockPos> predicate;
      private MBlockPos pos;
      private boolean isOld = true;
      private boolean pisOld = true;

      public MBPIteratorFilter(MBPIterator it, Predicate<MBlockPos> predicate) {
         this.it = it;
         this.predicate = predicate;
      }

      public void save() {
         this.it.save();
         this.pisOld = this.isOld;
         this.isOld = true;
      }

      public void restore() {
         this.it.restore();
         this.isOld = this.pisOld;
      }

      public boolean hasNext() {
         if (this.isOld) {
            this.isOld = false;

            for(this.pos = null; this.it.hasNext(); this.pos = null) {
               this.pos = (MBlockPos)this.it.next();
               if (this.predicate.test(this.pos)) {
                  return true;
               }
            }
         }

         return this.pos != null && this.predicate.test(this.pos);
      }

      public MBlockPos next() {
         this.isOld = true;
         return this.pos;
      }
   }

   private class StraightBlockPosProvider implements IBlockPosProvider {
      private final MBlockPos pos = new MBlockPos();
      private final MBlockPos pos2 = new MBlockPos();

      public MBPIterator getFront() {
         this.pos.coerceBlockLevel(HighwayBuilder.this.mc.field_1724).offset(HighwayBuilder.this.dir).offset(HighwayBuilder.this.leftDir, HighwayBuilder.this.getWidthLeft());
         return new MBPIterator() {
            private int w;
            private int y;
            private int pw;
            private int py;
            // $FF: synthetic field
            final HighwayBuilder.StraightBlockPosProvider this$1;

            {
               this.this$1 = this$1;
            }

            public boolean hasNext() {
               return this.w < (Integer)this.this$1.this$0.width.get() && this.y < (Integer)this.this$1.this$0.height.get();
            }

            public MBlockPos next() {
               this.this$1.pos2.set(this.this$1.pos).offset(this.this$1.this$0.rightDir, this.w).add(0, this.y, 0);
               ++this.w;
               if (this.w >= (Integer)this.this$1.this$0.width.get()) {
                  this.w = 0;
                  ++this.y;
               }

               return this.this$1.pos2;
            }

            public void save() {
               this.pw = this.w;
               this.py = this.y;
               this.w = this.y = 0;
            }

            public void restore() {
               this.w = this.pw;
               this.y = this.py;
            }
         };
      }

      public MBPIterator getFloor() {
         this.pos.coerceBlockLevel(HighwayBuilder.this.mc.field_1724).offset(HighwayBuilder.this.dir).offset(HighwayBuilder.this.leftDir, HighwayBuilder.this.getWidthLeft()).add(0, -1, 0);
         return new MBPIterator() {
            private int w;
            private int pw;
            // $FF: synthetic field
            final HighwayBuilder.StraightBlockPosProvider this$1;

            {
               this.this$1 = this$1;
            }

            public boolean hasNext() {
               return this.w < (Integer)this.this$1.this$0.width.get();
            }

            public MBlockPos next() {
               return this.this$1.pos2.set(this.this$1.pos).offset(this.this$1.this$0.rightDir, this.w++);
            }

            public void save() {
               this.pw = this.w;
               this.w = 0;
            }

            public void restore() {
               this.w = this.pw;
            }
         };
      }

      public MBPIterator getRailings(int state) {
         this.pos.coerceBlockLevel(HighwayBuilder.this.mc.field_1724).offset(HighwayBuilder.this.dir);
         return new MBPIterator() {
            private int i;
            private int y;
            private int pi;
            private int py;
            // $FF: synthetic field
            final int val$state;
            // $FF: synthetic field
            final HighwayBuilder.StraightBlockPosProvider this$1;

            {
               this.this$1 = this$1;
               this.val$state = var2;
               this.y = this.val$state;
            }

            public boolean hasNext() {
               return this.i < 2 && this.y < (this.val$state == 1 ? (Integer)this.this$1.this$0.height.get() : this.val$state + 1);
            }

            public MBlockPos next() {
               if (this.i == 0) {
                  this.this$1.pos2.set(this.this$1.pos).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft() + 1).add(0, this.y, 0);
               } else {
                  this.this$1.pos2.set(this.this$1.pos).offset(this.this$1.this$0.rightDir, this.this$1.this$0.getWidthRight() + 1).add(0, this.y, 0);
               }

               ++this.y;
               if (this.y >= (this.val$state == 1 ? (Integer)this.this$1.this$0.height.get() : this.val$state + 1)) {
                  this.y = this.val$state;
                  ++this.i;
               }

               return this.this$1.pos2;
            }

            public void save() {
               this.pi = this.i;
               this.py = this.y;
               this.i = 0;
               this.y = this.val$state;
            }

            public void restore() {
               this.i = this.pi;
               this.y = this.py;
            }
         };
      }

      public MBPIterator getLiquids() {
         this.pos.coerceBlockLevel(HighwayBuilder.this.mc.field_1724).offset(HighwayBuilder.this.dir, 2).offset(HighwayBuilder.this.leftDir, HighwayBuilder.this.getWidthLeft() + ((Boolean)HighwayBuilder.this.mineAboveRailings.get() ? 2 : 1));
         return new MBPIterator() {
            private int w;
            private int y;
            private int pw;
            private int py;
            // $FF: synthetic field
            final HighwayBuilder.StraightBlockPosProvider this$1;

            {
               this.this$1 = this$1;
            }

            private int getWidth() {
               return (Integer)this.this$1.this$0.width.get() + ((Boolean)this.this$1.this$0.mineAboveRailings.get() ? 2 : 0);
            }

            public boolean hasNext() {
               return this.w < this.getWidth() + 2 && this.y < (Integer)this.this$1.this$0.height.get() + 1;
            }

            public MBlockPos next() {
               this.this$1.pos2.set(this.this$1.pos).offset(this.this$1.this$0.rightDir, this.w).add(0, this.y, 0);
               ++this.w;
               if (this.w >= this.getWidth() + 2) {
                  this.w = 0;
                  ++this.y;
               }

               return this.this$1.pos2;
            }

            public void save() {
               this.pw = this.w;
               this.py = this.y;
               this.w = this.y = 0;
            }

            public void restore() {
               this.w = this.pw;
               this.y = this.py;
            }
         };
      }

      public MBPIterator getBlockade(boolean mine, BlockadeType blockadeType) {
         return new MBPIterator() {
            private int i;
            private int y;
            private int pi;
            private int py;
            // $FF: synthetic field
            final boolean val$mine;
            // $FF: synthetic field
            final HighwayBuilder.BlockadeType val$blockadeType;
            // $FF: synthetic field
            final HighwayBuilder.StraightBlockPosProvider this$1;

            {
               this.this$1 = this$1;
               this.val$mine = var2;
               this.val$blockadeType = var3;
               this.i = this.val$mine ? -1 : 0;
            }

            private MBlockPos get(int i) {
               this.this$1.pos.coerceBlockLevel(HighwayBuilder.access$12400(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir.opposite());
               MBlockPos var10000;
               switch (i) {
                  case -1 -> var10000 = this.this$1.pos;
                  case 0 -> var10000 = this.this$1.pos.offset(this.this$1.this$0.dir.opposite());
                  case 1 -> var10000 = this.this$1.pos.offset(this.this$1.this$0.leftDir);
                  case 2 -> var10000 = this.this$1.pos.offset(this.this$1.this$0.rightDir);
                  case 3 -> var10000 = this.this$1.pos.offset(this.this$1.this$0.dir, 2);
                  case 4 -> var10000 = this.this$1.pos.offset(this.this$1.this$0.dir).offset(this.this$1.this$0.leftDir);
                  case 5 -> var10000 = this.this$1.pos.offset(this.this$1.this$0.dir).offset(this.this$1.this$0.rightDir);
                  default -> throw new IllegalStateException("Unexpected value: " + i);
               }

               return var10000;
            }

            public boolean hasNext() {
               return this.i < this.val$blockadeType.columns && this.y < 2;
            }

            public MBlockPos next() {
               if ((Integer)this.this$1.this$0.width.get() == 1 && (Boolean)this.this$1.this$0.railings.get() && this.i > 0 && this.y == 0) {
                  ++this.y;
               }

               MBlockPos pos = this.get(this.i).add(0, this.y, 0);
               ++this.y;
               if (this.y > 1) {
                  this.y = 0;
                  ++this.i;
               }

               return pos;
            }

            public void save() {
               this.pi = this.i;
               this.py = this.y;
               this.i = this.y = 0;
            }

            public void restore() {
               this.i = this.pi;
               this.y = this.py;
            }

            public int placementsPerTick(HighwayBuilder b) {
               return 1;
            }
         };
      }
   }

   private class DiagonalBlockPosProvider implements IBlockPosProvider {
      private final MBlockPos pos = new MBlockPos();
      private final MBlockPos pos2 = new MBlockPos();

      public MBPIterator getFront() {
         this.pos.coerceBlockLevel(HighwayBuilder.this.mc.field_1724).offset(HighwayBuilder.this.dir.rotateLeft()).offset(HighwayBuilder.this.leftDir, HighwayBuilder.this.getWidthLeft() - 1);
         return new MBPIterator() {
            private int i;
            private int w;
            private int y;
            private int pi;
            private int pw;
            private int py;
            // $FF: synthetic field
            final HighwayBuilder.DiagonalBlockPosProvider this$1;

            {
               this.this$1 = this$1;
            }

            public boolean hasNext() {
               return this.i < 2 && this.w < (Integer)this.this$1.this$0.width.get() && this.y < (Integer)this.this$1.this$0.height.get();
            }

            public MBlockPos next() {
               this.this$1.pos2.set(this.this$1.pos).offset(this.this$1.this$0.rightDir, this.w).add(0, this.y++, 0);
               if (this.y >= (Integer)this.this$1.this$0.height.get()) {
                  this.y = 0;
                  ++this.w;
                  if (this.w >= (this.i == 0 ? (Integer)this.this$1.this$0.width.get() - 1 : (Integer)this.this$1.this$0.width.get())) {
                     this.w = 0;
                     ++this.i;
                     this.this$1.pos.coerceBlockLevel(HighwayBuilder.access$12600(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft());
                  }
               }

               return this.this$1.pos2;
            }

            private void initPos() {
               if (this.i == 0) {
                  this.this$1.pos.coerceBlockLevel(HighwayBuilder.access$12700(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir.rotateLeft()).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft() - 1);
               } else {
                  this.this$1.pos.coerceBlockLevel(HighwayBuilder.access$12800(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft());
               }

            }

            public void save() {
               this.pi = this.i;
               this.pw = this.w;
               this.py = this.y;
               this.i = this.w = this.y = 0;
               this.initPos();
            }

            public void restore() {
               this.i = this.pi;
               this.w = this.pw;
               this.y = this.py;
               this.initPos();
            }
         };
      }

      public MBPIterator getFloor() {
         this.pos.coerceBlockLevel(HighwayBuilder.this.mc.field_1724).add(0, -1, 0).offset(HighwayBuilder.this.dir.rotateLeft()).offset(HighwayBuilder.this.leftDir, HighwayBuilder.this.getWidthLeft() - 1);
         return new MBPIterator() {
            private int i;
            private int w;
            private int pi;
            private int pw;
            // $FF: synthetic field
            final HighwayBuilder.DiagonalBlockPosProvider this$1;

            {
               this.this$1 = this$1;
            }

            public boolean hasNext() {
               return this.i < 2 && this.w < (Integer)this.this$1.this$0.width.get();
            }

            public MBlockPos next() {
               this.this$1.pos2.set(this.this$1.pos).offset(this.this$1.this$0.rightDir, this.w++);
               if (this.w >= (this.i == 0 ? (Integer)this.this$1.this$0.width.get() - 1 : (Integer)this.this$1.this$0.width.get())) {
                  this.w = 0;
                  ++this.i;
                  this.this$1.pos.coerceBlockLevel(HighwayBuilder.access$13000(this.this$1.this$0).field_1724).add(0, -1, 0).offset(this.this$1.this$0.dir).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft());
               }

               return this.this$1.pos2;
            }

            private void initPos() {
               if (this.i == 0) {
                  this.this$1.pos.coerceBlockLevel(HighwayBuilder.access$13100(this.this$1.this$0).field_1724).add(0, -1, 0).offset(this.this$1.this$0.dir.rotateLeft()).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft() - 1);
               } else {
                  this.this$1.pos.coerceBlockLevel(HighwayBuilder.access$13200(this.this$1.this$0).field_1724).add(0, -1, 0).offset(this.this$1.this$0.dir).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft());
               }

            }

            public void save() {
               this.pi = this.i;
               this.pw = this.w;
               this.i = this.w = 0;
               this.initPos();
            }

            public void restore() {
               this.i = this.pi;
               this.w = this.pw;
               this.initPos();
            }
         };
      }

      public MBPIterator getRailings(int state) {
         this.pos.coerceBlockLevel(HighwayBuilder.this.mc.field_1724).offset(HighwayBuilder.this.dir.rotateLeft()).offset(HighwayBuilder.this.leftDir, HighwayBuilder.this.getWidthLeft());
         return new MBPIterator() {
            private int i;
            private int y;
            private int pi;
            private int py;
            // $FF: synthetic field
            final int val$state;
            // $FF: synthetic field
            final HighwayBuilder.DiagonalBlockPosProvider this$1;

            {
               this.this$1 = this$1;
               this.val$state = var2;
               this.y = this.val$state;
            }

            public boolean hasNext() {
               return this.i < 2 && this.y < (this.val$state == 1 ? (Integer)this.this$1.this$0.height.get() : this.val$state + 1);
            }

            public MBlockPos next() {
               this.this$1.pos2.set(this.this$1.pos).add(0, this.y++, 0);
               if (this.y >= (this.val$state == 1 ? (Integer)this.this$1.this$0.height.get() : this.val$state + 1)) {
                  this.y = this.val$state;
                  ++this.i;
                  this.this$1.pos.coerceBlockLevel(HighwayBuilder.access$13400(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir.rotateRight()).offset(this.this$1.this$0.rightDir, this.this$1.this$0.getWidthRight());
               }

               return this.this$1.pos2;
            }

            private void initPos() {
               if (this.i == 0) {
                  this.this$1.pos.coerceBlockLevel(HighwayBuilder.access$13500(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir.rotateLeft()).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft());
               } else {
                  this.this$1.pos.coerceBlockLevel(HighwayBuilder.access$13600(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir.rotateRight()).offset(this.this$1.this$0.rightDir, this.this$1.this$0.getWidthRight());
               }

            }

            public void save() {
               this.pi = this.i;
               this.py = this.y;
               this.i = 0;
               this.y = this.val$state;
               this.initPos();
            }

            public void restore() {
               this.i = this.pi;
               this.y = this.py;
               this.initPos();
            }
         };
      }

      public MBPIterator getLiquids() {
         boolean m = (Boolean)HighwayBuilder.this.mineAboveRailings.get();
         this.pos.coerceBlockLevel(HighwayBuilder.this.mc.field_1724).offset(HighwayBuilder.this.dir).offset(HighwayBuilder.this.dir.rotateLeft()).offset(HighwayBuilder.this.leftDir, HighwayBuilder.this.getWidthLeft());
         return new MBPIterator() {
            private int i;
            private int w;
            private int y;
            private int pi;
            private int pw;
            private int py;
            // $FF: synthetic field
            final boolean val$m;
            // $FF: synthetic field
            final HighwayBuilder.DiagonalBlockPosProvider this$1;

            {
               this.this$1 = this$1;
               this.val$m = var2;
            }

            private int getWidth() {
               return (Integer)this.this$1.this$0.width.get() + (this.i == 0 ? 1 : 0) + (this.val$m && this.i == 1 ? 2 : 0);
            }

            public boolean hasNext() {
               if (this.val$m && this.i == 1 && this.y == (Integer)this.this$1.this$0.height.get() && this.w == this.getWidth() - 1) {
                  return false;
               } else {
                  return this.i < 2 && this.w < this.getWidth() && this.y < (Integer)this.this$1.this$0.height.get() + 1;
               }
            }

            private void updateW() {
               ++this.w;
               if (this.w >= this.getWidth()) {
                  this.w = 0;
                  ++this.i;
                  this.this$1.pos.coerceBlockLevel(HighwayBuilder.access$13800(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir, 2).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft() + (this.val$m ? 1 : 0));
               }

            }

            public MBlockPos next() {
               if (this.i == (this.val$m ? 1 : 0) && this.y == (Integer)this.this$1.this$0.height.get() && (this.w == 0 || this.w == this.getWidth() - 1)) {
                  this.y = 0;
                  this.updateW();
               }

               this.this$1.pos2.set(this.this$1.pos).offset(this.this$1.this$0.rightDir, this.w).add(0, this.y++, 0);
               if (this.y >= (Integer)this.this$1.this$0.height.get() + 1) {
                  this.y = 0;
                  this.updateW();
               }

               return this.this$1.pos2;
            }

            private void initPos() {
               if (this.i == 0) {
                  this.this$1.pos.coerceBlockLevel(HighwayBuilder.access$13900(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir).offset(this.this$1.this$0.dir.rotateLeft()).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft());
               } else {
                  this.this$1.pos.coerceBlockLevel(HighwayBuilder.access$14000(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir, 2).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft() + (this.val$m ? 1 : 0));
               }

            }

            public void save() {
               this.pi = this.i;
               this.pw = this.w;
               this.py = this.y;
               this.i = this.w = this.y = 0;
               this.initPos();
            }

            public void restore() {
               this.i = this.pi;
               this.w = this.pw;
               this.y = this.py;
               this.initPos();
            }
         };
      }

      public MBPIterator getBlockade(boolean mine, BlockadeType blockadeType) {
         return new MBPIterator() {
            private int i;
            private int y;
            private int pi;
            private int py;
            // $FF: synthetic field
            final boolean val$mine;
            // $FF: synthetic field
            final HighwayBuilder.BlockadeType val$blockadeType;
            // $FF: synthetic field
            final HighwayBuilder.DiagonalBlockPosProvider this$1;

            {
               this.this$1 = this$1;
               this.val$mine = var2;
               this.val$blockadeType = var3;
               this.i = this.val$mine ? -1 : 0;
            }

            private MBlockPos get(int i) {
               HorizontalDirection dir2 = this.this$1.this$0.dir.rotateLeft().rotateLeftSkipOne();
               this.this$1.pos.coerceBlockLevel(HighwayBuilder.access$14100(this.this$1.this$0).field_1724).offset(dir2);
               MBlockPos var10000;
               switch (i) {
                  case -1 -> var10000 = this.this$1.pos;
                  case 0 -> var10000 = this.this$1.pos.offset(dir2);
                  case 1 -> var10000 = this.this$1.pos.offset(dir2.rotateLeftSkipOne());
                  case 2 -> var10000 = this.this$1.pos.offset(dir2.rotateLeftSkipOne().opposite());
                  case 3 -> var10000 = this.this$1.pos.offset(dir2.opposite(), 2);
                  case 4 -> var10000 = this.this$1.pos.offset(dir2.opposite()).offset(dir2.rotateLeftSkipOne());
                  case 5 -> var10000 = this.this$1.pos.offset(dir2.opposite()).offset(dir2.rotateLeftSkipOne().opposite());
                  default -> throw new IllegalStateException("Unexpected value: " + i);
               }

               return var10000;
            }

            public boolean hasNext() {
               return this.i < this.val$blockadeType.columns && this.y < 2;
            }

            public MBlockPos next() {
               MBlockPos pos = this.get(this.i).add(0, this.y, 0);
               ++this.y;
               if (this.y > 1) {
                  this.y = 0;
                  ++this.i;
               }

               return pos;
            }

            public void save() {
               this.pi = this.i;
               this.py = this.y;
               this.i = this.y = 0;
            }

            public void restore() {
               this.i = this.pi;
               this.y = this.py;
            }

            public int placementsPerTick(HighwayBuilder b) {
               return 1;
            }
         };
      }
   }

   public static class DoubleMineBlock {
      public static boolean rateLimited = false;
      public final class_2338 blockPos;
      public final class_2680 blockState;
      private final class_2248 block;
      private final class_2350 direction;
      private final HighwayBuilder b;
      private final Vector3d vec3 = new Vector3d((double)0.0F);
      private int normalStartTime;
      private int packetStartTime;
      private boolean packet;

      public DoubleMineBlock(HighwayBuilder b, class_2338 pos) {
         this.b = b;
         this.blockPos = pos;
         this.blockState = b.mc.field_1687.method_8320(this.blockPos);
         this.block = this.blockState.method_26204();
         this.direction = BlockUtils.getDirection(pos);
         this.packet = false;
      }

      public DoubleMineBlock startDestroying() {
         this.b.mc.field_1761.method_41931(this.b.mc.field_1687, (sequence) -> new class_2846(class_2847.field_12968, this.blockPos, this.direction, sequence));
         this.normalStartTime = this.b.mc.field_1724.field_6012;
         return this;
      }

      public DoubleMineBlock stopDestroying() {
         this.b.mc.field_1761.method_41931(this.b.mc.field_1687, (sequence) -> new class_2846(class_2847.field_12973, this.blockPos, this.direction, sequence));
         return this;
      }

      public DoubleMineBlock packetMine() {
         this.packetStartTime = this.b.mc.field_1724.field_6012;
         this.packet = true;
         return this.stopDestroying();
      }

      public boolean isReady() {
         return this.progress() >= ((Boolean)this.b.fastBreak.get() ? 0.7 : (double)1.0F);
      }

      public boolean shouldRemove() {
         boolean distance = !this.packet && Utils.distance(this.b.mc.field_1724.method_33571().field_1352, this.b.mc.field_1724.method_33571().field_1351, this.b.mc.field_1724.method_33571().field_1350, (double)(this.blockPos.method_10263() + this.direction.method_10148()), (double)(this.blockPos.method_10264() + this.direction.method_10164()), (double)(this.blockPos.method_10260() + this.direction.method_10165())) > this.b.mc.field_1724.method_55754();
         boolean timeout = this.progress() > (double)2.0F && this.b.mc.field_1724.field_6012 - (this.packet ? this.packetStartTime : this.normalStartTime) > 60;
         return distance || timeout;
      }

      public double progress() {
         int slot = this.b.mc.field_1724.method_31548().method_67532();
         return BlockUtils.getBreakDelta(slot, this.blockState) * (double)(this.b.mc.field_1724.field_6012 - (this.packet ? this.packetStartTime : this.normalStartTime) + 1);
      }

      public void renderLetter() {
         this.vec3.set((double)this.blockPos.method_10263() + (double)0.5F, (double)this.blockPos.method_10264() + (double)0.5F, (double)this.blockPos.method_10260() + (double)0.5F);
         if (NametagUtils.to2D(this.vec3, (double)2.0F)) {
            NametagUtils.begin(this.vec3);
            TextRenderer.get().begin((double)1.0F, false, true);
            String letter = this.packet ? "P" : "N";
            double w = TextRenderer.get().getWidth(letter) / (double)2.0F;
            TextRenderer.get().render(letter, -w, (double)0.0F, Color.WHITE, true);
            TextRenderer.get().end();
            NametagUtils.end();
         }
      }
   }

   private class RestockTask {
      public boolean materials;
      public boolean pickaxes;
      public boolean food;
      private final HighwayBuilder b;

      public RestockTask(HighwayBuilder b) {
         this.b = b;
      }

      public void setMaterials() {
         this.setTask(0);
      }

      public void setPickaxes() {
         this.setTask(1);
      }

      public void setFood() {
         this.setTask(2);
      }

      private void setTask(@Range(
   from = 0L,
   to = 2L
) int value) {
         this.complete();
         switch (value) {
            case 0 -> this.materials = true;
            case 1 -> this.pickaxes = true;
            case 2 -> this.food = true;
         }

         HighwayBuilder.this.setState(HighwayBuilder.State.Restock);
         this.b.info("Starting new restock task for " + this.item(), new Object[0]);
      }

      public void complete() {
         this.materials = false;
         this.pickaxes = false;
         this.food = false;
      }

      public boolean tasksInactive() {
         return !this.materials && !this.pickaxes && !this.food;
      }

      public String item() {
         if (this.materials) {
            return "building materials";
         } else if (this.pickaxes) {
            return "pickaxes";
         } else {
            return this.food ? "food" : "unknown";
         }
      }
   }

   private interface IBlockPosProvider {
      MBPIterator getFront();

      MBPIterator getFloor();

      MBPIterator getRailings(int var1);

      MBPIterator getLiquids();

      MBPIterator getBlockade(boolean var1, BlockadeType var2);
   }
}
