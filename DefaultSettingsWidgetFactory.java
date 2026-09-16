package meteordevelopment.meteorclient.gui;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.screens.settings.BlockDataSettingScreen;
import meteordevelopment.meteorclient.gui.screens.settings.BlockListSettingScreen;
import meteordevelopment.meteorclient.gui.screens.settings.BlockSettingScreen;
import meteordevelopment.meteorclient.gui.screens.settings.ColorSettingScreen;
import meteordevelopment.meteorclient.gui.screens.settings.EnchantmentListSettingScreen;
import meteordevelopment.meteorclient.gui.screens.settings.EntityTypeListSettingScreen;
import meteordevelopment.meteorclient.gui.screens.settings.FontFaceSettingScreen;
import meteordevelopment.meteorclient.gui.screens.settings.ItemListSettingScreen;
import meteordevelopment.meteorclient.gui.screens.settings.ItemSettingScreen;
import meteordevelopment.meteorclient.gui.screens.settings.ModuleListSettingScreen;
import meteordevelopment.meteorclient.gui.screens.settings.PacketBoolSettingScreen;
import meteordevelopment.meteorclient.gui.screens.settings.ParticleTypeListSettingScreen;
import meteordevelopment.meteorclient.gui.screens.settings.PotionSettingScreen;
import meteordevelopment.meteorclient.gui.screens.settings.ScreenHandlerSettingScreen;
import meteordevelopment.meteorclient.gui.screens.settings.SoundEventListSettingScreen;
import meteordevelopment.meteorclient.gui.screens.settings.StatusEffectAmplifierMapSettingScreen;
import meteordevelopment.meteorclient.gui.screens.settings.StatusEffectListSettingScreen;
import meteordevelopment.meteorclient.gui.screens.settings.StorageBlockListSettingScreen;
import meteordevelopment.meteorclient.gui.themes.meteor.widgets.WMeteorLabel;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.utils.CharFilter;
import meteordevelopment.meteorclient.gui.utils.SettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.widgets.WItem;
import meteordevelopment.meteorclient.gui.widgets.WItemWithLabel;
import meteordevelopment.meteorclient.gui.widgets.WKeybind;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WQuad;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.input.WBlockPosEdit;
import meteordevelopment.meteorclient.gui.widgets.input.WDoubleEdit;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.gui.widgets.input.WIntEdit;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;
import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.renderer.text.FontFace;
import meteordevelopment.meteorclient.settings.BlockDataSetting;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BlockPosSetting;
import meteordevelopment.meteorclient.settings.BlockSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorListSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnchantmentListSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.FontFaceSetting;
import meteordevelopment.meteorclient.settings.GenericSetting;
import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.ItemSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.ModuleListSetting;
import meteordevelopment.meteorclient.settings.PacketListSetting;
import meteordevelopment.meteorclient.settings.ParticleTypeListSetting;
import meteordevelopment.meteorclient.settings.PotionSetting;
import meteordevelopment.meteorclient.settings.ProvidedStringSetting;
import meteordevelopment.meteorclient.settings.ScreenHandlerListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.settings.SoundEventListSetting;
import meteordevelopment.meteorclient.settings.StatusEffectAmplifierMapSetting;
import meteordevelopment.meteorclient.settings.StatusEffectListSetting;
import meteordevelopment.meteorclient.settings.StorageBlockListSetting;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.settings.Vector3dSetting;
import meteordevelopment.meteorclient.systems.hud.elements.keyboard.KeyboardHud;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.MyPotion;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1074;
import net.minecraft.class_1792;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import org.apache.commons.lang3.Strings;
import org.joml.Vector3d;

public class DefaultSettingsWidgetFactory extends SettingsWidgetFactory {
   private static final SettingColor WHITE = new SettingColor();

