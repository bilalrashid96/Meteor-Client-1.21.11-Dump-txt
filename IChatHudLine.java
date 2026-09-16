package meteordevelopment.meteorclient.mixininterface;

import com.mojang.authlib.GameProfile;

public interface IChatHudLine {
   String meteor$getText();

   int meteor$getId();

   void meteor$setId(int var1);

   GameProfile meteor$getSender();

   void meteor$setSender(GameProfile var1);
}
