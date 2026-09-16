package meteordevelopment.meteorclient.systems.hud.screens;

import java.util.Objects;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.hud.HudBox;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.XAnchor;
import meteordevelopment.meteorclient.systems.hud.YAnchor;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import net.minecraft.class_332;

public class HudElementScreen extends WindowScreen {
   private final HudElement element;
   private WContainer settingsC1;
   private WContainer settingsC2;
   private final Settings settings;

   public HudElementScreen(GuiTheme theme, HudElement element) {
      super(theme, element.info.title);
      this.element = element;
      this.settings = new Settings();
      SettingGroup sg = this.settings.createGroup("Anchors");
      sg.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-anchors")).description("Automatically assigns anchors based on the position.")).defaultValue(true)).onModuleActivated((booleanSetting) -> booleanSetting.set(element.autoAnchors))).onChanged((aBoolean) -> {
         if (aBoolean) {
            element.box.updateAnchors();
         }

         element.autoAnchors = aBoolean;
      })).build());
      EnumSetting.Builder var10001 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("x-anchor")).description("Horizontal anchor.")).defaultValue(XAnchor.Left)).visible(() -> !element.autoAnchors)).onModuleActivated((xAnchorSetting) -> xAnchorSetting.set(element.box.xAnchor));
      HudBox var10002 = element.box;
      Objects.requireNonNull(var10002);
      sg.add(((EnumSetting.Builder)var10001.onChanged(var10002::setXAnchor)).build());
      var10001 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("y-anchor")).description("Vertical anchor.")).defaultValue(YAnchor.Top)).visible(() -> !element.autoAnchors)).onModuleActivated((yAnchorSetting) -> yAnchorSetting.set(element.box.yAnchor));
      var10002 = element.box;
      Objects.requireNonNull(var10002);
      sg.add(((EnumSetting.Builder)var10001.onChanged(var10002::setYAnchor)).build());
   }

   public void initWidgets() {
      this.add(this.theme.label(this.element.info.description, (double)Utils.getWindowWidth() / (double)2.0F));
      if (this.element.settings.sizeGroups() > 0) {
         this.element.settings.onActivated();
         this.settingsC1 = (WContainer)this.add(this.theme.verticalList()).expandX().widget();
         this.settingsC1.add(this.theme.settings(this.element.settings)).expandX();
      }

      this.settings.onActivated();
      this.settingsC2 = (WContainer)this.add(this.theme.verticalList()).expandX().widget();
      this.settingsC2.add(this.theme.settings(this.settings)).expandX();
      this.add(this.theme.horizontalSeparator()).expandX();
      WWidget widget = this.element.getWidget(this.theme);
      if (widget != null) {
         Cell<WWidget> cell = this.add(widget);
         if (widget instanceof WContainer) {
            cell.expandX();
         }

         this.add(this.theme.horizontalSeparator()).expandX();
      }

      WHorizontalList bottomList = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
      bottomList.add(this.theme.label("Active:"));
      WCheckbox active = (WCheckbox)bottomList.add(this.theme.checkbox(this.element.isActive())).widget();
      active.action = () -> {
         if (this.element.isActive() != active.checked) {
            this.element.toggle();
         }

      };
      WMinus remove = (WMinus)bottomList.add(this.theme.minus()).expandCellX().right().widget();
      remove.action = () -> {
         this.element.remove();
         this.method_25419();
      };
   }

   public void method_25393() {
      super.method_25393();
      if (this.settingsC1 != null) {
         this.element.settings.tick(this.settingsC1, this.theme);
      }

      this.settings.tick(this.settingsC2, this.theme);
   }

   protected void onRenderBefore(class_332 drawContext, float delta) {
      HudEditorScreen.renderElements(drawContext);
   }

   public boolean toClipboard() {
      return NbtUtils.toClipboard(this.element);
   }

   public boolean fromClipboard() {
      return NbtUtils.fromClipboard(this.element);
   }
}
