package meteordevelopment.meteorclient.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.screens.ModuleScreen;
import meteordevelopment.meteorclient.gui.screens.ModulesScreen;
import meteordevelopment.meteorclient.gui.screens.NotebotSongsScreen;
import meteordevelopment.meteorclient.gui.screens.ProxiesScreen;
import meteordevelopment.meteorclient.gui.screens.accounts.AccountsScreen;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.utils.CharFilter;
import meteordevelopment.meteorclient.gui.utils.SettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.utils.WindowConfig;
import meteordevelopment.meteorclient.gui.widgets.WAccount;
import meteordevelopment.meteorclient.gui.widgets.WHorizontalSeparator;
import meteordevelopment.meteorclient.gui.widgets.WItem;
import meteordevelopment.meteorclient.gui.widgets.WItemWithLabel;
import meteordevelopment.meteorclient.gui.widgets.WKeybind;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WQuad;
import meteordevelopment.meteorclient.gui.widgets.WTexture;
import meteordevelopment.meteorclient.gui.widgets.WTooltip;
import meteordevelopment.meteorclient.gui.widgets.WTopBar;
import meteordevelopment.meteorclient.gui.widgets.WVerticalSeparator;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.gui.widgets.input.WBlockPosEdit;
import meteordevelopment.meteorclient.gui.widgets.input.WDoubleEdit;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.gui.widgets.input.WIntEdit;
import meteordevelopment.meteorclient.gui.widgets.input.WSlider;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WConfirmedButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WConfirmedMinus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WFavorite;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;
import meteordevelopment.meteorclient.renderer.Texture;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.Names;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_437;

public abstract class GuiTheme implements ISerializable<GuiTheme> {
   public static final double TITLE_TEXT_SCALE = (double)1.25F;
   public final String name;
   public final Settings settings = new Settings();
   public boolean disableHoverColor;
   protected SettingsWidgetFactory settingsFactory;
   protected final Map<String, WindowConfig> windowConfigs = new HashMap();

   public GuiTheme(String name) {
      this.name = name;
   }

   public void beforeRender() {
      this.disableHoverColor = false;
   }

   public abstract WWindow window(WWidget var1, String var2);

   public WWindow window(String title) {
      return this.window((WWidget)null, title);
   }

   public abstract WLabel label(String var1, boolean var2, double var3);

   public WLabel label(String text, boolean title) {
      return this.label(text, title, (double)0.0F);
   }

   public WLabel label(String text, double maxWidth) {
      return this.label(text, false, maxWidth);
   }

   public WLabel label(String text) {
      return this.label(text, false);
   }

   public abstract WHorizontalSeparator horizontalSeparator(String var1);

   public WHorizontalSeparator horizontalSeparator() {
      return this.horizontalSeparator((String)null);
   }

   public abstract WVerticalSeparator verticalSeparator();

   protected abstract WButton button(String var1, GuiTexture var2);

   public WButton button(String text) {
      return this.button(text, (GuiTexture)null);
   }

   public WButton button(GuiTexture texture) {
      return this.button((String)null, texture);
   }

   protected abstract WConfirmedButton confirmedButton(String var1, String var2, GuiTexture var3);

   public WConfirmedButton confirmedButton(String text, String confirmText) {
      return this.confirmedButton(text, confirmText, (GuiTexture)null);
   }

   public WConfirmedButton confirmedButton(GuiTexture texture) {
      return this.confirmedButton((String)null, (String)null, texture);
   }

   public abstract WMinus minus();

   public abstract WConfirmedMinus confirmedMinus();

   public abstract WPlus plus();

   public abstract WCheckbox checkbox(boolean var1);

   public abstract WSlider slider(double var1, double var3, double var5);

   public abstract WTextBox textBox(String var1, String var2, CharFilter var3, Class<? extends WTextBox.Renderer> var4);

