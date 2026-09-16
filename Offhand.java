package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.Objects;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1304;
import net.minecraft.class_1743;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_3489;
import net.minecraft.class_9334;

public class Offhand extends Module {
   private final SettingGroup sgCombat;
   private final SettingGroup sgTotem;
   private final Setting<Integer> delayTicks;
   private final Setting<Item> preferreditem;
   private final Setting<Boolean> hotbar;
   private final Setting<Boolean> rightgapple;
   private final Setting<Boolean> SwordGap;
   private final Setting<Boolean> alwaysSwordGap;
   private final Setting<Boolean> alwaysPot;
   private final Setting<Boolean> potionClick;
   private final Setting<Double> minHealth;
   private final Setting<Boolean> elytra;
   private final Setting<Boolean> falling;
   private final Setting<Boolean> explosion;
   private boolean isClicking;
   private boolean sentMessage;
   private Item currentItem;
   public boolean locked;
   private int totems;
   private int ticks;

   public Offhand() {
      super(Categories.Combat, "offhand", "Allows you to hold specified items in your offhand.");
      this.sgCombat = this.settings.createGroup("Combat");
      this.sgTotem = this.settings.createGroup("Totem");
      this.delayTicks = this.sgCombat.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("item-switch-delay")).description("The delay in ticks between slot movements.")).defaultValue(0)).min(0).sliderMax(20).build());
      this.preferreditem = this.sgCombat.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("item")).description("Which item to hold in your offhand.")).defaultValue(Offhand.Item.Crystal)).build());
      this.hotbar = this.sgCombat.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("hotbar")).description("Whether to use items from your hotbar.")).defaultValue(false)).build());
      this.rightgapple = this.sgCombat.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("right-gapple")).description("Will switch to a gapple when holding right click.(DO NOT USE WITH POTION ON)")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgCombat;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sword-gapple")).description("Will switch to a gapple when holding a sword and right click.")).defaultValue(false);
      Setting var10003 = this.rightgapple;
      Objects.requireNonNull(var10003);
      this.SwordGap = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.alwaysSwordGap = this.sgCombat.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("always-gap-on-sword")).description("Holds an Enchanted Golden Apple when you are holding a sword.")).defaultValue(false)).visible(() -> !(Boolean)this.rightgapple.get())).build());
      this.alwaysPot = this.sgCombat.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("always-pot-on-sword")).description("Will switch to a potion when holding a sword")).defaultValue(false)).visible(() -> !(Boolean)this.rightgapple.get() && !(Boolean)this.alwaysSwordGap.get())).build());
      this.potionClick = this.sgCombat.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sword-pot")).description("Will switch to a potion when holding a sword and right click.")).defaultValue(false)).visible(() -> !(Boolean)this.rightgapple.get() && !(Boolean)this.alwaysPot.get() && !(Boolean)this.alwaysSwordGap.get())).build());
      this.minHealth = this.sgTotem.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("min-health")).description("Will hold a totem when below this amount of health.")).defaultValue((double)10.0F).range((double)0.0F, (double)36.0F).sliderRange((double)0.0F, (double)36.0F).build());
      this.elytra = this.sgTotem.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("elytra")).description("Will always hold a totem while flying with an elytra.")).defaultValue(false)).build());
      this.falling = this.sgTotem.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("falling")).description("Will hold a totem if fall damage could kill you.")).defaultValue(false)).build());
      this.explosion = this.sgTotem.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("explosion")).description("Will hold a totem when explosion damage could kill you.")).defaultValue(true)).build());
   }

   public void onActivate() {
      this.ticks = 0;
      this.sentMessage = false;
      this.isClicking = false;
      this.currentItem = this.preferreditem.get();
   }

   @EventHandler(
      priority = 1199
   )
   private void onTick(TickEvent.Pre event) {
      FindItemResult result = InvUtils.find(class_1802.field_8288);
      this.totems = result.count();
      if (this.totems <= 0) {
         this.locked = false;
      } else if (this.ticks > (Integer)this.delayTicks.get()) {
         boolean low = (double)(this.mc.field_1724.method_6032() + this.mc.field_1724.method_6067() - PlayerUtils.possibleHealthReductions((Boolean)this.explosion.get(), (Boolean)this.falling.get())) <= (Double)this.minHealth.get();
         boolean ely = (Boolean)this.elytra.get() && this.mc.field_1724.method_6118(class_1304.field_6174).method_7909() == class_1802.field_8833 && this.mc.field_1724.method_6128();
         FindItemResult item = InvUtils.find((itemStack) -> itemStack.method_7909() == this.currentItem.item, 0, 35);
         this.locked = low || ely;
         if (this.locked && this.mc.field_1724.method_6079().method_7909() != class_1802.field_8288) {
            InvUtils.move().from(result.slot()).toOffhand();
         }

         this.ticks = 0;
         return;
      }

      ++this.ticks;
      AutoTotem autoTotem = (AutoTotem)Modules.get().get(AutoTotem.class);
      this.currentItem = this.preferreditem.get();
      if ((Boolean)this.rightgapple.get()) {
         if (!this.locked) {
            if ((Boolean)this.SwordGap.get() && this.mc.field_1724.method_6047().method_31573(class_3489.field_42611) && this.isClicking) {
               this.currentItem = Offhand.Item.EGap;
            }

            if (!(Boolean)this.SwordGap.get() && this.isClicking) {
               this.currentItem = Offhand.Item.EGap;
            }
         }
      } else if ((this.mc.field_1724.method_6047().method_31573(class_3489.field_42611) || this.mc.field_1724.method_6047().method_7909() instanceof class_1743) && (Boolean)this.alwaysSwordGap.get()) {
         this.currentItem = Offhand.Item.EGap;
      } else if ((Boolean)this.potionClick.get()) {
         if (!this.locked && this.mc.field_1724.method_6047().method_31573(class_3489.field_42611) && this.isClicking) {
            this.currentItem = Offhand.Item.Potion;
         }
      } else if ((this.mc.field_1724.method_6047().method_31573(class_3489.field_42611) || this.mc.field_1724.method_6047().method_7909() instanceof class_1743) && (Boolean)this.alwaysPot.get()) {
         this.currentItem = Offhand.Item.Potion;
      } else {
         this.currentItem = this.preferreditem.get();
      }

      if (this.mc.field_1724.method_6079().method_7909() != this.currentItem.item && this.ticks >= (Integer)this.delayTicks.get()) {
         if (!this.locked) {
            FindItemResult item = InvUtils.find((itemStack) -> itemStack.method_7909() == this.currentItem.item, (Boolean)this.hotbar.get() ? 0 : 9, 35);
            if (!item.found()) {
               if (!this.sentMessage) {
                  this.warning("Chosen item not found.", new Object[0]);
                  this.sentMessage = true;
               }
            } else if (this.isClicking || !autoTotem.isLocked() && !item.isOffhand()) {
               InvUtils.move().from(item.slot()).toOffhand();
               this.sentMessage = false;
            }

            this.ticks = 0;
            return;
         }

         ++this.ticks;
      }

   }

   @EventHandler
   private void onMouseClick(MouseClickEvent event) {
      this.isClicking = this.mc.field_1755 == null && !((AutoTotem)Modules.get().get(AutoTotem.class)).isLocked() && !this.usableItem() && !this.mc.field_1724.method_6115() && event.action == KeyAction.Press && event.button() == 1;
   }

   private boolean usableItem() {
      return this.mc.field_1724.method_6047().method_7909() == class_1802.field_8102 || this.mc.field_1724.method_6047().method_7909() == class_1802.field_8547 || this.mc.field_1724.method_6047().method_7909() == class_1802.field_8399 || this.mc.field_1724.method_6047().method_7909().method_57347().method_57832(class_9334.field_50075);
   }

   public String getInfoString() {
      return ((Item)this.preferreditem.get()).name();
   }

   public static enum Item {
      EGap(class_1802.field_8367),
      Gap(class_1802.field_8463),
      Crystal(class_1802.field_8301),
      Totem(class_1802.field_8288),
      Shield(class_1802.field_8255),
      Potion(class_1802.field_8574);

      final class_1792 item;

      private Item(class_1792 item) {
         this.item = item;
      }

      // $FF: synthetic method
      private static Item[] $values() {
         return new Item[]{EGap, Gap, Crystal, Totem, Shield, Potion};
      }
   }
}
