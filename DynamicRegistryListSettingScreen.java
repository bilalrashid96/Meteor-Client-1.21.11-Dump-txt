package meteordevelopment.meteorclient.gui.screens.settings.base;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.class_151;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_5321;
import net.minecraft.class_7225;
import net.minecraft.class_7887;

public abstract class DynamicRegistryListSettingScreen<T> extends CollectionListSettingScreen<class_5321<T>> {
   protected final class_5321<class_2378<T>> registryKey;

   public DynamicRegistryListSettingScreen(GuiTheme theme, String title, Setting<?> setting, Collection<class_5321<T>> collection, class_5321<class_2378<T>> registryKey) {
      super(theme, title, setting, collection, createUniverse(collection, registryKey));
      this.registryKey = registryKey;
   }

   private static <T> Iterable<class_5321<T>> createUniverse(Collection<class_5321<T>> collection, class_5321<class_2378<T>> registryKey) {
      Set<class_5321<T>> set = new ReferenceOpenHashSet(collection);
      ((class_7225.class_7874)Optional.ofNullable(class_310.method_1551().method_1562()).map((networkHandler) -> networkHandler.method_29091()).orElseGet(class_7887::method_46817)).method_46759(registryKey).ifPresent((registry) -> {
         Stream var10000 = registry.method_46754();
         Objects.requireNonNull(set);
         var10000.forEach(set::add);
      });
      return set;
   }

   protected void postWidgets(WTable left, WTable right) {
      if (!left.cells.isEmpty()) {
         left.add(this.theme.horizontalSeparator()).expandX();
         left.row();
      }

      WHorizontalList manualEntry = (WHorizontalList)left.add(this.theme.horizontalList()).expandX().widget();
      WTextBox textBox = (WTextBox)manualEntry.add(this.theme.textBox("minecraft:")).expandX().minWidth((double)120.0F).widget();
      ((WPlus)manualEntry.add(this.theme.plus()).expandCellX().right().widget()).action = () -> {
         String entry = textBox.get().trim();

         try {
            class_2960 id = entry.contains(":") ? class_2960.method_60654(entry) : class_2960.method_60656(entry);
            this.addValue(class_5321.method_29179(this.registryKey, id));
         } catch (class_151 var4) {
         }

      };
   }
}