   public DefaultSettingsWidgetFactory(GuiTheme theme) {
      super(theme);
      this.factories.put(BoolSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.boolW(table, (BoolSetting)setting));
      this.factories.put(IntSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.intW(table, (IntSetting)setting));
      this.factories.put(DoubleSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.doubleW(table, (DoubleSetting)setting));
      this.factories.put(StringSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.stringW(table, (StringSetting)setting));
      this.factories.put(EnumSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.enumW(table, (EnumSetting)setting));
      this.factories.put(ProvidedStringSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.providedStringW(table, (ProvidedStringSetting)setting));
      this.factories.put(GenericSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.genericW(table, (GenericSetting)setting));
      this.factories.put(ColorSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.colorW(table, (ColorSetting)setting));
      this.factories.put(KeybindSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.keybindW(table, (KeybindSetting)setting));
      this.factories.put(BlockSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.blockW(table, (BlockSetting)setting));
      this.factories.put(BlockListSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.blockListW(table, (BlockListSetting)setting));
      this.factories.put(ItemSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.itemW(table, (ItemSetting)setting));
      this.factories.put(ItemListSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.itemListW(table, (ItemListSetting)setting));
      this.factories.put(EntityTypeListSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.entityTypeListW(table, (EntityTypeListSetting)setting));
      this.factories.put(EnchantmentListSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.enchantmentListW(table, (EnchantmentListSetting)setting));
      this.factories.put(ModuleListSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.moduleListW(table, (ModuleListSetting)setting));
      this.factories.put(PacketListSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.packetListW(table, (PacketListSetting)setting));
      this.factories.put(ParticleTypeListSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.particleTypeListW(table, (ParticleTypeListSetting)setting));
      this.factories.put(SoundEventListSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.soundEventListW(table, (SoundEventListSetting)setting));
      this.factories.put(StatusEffectAmplifierMapSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.statusEffectAmplifierMapW(table, (StatusEffectAmplifierMapSetting)setting));
      this.factories.put(StatusEffectListSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.statusEffectListW(table, (StatusEffectListSetting)setting));
      this.factories.put(StorageBlockListSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.storageBlockListW(table, (StorageBlockListSetting)setting));
      this.factories.put(ScreenHandlerListSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.screenHandlerListW(table, (ScreenHandlerListSetting)setting));
      this.factories.put(BlockDataSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.blockDataW(table, (BlockDataSetting)setting));
      this.factories.put(PotionSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.potionW(table, (PotionSetting)setting));
      this.factories.put(StringListSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.stringListW(table, (StringListSetting)setting));
      this.factories.put(BlockPosSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.blockPosW(table, (BlockPosSetting)setting));
      this.factories.put(ColorListSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.colorListW(table, (ColorListSetting)setting));
      this.factories.put(FontFaceSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.fontW(table, (FontFaceSetting)setting));
      this.factories.put(Vector3dSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.vector3dW(table, (Vector3dSetting)setting));
      this.factories.put(KeyboardHud.CustomKeyListSetting.class, (SettingsWidgetFactory.Factory)(table, setting) -> this.customKeyListW(table, (KeyboardHud.CustomKeyListSetting)setting));
   }

   public WWidget create(GuiTheme theme, Settings settings, String filter) {
      WVerticalList list = theme.verticalList();
      List<RemoveInfo> removeInfoList = new ArrayList();

      for(SettingGroup group : settings.groups) {
         this.group(list, group, filter, removeInfoList);
      }

      list.calculateSize();
      list.minWidth = list.width;

      for(RemoveInfo removeInfo : removeInfoList) {
         removeInfo.remove(list);
      }

      return list;
   }

   protected double settingTitleTopMargin() {
      return (double)6.0F;
   }

   private void group(WVerticalList list, SettingGroup group, String filter, List<RemoveInfo> removeInfoList) {
      WSection section = (WSection)list.add(this.theme.section(group.name, group.sectionExpanded)).expandX().widget();
      section.action = () -> group.sectionExpanded = section.isExpanded();
      WTable table = (WTable)section.add(this.theme.table()).expandX().widget();
      RemoveInfo removeInfo = null;

      for(Setting<?> setting : group) {
         if (Strings.CI.contains(setting.title, filter)) {
            boolean visible = setting.isVisible();
            setting.lastWasVisible = visible;
            if (!visible) {
               if (removeInfo == null) {
                  removeInfo = new RemoveInfo(section, table);
               }

               removeInfo.markRowForRemoval();
            }

            ((WLabel)table.add(this.theme.label(setting.title)).top().marginTop(this.settingTitleTopMargin()).widget()).tooltip = setting.description;
            SettingsWidgetFactory.Factory factory = this.getFactory(setting.getClass());
            if (factory != null) {
               factory.create(table, setting);
            }

            table.row();
         }
      }

      if (removeInfo != null) {
         removeInfoList.add(removeInfo);
      }

   }

