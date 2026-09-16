package meteordevelopment.meteorclient.systems.modules.render;

import java.util.List;
import meteordevelopment.meteorclient.events.render.ApplyTransformationEvent;
import meteordevelopment.meteorclient.events.render.RenderItemEntityEvent;
import meteordevelopment.meteorclient.mixin.ItemRenderStateAccessor;
import meteordevelopment.meteorclient.mixin.LayerRenderStateAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_10444;
import net.minecraft.class_1542;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import net.minecraft.class_5819;
import net.minecraft.class_777;
import net.minecraft.class_7833;
import net.minecraft.class_804;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ItemPhysics extends Module {
   private static final float PIXEL_SIZE = 0.0625F;
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> randomRotation;
   private final class_5819 random;
   private boolean skipTransformation;

   public ItemPhysics() {
      super(Categories.Render, "item-physics", "Applies physics to items on the ground.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.randomRotation = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("random-rotation")).description("Adds a random rotation to every item.")).defaultValue(true)).build());
      this.random = class_5819.method_43053();
   }

   @EventHandler
   private void onRenderItemEntity(RenderItemEntityEvent event) {
      event.cancel();
      if (!event.renderState.field_55310.method_65606() && event.itemEntity != null) {
         class_4587 matrices = event.matrixStack;
         this.random.method_43052((long)event.itemEntity.method_5628() * 89748956L);

         for(int i = 0; i < ((ItemRenderStateAccessor)event.renderState.field_55310).meteor$getLayerCount(); ++i) {
            class_10444.class_10446 layer = ((ItemRenderStateAccessor)event.renderState.field_55310).meteor$getLayers()[i];
            ModelInfo info = this.getInfo(layer.method_67997());
            matrices.method_22903();
            this.applyTransformation(matrices, ((LayerRenderStateAccessor)layer).meteor$getTransform());
            matrices.method_46416(0.0F, info.offsetY, 0.0F);
            this.offsetInWater(matrices, event.itemEntity);
            if (info.flat) {
               matrices.method_22907(class_7833.field_40714.rotationDegrees(90.0F));
               matrices.method_46416(0.0F, 0.0F, info.offsetZ);
            }

            if ((Boolean)this.randomRotation.get()) {
               class_7833 axis = class_7833.field_40716;
               float x = 0.5F;
               float y = 0.0F;
               float z = 0.5F;
               if (info.flat) {
                  axis = class_7833.field_40718;
                  y = 0.5F;
                  z = 0.0F;
               }

               float degrees = (this.random.method_43057() * 2.0F - 1.0F) * 90.0F;
               matrices.method_46416(x, y, z);
               matrices.method_22907(axis.rotationDegrees(degrees));
               matrices.method_46416(-x, -y, -z);
            }

            this.renderLayer(event, info);
            matrices.method_22909();
         }

      }
   }

   @EventHandler
   private void onApplyTransformation(ApplyTransformationEvent event) {
      if (this.skipTransformation) {
         event.cancel();
      }

   }

   private void renderLayer(RenderItemEntityEvent event, ModelInfo info) {
      class_4587 matrices = event.matrixStack;
      this.skipTransformation = true;

      for(int j = 0; j < event.renderState.field_55311; ++j) {
         matrices.method_22903();
         if (j > 0) {
            float x = (this.random.method_43057() * 2.0F - 1.0F) * 0.25F;
            float z = (this.random.method_43057() * 2.0F - 1.0F) * 0.25F;
            this.translate(matrices, info, x, 0.0F, z);
         }

         event.renderState.field_55310.method_65604(matrices, event.renderCommandQueue, event.light, class_4608.field_21444, event.renderState.field_61821);
         matrices.method_22909();
         float y = Math.max(this.random.method_43057() * 0.0625F, 0.03125F);
         this.translate(matrices, info, 0.0F, y, 0.0F);
      }

      this.skipTransformation = false;
   }

   private void translate(class_4587 matrices, ModelInfo info, float x, float y, float z) {
      if (info.flat) {
         float temp = y;
         y = z;
         z = -temp;
      }

      matrices.method_46416(x, y, z);
   }

   private void applyTransformation(class_4587 matrices, class_804 transform) {
      transform = new class_804(transform.comp_3747(), new Vector3f(transform.comp_3748().x(), 0.0F, transform.comp_3748().z()), transform.comp_3749());
      transform.method_23075(false, matrices.method_23760());
   }

   private void offsetInWater(class_4587 matrices, class_1542 entity) {
      if (entity.method_5799()) {
         matrices.method_46416(0.0F, 0.333F, 0.0F);
      }

   }

   private ModelInfo getInfo(List<class_777> quads) {
      float minX = Float.MAX_VALUE;
      float maxX = Float.MIN_VALUE;
      float minY = Float.MAX_VALUE;
      float maxY = Float.MIN_VALUE;
      float minZ = Float.MAX_VALUE;
      float maxZ = Float.MIN_VALUE;

      for(class_777 quad : quads) {
         for(int i = 0; i < 4; ++i) {
            Vector3fc vec = quad.method_76648(i);
            minY = Math.min(minY, vec.y());
            maxY = Math.max(maxY, vec.y());
            minZ = Math.min(minZ, vec.z());
            maxZ = Math.max(maxZ, vec.z());
            minX = Math.min(minX, vec.x());
            maxX = Math.max(maxX, vec.x());
         }
      }

      if (minX == Float.MAX_VALUE) {
         minX = 0.0F;
      }

      if (minY == Float.MAX_VALUE) {
         minY = 0.0F;
      }

      if (minZ == Float.MAX_VALUE) {
         minZ = 0.0F;
      }

      if (maxX == Float.MIN_VALUE) {
         maxX = 1.0F;
      }

      if (maxY == Float.MIN_VALUE) {
         maxY = 1.0F;
      }

      if (maxZ == Float.MIN_VALUE) {
         maxZ = 1.0F;
      }

      float x = maxX - minX;
      float y = maxY - minY;
      float z = maxZ - minZ;
      boolean flat = x > 0.0625F && y > 0.0625F && z <= 0.0625F;
      return new ModelInfo(flat, 0.5F - minY, -maxZ);
   }

   static record ModelInfo(boolean flat, float offsetY, float offsetZ) {
   }
}
