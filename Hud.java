package meteordevelopment.meteorclient.systems.hud;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.CustomFontChangedEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorListSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.hud.elements.ActiveModulesHud;
import meteordevelopment.meteorclient.systems.hud.elements.ArmorHud;
import meteordevelopment.meteorclient.systems.hud.elements.CombatHud;
import meteordevelopment.meteorclient.systems.hud.elements.CompassHud;
import meteordevelopment.meteorclient.systems.hud.elements.HoleHud;
import meteordevelopment.meteorclient.systems.hud.elements.InventoryHud;
import meteordevelopment.meteorclient.systems.hud.elements.ItemHud;
import meteordevelopment.meteorclient.systems.hud.elements.LagNotifierHud;
import meteordevelopment.meteorclient.systems.hud.elements.MapHud;
import meteordevelopment.meteorclient.systems.hud.elements.MeteorTextHud;
import meteordevelopment.meteorclient.systems.hud.elements.ModuleInfosHud;
import meteordevelopment.meteorclient.systems.hud.elements.PlayerModelHud;
import meteordevelopment.meteorclient.systems.hud.elements.PlayerRadarHud;
import meteordevelopment.meteorclient.systems.hud.elements.PotionTimersHud;
import meteordevelopment.meteorclient.systems.hud.elements.keyboard.KeyboardHud;
import meteordevelopment.meteorclient.systems.hud.screens.HudEditorScreen;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2487;
import net.minecraft.class_2520;
import org.jetbrains.annotations.NotNull;

public class Hud extends System<Hud> implements Iterable<HudElement> {
   public static final HudGroup GROUP = new HudGroup("Meteor");
   public boolean active;
   public Settings settings = new Settings();
   public final Map<String, HudElementInfo<?>> infos = new TreeMap();
   private final List<HudElement> elements = new ArrayList();
   private final SettingGroup sgGeneral;
   private final SettingGroup sgEditor;
   private final SettingGroup sgKeybind;
   private final Setting<Boolean> customFont;
   private final Setting<Boolean> hideInMenus;
   private final Setting<Double> textScale;
   public final Setting<List<SettingColor>> textColors;
   public final Setting<Integer> border;
   public final Setting<Integer> snappingRange;
   private final Setting<Keybind> keybind;
   private boolean resetToDefaultElements;

