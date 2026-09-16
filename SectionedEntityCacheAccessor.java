package meteordevelopment.meteorclient.mixin;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import net.minecraft.class_5568;
import net.minecraft.class_5572;
import net.minecraft.class_5573;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_5573.class})
public interface SectionedEntityCacheAccessor {
   @Accessor("field_27253")
   LongSortedSet meteor$getTrackedPositions();

   @Accessor("field_27252")
   <T extends class_5568> Long2ObjectMap<class_5572<T>> meteor$getTrackingSections();
}
