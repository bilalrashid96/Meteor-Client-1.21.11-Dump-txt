package meteordevelopment.meteorclient.settings;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_2338;
import net.minecraft.class_2487;

public class BlockPosSetting extends Setting<class_2338> {
   public BlockPosSetting(String name, String description, class_2338 defaultValue, Consumer<class_2338> onChanged, Consumer<Setting<class_2338>> onModuleActivated, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
   }

   protected class_2338 parseImpl(String str) {
      List<String> values = List.of(str.split(","));
      if (values.size() != 3) {
         return null;
      } else {
         class_2338 bp = null;

         try {
            bp = new class_2338(Integer.parseInt((String)values.get(0)), Integer.parseInt((String)values.get(1)), Integer.parseInt((String)values.get(2)));
         } catch (NumberFormatException var5) {
         }

         return bp;
      }
   }

   protected boolean isValueValid(class_2338 value) {
      return true;
   }

   protected class_2487 save(class_2487 tag) {
      tag.method_10539("value", new int[]{((class_2338)this.value).method_10263(), ((class_2338)this.value).method_10264(), ((class_2338)this.value).method_10260()});
      return tag;
   }

   protected class_2338 load(class_2487 tag) {
      if (tag.method_10561("value").isPresent()) {
         int[] value = (int[])tag.method_10561("value").get();
         this.set(new class_2338(value[0], value[1], value[2]));
      }

      return (class_2338)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<Builder, class_2338, BlockPosSetting> {
      public Builder() {
         super(new class_2338(0, 0, 0));
      }

      public BlockPosSetting build() {
         return new BlockPosSetting(this.name, this.description, this.defaultValue, this.onChanged, this.onModuleActivated, this.visible);
      }
   }
}
