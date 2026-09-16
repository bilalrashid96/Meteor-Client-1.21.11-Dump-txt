package meteordevelopment.meteorclient.utils.render;

import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_1087;
import net.minecraft.class_10889;
import net.minecraft.class_11661;
import net.minecraft.class_11683;
import net.minecraft.class_11684;
import net.minecraft.class_11954;
import net.minecraft.class_12249;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2464;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_5819;
import net.minecraft.class_777;
import net.minecraft.class_827;
import org.joml.Vector3fc;

public abstract class SimpleBlockRenderer {
   private static final class_4587 MATRICES = new class_4587();
   private static final List<class_10889> PARTS = new ArrayList();
   private static final class_2350[] DIRECTIONS = class_2350.values();
   private static final class_5819 RANDOM = class_5819.method_43047();
   private static final class_11661 renderCommandQueue = new class_11661();
   private static class_4597 provider;
   private static final class_11684 renderDispatcher;

   private SimpleBlockRenderer() {
   }

   public static void renderWithBlockEntity(class_2586 blockEntity, float tickDelta, IVertexConsumerProvider vertexConsumerProvider) {
      vertexConsumerProvider.setOffset(blockEntity.method_11016().method_10263(), blockEntity.method_11016().method_10264(), blockEntity.method_11016().method_10260());
      render(blockEntity.method_11016(), blockEntity.method_11010(), vertexConsumerProvider);
      class_827<class_2586, class_11954> renderer = MeteorClient.mc.method_31975().method_3550(blockEntity);
      if (renderer != null && blockEntity.method_11002() && blockEntity.method_11017().method_20526(blockEntity.method_11010())) {
         provider = vertexConsumerProvider;
         class_11954 state = renderer.method_74335();
         renderer.method_74331(blockEntity, state, tickDelta, MeteorClient.mc.field_1773.method_19418().method_71156(), (class_11683.class_11792)null);
         renderer.method_3569(state, MATRICES, renderCommandQueue, MeteorClient.mc.field_1773.method_72912().field_63082);
         renderDispatcher.method_73002();
         renderCommandQueue.method_72954();
         provider = null;
      }

      vertexConsumerProvider.setOffset(0, 0, 0);
   }

   public static void render(class_2338 pos, class_2680 state, class_4597 consumerProvider) {
      if (state.method_26217() == class_2464.field_11458) {
         class_4588 consumer = consumerProvider.method_73477(class_12249.method_75965());
         class_1087 model = MeteorClient.mc.method_1541().method_3349(state);
         model.method_68513(RANDOM, PARTS);
         class_243 offset = state.method_26226(pos);
         float offsetX = (float)offset.field_1352;
         float offsetY = (float)offset.field_1351;
         float offsetZ = (float)offset.field_1350;

         for(class_10889 part : PARTS) {
            for(class_2350 direction : DIRECTIONS) {
               List<class_777> quads = part.method_68509(direction);
               if (!quads.isEmpty()) {
                  renderQuads(quads, offsetX, offsetY, offsetZ, consumer);
               }
            }

            List<class_777> quads = part.method_68509((class_2350)null);
            if (!quads.isEmpty()) {
               renderQuads(quads, offsetX, offsetY, offsetZ, consumer);
            }
         }

         PARTS.clear();
      }
   }

   private static void renderQuads(List<class_777> quads, float offsetX, float offsetY, float offsetZ, class_4588 consumer) {
      for(class_777 quad : quads) {
         for(int j = 0; j < 4; ++j) {
            Vector3fc vec = quad.method_76648(j);
            consumer.method_22912(offsetX + vec.x(), offsetY + vec.y(), offsetZ + vec.z());
         }
      }

   }

   static {
      renderDispatcher = new class_11684(renderCommandQueue, MeteorClient.mc.method_1541(), new WrapperImmediateVertexConsumerProvider(() -> provider), MeteorClient.mc.method_72703(), NoopOutlineVertexConsumerProvider.INSTANCE, NoopImmediateVertexConsumerProvider.INSTANCE, MeteorClient.mc.field_1772);
   }
}
