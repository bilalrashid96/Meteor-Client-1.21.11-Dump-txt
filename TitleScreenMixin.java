package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.player.TitleScreenCredits;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_442;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_442.class})
public abstract class TitleScreenMixin extends class_437 {
   public TitleScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"method_25394"},
      at = {@At("TAIL")}
   )
   private void onRender(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if ((Boolean)Config.get().titleScreenCredits.get()) {
         TitleScreenCredits.render(context);
      }

   }

   @Inject(
      method = {"method_25402"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onMouseClicked(class_11909 click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
      if ((Boolean)Config.get().titleScreenCredits.get() && click.method_74245() == 0 && TitleScreenCredits.onClicked(click.comp_4798(), click.comp_4799())) {
         cir.setReturnValue(true);
      }

   }
}