   private void boolW(WTable table, BoolSetting setting) {
      WCheckbox checkbox = (WCheckbox)table.add(this.theme.checkbox((Boolean)setting.get())).expandCellX().widget();
      checkbox.action = () -> setting.set(checkbox.checked);
      this.reset(table, setting, () -> checkbox.checked = (Boolean)setting.get());
   }

   private void intW(WTable table, IntSetting setting) {
      WIntEdit edit = (WIntEdit)table.add(this.theme.intEdit((Integer)setting.get(), setting.min, setting.max, setting.sliderMin, setting.sliderMax, setting.noSlider)).expandX().widget();
      edit.action = () -> {
         if (!setting.set(edit.get())) {
            edit.set((Integer)setting.get());
         }

      };
      this.reset(table, setting, () -> edit.set((Integer)setting.get()));
   }

   private void doubleW(WTable table, DoubleSetting setting) {
      WDoubleEdit edit = this.theme.doubleEdit((Double)setting.get(), setting.min, setting.max, setting.sliderMin, setting.sliderMax, setting.decimalPlaces, setting.noSlider);
      table.add(edit).expandX();
      Runnable action = () -> {
         if (!setting.set(edit.get())) {
            edit.set((Double)setting.get());
         }

      };
      if (setting.onSliderRelease) {
         edit.actionOnRelease = action;
      } else {
         edit.action = action;
      }

      this.reset(table, setting, () -> edit.set((Double)setting.get()));
   }

   private void stringW(WTable table, StringSetting setting) {
      CharFilter filter = setting.filter == null ? (text, c) -> true : setting.filter;
      Cell<WTextBox> cell = table.<WTextBox>add(this.theme.textBox((String)setting.get(), setting.placeholder, filter, setting.renderer));
      if (setting.wide) {
         cell.minWidth((double)Utils.getWindowWidth() - (double)Utils.getWindowWidth() / (double)4.0F);
      }

      WTextBox textBox = cell.expandX().widget();
      textBox.action = () -> setting.set(textBox.get());
      this.reset(table, setting, () -> textBox.set((String)setting.get()));
   }

   private void stringListW(WTable table, StringListSetting setting) {
      WTable wtable = (WTable)table.add(this.theme.table()).expandX().widget();
      StringListSetting.fillTable(this.theme, wtable, setting);
   }

   private <T extends Enum<?>> void enumW(WTable table, EnumSetting<T> setting) {
      WDropdown<T> dropdown = (WDropdown)table.<T>add(this.theme.dropdown((Enum)setting.get())).expandCellX().widget();
      dropdown.action = () -> setting.set(dropdown.get());
      this.reset(table, setting, () -> dropdown.set((Enum)setting.get()));
   }

   private void providedStringW(WTable table, ProvidedStringSetting setting) {
      WDropdown<String> dropdown = (WDropdown)table.<String>add(this.theme.dropdown((String[])setting.supplier.get(), (String)setting.get())).expandCellX().widget();
      dropdown.action = () -> setting.set(dropdown.get());
      this.reset(table, setting, () -> dropdown.set((String)setting.get()));
   }

   private void genericW(WTable table, GenericSetting<?> setting) {
      WButton edit = (WButton)table.add(this.theme.button(GuiRenderer.EDIT)).widget();
      edit.action = () -> MeteorClient.mc.method_1507(setting.createScreen(this.theme));
      this.reset(table, setting, (Runnable)null);
   }

   private void colorW(WTable table, ColorSetting setting) {
      WHorizontalList list = (WHorizontalList)table.add(this.theme.horizontalList()).expandX().widget();
      WQuad quad = (WQuad)list.add(this.theme.quad((Color)setting.get())).widget();
      WButton edit = (WButton)list.add(this.theme.button(GuiRenderer.EDIT)).widget();
      edit.action = () -> MeteorClient.mc.method_1507(new ColorSettingScreen(this.theme, setting));
      this.reset(table, setting, () -> quad.color = (Color)setting.get());
   }

   private void keybindW(WTable table, KeybindSetting setting) {
      WHorizontalList list = (WHorizontalList)table.add(this.theme.horizontalList()).expandX().widget();
      WKeybind keybind = (WKeybind)list.add(this.theme.keybind((Keybind)setting.get(), (Keybind)setting.getDefaultValue())).expandX().widget();
      Objects.requireNonNull(setting);
      keybind.action = setting::onChanged;
      setting.widget = keybind;
      WButton reset = (WButton)list.add(this.theme.button(GuiRenderer.RESET)).expandCellX().right().widget();
      Objects.requireNonNull(keybind);
      reset.action = keybind::resetBind;
      reset.tooltip = "Reset";
   }