   public WTextBox textBox(String text, CharFilter filter, Class<? extends WTextBox.Renderer> renderer) {
      return this.textBox(text, (String)null, filter, renderer);
   }

   public WTextBox textBox(String text, String placeholder, CharFilter filter) {
      return this.textBox(text, placeholder, filter, (Class)null);
   }

   public WTextBox textBox(String text, CharFilter filter) {
      return this.textBox(text, (CharFilter)filter, (Class)null);
   }

   public WTextBox textBox(String text, String placeholder) {
      return this.textBox(text, placeholder, (text1, c) -> true, (Class)null);
   }

   public WTextBox textBox(String text) {
      return this.textBox(text, (CharFilter)((text1, c) -> true), (Class)null);
   }

   public abstract <T> WDropdown<T> dropdown(T[] var1, T var2);

   public <T extends Enum<?>> WDropdown<T> dropdown(T value) {
      Class<?> klass = value.getDeclaringClass();
      T[] values = (T[])(klass.getEnumConstants());
      return this.<T>dropdown(values, value);
   }

   public abstract WTriangle triangle();

   public abstract WTooltip tooltip(String var1);

   public abstract WView view();

   public WVerticalList verticalList() {
      return (WVerticalList)this.w(new WVerticalList());
   }

   public WHorizontalList horizontalList() {
      return (WHorizontalList)this.w(new WHorizontalList());
   }

   public WTable table() {
      return (WTable)this.w(new WTable());
   }

   public abstract WSection section(String var1, boolean var2, WWidget var3);

   public WSection section(String title, boolean expanded) {
      return this.section(title, expanded, (WWidget)null);
   }

   public WSection section(String title) {
      return this.section(title, true);
   }

   public abstract WAccount account(WidgetScreen var1, Account<?> var2);

   public WWidget module(Module module) {
      return this.module(module, module.title);
   }

   public abstract WWidget module(Module var1, String var2);

   public abstract WQuad quad(Color var1);

   public abstract WTopBar topBar();

   public abstract WFavorite favorite(boolean var1);

   public WItem item(class_1799 itemStack) {
      return (WItem)this.w(new WItem(itemStack));
   }

   public WItemWithLabel itemWithLabel(class_1799 stack, String name) {
      return (WItemWithLabel)this.w(new WItemWithLabel(stack, name));
   }

   public WItemWithLabel itemWithLabel(class_1799 stack) {
      return this.itemWithLabel(stack, Names.get(stack.method_7909()));
   }

   public WTexture texture(double width, double height, double rotation, Texture texture) {
      return (WTexture)this.w(new WTexture(width, height, rotation, texture));
   }

   public WIntEdit intEdit(int value, int min, int max, int sliderMin, int sliderMax, boolean noSlider) {
      return (WIntEdit)this.w(new WIntEdit(value, min, max, sliderMin, sliderMax, noSlider));
   }

   public WIntEdit intEdit(int value, int min, int max, int sliderMin, int sliderMax) {
      return (WIntEdit)this.w(new WIntEdit(value, min, max, sliderMin, sliderMax, false));
   }

   public WIntEdit intEdit(int value, int min, int max, boolean noSlider) {
      return (WIntEdit)this.w(new WIntEdit(value, min, max, 0, 0, noSlider));
   }

   public WDoubleEdit doubleEdit(double value, double min, double max, double sliderMin, double sliderMax, int decimalPlaces, boolean noSlider) {
      return (WDoubleEdit)this.w(new WDoubleEdit(value, min, max, sliderMin, sliderMax, decimalPlaces, noSlider));
   }

   public WDoubleEdit doubleEdit(double value, double min, double max, double sliderMin, double sliderMax) {
      return (WDoubleEdit)this.w(new WDoubleEdit(value, min, max, sliderMin, sliderMax, 3, false));
   }

   public WDoubleEdit doubleEdit(double value, double min, double max) {
      return (WDoubleEdit)this.w(new WDoubleEdit(value, min, max, (double)0.0F, (double)10.0F, 3, false));
   }

