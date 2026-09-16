package meteordevelopment.meteorclient.mixin;

import com.mojang.authlib.GameProfile;
import meteordevelopment.meteorclient.mixininterface.IChatHudLine;
import net.minecraft.class_2561;
import net.minecraft.class_303;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin({class_303.class})
public abstract class ChatHudLineMixin implements IChatHudLine {
   @Shadow
   @Final
   private class_2561 comp_893;
   @Unique
   private int id;
   @Unique
   private GameProfile sender;

   public String meteor$getText() {
      return this.comp_893.getString();
   }

   public int meteor$getId() {
      return this.id;
   }

   public void meteor$setId(int id) {
      this.id = id;
   }

   public GameProfile meteor$getSender() {
      return this.sender;
   }

   public void meteor$setSender(GameProfile profile) {
      this.sender = profile;
   }
}