   public Hud() {
      super("hud");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgEditor = this.settings.createGroup("Editor");
      this.sgKeybind = this.settings.createGroup("Bind");
      this.customFont = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-font")).description("Text will use custom font.")).defaultValue(true)).onChanged((aBoolean) -> {
         for(HudElement element : this.elements) {
            element.onFontChanged();
         }

      })).build());
      this.hideInMenus = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("hide-in-menus")).description("Hides the meteor hud when in inventory screens or game menus.")).defaultValue(false)).build());
      this.textScale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("text-scale")).description("Scale of text if not overridden by the element.")).defaultValue((double)1.0F).min((double)0.5F).sliderRange((double)0.5F, (double)3.0F).build());
      this.textColors = this.sgGeneral.add(((ColorListSetting.Builder)((ColorListSetting.Builder)((ColorListSetting.Builder)(new ColorListSetting.Builder()).name("text-colors")).description("Colors used for the Text element.")).defaultValue(List.of(new SettingColor(), new SettingColor(175, 175, 175), new SettingColor(25, 225, 25), new SettingColor(225, 25, 25)))).build());
      this.border = this.sgEditor.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("border")).description("Space around the edges of the screen.")).defaultValue(4)).sliderMax(20).build());
      this.snappingRange = this.sgEditor.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("snapping-range")).description("Snapping range in editor.")).defaultValue(10)).sliderMax(20).build());
      this.keybind = this.sgKeybind.add(((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("bind")).defaultValue(Keybind.none())).action(() -> this.active = !this.active).build());
   }

   public static Hud get() {
      return (Hud)Systems.get(Hud.class);
   }

   public void init() {
      this.settings.registerColorSettings((Module)null);
      this.register(MeteorTextHud.INFO);
      this.register(ItemHud.INFO);
      this.register(InventoryHud.INFO);
      this.register(CompassHud.INFO);
      this.register(ArmorHud.INFO);
      this.register(HoleHud.INFO);
      this.register(PlayerModelHud.INFO);
      this.register(ActiveModulesHud.INFO);
      this.register(LagNotifierHud.INFO);
      this.register(PlayerRadarHud.INFO);
      this.register(ModuleInfosHud.INFO);
      this.register(PotionTimersHud.INFO);
      this.register(CombatHud.INFO);
      this.register(MapHud.INFO);
      this.register(KeyboardHud.INFO);
      if (this.isFirstInit) {
         this.resetToDefaultElements();
      }

   }

   public void register(HudElementInfo<?> info) {
      this.infos.put(info.name, info);
   }

   private void add(HudElement element, int x, int y, XAnchor xAnchor, YAnchor yAnchor) {
      element.box.setPos(x, y);
      if (xAnchor != null && yAnchor != null) {
         element.box.xAnchor = xAnchor;
         element.box.yAnchor = yAnchor;
      } else {
         element.box.updateAnchors();
      }

      element.settings.registerColorSettings((Module)null);
      this.elements.add(element);
   }

   public void add(HudElementInfo<?> info, int x, int y, XAnchor xAnchor, YAnchor yAnchor) {
      this.add(info.create(), x, y, xAnchor, yAnchor);
   }

   public void add(HudElementInfo<?> info, int x, int y) {
      this.add((HudElementInfo)info, x, y, (XAnchor)null, (YAnchor)null);
   }

   public void add(@NotNull HudElementInfo.Preset preset, int x, int y, XAnchor xAnchor, YAnchor yAnchor) {
      HudElement element = preset.info.create();
      preset.callback.accept(element);
      this.add(element, x, y, xAnchor, yAnchor);
   }

   public void add(@NotNull HudElementInfo<?>.Preset preset, int x, int y) {
      this.add((HudElementInfo.Preset)preset, x, y, (XAnchor)null, (YAnchor)null);
   }

   void remove(HudElement element) {
      element.settings.unregisterColorSettings();
      this.elements.remove(element);
   }

   public void clear() {
      this.elements.clear();
   }

   public void resetToDefaultElements() {
      this.resetToDefaultElements = true;
   }

   private void resetToDefaultElementsImpl() {
      this.elements.clear();
      int h = (int)Math.ceil(HudRenderer.INSTANCE.textHeight(true));
      this.add((HudElementInfo.Preset)MeteorTextHud.WATERMARK, 4, 4, XAnchor.Left, YAnchor.Top);
      this.add((HudElementInfo.Preset)MeteorTextHud.FPS, 4, 4 + h, XAnchor.Left, YAnchor.Top);
      this.add((HudElementInfo.Preset)MeteorTextHud.TPS, 4, 4 + h * 2, XAnchor.Left, YAnchor.Top);
      this.add((HudElementInfo.Preset)MeteorTextHud.PING, 4, 4 + h * 3, XAnchor.Left, YAnchor.Top);
      this.add((HudElementInfo.Preset)MeteorTextHud.SPEED, 4, 4 + h * 4, XAnchor.Left, YAnchor.Top);
      this.add((HudElementInfo)ActiveModulesHud.INFO, -4, 4, XAnchor.Right, YAnchor.Top);
      this.add((HudElementInfo.Preset)MeteorTextHud.POSITION, -4, -4, XAnchor.Right, YAnchor.Bottom);
      this.add((HudElementInfo.Preset)MeteorTextHud.OPPOSITE_POSITION, -4, -4 - h, XAnchor.Right, YAnchor.Bottom);
      this.add((HudElementInfo.Preset)MeteorTextHud.ROTATION, -4, -4 - h * 2, XAnchor.Right, YAnchor.Bottom);
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (!Utils.isLoading()) {
         if (this.resetToDefaultElements) {
            this.resetToDefaultElementsImpl();
            this.resetToDefaultElements = false;
         }

         if (this.active || HudEditorScreen.isOpen()) {
            for(HudElement element : this.elements) {
               if (element.isActive() || element.isInEditor()) {
                  element.tick(HudRenderer.INSTANCE);
               }
            }

         }
      }
   }

   @EventHandler
   private void onRender(Render2DEvent event) {
      if (!Utils.isLoading()) {
         if (this.active && !this.shouldHideHud()) {
            if (!MeteorClient.mc.field_1690.field_1842 && !MeteorClient.mc.field_61504.method_72776() || HudEditorScreen.isOpen()) {
               HudRenderer.INSTANCE.begin(event.drawContext);

               for(HudElement element : this.elements) {
                  element.updatePos();
                  if (element.isActive() || element.isInEditor()) {
                     element.render(HudRenderer.INSTANCE);
                  }
               }

               HudRenderer.INSTANCE.end();
            }
         }
      }
   }

   private boolean shouldHideHud() {
      return (Boolean)this.hideInMenus.get() && MeteorClient.mc.field_1755 != null && !(MeteorClient.mc.field_1755 instanceof WidgetScreen);
   }

   @EventHandler
   private void onCustomFontChanged(CustomFontChangedEvent event) {
      if ((Boolean)this.customFont.get()) {
         for(HudElement element : this.elements) {
            element.onFontChanged();
         }
      }

   }

   public boolean hasCustomFont() {
      return (Boolean)this.customFont.get();
   }

   public double getTextScale() {
      return (Double)this.textScale.get();
   }

   public @NotNull Iterator<HudElement> iterator() {
      return this.elements.iterator();
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10569("__version__", 1);
      tag.method_10556("active", this.active);
      tag.method_10566("settings", this.settings.toTag());
      tag.method_10566("elements", NbtUtils.listToTag(this.elements));
      return tag;
   }

   public Hud fromTag(class_2487 tag) {
      if (!tag.method_10545("__version__")) {
         this.resetToDefaultElements();
         return this;
      } else {
         tag.method_10577("active").ifPresent((active1) -> this.active = active1);
         this.settings.fromTag(tag.method_68568("settings"));
         this.elements.clear();

         for(class_2520 e : tag.method_68569("elements")) {
            class_2487 c = (class_2487)e;
            if (!c.method_10558("name").isEmpty()) {
               HudElementInfo<?> info = (HudElementInfo)this.infos.get(c.method_10558("name").get());
               if (info != null) {
                  HudElement element = info.create();
                  element.fromTag(c);
                  element.settings.registerColorSettings((Module)null);
                  this.elements.add(element);
               }
            }
         }

         return this;
      }
   }
}
