package meteordevelopment.meteorclient.mixin;

import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.screens.EditBookTitleAndAuthorScreen;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2507;
import net.minecraft.class_2519;
import net.minecraft.class_2561;
import net.minecraft.class_3872;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_3872.class})
public abstract class BookScreenMixin extends class_437 {
   @Shadow
   private class_3872.class_3931 field_17418;
   @Shadow
   private int field_17119;

   @Shadow
   protected abstract void method_17058();

   @Shadow
   protected abstract void method_17057();

   public BookScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"method_25426"},
      at = {@At("TAIL")}
   )
   private void onInit(CallbackInfo info) {
      this.method_37063((new class_4185.class_7840(class_2561.method_43470("Copy"), (button) -> {
         class_2499 listTag = new class_2499();

         for(int i = 0; i < this.field_17418.method_17560(); ++i) {
            listTag.add(class_2519.method_23256(this.field_17418.method_17563(i).getString()));
         }

         class_2487 tag = new class_2487();
         tag.method_10566("pages", listTag);
         tag.method_10569("currentPage", this.field_17119);
         FastByteArrayOutputStream bytes = new FastByteArrayOutputStream();
         DataOutputStream out = new DataOutputStream(bytes);

         try {
            class_2507.method_55324(tag, out);
         } catch (IOException e) {
            MeteorClient.LOG.error("Error writing the book to the output stream", e);
         }

         String encoded = Base64.getEncoder().encodeToString(bytes.array);
         long available = (long)MemoryStack.stackGet().getPointer();
         long size = (long)MemoryUtil.memLengthUTF8(encoded, true);
         if (size > available) {
            ChatUtils.error("Could not copy to clipboard: Out of memory.");
         } else {
            GLFW.glfwSetClipboardString(MeteorClient.mc.method_22683().method_4490(), encoded);
         }

      })).method_46433(4, 4).method_46437(120, 20).method_46431());
      class_1799 itemStack = MeteorClient.mc.field_1724.method_6047();
      class_1268 hand = class_1268.field_5808;
      if (itemStack.method_7909() != class_1802.field_8360) {
         itemStack = MeteorClient.mc.field_1724.method_6079();
         hand = class_1268.field_5810;
      }

      if (itemStack.method_7909() == class_1802.field_8360) {
         this.method_37063((new class_4185.class_7840(class_2561.method_43470("Edit title & author"), (button) -> MeteorClient.mc.method_1507(new EditBookTitleAndAuthorScreen(GuiThemes.get(), itemStack, hand)))).method_46433(4, 26).method_46437(120, 20).method_46431());
      }
   }

   public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (verticalAmount == (double)0.0F) {
         return super.method_25401(mouseX, mouseY, horizontalAmount, verticalAmount);
      } else {
         if (verticalAmount < (double)0.0F) {
            this.method_17058();
         } else {
            this.method_17057();
         }

         return true;
      }
   }
}