   private void blockW(WTable table, BlockSetting setting) {
      WHorizontalList list = (WHorizontalList)table.add(this.theme.horizontalList()).expandX().widget();
      WItem item = (WItem)list.add(this.theme.item(((class_2248)setting.get()).method_8389().method_7854())).widget();
      WButton select = (WButton)list.add(this.theme.button("Select")).widget();
      select.action = () -> {
         BlockSettingScreen screen = new BlockSettingScreen(this.theme, setting);
         screen.onClosed(() -> item.set(((class_2248)setting.get()).method_8389().method_7854()));
         MeteorClient.mc.method_1507(screen);
      };
      this.reset(table, setting, () -> item.set(((class_2248)setting.get()).method_8389().method_7854()));
   }

   private void blockPosW(WTable table, BlockPosSetting setting) {
      WBlockPosEdit edit = (WBlockPosEdit)table.add(this.theme.blockPosEdit((class_2338)setting.get())).expandX().widget();
      edit.actionOnRelease = () -> {
         if (!setting.set(edit.get())) {
            edit.set((class_2338)setting.get());
         }

      };
      this.reset(table, setting, () -> edit.set((class_2338)setting.get()));
   }

   private void blockListW(WTable table, BlockListSetting setting) {
      this.selectW(table, setting, () -> MeteorClient.mc.method_1507(new BlockListSettingScreen(this.theme, setting)));
   }

   private void itemW(WTable table, ItemSetting setting) {
      WHorizontalList list = (WHorizontalList)table.add(this.theme.horizontalList()).expandX().widget();
      WItem item = (WItem)list.add(this.theme.item(((class_1792)setting.get()).method_8389().method_7854())).widget();
      WButton select = (WButton)list.add(this.theme.button("Select")).widget();
      select.action = () -> {
         ItemSettingScreen screen = new ItemSettingScreen(this.theme, setting);
         screen.onClosed(() -> item.set(((class_1792)setting.get()).method_7854()));
         MeteorClient.mc.method_1507(screen);
      };
      this.reset(table, setting, () -> item.set(((class_1792)setting.get()).method_7854()));
   }

   private void itemListW(WTable table, ItemListSetting setting) {
      this.selectW(table, setting, () -> MeteorClient.mc.method_1507(new ItemListSettingScreen(this.theme, setting)));
   }

   private void entityTypeListW(WTable table, EntityTypeListSetting setting) {
      this.selectW(table, setting, () -> MeteorClient.mc.method_1507(new EntityTypeListSettingScreen(this.theme, setting)));
   }

   private void enchantmentListW(WTable table, EnchantmentListSetting setting) {
      this.selectW(table, setting, () -> MeteorClient.mc.method_1507(new EnchantmentListSettingScreen(this.theme, setting)));
   }

   private void moduleListW(WTable table, ModuleListSetting setting) {
      this.selectW(table, setting, () -> MeteorClient.mc.method_1507(new ModuleListSettingScreen(this.theme, setting)));
   }

   private void packetListW(WTable table, PacketListSetting setting) {
      this.selectW(table, setting, () -> MeteorClient.mc.method_1507(new PacketBoolSettingScreen(this.theme, setting)));
   }

   private void particleTypeListW(WTable table, ParticleTypeListSetting setting) {
      this.selectW(table, setting, () -> MeteorClient.mc.method_1507(new ParticleTypeListSettingScreen(this.theme, setting)));
   }

   private void soundEventListW(WTable table, SoundEventListSetting setting) {
      this.selectW(table, setting, () -> MeteorClient.mc.method_1507(new SoundEventListSettingScreen(this.theme, setting)));
   }

   private void statusEffectAmplifierMapW(WTable table, StatusEffectAmplifierMapSetting setting) {
      this.selectW(table, setting, () -> MeteorClient.mc.method_1507(new StatusEffectAmplifierMapSettingScreen(this.theme, setting)));
   }

   private void statusEffectListW(WTable table, StatusEffectListSetting setting) {
      this.selectW(table, setting, () -> MeteorClient.mc.method_1507(new StatusEffectListSettingScreen(this.theme, setting)));
   }

