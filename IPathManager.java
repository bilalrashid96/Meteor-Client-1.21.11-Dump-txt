package meteordevelopment.meteorclient.pathing;

import java.util.function.Predicate;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.Settings;
import net.minecraft.class_1297;
import net.minecraft.class_2248;
import net.minecraft.class_2338;

public interface IPathManager {
   String getName();

   boolean isPathing();

   void pause();

   void resume();

   void stop();

   default void moveTo(class_2338 pos) {
      this.moveTo(pos, false);
   }

   void moveTo(class_2338 var1, boolean var2);

   void moveInDirection(float var1);

   void mine(class_2248... var1);

   void follow(Predicate<class_1297> var1);

   float getTargetYaw();

   float getTargetPitch();

   ISettings getSettings();

   public interface ISettings {
      Settings get();

      Setting<Boolean> getWalkOnWater();

      Setting<Boolean> getWalkOnLava();

      Setting<Boolean> getStep();

      Setting<Boolean> getNoFall();

      void save();
   }
}