   public WBlockPosEdit blockPosEdit(class_2338 value) {
      return (WBlockPosEdit)this.w(new WBlockPosEdit(value));
   }

   public WKeybind keybind(Keybind keybind) {
      return this.keybind(keybind, Keybind.none());
   }

   public WKeybind keybind(Keybind keybind, Keybind defaultValue) {
      return (WKeybind)this.w(new WKeybind(keybind, defaultValue));
   }

   public WWidget settings(Settings settings, String filter) {
      return this.settingsFactory.create(this, settings, filter);
   }

   public WWidget settings(Settings settings) {
      return this.settings(settings, "");
   }

   public TabScreen modulesScreen() {
      return new ModulesScreen(this);
   }

   public boolean isModulesScreen(class_437 screen) {
      return screen instanceof ModulesScreen;
   }

   public WidgetScreen moduleScreen(Module module) {
      return new ModuleScreen(this, module);
   }

   public WidgetScreen accountsScreen() {
      return new AccountsScreen(this);
   }

   public NotebotSongsScreen notebotSongs() {
      return new NotebotSongsScreen(this);
   }

   public WidgetScreen proxiesScreen() {
      return new ProxiesScreen(this);
   }

   public abstract Color textColor();

   public abstract Color textSecondaryColor();

   public abstract Color starscriptTextColor();

   public abstract Color starscriptBraceColor();

   public abstract Color starscriptParenthesisColor();

   public abstract Color starscriptDotColor();

   public abstract Color starscriptCommaColor();

   public abstract Color starscriptOperatorColor();

   public abstract Color starscriptStringColor();

   public abstract Color starscriptNumberColor();

   public abstract Color starscriptKeywordColor();

   public abstract Color starscriptAccessedObjectColor();

   public abstract TextRenderer textRenderer();

   public abstract double scale(double var1);

   public abstract boolean categoryIcons();

   public abstract boolean hideHUD();

   public double textWidth(String text, int length, boolean title) {
      return this.scale(this.textRenderer().getWidth(text, length, false) * (title ? (double)1.25F : (double)1.0F));
   }

   public double textWidth(String text) {
      return this.textWidth(text, text.length(), false);
   }

   public double textHeight(boolean title) {
      return this.scale(this.textRenderer().getHeight() * (title ? (double)1.25F : (double)1.0F));
   }

   public double textHeight() {
      return this.textHeight(false);
   }

   public double pad() {
      return this.scale((double)6.0F);
   }

   public WindowConfig getWindowConfig(String id) {
      WindowConfig config = (WindowConfig)this.windowConfigs.get(id);
      if (config != null) {
         return config;
      } else {
         config = new WindowConfig();
         this.windowConfigs.put(id, config);
         return config;
      }
   }

   public void clearWindowConfigs() {
      this.windowConfigs.clear();
   }

   protected <T extends WWidget> T w(T widget) {
      widget.theme = this;
      return widget;
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10582("name", this.name);
      tag.method_10566("settings", this.settings.toTag());
      class_2487 configs = new class_2487();

      for(String id : this.windowConfigs.keySet()) {
         configs.method_10566(id, ((WindowConfig)this.windowConfigs.get(id)).toTag());
      }

      tag.method_10566("windowConfigs", configs);
      return tag;
   }

   public GuiTheme fromTag(class_2487 tag) {
      Optional var10000 = tag.method_10562("settings");
      Settings var10001 = this.settings;
      Objects.requireNonNull(var10001);
      var10000.ifPresent(var10001::fromTag);
      tag.method_10562("windowConfigs").ifPresent((configs) -> {
         for(String id : configs.method_10541()) {
            this.windowConfigs.put(id, (new WindowConfig()).fromTag((class_2487)configs.method_10562(id).get()));
         }

      });
      return this;
   }
}
