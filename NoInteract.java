package meteordevelopment.meteorclient.systems.modules.player;

import java.util.List;
import java.util.Set;
import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import meteordevelopment.meteorclient.events.entity.player.InteractBlockEvent;
import meteordevelopment.meteorclient.events.entity.player.InteractEntityEvent;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1429;
import net.minecraft.class_1657;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_3965;

public class NoInteract extends Module {
   private final SettingGroup sgBlocks;
   private final SettingGroup sgEntities;
   private final Setting<List<class_2248>> blockMine;
   private final Setting<ListMode> blockMineMode;
   private final Setting<List<class_2248>> blockInteract;
   private final Setting<ListMode> blockInteractMode;
   private final Setting<HandMode> blockInteractHand;
   private final Setting<Set<class_1299<?>>> entityHit;
   private final Setting<ListMode> entityHitMode;
   private final Setting<Set<class_1299<?>>> entityInteract;
   private final Setting<ListMode> entityInteractMode;
   private final Setting<HandMode> entityInteractHand;
   private final Setting<InteractMode> friends;
   private final Setting<InteractMode> babies;
   private final Setting<InteractMode> nametagged;

   public NoInteract() {
      super(Categories.Player, "no-interact", "Blocks interactions with certain types of inputs.");
      this.sgBlocks = this.settings.createGroup("Blocks");
      this.sgEntities = this.settings.createGroup("Entities");
      this.blockMine = this.sgBlocks.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("block-mine")).description("Cancels block mining.")).build());
      this.blockMineMode = this.sgBlocks.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("block-mine-mode")).description("List mode to use for block mine.")).defaultValue(NoInteract.ListMode.BlackList)).build());
      this.blockInteract = this.sgBlocks.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("block-interact")).description("Cancels block interaction.")).build());
      this.blockInteractMode = this.sgBlocks.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("block-interact-mode")).description("List mode to use for block interact.")).defaultValue(NoInteract.ListMode.BlackList)).build());
      this.blockInteractHand = this.sgBlocks.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("block-interact-hand")).description("Cancels block interaction if performed by this hand.")).defaultValue(NoInteract.HandMode.None)).build());
      this.entityHit = this.sgEntities.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entity-hit")).description("Cancel entity hitting.")).onlyAttackable().build());
      this.entityHitMode = this.sgEntities.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("entity-hit-mode")).description("List mode to use for entity hit.")).defaultValue(NoInteract.ListMode.BlackList)).build());
      this.entityInteract = this.sgEntities.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entity-interact")).description("Cancel entity interaction.")).onlyAttackable().build());
      this.entityInteractMode = this.sgEntities.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("entity-interact-mode")).description("List mode to use for entity interact.")).defaultValue(NoInteract.ListMode.BlackList)).build());
      this.entityInteractHand = this.sgEntities.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("entity-interact-hand")).description("Cancels entity interaction if performed by this hand.")).defaultValue(NoInteract.HandMode.None)).build());
      this.friends = this.sgEntities.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("friends")).description("Friends cancel mode.")).defaultValue(NoInteract.InteractMode.None)).build());
      this.babies = this.sgEntities.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("babies")).description("Baby entity cancel mode.")).defaultValue(NoInteract.InteractMode.None)).build());
      this.nametagged = this.sgEntities.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("nametagged")).description("Nametagged entity cancel mode.")).defaultValue(NoInteract.InteractMode.None)).build());
   }

   @EventHandler(
      priority = 100
   )
   private void onStartBreakingBlockEvent(StartBreakingBlockEvent event) {
      if (!this.shouldAttackBlock(event.blockPos)) {
         event.cancel();
      }

   }

   @EventHandler
   private void onInteractBlock(InteractBlockEvent event) {
      if (!this.shouldInteractBlock(event.result, event.hand)) {
         event.cancel();
      }

   }

   @EventHandler(
      priority = 100
   )
   private void onAttackEntity(AttackEntityEvent event) {
      if (!this.shouldAttackEntity(event.entity)) {
         event.cancel();
      }

   }

   @EventHandler
   private void onInteractEntity(InteractEntityEvent event) {
      if (!this.shouldInteractEntity(event.entity, event.hand)) {
         event.cancel();
      }

   }

   private boolean shouldAttackBlock(class_2338 blockPos) {
      if (this.blockMineMode.get() == NoInteract.ListMode.WhiteList && ((List)this.blockMine.get()).contains(this.mc.field_1687.method_8320(blockPos).method_26204())) {
         return false;
      } else {
         return this.blockMineMode.get() != NoInteract.ListMode.BlackList || !((List)this.blockMine.get()).contains(this.mc.field_1687.method_8320(blockPos).method_26204());
      }
   }

   private boolean shouldInteractBlock(class_3965 hitResult, class_1268 hand) {
      if (this.blockInteractHand.get() != NoInteract.HandMode.Both && (this.blockInteractHand.get() != NoInteract.HandMode.Mainhand || hand != class_1268.field_5808) && (this.blockInteractHand.get() != NoInteract.HandMode.Offhand || hand != class_1268.field_5810)) {
         if (this.blockInteractMode.get() == NoInteract.ListMode.BlackList && ((List)this.blockInteract.get()).contains(this.mc.field_1687.method_8320(hitResult.method_17777()).method_26204())) {
            return false;
         } else {
            return this.blockInteractMode.get() != NoInteract.ListMode.WhiteList || ((List)this.blockInteract.get()).contains(this.mc.field_1687.method_8320(hitResult.method_17777()).method_26204());
         }
      } else {
         return false;
      }
   }

   private boolean shouldAttackEntity(class_1297 entity) {
      if ((this.friends.get() == NoInteract.InteractMode.Both || this.friends.get() == NoInteract.InteractMode.Hit) && entity instanceof class_1657 && !Friends.get().shouldAttack((class_1657)entity)) {
         return false;
      } else if ((this.babies.get() == NoInteract.InteractMode.Both || this.babies.get() == NoInteract.InteractMode.Hit) && entity instanceof class_1429 && ((class_1429)entity).method_6109()) {
         return false;
      } else if ((this.nametagged.get() == NoInteract.InteractMode.Both || this.nametagged.get() == NoInteract.InteractMode.Hit) && entity.method_16914()) {
         return false;
      } else if (this.entityHitMode.get() == NoInteract.ListMode.BlackList && ((Set)this.entityHit.get()).contains(entity.method_5864())) {
         return false;
      } else {
         return this.entityHitMode.get() != NoInteract.ListMode.WhiteList || ((Set)this.entityHit.get()).contains(entity.method_5864());
      }
   }

   private boolean shouldInteractEntity(class_1297 entity, class_1268 hand) {
      if (this.entityInteractHand.get() != NoInteract.HandMode.Both && (this.entityInteractHand.get() != NoInteract.HandMode.Mainhand || hand != class_1268.field_5808) && (this.entityInteractHand.get() != NoInteract.HandMode.Offhand || hand != class_1268.field_5810)) {
         if ((this.friends.get() == NoInteract.InteractMode.Both || this.friends.get() == NoInteract.InteractMode.Interact) && entity instanceof class_1657 && !Friends.get().shouldAttack((class_1657)entity)) {
            return false;
         } else if ((this.babies.get() == NoInteract.InteractMode.Both || this.babies.get() == NoInteract.InteractMode.Interact) && entity instanceof class_1429 && ((class_1429)entity).method_6109()) {
            return false;
         } else if ((this.nametagged.get() == NoInteract.InteractMode.Both || this.nametagged.get() == NoInteract.InteractMode.Interact) && entity.method_16914()) {
            return false;
         } else if (this.entityInteractMode.get() == NoInteract.ListMode.BlackList && ((Set)this.entityInteract.get()).contains(entity.method_5864())) {
            return false;
         } else {
            return this.entityInteractMode.get() != NoInteract.ListMode.WhiteList || ((Set)this.entityInteract.get()).contains(entity.method_5864());
         }
      } else {
         return false;
      }
   }

   public static enum HandMode {
      Mainhand,
      Offhand,
      Both,
      None;

      // $FF: synthetic method
      private static HandMode[] $values() {
         return new HandMode[]{Mainhand, Offhand, Both, None};
      }
   }

   public static enum ListMode {
      WhiteList,
      BlackList;

      // $FF: synthetic method
      private static ListMode[] $values() {
         return new ListMode[]{WhiteList, BlackList};
      }
   }

   public static enum InteractMode {
      Hit,
      Interact,
      Both,
      None;

      // $FF: synthetic method
      private static InteractMode[] $values() {
         return new InteractMode[]{Hit, Interact, Both, None};
      }
   }
}
