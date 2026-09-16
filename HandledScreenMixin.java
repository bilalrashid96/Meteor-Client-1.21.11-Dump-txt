package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.InventoryTweaks;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import meteordevelopment.meteorclient.systems.modules.render.ItemHighlight;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_3936;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_5684;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_465.class})
public abstract class HandledScreenMixin<T extends class_1703> extends class_437 implements class_3936<T> {
   @Shadow
   protected class_1735 field_2787;
   @Shadow
   protected int field_2776;
   @Shadow
   protected int field_2800;
   @Shadow
   private boolean field_2783;

   @Shadow
   protected abstract @Nullable class_1735 method_64240(double var1, double var3);

   @Shadow
   public abstract T method_17577();

   @Shadow
   protected abstract void method_2383(class_1735 var1, int var2, int var3, class_1713 var4);

   @Shadow
   public abstract void method_25419();

   public HandledScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"method_25426"},
      at = {@At("TAIL")}
   )
   private void onInit(CallbackInfo info) {
      InventoryTweaks invTweaks = (InventoryTweaks)Modules.get().get(InventoryTweaks.class);
      if (invTweaks.isActive() && invTweaks.showButtons() && invTweaks.canSteal(this.method_17577())) {
         this.method_37063((new class_4185.class_7840(class_2561.method_43470("Steal"), (button) -> invTweaks.steal(this.method_17577()))).method_46433(this.field_2776, this.field_2800 - 22).method_46437(40, 20).method_46431());
         this.method_37063((new class_4185.class_7840(class_2561.method_43470("Dump"), (button) -> invTweaks.dump(this.method_17577()))).method_46433(this.field_2776 + 42, this.field_2800 - 22).method_46437(40, 20).method_46431());
      }

   }

   @Inject(
      method = {"method_25403"},
      at = {@At("TAIL")}
   )
   private void onMouseDragged(class_11909 click, double offsetX, double offsetY, CallbackInfoReturnable<Boolean> cir) {
      if (click.method_74245() == 0 && !this.field_2783 && ((InventoryTweaks)Modules.get().get(InventoryTweaks.class)).mouseDragItemMove()) {
         class_1735 slot = this.method_64240(click.comp_4798(), click.comp_4799());
         if (slot != null && slot.method_7681() && MeteorClient.mc.method_74187()) {
            this.method_2383(slot, slot.field_7874, click.method_74245(), class_1713.field_7794);
         }

      }
   }

   @Inject(
      method = {"method_25402"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void mouseClicked(class_11909 click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
      BetterTooltips tooltips = (BetterTooltips)Modules.get().get(BetterTooltips.class);
      if (tooltips.shouldOpenContents(click) && this.field_2787 != null && !this.field_2787.method_7677().method_7960() && this.method_17577().method_34255().method_7960() && tooltips.openContent(this.field_2787.method_7677())) {
         cir.setReturnValue(true);
      }

   }

   @Inject(
      method = {"method_25404"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void keyPressed(class_11908 input, CallbackInfoReturnable<Boolean> cir) {
      BetterTooltips tooltips = (BetterTooltips)Modules.get().get(BetterTooltips.class);
      if (tooltips.shouldOpenContents(input) && this.field_2787 != null && !this.field_2787.method_7677().method_7960() && this.method_17577().method_34255().method_7960() && tooltips.openContent(this.field_2787.method_7677())) {
         cir.setReturnValue(true);
      }

   }

   @Inject(
      method = {"method_2385"},
      at = {@At("HEAD")}
   )
   private void onDrawSlot(class_332 context, class_1735 slot, int mouseX, int mouseY, CallbackInfo ci) {
      int color = ((ItemHighlight)Modules.get().get(ItemHighlight.class)).getColor(slot.method_7677());
      if (color != -1) {
         context.method_25294(slot.field_7873, slot.field_7872, slot.field_7873 + 16, slot.field_7872 + 16, color);
      }

   }

   @ModifyReturnValue(
      method = {"method_62001"},
      at = {@At("RETURN")}
   )
   private boolean isTooltipSticky(boolean original, class_1799 item) {
      Object var4 = item.method_32347().orElse((Object)null);
      if (!(var4 instanceof class_5684 component)) {
         return original;
      } else {
         return original || component.method_62003();
      }
   }
}
