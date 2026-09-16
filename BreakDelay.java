package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.events.entity.player.BlockBreakingCooldownEvent;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;

public class BreakDelay extends Module {
   SettingGroup sgGeneral;
   private final Setting<Integer> cooldown;
   private final Setting<Boolean> noInstaBreak;
   private boolean breakBlockCooldown;

   public BreakDelay() {
      super(Categories.Player, "break-delay", "Changes the delay between breaking blocks.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.cooldown = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("cooldown")).description("Block break cooldown in ticks.")).defaultValue(0)).min(0).sliderMax(5).build());
      this.noInstaBreak = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("no-insta-break")).description("Prevents you from misbreaking blocks if you can instantly break them.")).defaultValue(false)).build());
      this.breakBlockCooldown = false;
   }

   @EventHandler
   private void onBlockBreakingCooldown(BlockBreakingCooldownEvent event) {
      if (this.breakBlockCooldown) {
         event.cooldown = 5;
         this.breakBlockCooldown = false;
      } else {
         event.cooldown = (Integer)this.cooldown.get();
      }

   }

   @EventHandler
   private void onClick(MouseClickEvent event) {
      if (event.action == KeyAction.Press && (Boolean)this.noInstaBreak.get()) {
         this.breakBlockCooldown = true;
      }

   }

   public boolean preventInstaBreak() {
      return this.isActive() && (Boolean)this.noInstaBreak.get();
   }
}
