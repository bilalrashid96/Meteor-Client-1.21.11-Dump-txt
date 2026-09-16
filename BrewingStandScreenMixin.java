package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.AutoBrewer;
import net.minecraft.class_1661;
import net.minecraft.class_1708;
import net.minecraft.class_2561;
import net.minecraft.class_465;
import net.minecraft.class_472;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({class_472.class})
public abstract class BrewingStandScreenMixin extends class_465<class_1708> {
   public BrewingStandScreenMixin(class_1708 container, class_1661 playerInventory, class_2561 name) {
      super(container, playerInventory, name);
   }

   public void method_37432() {
      super.method_37432();
      if (Modules.get().isActive(AutoBrewer.class)) {
         ((AutoBrewer)Modules.get().get(AutoBrewer.class)).tick((class_1708)this.field_2797);
      }

   }

   public void method_25419() {
      if (Modules.get().isActive(AutoBrewer.class)) {
         ((AutoBrewer)Modules.get().get(AutoBrewer.class)).onBrewingStandClose();
      }

      super.method_25419();
   }
}
