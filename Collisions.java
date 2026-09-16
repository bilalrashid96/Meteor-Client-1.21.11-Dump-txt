package meteordevelopment.meteorclient.systems.modules.world;

import java.util.List;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2231;
import net.minecraft.class_2241;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2266;
import net.minecraft.class_2275;
import net.minecraft.class_2533;
import net.minecraft.class_2537;
import net.minecraft.class_2538;
import net.minecraft.class_2560;
import net.minecraft.class_259;
import net.minecraft.class_2596;
import net.minecraft.class_2828;
import net.minecraft.class_2833;
import net.minecraft.class_3830;
import net.minecraft.class_3922;
import net.minecraft.class_4622;
import net.minecraft.class_4770;
import net.minecraft.class_5635;

public class Collisions extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<List<class_2248>> blocks;
   private final Setting<Boolean> magma;
   private final Setting<Boolean> unloadedChunks;
   private final Setting<Boolean> ignoreBorder;

   public Collisions() {
      super(Categories.World, "collisions", "Adds collision boxes to certain blocks/areas.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("blocks")).description("What blocks should be added collision box.")).filter(this::blockFilter).build());
      this.magma = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("magma")).description("Prevents you from walking over magma blocks.")).defaultValue(false)).build());
      this.unloadedChunks = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("unloaded-chunks")).description("Stops you from going into unloaded chunks.")).defaultValue(false)).build());
      this.ignoreBorder = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-border")).description("Removes world border collision.")).defaultValue(false)).build());
   }

   @EventHandler
   private void onCollisionShape(CollisionShapeEvent event) {
      if (this.mc.field_1687 != null && this.mc.field_1724 != null) {
         if (event.state.method_26227().method_15769()) {
            if (((List)this.blocks.get()).contains(event.state.method_26204())) {
               event.shape = class_259.method_1077();
            } else if ((Boolean)this.magma.get() && !this.mc.field_1724.method_5715() && event.state.method_26215() && this.mc.field_1687.method_8320(event.pos.method_10074()).method_26204() == class_2246.field_10092) {
               event.shape = class_259.method_1077();
            }

         }
      }
   }

   @EventHandler
   private void onPlayerMove(PlayerMoveEvent event) {
      int x = (int)(this.mc.field_1724.method_23317() + event.movement.field_1352) >> 4;
      int z = (int)(this.mc.field_1724.method_23321() + event.movement.field_1350) >> 4;
      if ((Boolean)this.unloadedChunks.get() && !this.mc.field_1687.method_2935().method_12123(x, z)) {
         ((IVec3d)event.movement).meteor$set((double)0.0F, event.movement.field_1351, (double)0.0F);
      }

   }

   @EventHandler
   private void onPacketSend(PacketEvent.Send event) {
      if ((Boolean)this.unloadedChunks.get()) {
         class_2596 var4 = event.packet;
         if (var4 instanceof class_2833) {
            class_2833 packet = (class_2833)var4;
            if (!this.mc.field_1687.method_2935().method_12123((int)packet.comp_3350().method_10216() >> 4, (int)packet.comp_3350().method_10215() >> 4)) {
               this.mc.field_1724.method_5854().method_30634(this.mc.field_1724.method_5854().field_6014, this.mc.field_1724.method_5854().field_6036, this.mc.field_1724.method_5854().field_5969);
               event.cancel();
            }
         } else {
            var4 = event.packet;
            if (var4 instanceof class_2828) {
               class_2828 packet = (class_2828)var4;
               if (!this.mc.field_1687.method_2935().method_12123((int)packet.method_12269(this.mc.field_1724.method_23317()) >> 4, (int)packet.method_12274(this.mc.field_1724.method_23321()) >> 4)) {
                  event.cancel();
               }
            }
         }

      }
   }

   private boolean blockFilter(class_2248 block) {
      return block instanceof class_4770 || block instanceof class_2231 || block instanceof class_2538 || block instanceof class_2537 || block instanceof class_2560 || block instanceof class_3922 || block instanceof class_3830 || block instanceof class_2266 || block instanceof class_2241 || block instanceof class_2533 || block instanceof class_5635 || block instanceof class_2275 || block instanceof class_4622;
   }

   public boolean ignoreBorder() {
      return this.isActive() && (Boolean)this.ignoreBorder.get();
   }
}
