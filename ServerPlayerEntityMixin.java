package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Anchor;
import meteordevelopment.meteorclient.systems.modules.movement.Scaffold;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1937;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_3222.class})
public abstract class ServerPlayerEntityMixin extends class_1309 {
   protected ServerPlayerEntityMixin(class_1299<? extends class_1309> entityType, class_1937 world) {
      super(entityType, world);
   }

   @Inject(
      method = {"method_6043"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void dontJump(CallbackInfo ci) {
      if (this.method_73183().method_8608()) {
         Anchor module = (Anchor)Modules.get().get(Anchor.class);
         if (module.isActive() && module.cancelJump) {
            ci.cancel();
         } else if (((Scaffold)Modules.get().get(Scaffold.class)).towering()) {
            ci.cancel();
         }

      }
   }
}
