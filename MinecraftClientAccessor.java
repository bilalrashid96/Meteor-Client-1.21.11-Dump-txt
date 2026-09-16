package meteordevelopment.meteorclient.mixin;

import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1071;
import net.minecraft.class_310;
import net.minecraft.class_320;
import net.minecraft.class_5520;
import net.minecraft.class_6360;
import net.minecraft.class_7497;
import net.minecraft.class_7574;
import net.minecraft.class_7853;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_310.class})
public interface MinecraftClientAccessor {
   @Accessor("field_1738")
   static int meteor$getFps() {
      return 0;
   }

   @Mutable
   @Accessor("field_1726")
   void meteor$setSession(class_320 var1);

   @Accessor("field_33697")
   class_6360 meteor$getResourceReloadLogger();

   @Accessor("field_1771")
   int meteor$getAttackCooldown();

   @Accessor("field_1771")
   void meteor$setAttackCooldown(int var1);

   @Invoker("method_1536")
   boolean meteor$leftClick();

   @Mutable
   @Accessor("field_39068")
   void meteor$setProfileKeys(class_7853 var1);

   @Mutable
   @Accessor("field_26902")
   void meteor$setUserApiService(UserApiService var1);

   @Mutable
   @Accessor("field_1707")
   void meteor$setSkinProvider(class_1071 var1);

   @Mutable
   @Accessor("field_26842")
   void meteor$setSocialInteractionsManager(class_5520 var1);

   @Mutable
   @Accessor("field_39492")
   void meteor$setAbuseReportContext(class_7574 var1);

   @Mutable
   @Accessor("field_45899")
   void meteor$setGameProfileFuture(CompletableFuture<ProfileResult> var1);

   @Mutable
   @Accessor("field_62106")
   void meteor$setApiServices(class_7497 var1);

   @Invoker("method_1508")
   void meteor$handleInputEvents();
}
