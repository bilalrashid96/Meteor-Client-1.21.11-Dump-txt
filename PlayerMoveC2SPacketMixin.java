package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.IPlayerMoveC2SPacket;
import net.minecraft.class_2828;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({class_2828.class})
public abstract class PlayerMoveC2SPacketMixin implements IPlayerMoveC2SPacket {
   @Unique
   private int tag;

   public void meteor$setTag(int tag) {
      this.tag = tag;
   }

   public int meteor$getTag() {
      return this.tag;
   }
}
