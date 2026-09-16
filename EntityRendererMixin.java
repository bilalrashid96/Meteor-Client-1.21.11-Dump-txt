package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.ESP;
import meteordevelopment.meteorclient.systems.modules.render.Fullbright;
import meteordevelopment.meteorclient.systems.modules.render.Nametags;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_10017;
import net.minecraft.class_10042;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1540;
import net.minecraft.class_1657;
import net.minecraft.class_1944;
import net.minecraft.class_2561;
import net.minecraft.class_4604;
import net.minecraft.class_5617;
import net.minecraft.class_897;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_897.class})
public abstract class EntityRendererMixin<T extends class_1297, S extends class_10017> {
   @Unique
   private ESP esp;
   @Unique
   private NoRender noRender;

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void onInit(class_5617.class_5618 context, CallbackInfo ci) {
      this.esp = (ESP)Modules.get().get(ESP.class);
      this.noRender = (NoRender)Modules.get().get(NoRender.class);
   }

   @Inject(
      method = {"method_62426"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderLabel(T entity, CallbackInfoReturnable<class_2561> cir) {
      if (this.noRender.noNametags()) {
         cir.setReturnValue((Object)null);
      }

      if (entity instanceof class_1657 player) {
         if (((Nametags)Modules.get().get(Nametags.class)).playerNametags() && (EntityUtils.getGameMode(player) != null || !((Nametags)Modules.get().get(Nametags.class)).excludeBots())) {
            cir.setReturnValue((Object)null);
         }

      }
   }

   @Inject(
      method = {"method_3933"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void shouldRender(T entity, class_4604 frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
      if (this.noRender.noEntity(entity)) {
         cir.setReturnValue(false);
      }

      if (this.noRender.noFallingBlocks() && entity instanceof class_1540) {
         cir.setReturnValue(false);
      }

   }

   @Inject(
      method = {"method_62406"},
      at = {@At("HEAD")},
      cancellable = true
   )
   void canBeCulled(T entity, CallbackInfoReturnable<Boolean> cir) {
      if (this.esp.forceRender()) {
         cir.setReturnValue(false);
      }

   }

   @ModifyReturnValue(
      method = {"method_27950"},
      at = {@At("RETURN")}
   )
   private int onGetSkyLight(int original) {
      return Math.max(((Fullbright)Modules.get().get(Fullbright.class)).getLuminance(class_1944.field_9284), original);
   }

   @ModifyReturnValue(
      method = {"method_24087"},
      at = {@At("RETURN")}
   )
   private int onGetBlockLight(int original) {
      return Math.max(((Fullbright)Modules.get().get(Fullbright.class)).getLuminance(class_1944.field_9282), original);
   }

   @ModifyExpressionValue(
      method = {"method_62354"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1937;method_8314(Lnet/minecraft/class_1944;Lnet/minecraft/class_2338;)I"
)}
   )
   private int onGetLightLevel(int original) {
      return Math.max(((Fullbright)Modules.get().get(Fullbright.class)).getLuminance(class_1944.field_9282), original);
   }

   @Inject(
      method = {"method_62354"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_10017;field_61821:I",
   shift = Shift.AFTER,
   opcode = 181
)}
   )
   private void onGetOutlineColor(T entity, S state, float tickProgress, CallbackInfo ci) {
      if (this.esp.isGlow() && !this.esp.shouldSkip(entity)) {
         Color color = this.esp.getColor(entity);
         if (color == null) {
            return;
         }

         state.field_61821 = color.getPacked();
      }

   }

   @Inject(
      method = {"method_73154(Lnet/minecraft/class_1297;Lnet/minecraft/class_10017;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void updateShadow(class_1297 entity, class_10017 renderState, CallbackInfo ci) {
      if (this.noRender.noDeadEntities() && entity instanceof class_1309 && renderState instanceof class_10042) {
         class_10042 livingEntityRenderState = (class_10042)renderState;
         if (livingEntityRenderState.field_53449 > 0.0F) {
            ci.cancel();
         }
      }

   }
}
