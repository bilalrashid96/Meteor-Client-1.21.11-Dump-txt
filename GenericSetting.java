package meteordevelopment.meteorclient.settings;

import java.util.function.Consumer;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import net.minecraft.class_2487;

public class GenericSetting<T extends IGeneric<T>> extends Setting<T> {
   public GenericSetting(String name, String description, T defaultValue, Consumer<T> onChanged, Consumer<Setting<T>> onModuleActivated, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
   }

   public WidgetScreen createScreen(GuiTheme theme) {
      return ((IGeneric)this.get()).createScreen(theme, this);
   }

   public void resetImpl() {
      if (this.value == null) {
         this.value = (T)((IGeneric)this.defaultValue).copy();
      }

      ((IGeneric)this.value).set(this.defaultValue);
   }

   protected T parseImpl(String str) {
      return (T)(((IGeneric)this.defaultValue).copy());
   }

   protected boolean isValueValid(T value) {
      return true;
   }

   public class_2487 save(class_2487 tag) {
      tag.method_10566("value", ((IGeneric)this.get()).toTag());
      return tag;
   }

   public T load(class_2487 tag) {
      ((IGeneric)this.get()).fromTag(tag.method_68568("value"));
      return (T)(this.get());
   }

   public static class Builder<T extends IGeneric<T>> extends Setting.SettingBuilder<Builder<T>, T, GenericSetting<T>> {
      public Builder() {
         super((Object)null);
      }

      public GenericSetting<T> build() {
         return new GenericSetting<T>(this.name, this.description, this.defaultValue, this.onChanged, this.onModuleActivated, this.visible);
      }
   }
}
