package meteordevelopment.meteorclient.systems.modules.misc;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.DropItemsEvent;
import meteordevelopment.meteorclient.events.entity.player.InteractBlockEvent;
import meteordevelopment.meteorclient.events.entity.player.InteractEntityEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.events.packets.InventoryEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.HandledScreenAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.ScreenHandlerListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.InventorySorter;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.player.SlotUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1533;
import net.minecraft.class_1703;
import net.minecraft.class_1735;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2248;
import net.minecraft.class_2596;
import net.minecraft.class_2815;
import net.minecraft.class_3917;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_8168;

public class InventoryTweaks extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgSorting;
   private final SettingGroup sgAntiDrop;
   private final SettingGroup sgAutoDrop;
   private final SettingGroup sgStealDump;
   private final SettingGroup sgAutoSteal;
   private final Setting<Boolean> mouseDragItemMove;
   private final Setting<Boolean> xCarry;
   private final Setting<Boolean> uncapBundleScrolling;
   private final Setting<Boolean> frameInput;
   private final Setting<Boolean> sortingEnabled;
   private final Setting<Keybind> sortingKey;
   private final Setting<Integer> sortingDelay;
   private final Setting<Boolean> disableInCreative;
   private final Setting<List<class_1792>> antiDropItems;
   private final Setting<Boolean> antiItemFrame;
   private final Setting<Keybind> antiDropOverrideBind;
   private final Setting<List<class_1792>> autoDropItems;
   private final Setting<Boolean> autoDropExcludeEquipped;
   private final Setting<Boolean> autoDropExcludeHotbar;
   private final Setting<Boolean> autoDropOnlyFullStacks;
   public final Setting<List<class_3917<?>>> stealScreens;
   private final Setting<Boolean> buttons;
   private final Setting<Boolean> stealDrop;
   private final Setting<Boolean> dropBackwards;
   private final Setting<ListMode> dumpFilter;
   private final Setting<List<class_1792>> dumpItems;
   private final Setting<ListMode> stealFilter;
   private final Setting<List<class_1792>> stealItems;
   private final Setting<Boolean> autoSteal;
   private final Setting<Boolean> autoDump;
   private final Setting<Integer> autoStealDelay;
   private final Setting<Integer> autoStealInitDelay;
   private final Setting<Integer> autoStealRandomDelay;
   private InventorySorter sorter;
   private boolean invOpened;

   public InventoryTweaks() {
      super(Categories.Misc, "inventory-tweaks", "Various inventory related utilities.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgSorting = this.settings.createGroup("Sorting");
      this.sgAntiDrop = this.settings.createGroup("Anti Drop");
      this.sgAutoDrop = this.settings.createGroup("Auto Drop");
      this.sgStealDump = this.settings.createGroup("Steal and Dump");
      this.sgAutoSteal = this.settings.createGroup("Auto Steal");
      this.mouseDragItemMove = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("mouse-drag-item-move")).description("Moving mouse over items while holding shift will transfer it to the other container.")).defaultValue(true)).build());
      this.xCarry = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("xcarry")).description("Allows you to store four extra item stacks in your crafting grid.")).defaultValue(true)).onChanged((v) -> {
         if (!v && Utils.canUpdate()) {
            this.mc.field_1724.field_3944.method_52787(new class_2815(this.mc.field_1724.field_7498.field_7763));
            this.invOpened = false;
         }
      })).build());
      this.uncapBundleScrolling = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("uncap-bundle-scrolling")).description("Whether to uncap the bundle scrolling feature to let you select any item.")).defaultValue(true)).build());
      this.frameInput = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("frame-input-handling")).description("Changes input handling to work every frame instead of every tick. A very minor effect but may\nmake inputs feel smoother, especially in laggy environments. Will flag anticheats that check packet order (Grim).")).defaultValue(false)).build());
      this.sortingEnabled = this.sgSorting.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sorting-enabled")).description("Automatically sorts stacks in inventory.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgSorting;
      KeybindSetting.Builder var10002 = (KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("sorting-key")).description("Key to trigger the sort.");
      Setting var10003 = this.sortingEnabled;
      Objects.requireNonNull(var10003);
      this.sortingKey = var10001.add(((KeybindSetting.Builder)((KeybindSetting.Builder)var10002.visible(var10003::get)).defaultValue(Keybind.fromButton(2))).build());
      var10001 = this.sgSorting;
      IntSetting.Builder var4 = (IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("sorting-delay")).description("Delay in ticks between moving items when sorting.");
      var10003 = this.sortingEnabled;
      Objects.requireNonNull(var10003);
      this.sortingDelay = var10001.add(((IntSetting.Builder)((IntSetting.Builder)var4.visible(var10003::get)).defaultValue(1)).min(0).build());
      var10001 = this.sgSorting;
      BoolSetting.Builder var5 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-in-creative")).description("Disables the inventory sorter when in creative mode.")).defaultValue(true);
      var10003 = this.sortingEnabled;
      Objects.requireNonNull(var10003);
      this.disableInCreative = var10001.add(((BoolSetting.Builder)var5.visible(var10003::get)).build());
      this.antiDropItems = this.sgAntiDrop.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("anti-drop-items")).description("Items to prevent dropping. Doesn't work in creative inventory screen.")).build());
      this.antiItemFrame = this.sgAntiDrop.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("item-frames")).description("Prevent anti-drop items from being placed in item frames or pots")).defaultValue(true)).build());
      this.antiDropOverrideBind = this.sgAntiDrop.add(((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("override-bind")).description("Hold this bind to temporarily bypass anti-drop")).build());
      this.autoDropItems = this.sgAutoDrop.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("auto-drop-items")).description("Items to drop.")).build());
      this.autoDropExcludeEquipped = this.sgAutoDrop.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("exclude-equipped")).description("Whether or not to drop items equipped in armor slots.")).defaultValue(true)).build());
      this.autoDropExcludeHotbar = this.sgAutoDrop.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("exclude-hotbar")).description("Whether or not to drop items from your hotbar.")).defaultValue(false)).build());
      this.autoDropOnlyFullStacks = this.sgAutoDrop.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-full-stacks")).description("Only drops the items if the stack is full.")).defaultValue(false)).build());
      this.stealScreens = this.sgStealDump.add(((ScreenHandlerListSetting.Builder)((ScreenHandlerListSetting.Builder)((ScreenHandlerListSetting.Builder)(new ScreenHandlerListSetting.Builder()).name("steal-screens")).description("Select the screens to display buttons and auto steal.")).defaultValue(List.of(class_3917.field_17326, class_3917.field_17327))).build());
      this.buttons = this.sgStealDump.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("inventory-buttons")).description("Shows steal and dump buttons in container guis.")).defaultValue(true)).build());
      this.stealDrop = this.sgStealDump.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("steal-drop")).description("Drop items to the ground instead of stealing them.")).defaultValue(false)).build());
      var10001 = this.sgStealDump;
      var5 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("drop-backwards")).description("Drop items behind you.")).defaultValue(false);
      var10003 = this.stealDrop;
      Objects.requireNonNull(var10003);
      this.dropBackwards = var10001.add(((BoolSetting.Builder)var5.visible(var10003::get)).build());
      this.dumpFilter = this.sgStealDump.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("dump-filter")).description("Dump mode.")).defaultValue(InventoryTweaks.ListMode.None)).build());
      this.dumpItems = this.sgStealDump.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("dump-items")).description("Items to dump.")).build());
      this.stealFilter = this.sgStealDump.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("steal-filter")).description("Steal mode.")).defaultValue(InventoryTweaks.ListMode.None)).build());
      this.stealItems = this.sgStealDump.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("steal-items")).description("Items to steal.")).build());
      this.autoSteal = this.sgAutoSteal.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-steal")).description("Automatically removes all possible items when you open a container.")).defaultValue(false)).onChanged((val) -> this.checkAutoStealSettings())).build());
      this.autoDump = this.sgAutoSteal.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-dump")).description("Automatically dumps all possible items when you open a container.")).defaultValue(false)).onChanged((val) -> this.checkAutoStealSettings())).build());
      this.autoStealDelay = this.sgAutoSteal.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("The minimum delay between stealing the next stack in milliseconds.")).defaultValue(20)).sliderMax(1000).build());
      this.autoStealInitDelay = this.sgAutoSteal.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("initial-delay")).description("The initial delay before stealing in milliseconds. 0 to use normal delay instead.")).defaultValue(50)).sliderMax(1000).build());
      this.autoStealRandomDelay = this.sgAutoSteal.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("random")).description("Randomly adds a delay of up to the specified time in milliseconds.")).min(0).sliderMax(1000).defaultValue(50)).build());
   }

   public void onActivate() {
      this.invOpened = false;
   }

   public void onDeactivate() {
      this.sorter = null;
      if (this.invOpened) {
         this.mc.field_1724.field_3944.method_52787(new class_2815(this.mc.field_1724.field_7498.field_7763));
      }

   }

   @EventHandler
   private void onKey(KeyEvent event) {
      if (event.action == KeyAction.Press) {
         if (((Keybind)this.sortingKey.get()).matches(event.input) && this.sort()) {
            event.cancel();
         }

      }
   }

   @EventHandler
   private void onMouseClick(MouseClickEvent event) {
      if (event.action == KeyAction.Press) {
         if (((Keybind)this.sortingKey.get()).matches(event.input) && this.sort()) {
            event.cancel();
         }

      }
   }

   private boolean sort() {
      if ((Boolean)this.sortingEnabled.get()) {
         class_437 var2 = this.mc.field_1755;
         if (var2 instanceof class_465) {
            class_465<?> screen = (class_465)var2;
            if (this.sorter == null && (!this.mc.field_1724.method_68878() || !(Boolean)this.disableInCreative.get())) {
               if (!this.mc.field_1724.field_7512.method_34255().method_7960()) {
                  FindItemResult empty = InvUtils.findEmpty();
                  if (!empty.found()) {
                     InvUtils.click().slot(-999);
                  } else {
                     InvUtils.click().slot(empty.slot());
                  }
               }

               class_1735 focusedSlot = ((HandledScreenAccessor)screen).meteor$getFocusedSlot();
               if (focusedSlot == null) {
                  return false;
               }

               this.sorter = new InventorySorter(screen, focusedSlot);
               return true;
            }
         }
      }

      return false;
   }

   @EventHandler
   private void onOpenScreen(OpenScreenEvent event) {
      this.sorter = null;
   }

   @EventHandler
   private void onTickPre(TickEvent.Pre event) {
      if (this.sorter != null && this.sorter.tick((Integer)this.sortingDelay.get())) {
         this.sorter = null;
      }

   }

   @EventHandler
   private void onTickPost(TickEvent.Post event) {
      if (Utils.canUpdate() && !(this.mc.field_1755 instanceof class_465) && !((List)this.autoDropItems.get()).isEmpty()) {
         for(int i = (Boolean)this.autoDropExcludeHotbar.get() ? 9 : 0; i < this.mc.field_1724.method_31548().method_5439(); ++i) {
            class_1799 itemStack = this.mc.field_1724.method_31548().method_5438(i);
            if (((List)this.autoDropItems.get()).contains(itemStack.method_7909()) && (!(Boolean)this.autoDropOnlyFullStacks.get() || itemStack.method_7947() == itemStack.method_7914()) && (!(Boolean)this.autoDropExcludeEquipped.get() || !SlotUtils.isArmor(i))) {
               InvUtils.drop().slot(i);
            }
         }

      }
   }

   @EventHandler
   private void onDropItems(DropItemsEvent event) {
      if (!((Keybind)this.antiDropOverrideBind.get()).isPressed()) {
         if (((List)this.antiDropItems.get()).contains(event.itemStack.method_7909())) {
            event.cancel();
         }

      }
   }

   @EventHandler
   private void onInteractEntity(InteractEntityEvent event) {
      if ((Boolean)this.antiItemFrame.get() && !((Keybind)this.antiDropOverrideBind.get()).isPressed()) {
         if (event.entity instanceof class_1533) {
            class_1792 item = this.mc.field_1724.method_5998(event.hand).method_7909();
            if (((List)this.antiDropItems.get()).contains(item)) {
               event.cancel();
            }

         }
      }
   }

   @EventHandler
   private void onInteractBlock(InteractBlockEvent event) {
      if ((Boolean)this.antiItemFrame.get() && !((Keybind)this.antiDropOverrideBind.get()).isPressed()) {
         if (event.hand == class_1268.field_5808) {
            class_2248 block = this.mc.field_1687.method_8320(event.result.method_17777()).method_26204();
            if (block instanceof class_8168) {
               class_1792 item = this.mc.field_1724.method_5998(event.hand).method_7909();
               if (((List)this.antiDropItems.get()).contains(item)) {
                  event.cancel();
               }

            }
         }
      }
   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      if ((Boolean)this.xCarry.get()) {
         class_2596 var3 = event.packet;
         if (var3 instanceof class_2815) {
            class_2815 packet = (class_2815)var3;
            if (packet.method_36168() == this.mc.field_1724.field_7498.field_7763) {
               this.invOpened = true;
               event.cancel();
            }

            return;
         }
      }

   }

   private void checkAutoStealSettings() {
      if ((Boolean)this.autoSteal.get() && (Boolean)this.autoDump.get()) {
         this.error("You can't enable Auto Steal and Auto Dump at the same time!", new Object[0]);
         this.autoDump.set(false);
      }

   }

   private int getSleepTime() {
      return (Integer)this.autoStealDelay.get() + ((Integer)this.autoStealRandomDelay.get() > 0 ? ThreadLocalRandom.current().nextInt(0, (Integer)this.autoStealRandomDelay.get()) : 0);
   }

   private void moveSlots(class_1703 handler, int start, int end, boolean steal) {
      boolean initial = (Integer)this.autoStealInitDelay.get() != 0;

      for(int i = start; i < end; ++i) {
         if (handler.method_7611(i).method_7681()) {
            if (this.mc.field_1755 == null || !Utils.canUpdate()) {
               break;
            }

            class_1792 item = handler.method_7611(i).method_7677().method_7909();
            if (steal) {
               if (this.stealFilter.get() == InventoryTweaks.ListMode.Whitelist && !((List)this.stealItems.get()).contains(item) || this.stealFilter.get() == InventoryTweaks.ListMode.Blacklist && ((List)this.stealItems.get()).contains(item)) {
                  continue;
               }
            } else if (this.dumpFilter.get() == InventoryTweaks.ListMode.Whitelist && !((List)this.dumpItems.get()).contains(item) || this.dumpFilter.get() == InventoryTweaks.ListMode.Blacklist && ((List)this.dumpItems.get()).contains(item)) {
               continue;
            }

            int sleep;
            if (initial) {
               sleep = (Integer)this.autoStealInitDelay.get();
               initial = false;
            } else {
               sleep = this.getSleepTime();
            }

            if (sleep > 0) {
               try {
                  Thread.sleep((long)sleep);
               } catch (InterruptedException e) {
                  MeteorClient.LOG.error("Error when sleeping the slot mover", e);
               }
            }

            if (this.mc.field_1755 == null || !Utils.canUpdate()) {
               break;
            }

            if (steal && (Boolean)this.stealDrop.get()) {
               if ((Boolean)this.dropBackwards.get()) {
                  Rotations.rotate((double)(this.mc.field_1724.method_36454() - 180.0F), (double)this.mc.field_1724.method_36455(), () -> InvUtils.drop().slotId(i));
               }
            } else {
               InvUtils.shiftClick().slotId(i);
            }
         }
      }

   }

   public void steal(class_1703 handler) {
      MeteorExecutor.execute(() -> this.moveSlots(handler, 0, SlotUtils.indexToId(9), true));
   }

   public void dump(class_1703 handler) {
      int playerInvOffset = SlotUtils.indexToId(9);
      MeteorExecutor.execute(() -> this.moveSlots(handler, playerInvOffset, playerInvOffset + 36, false));
   }

   public boolean showButtons() {
      return this.isActive() && (Boolean)this.buttons.get();
   }

   public boolean mouseDragItemMove() {
      return this.isActive() && (Boolean)this.mouseDragItemMove.get();
   }

   public boolean uncapBundleScrolling() {
      return this.isActive() && (Boolean)this.uncapBundleScrolling.get();
   }

   public boolean frameInput() {
      return this.isActive() && (Boolean)this.frameInput.get();
   }

   public boolean canSteal(class_1703 handler) {
      try {
         return ((List)this.stealScreens.get()).contains(handler.method_17358());
      } catch (UnsupportedOperationException var3) {
         return false;
      }
   }

   @EventHandler
   private void onInventory(InventoryEvent event) {
      class_1703 handler = this.mc.field_1724.field_7512;
      if (this.canSteal(handler) && event.packet.comp_3837() == handler.field_7763) {
         if ((Boolean)this.autoSteal.get()) {
            this.steal(handler);
         } else if ((Boolean)this.autoDump.get()) {
            this.dump(handler);
         }
      }

   }

   public static enum ListMode {
      Whitelist,
      Blacklist,
      None;

      // $FF: synthetic method
      private static ListMode[] $values() {
         return new ListMode[]{Whitelist, Blacklist, None};
      }
   }
}
