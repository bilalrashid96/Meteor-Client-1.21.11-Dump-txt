package meteordevelopment.meteorclient.systems.modules.render.marker;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.MarkerScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.Dimension;
import net.minecraft.class_2487;
import net.minecraft.class_437;

public abstract class BaseMarker implements ISerializable<BaseMarker> {
   public final Settings settings = new Settings();
   protected final SettingGroup sgBase;
   public final Setting<String> name;
   protected final Setting<String> description;
   private final Setting<Dimension> dimension;
   private final Setting<Boolean> active;

   public BaseMarker(String name) {
      this.sgBase = this.settings.createGroup("Base");
      this.name = this.sgBase.add(((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("name")).description("Custom name for this marker.")).build());
      this.description = this.sgBase.add(((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("description")).description("Custom description for this marker.")).build());
      this.dimension = this.sgBase.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("dimension")).description("In which dimension this marker should be visible.")).defaultValue(Dimension.Overworld)).build());
      this.active = this.sgBase.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("active")).description("Is this marker visible.")).defaultValue(false)).build());
      this.name.set(name);
      this.dimension.set(PlayerUtils.getDimension());
   }

   protected void render(Render3DEvent event) {
   }

   protected void tick() {
   }

   public class_437 getScreen(GuiTheme theme) {
      return new MarkerScreen(theme, this);
   }

   public WWidget getWidget(GuiTheme theme) {
      return null;
   }

   public String getName() {
      return this.name.get();
   }

   public String getTypeName() {
      return null;
   }

   public boolean isActive() {
      return (Boolean)this.active.get();
   }

   public boolean isVisible() {
      return this.isActive() && PlayerUtils.getDimension() == this.dimension.get();
   }

   public Dimension getDimension() {
      return this.dimension.get();
   }

   public void toggle() {
      this.active.set(!(Boolean)this.active.get());
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10566("settings", this.settings.toTag());
      return tag;
   }

   public BaseMarker fromTag(class_2487 tag) {
      class_2487 settingsTag = (class_2487)tag.method_10580("settings");
      if (settingsTag != null) {
         this.settings.fromTag(settingsTag);
      }

      return this;
   }
}
