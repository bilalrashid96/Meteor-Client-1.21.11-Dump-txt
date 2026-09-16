package meteordevelopment.meteorclient.mixin.sodium;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.util.FogParameters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({SodiumWorldRenderer.class})
public class SodiumWorldRendererMixin {
   @Unique
   private static final FogParameters DISABLED_FOG = new FogParameters(0.0F, 0.0F, 0.0F, 0.0F, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);

   @ModifyVariable(
      method = {"setupTerrain"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private FogParameters modifyFogParameters(FogParameters fogParameters) {
      if (Modules.get() == null) {
         return fogParameters;
      } else {
         return ((NoRender)Modules.get().get(NoRender.class)).noFog() ? DISABLED_FOG : fogParameters;
      }
   }
}
