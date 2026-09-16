package meteordevelopment.meteorclient.events.render;

import java.util.Iterator;
import net.minecraft.class_2561;
import net.minecraft.class_345;

public class RenderBossBarEvent {
   public static class BossText {
      private static final BossText INSTANCE = new BossText();
      public class_345 bossBar;
      public class_2561 name;

      public static BossText get(class_345 bossBar, class_2561 name) {
         INSTANCE.bossBar = bossBar;
         INSTANCE.name = name;
         return INSTANCE;
      }
   }

   public static class BossSpacing {
      private static final BossSpacing INSTANCE = new BossSpacing();
      public int spacing;

      public static BossSpacing get(int spacing) {
         INSTANCE.spacing = spacing;
         return INSTANCE;
      }
   }

   public static class BossIterator {
      private static final BossIterator INSTANCE = new BossIterator();
      public Iterator<class_345> iterator;

      public static BossIterator get(Iterator<class_345> iterator) {
         INSTANCE.iterator = iterator;
         return INSTANCE;
      }
   }
}
