package meteordevelopment.meteorclient.mixin;

import java.util.Optional;
import meteordevelopment.meteorclient.mixininterface.IExplosionS2CPacket;
import net.minecraft.class_243;
import net.minecraft.class_2664;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({class_2664.class})
public abstract class ExplosionS2CPacketMixin implements IExplosionS2CPacket {
   @Shadow
   @Final
   @Mutable
   private Optional<class_243> comp_2884;

   public void meteor$setVelocityX(float velocity) {
      if (this.comp_2884.isPresent()) {
         class_243 kb = (class_243)this.comp_2884.get();
         this.comp_2884 = Optional.of(new class_243((double)velocity, kb.field_1351, kb.field_1350));
      } else {
         this.comp_2884 = Optional.of(new class_243((double)velocity, (double)0.0F, (double)0.0F));
      }

   }

   public void meteor$setVelocityY(float velocity) {
      if (this.comp_2884.isPresent()) {
         class_243 kb = (class_243)this.comp_2884.get();
         this.comp_2884 = Optional.of(new class_243(kb.field_1352, (double)velocity, kb.field_1350));
      } else {
         this.comp_2884 = Optional.of(new class_243((double)0.0F, (double)velocity, (double)0.0F));
      }

   }

   public void meteor$setVelocityZ(float velocity) {
      if (this.comp_2884.isPresent()) {
         class_243 kb = (class_243)this.comp_2884.get();
         this.comp_2884 = Optional.of(new class_243(kb.field_1352, kb.field_1351, (double)velocity));
      } else {
         this.comp_2884 = Optional.of(new class_243((double)0.0F, (double)0.0F, (double)velocity));
      }

   }
}
