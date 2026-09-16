package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Chams;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_10055;
import net.minecraft.class_1007;
import net.minecraft.class_1058;
import net.minecraft.class_11659;
import net.minecraft.class_11683;
import net.minecraft.class_11890;
import net.minecraft.class_11901;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_5617;
import net.minecraft.class_591;
import net.minecraft.class_630;
import net.minecraft.class_922;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1007.class})
public abstract class PlayerEntityRendererMixin<AvatarlikeEntity extends class_11890 & class_11901> extends class_922<AvatarlikeEntity, class_10055, class_591> {
   @Unique
   private Chams chams;

   public PlayerEntityRendererMixin(class_5617.class_5618 ctx, class_591 model, float shadowRadius) {
      super(ctx, model, shadowRadius);
   }

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void init$chams(CallbackInfo info) {
      this.chams = (Chams)Modules.get().get(Chams.class);
   }

   @Inject(
      method = {"method_62604(Lnet/minecraft/class_11890;Lnet/minecraft/class_10055;F)V"},
      at = {@At("RETURN")}
   )
   private void updateRenderState$scale(AvatarlikeEntity player, class_10055 state, float f, CallbackInfo ci) {
      if (this.chams.isActive() && (Boolean)this.chams.players.get()) {
         if (!(Boolean)this.chams.ignoreSelf.get() || player != MeteorClient.mc.field_1724) {
            float v = ((Double)this.chams.playersScale.get()).floatValue();
            state.field_53453 *= v;
            if (state.field_53338 != null) {
               ((IVec3d)state.field_53338).meteor$setY(state.field_53338.field_1351 + (double)(player.method_17682() * v - player.method_17682()));
            }

         }
      }
   }

   @ModifyExpressionValue(
      method = {"method_23205"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_12249;method_76000(Lnet/minecraft/class_2960;)Lnet/minecraft/class_1921;"
)}
   )
   private class_1921 renderArm$texture(class_1921 original, class_4587 matrixStack, class_11659 entityRenderCommandQueue, int light, class_2960 skinTexture, class_630 modelPart, boolean sleeveVisible) {
      if (this.chams.isActive() && (Boolean)this.chams.hand.get()) {
         class_2960 texture = (Boolean)this.chams.handTexture.get() ? skinTexture : Chams.BLANK;
         return class_12249.method_76000(texture);
      } else {
         return original;
      }
   }

   @WrapWithCondition(
      method = {"method_23205"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_11659;method_73491(Lnet/minecraft/class_630;Lnet/minecraft/class_4587;Lnet/minecraft/class_1921;IILnet/minecraft/class_1058;)V"
)}
   )
   private boolean renderArm$color(class_11659 instance, class_630 modelPart, class_4587 matrixStack, class_1921 renderLayer, int light, int uv, class_1058 sprite) {
      if (this.chams.isActive() && (Boolean)this.chams.hand.get()) {
         instance.method_73492(modelPart, matrixStack, renderLayer, light, uv, (class_1058)null, ((SettingColor)this.chams.handColor.get()).getPacked(), (class_11683.class_11792)null);
         return false;
      } else {
         return true;
      }
   }

   @Inject(
      method = {"method_62604(Lnet/minecraft/class_11890;Lnet/minecraft/class_10055;F)V"},
      at = {@At("RETURN")}
   )
   private void updateRenderState$rotations(AvatarlikeEntity player, class_10055 state, float f, CallbackInfo info) {
      if (Rotations.rotating && player == MeteorClient.mc.field_1724) {
         state.field_53447 = 0.0F;
         state.field_53446 = Rotations.serverYaw;
         state.field_53448 = Rotations.serverPitch;
      }

   }
}
