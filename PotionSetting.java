package meteordevelopment.meteorclient.settings;

import java.util.function.Consumer;
import meteordevelopment.meteorclient.utils.misc.MyPotion;

public class PotionSetting extends EnumSetting<MyPotion> {
   public PotionSetting(String name, String description, MyPotion defaultValue, Consumer<MyPotion> onChanged, Consumer<Setting<MyPotion>> onModuleActivated, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
   }

   public static class Builder extends EnumSetting.Builder<MyPotion> {
      public EnumSetting<MyPotion> build() {
         return new PotionSetting(this.name, this.description, this.defaultValue, this.onChanged, this.onModuleActivated, this.visible);
      }
   }
}
