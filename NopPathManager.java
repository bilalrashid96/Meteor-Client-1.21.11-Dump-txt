package meteordevelopment.meteorclient.pathing;

import java.util.function.Predicate;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.Settings;
import net.minecraft.class_1297;
import net.minecraft.class_2248;
import net.minecraft.class_2338;

public class NopPathManager implements IPathManager {
   private final NopSettings settings = new NopSettings();

   public String getName() {
      return "none";
   }

   public boolean isPathing() {
      return false;
   }

   public void pause() {
   }

   public void resume() {
   }

   public void stop() {
   }

   public void moveTo(class_2338 pos, boolean ignoreY) {
   }

   public void moveInDirection(float yaw) {
   }

   public void mine(class_2248... blocks) {
   }

   public void follow(Predicate<class_1297> entity) {
   }

   public float getTargetYaw() {
      return 0.0F;
   }

   public float getTargetPitch() {
      return 0.0F;
   }

   public IPathManager.ISettings getSettings() {
      return this.settings;
   }

   private static class NopSettings implements IPathManager.ISettings {
      private final Settings settings = new Settings();
      private final Setting<Boolean> setting = (new BoolSetting.Builder()).build();

      public Settings get() {
         return this.settings;
      }

      public Setting<Boolean> getWalkOnWater() {
         this.setting.reset();
         return this.setting;
      }

      public Setting<Boolean> getWalkOnLava() {
         this.setting.reset();
         return this.setting;
      }

      public Setting<Boolean> getStep() {
         this.setting.reset();
         return this.setting;
      }

      public Setting<Boolean> getNoFall() {
         this.setting.reset();
         return this.setting;
      }

      public void save() {
      }
   }
}
