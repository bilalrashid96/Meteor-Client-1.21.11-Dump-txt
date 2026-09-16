package meteordevelopment.meteorclient.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.GUIMove;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.text.MeteorClickEvent;
import meteordevelopment.meteorclient.utils.misc.text.RunnableClickEvent;
import net.minecraft.class_11908;
import net.minecraft.class_2558;
import net.minecraft.class_310;
import net.minecraft.class_408;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {class_437.class},
   priority = 500
)
public abstract class ScreenMixin {
   @Inject(
      method = {"method_52752"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderInGameBackground(CallbackInfo info) {
      if (Utils.canUpdate() && ((NoRender)Modules.get().get(NoRender.class)).noGuiBackground()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"method_71847"},
      at = {@At(
   value = "INVOKE",
   target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V",
   remap = false
)},
      cancellable = true
   )
   private static void onHandleBasicClickEvent(class_2558 clickEvent, class_310 client, class_437 screen, CallbackInfo ci) {
      if (clickEvent instanceof RunnableClickEvent runnableClickEvent) {
         runnableClickEvent.runnable.run();
         ci.cancel();
      } else if (clickEvent instanceof MeteorClickEvent meteorClickEvent) {
         if (meteorClickEvent.value.startsWith(Config.get().prefix.get())) {
            try {
               Commands.dispatch(meteorClickEvent.value.substring(((String)Config.get().prefix.get()).length()));
            } catch (CommandSyntaxException e) {
               MeteorClient.LOG.error("Failed to run command", e);
            } finally {
               ci.cancel();
            }
         }
      }

   }

   @Inject(
      method = {"method_25404"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onKeyPressed(class_11908 input, CallbackInfoReturnable<Boolean> cir) {
      if (!(this instanceof class_408)) {
         GUIMove guiMove = (GUIMove)Modules.get().get(GUIMove.class);
         List<Integer> arrows = List.of(262, 263, 264, 265);
         if (guiMove.disableArrows() && arrows.contains(input.comp_4795()) || guiMove.disableSpace() && input.comp_4795() == 32) {
            cir.setReturnValue(true);
         }

      }
   }
}
