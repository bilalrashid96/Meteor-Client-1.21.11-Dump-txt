package meteordevelopment.meteorclient.utils.player;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.BlockActivateEvent;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1263;
import net.minecraft.class_1707;
import net.minecraft.class_1799;
import net.minecraft.class_2336;
import net.minecraft.class_2371;
import net.minecraft.class_476;

public class EChestMemory {
   public static final class_2371<class_1799> ITEMS;
   private static int echestOpenedState;
   private static boolean isKnown;

   private EChestMemory() {
   }

   @PreInit
   public static void init() {
      MeteorClient.EVENT_BUS.subscribe(EChestMemory.class);
   }

   @EventHandler
   private static void onBlockActivate(BlockActivateEvent event) {
      if (event.blockState.method_26204() instanceof class_2336 && echestOpenedState == 0) {
         echestOpenedState = 1;
      }

   }

   @EventHandler
   private static void onOpenScreenEvent(OpenScreenEvent event) {
      if (echestOpenedState == 1 && event.screen instanceof class_476) {
         echestOpenedState = 2;
      } else if (echestOpenedState != 0) {
         if (MeteorClient.mc.field_1755 instanceof class_476) {
            class_1707 container = (class_1707)((class_476)MeteorClient.mc.field_1755).method_17577();
            if (container != null) {
               class_1263 inv = container.method_7629();

               for(int i = 0; i < 27; ++i) {
                  ITEMS.set(i, inv.method_5438(i));
               }

               isKnown = true;
               echestOpenedState = 0;
            }
         }
      }
   }

   @EventHandler
   private static void onLeaveEvent(GameLeftEvent event) {
      ITEMS.clear();
      isKnown = false;
   }

   public static boolean isKnown() {
      return isKnown;
   }

   static {
      ITEMS = class_2371.method_10213(27, class_1799.field_8037);
      isKnown = false;
   }
}