   private void storageBlockListW(WTable table, StorageBlockListSetting setting) {
      this.selectW(table, setting, () -> MeteorClient.mc.method_1507(new StorageBlockListSettingScreen(this.theme, setting)));
   }

   private void screenHandlerListW(WTable table, ScreenHandlerListSetting setting) {
      this.selectW(table, setting, () -> MeteorClient.mc.method_1507(new ScreenHandlerSettingScreen(this.theme, setting)));
   }

   private void blockDataW(WTable table, BlockDataSetting<?> setting) {
      WButton button = (WButton)table.add(this.theme.button(GuiRenderer.EDIT)).expandCellX().widget();
      button.action = () -> MeteorClient.mc.method_1507(new BlockDataSettingScreen(this.theme, setting));
      this.reset(table, setting, (Runnable)null);
   }

   private void potionW(WTable table, PotionSetting setting) {
      WHorizontalList list = (WHorizontalList)table.add(this.theme.horizontalList()).expandX().widget();
      WItemWithLabel item = (WItemWithLabel)list.add(this.theme.itemWithLabel(((MyPotion)setting.get()).potion, class_1074.method_4662(((MyPotion)setting.get()).potion.method_7909().method_7876(), new Object[0]))).widget();
      WButton button = (WButton)list.add(this.theme.button("Select")).expandCellX().widget();
      button.action = () -> {
         WidgetScreen screen = new PotionSettingScreen(this.theme, setting);
         screen.onClosed(() -> item.set(((MyPotion)setting.get()).potion));
         MeteorClient.mc.method_1507(screen);
      };
      this.reset(list, setting, () -> item.set(((MyPotion)setting.get()).potion));
   }

   private void fontW(WTable table, FontFaceSetting setting) {
      WHorizontalList list = (WHorizontalList)table.add(this.theme.horizontalList()).expandX().widget();
      WLabel label = (WLabel)list.add(this.theme.label(((FontFace)setting.get()).info.family())).widget();
      WButton button = (WButton)list.add(this.theme.button("Select")).expandCellX().widget();
      button.action = () -> {
         WidgetScreen screen = new FontFaceSettingScreen(this.theme, setting);
         screen.onClosed(() -> label.set(((FontFace)setting.get()).info.family()));
         MeteorClient.mc.method_1507(screen);
      };
      this.reset(list, setting, () -> label.set(Fonts.DEFAULT_FONT.info.family()));
   }

   private void colorListW(WTable table, ColorListSetting setting) {
      WTable tab = (WTable)table.add(this.theme.table()).expandX().widget();
      WTable t = (WTable)tab.add(this.theme.table()).expandX().widget();
      tab.row();
      this.colorListWFill(t, setting);
      WPlus add = (WPlus)tab.add(this.theme.plus()).expandCellX().widget();
      add.action = () -> {
         ((List)setting.get()).add(new SettingColor());
         setting.onChanged();
         t.clear();
         this.colorListWFill(t, setting);
      };
      this.reset(tab, setting, () -> {
         t.clear();
         this.colorListWFill(t, setting);
      });
   }

   private void colorListWFill(WTable t, ColorListSetting setting) {
      int i = 0;

      for(SettingColor color : (List)setting.get()) {
         t.add(this.theme.label(i + ":"));
         t.add(this.theme.quad(color)).widget();
         WButton edit = (WButton)t.add(this.theme.button(GuiRenderer.EDIT)).widget();
         edit.action = () -> {
            SettingColor defaultValue = WHITE;
            if (i < ((List)setting.getDefaultValue()).size()) {
               defaultValue = (SettingColor)((List)setting.getDefaultValue()).get(i);
            }

            ColorSetting set = new ColorSetting(setting.name, setting.description, defaultValue, (settingColor) -> {
               ((SettingColor)((List)setting.get()).get(i)).set((Color)settingColor);
               setting.onChanged();
            }, (Consumer)null, (IVisible)null);
            set.set((SettingColor)((List)setting.get()).get(i));
            MeteorClient.mc.method_1507(new ColorSettingScreen(this.theme, set));
         };
         WMinus remove = (WMinus)t.add(this.theme.minus()).expandCellX().right().widget();
         remove.action = () -> {
            ((List)setting.get()).remove(i);
            setting.onChanged();
            t.clear();
            this.colorListWFill(t, setting);
         };
         t.row();
         ++i;
      }

   }

