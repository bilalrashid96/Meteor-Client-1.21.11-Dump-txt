package meteordevelopment.meteorclient.events.render;

import meteordevelopment.meteorclient.events.Cancellable;
import meteordevelopment.meteorclient.mixininterface.IEntityRenderState;
import net.minecraft.class_10039;
import net.minecraft.class_10442;
import net.minecraft.class_11659;
import net.minecraft.class_1542;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import org.jspecify.annotations.Nullable;

public class RenderItemEntityEvent extends Cancellable {
   private static final RenderItemEntityEvent INSTANCE = new RenderItemEntityEvent();
   public @Nullable class_1542 itemEntity;
   public class_10039 renderState;
   public float tickDelta;
   public class_4587 matrixStack;
   public class_4597 vertexConsumerProvider;
   public int light;
   public class_10442 itemModelManager;
   public class_11659 renderCommandQueue;

   public static RenderItemEntityEvent get(class_10039 renderState, float tickDelta, class_4587 matrixStack, class_4597 vertexConsumerProvider, int light, class_10442 itemModelManager, class_11659 renderCommandQueue) {
      INSTANCE.setCancelled(false);
      INSTANCE.itemEntity = (class_1542)((IEntityRenderState)renderState).meteor$getEntity();
      INSTANCE.renderState = renderState;
      INSTANCE.tickDelta = tickDelta;
      INSTANCE.matrixStack = matrixStack;
      INSTANCE.vertexConsumerProvider = vertexConsumerProvider;
      INSTANCE.light = light;
      INSTANCE.itemModelManager = itemModelManager;
      INSTANCE.renderCommandQueue = renderCommandQueue;
      return INSTANCE;
   }
}
