package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;

public class Reach extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> blockReach;
   private final Setting<Double> entityReach;

   public Reach() {
      super(Categories.Player, "reach", "Gives you super long arms.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.blockReach = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("extra-block-reach")).description("The distance to add to your block reach.")).sliderMax((double)1.0F).build());
      this.entityReach = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("extra-entity-reach")).description("The distance to add to your entity reach.")).sliderMax((double)1.0F).build());
   }

   public WWidget getWidget(GuiTheme theme) {
      return theme.label("Note: on vanilla servers you may give yourself up to 4 blocks of additional reach for specific actions - interacting with block entities (chests, furnaces, etc.) or with vehicles. This does not work on paper servers.", (double)Utils.getWindowWidth() / (double)3.0F);
   }

   public double blockReach() {
      return this.isActive() ? (Double)this.blockReach.get() : (double)0.0F;
   }

   public double entityReach() {
      return this.isActive() ? (Double)this.entityReach.get() : (double)0.0F;
   }
}