   private void vector3dW(WTable table, Vector3dSetting setting) {
      WTable internal = (WTable)table.add(this.theme.table()).expandX().widget();
      WDoubleEdit x = this.addVectorComponent(internal, "X", ((Vector3d)setting.get()).x, (val) -> ((Vector3d)setting.get()).x = val, setting);
      WDoubleEdit y = this.addVectorComponent(internal, "Y", ((Vector3d)setting.get()).y, (val) -> ((Vector3d)setting.get()).y = val, setting);
      WDoubleEdit z = this.addVectorComponent(internal, "Z", ((Vector3d)setting.get()).z, (val) -> ((Vector3d)setting.get()).z = val, setting);
      this.reset(table, setting, () -> {
         x.set(((Vector3d)setting.get()).x);
         y.set(((Vector3d)setting.get()).y);
         z.set(((Vector3d)setting.get()).z);
      });
   }

   private WDoubleEdit addVectorComponent(WTable table, String label, double value, Consumer<Double> update, Vector3dSetting setting) {
      table.add(this.theme.label(label + ": "));
      WDoubleEdit component = (WDoubleEdit)table.add(this.theme.doubleEdit(value, setting.min, setting.max, setting.sliderMin, setting.sliderMax, setting.decimalPlaces, setting.noSlider)).expandX().widget();
      if (setting.onSliderRelease) {
         component.actionOnRelease = () -> {
            update.accept(component.get());
            setting.onChanged();
         };
      } else {
         component.action = () -> {
            update.accept(component.get());
            setting.onChanged();
         };
      }

      table.row();
      return component;
   }

   private void customKeyListW(WTable table, KeyboardHud.CustomKeyListSetting setting) {
      WTable wtable = (WTable)table.add(this.theme.table()).expandX().widget();
      KeyboardHud.fillTable(this.theme, wtable, setting);
   }

   private void selectW(WContainer c, Setting<?> setting, Runnable action) {
      boolean addCount = DefaultSettingsWidgetFactory.WSelectedCountLabel.getSize(setting) != -1;
      WContainer c2 = c;
      if (addCount) {
         c2 = (WContainer)c.add(this.theme.horizontalList()).expandCellX().widget();
         ((WHorizontalList)c2).spacing *= (double)2.0F;
      }

      WButton button = (WButton)c2.add(this.theme.button("Select")).expandCellX().widget();
      button.action = action;
      if (addCount) {
         c2.add((new WSelectedCountLabel(setting)).color(this.theme.textSecondaryColor()));
      }

      this.reset(c, setting, (Runnable)null);
   }

   private void reset(WContainer c, Setting<?> setting, Runnable action) {
      WButton reset = (WButton)c.add(this.theme.button(GuiRenderer.RESET)).widget();
      reset.action = () -> {
         setting.reset();
         if (action != null) {
            action.run();
         }

      };
      reset.tooltip = "Reset";
   }

   private static class RemoveInfo {
      private final WSection section;
      private final WTable table;
      private final IntList rowIds = new IntArrayList();

      public RemoveInfo(WSection section, WTable table) {
         this.section = section;
         this.table = table;
      }

      public void markRowForRemoval() {
         this.rowIds.add(this.table.rowI());
      }

      public void remove(WVerticalList list) {
         for(int i = 0; i < this.rowIds.size(); ++i) {
            this.table.removeRow(this.rowIds.getInt(i) - i);
         }

         if (this.table.cells.isEmpty()) {
            list.cells.removeIf((cell) -> cell.widget() == this.section);
         }

      }
   }

   private static class WSelectedCountLabel extends WMeteorLabel {
      private final Setting<?> setting;
      private int lastSize = -1;

      public WSelectedCountLabel(Setting<?> setting) {
         super("", false);
         this.setting = setting;
      }

      protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         int size = getSize(this.setting);
         if (size != this.lastSize) {
            this.set("(" + size + " selected)");
            this.lastSize = size;
         }

         super.onRender(renderer, mouseX, mouseY, delta);
      }

      public static int getSize(Setting<?> setting) {
         Object var2 = setting.get();
         if (var2 instanceof Collection<?> collection) {
            return collection.size();
         } else {
            var2 = setting.get();
            if (var2 instanceof Map<?, ?> map) {
               return map.size();
            } else {
               return -1;
            }
         }
      }
   }
}
