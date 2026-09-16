package meteordevelopment.meteorclient.systems.modules.player;

import java.util.List;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.ClientPlayerInteractionManagerAccessor;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2596;
import net.minecraft.class_2846;
import net.minecraft.class_2846.class_2847;

public class SpeedMine extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<Mode> mode;
   private final Setting<List<class_2248>> blocks;
   private final Setting<ListMode> blocksFilter;
   public final Setting<Double> modifier;
   private final Setting<Integer> hasteAmplifier;
   private final Setting<Boolean> instamine;
   private final Setting<Boolean> grimBypass;

   public SpeedMine() {
      super(Categories.Player, "speed-mine", "Allows you to quickly mine blocks.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).defaultValue(SpeedMine.Mode.Damage)).onChanged((mode) -> this.removeHaste())).build());
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("blocks")).description("Selected blocks.")).filter((block) -> block.method_36555() > 0.0F).visible(() -> this.mode.get() != SpeedMine.Mode.Haste)).build());
      this.blocksFilter = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("blocks-filter")).description("How to use the blocks setting.")).defaultValue(SpeedMine.ListMode.Blacklist)).visible(() -> this.mode.get() != SpeedMine.Mode.Haste)).build());
      this.modifier = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("modifier")).description("Mining speed modifier. An additional value of 0.2 is equivalent to one haste level (1.2 = haste 1).")).defaultValue(1.4).visible(() -> this.mode.get() == SpeedMine.Mode.Normal)).min((double)0.0F).build());
      this.hasteAmplifier = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("haste-amplifier")).description("What value of haste to give you. Above 2 not recommended.")).defaultValue(2)).min(1).visible(() -> this.mode.get() == SpeedMine.Mode.Haste)).onChanged((i) -> this.removeHaste())).build());
      this.instamine = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("instamine")).description("Whether or not to instantly mine blocks under certain conditions.")).defaultValue(true)).visible(() -> this.mode.get() == SpeedMine.Mode.Damage)).build());
      this.grimBypass = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("grim-bypass")).description("Bypasses Grim's fastbreak check, working as of 2.3.58")).defaultValue(false)).visible(() -> this.mode.get() == SpeedMine.Mode.Damage)).build());
   }

   public void onDeactivate() {
      this.removeHaste();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (Utils.canUpdate()) {
         if (this.mode.get() == SpeedMine.Mode.Haste) {
            class_1293 haste = this.mc.field_1724.method_6112(class_1294.field_5917);
            if (haste == null || haste.method_5578() <= (Integer)this.hasteAmplifier.get() - 1) {
               this.mc.field_1724.method_26082(new class_1293(class_1294.field_5917, -1, (Integer)this.hasteAmplifier.get() - 1, false, false, false), (class_1297)null);
            }
         } else if (this.mode.get() == SpeedMine.Mode.Damage) {
            ClientPlayerInteractionManagerAccessor im = (ClientPlayerInteractionManagerAccessor)this.mc.field_1761;
            float progress = im.meteor$getBreakingProgress();
            class_2338 pos = im.meteor$getCurrentBreakingBlockPos();
            if (pos == null || progress <= 0.0F) {
               return;
            }

            if (progress + this.mc.field_1687.method_8320(pos).method_26165(this.mc.field_1724, this.mc.field_1687, pos) >= 0.7F) {
               im.meteor$setCurrentBreakingProgress(1.0F);
            }
         }

      }
   }

   @EventHandler
   private void onPacket(PacketEvent.Send event) {
      if (this.mode.get() == SpeedMine.Mode.Damage && (Boolean)this.grimBypass.get()) {
         class_2596 var3 = event.packet;
         if (var3 instanceof class_2846) {
            class_2846 packet = (class_2846)var3;
            if (packet.method_12363() == class_2847.field_12973) {
               this.mc.method_1562().method_52787(new class_2846(class_2847.field_12971, packet.method_12362().method_10084(), packet.method_12360()));
            }
         }

      }
   }

   private void removeHaste() {
      if (Utils.canUpdate()) {
         class_1293 haste = this.mc.field_1724.method_6112(class_1294.field_5917);
         if (haste != null && !haste.method_5592()) {
            this.mc.field_1724.method_6016(class_1294.field_5917);
         }

      }
   }

   public boolean filter(class_2248 block) {
      if (this.blocksFilter.get() == SpeedMine.ListMode.Blacklist && !((List)this.blocks.get()).contains(block)) {
         return true;
      } else {
         return this.blocksFilter.get() == SpeedMine.ListMode.Whitelist && ((List)this.blocks.get()).contains(block);
      }
   }

   public boolean instamine() {
      return this.isActive() && this.mode.get() == SpeedMine.Mode.Damage && (Boolean)this.instamine.get();
   }

   public static enum Mode {
      Normal,
      Haste,
      Damage;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Normal, Haste, Damage};
      }
   }

   public static enum ListMode {
      Whitelist,
      Blacklist;

      // $FF: synthetic method
      private static ListMode[] $values() {
         return new ListMode[]{Whitelist, Blacklist};
      }
   }
}
