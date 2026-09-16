package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_2641;
import net.minecraft.class_5455;
import net.minecraft.class_634;
import net.minecraft.class_637;
import net.minecraft.class_7610;
import net.minecraft.class_7637;
import net.minecraft.class_7699;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_634.class})
public interface ClientPlayNetworkHandlerAccessor {
   @Accessor("field_19144")
   int meteor$getChunkLoadDistance();

   @Accessor("field_39808")
   class_7610.class_7612 meteor$getMessagePacker();

   @Accessor("field_39858")
   class_7637 meteor$getLastSeenMessagesCollector();

   @Accessor("field_25063")
   class_5455.class_6890 meteor$getCombinedDynamicRegistries();

   @Accessor("field_45600")
   class_7699 meteor$getEnabledFeatures();

   @Accessor("field_60784")
   static class_2641.class_11408<class_637> meteor$getCommandNodeFactory() {
      return null;
   }
}
