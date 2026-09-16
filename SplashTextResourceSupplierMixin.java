package meteordevelopment.meteorclient.mixin;

import java.util.List;
import java.util.Random;
import meteordevelopment.meteorclient.systems.config.Config;
import net.minecraft.class_2561;
import net.minecraft.class_4008;
import net.minecraft.class_8519;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_4008.class})
public abstract class SplashTextResourceSupplierMixin {
   @Unique
   private boolean override = true;
   @Unique
   private static final Random random = new Random();
   @Unique
   private final List<String> meteorSplashes = getMeteorSplashes();

   @Inject(
      method = {"method_18174"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onApply(CallbackInfoReturnable<class_8519> cir) {
      if (Config.get() != null && (Boolean)Config.get().titleScreenSplashes.get()) {
         if (this.override) {
            cir.setReturnValue(new class_8519(class_2561.method_43470((String)this.meteorSplashes.get(random.nextInt(this.meteorSplashes.size())))));
         }

         this.override = !this.override;
      }
   }

   @Unique
   private static List<String> getMeteorSplashes() {
      return List.of("Meteor on Crack!", "Star Meteor Client on GitHub!", "Based utility mod.", "§6MineGame159 §fbased god", "§4meteorclient.com", "§4Meteor on Crack!", "§6Meteor on Crack!");
   }
}
