package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerManager;

public class FakePlayer extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<String> name;
   public final Setting<Boolean> copyInv;
   public final Setting<Integer> health;
   private WTable table;

   public FakePlayer() {
      super(Categories.Player, "fake-player", "Spawns a client-side fake player for testing usages. No need to be active.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.name = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("name")).description("The name of the fake player.")).defaultValue("seasnail8169")).build());
      this.copyInv = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("copy-inv")).description("Copies your inventory to the fake player.")).defaultValue(true)).build());
      this.health = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("health")).description("The fake player's default health.")).defaultValue(20)).min(1).sliderRange(1, 100).build());
   }

   public void onDeactivate() {
      FakePlayerManager.clear();
      if (this.table != null) {
         this.table.clear();
      }

   }

   public WWidget getWidget(GuiTheme theme) {
      this.table = theme.table();
      this.fillTable(theme, this.table);
      return this.table;
   }

   private void fillTable(GuiTheme theme, WTable table) {
      for(FakePlayerEntity fakePlayer : FakePlayerManager.getFakePlayers()) {
         table.add(theme.label(fakePlayer.method_5477().getString()));
         WMinus delete = (WMinus)table.add(theme.minus()).expandCellX().right().widget();
         delete.action = () -> {
            FakePlayerManager.remove(fakePlayer);
            table.clear();
            this.fillTable(theme, table);
         };
         table.row();
      }

      WButton spawn = (WButton)table.add(theme.button("Spawn")).expandCellX().right().widget();
      spawn.action = () -> {
         FakePlayerManager.add(this.name.get(), (float)(Integer)this.health.get(), (Boolean)this.copyInv.get());
         table.clear();
         this.fillTable(theme, table);
      };
      WButton clear = (WButton)table.add(theme.button("Clear All")).right().widget();
      clear.action = () -> {
         FakePlayerManager.clear();
         table.clear();
         this.fillTable(theme, table);
      };
   }

   public String getInfoString() {
      return String.valueOf(FakePlayerManager.count());
   }
}
