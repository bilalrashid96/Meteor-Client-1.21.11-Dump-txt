package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import java.util.List;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BetterTab;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_355;
import net.minecraft.class_640;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_355.class})
public abstract class PlayerListHudMixin {
   @Shadow
   protected abstract List<class_640> method_48213();

   @ModifyConstant(
      constant = {@Constant(
   longValue = 80L
)},
      method = {"method_48213"}
   )
   private long modifyCount(long count) {
      BetterTab module = (BetterTab)Modules.get().get(BetterTab.class);
      return module.isActive() ? (long)(Integer)module.tabSize.get() : count;
   }

   @Inject(
      method = {"method_1918"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getPlayerName(class_640 playerListEntry, CallbackInfoReturnable<class_2561> info) {
      BetterTab betterTab = (BetterTab)Modules.get().get(BetterTab.class);
      if (betterTab.isActive()) {
         info.setReturnValue(betterTab.getPlayerName(playerListEntry));
      }

   }

   @ModifyArg(
      method = {"method_1919"},
      at = @At(
   value = "INVOKE",
   target = "Ljava/lang/Math;min(II)I"
),
      index = 0
   )
   private int modifyWidth(int width) {
      BetterTab module = (BetterTab)Modules.get().get(BetterTab.class);
      return module.isActive() && (Boolean)module.accurateLatency.get() ? width + 30 : width;
   }

   @Inject(
      method = {"method_1919"},
      at = {@At(
   value = "INVOKE",
   target = "Ljava/lang/Math;min(II)I",
   shift = Shift.BEFORE
)}
   )
   private void modifyHeight(CallbackInfo ci, @Local(ordinal = 5) LocalIntRef o, @Local(ordinal = 6) LocalIntRef p) {
      BetterTab module = (BetterTab)Modules.get().get(BetterTab.class);
      if (module.isActive()) {
         int newP = 1;

         int newO;
         for(int totalPlayers = newO = this.method_48213().size(); newO > (Integer)module.tabHeight.get(); newO = (totalPlayers + newP - 1) / newP) {
            ++newP;
         }

         o.set(newO);
         p.set(newP);
      }
   }

   @Inject(
      method = {"method_1923"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderLatencyIcon(class_332 context, int width, int x, int y, class_640 entry, CallbackInfo ci) {
      BetterTab betterTab = (BetterTab)Modules.get().get(BetterTab.class);
      if (betterTab.isActive() && (Boolean)betterTab.accurateLatency.get()) {
         class_310 mc = class_310.method_1551();
         class_327 textRenderer = mc.field_1772;
         int latency = class_3532.method_15340(entry.method_2959(), 0, 9999);
         int color = latency < 150 ? -16717456 : (latency < 300 ? -1585120 : -2670024);
         String text = latency + "ms";
         context.method_25303(textRenderer, text, x + width - textRenderer.method_1727(text), y, color);
         ci.cancel();
      }

   }
}
