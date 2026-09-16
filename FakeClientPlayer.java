package meteordevelopment.meteorclient.utils.misc;

import com.mojang.authlib.GameProfile;
import java.util.Map;
import java.util.UUID;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.PreInit;
import net.minecraft.class_11653;
import net.minecraft.class_1267;
import net.minecraft.class_1657;
import net.minecraft.class_2535;
import net.minecraft.class_2598;
import net.minecraft.class_338;
import net.minecraft.class_437;
import net.minecraft.class_5455;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.minecraft.class_640;
import net.minecraft.class_745;
import net.minecraft.class_761;
import net.minecraft.class_7699;
import net.minecraft.class_7975;
import net.minecraft.class_8675;
import net.minecraft.class_9782;

public class FakeClientPlayer {
   private static class_638 world;
   private static class_1657 player;
   private static class_640 playerListEntry;
   private static UUID lastId;
   private static boolean needsNewEntry;

   private FakeClientPlayer() {
   }

   @PreInit
   public static void init() {
      MeteorClient.EVENT_BUS.subscribe(FakeClientPlayer.class);
   }

   public static class_1657 getPlayer() {
      UUID id = MeteorClient.mc.method_1548().method_44717();
      if (player == null || !id.equals(lastId)) {
         if (world == null) {
            world = new class_638(new class_634(MeteorClient.mc, new class_2535(class_2598.field_11942), new class_8675((class_11653)null, new GameProfile(MeteorClient.mc.method_1548().method_44717(), MeteorClient.mc.method_1548().method_1676()), (class_7975)null, (class_5455.class_6890)null, (class_7699)null, (String)null, MeteorClient.mc.method_1558(), (class_437)null, (Map)null, (class_338.class_9477)null, (Map)null, (class_9782)null, (Map)null, false)), new class_638.class_5271(class_1267.field_5802, false, false), world.method_27983(), world.method_40134(), 1, 1, (class_761)null, false, 0L, world.method_8615());
         }

         player = new class_745(world, new GameProfile(id, MeteorClient.mc.method_1548().method_1676()));
         lastId = id;
         needsNewEntry = true;
      }

      return player;
   }

   public static class_640 getPlayerListEntry() {
      if (playerListEntry == null || needsNewEntry) {
         playerListEntry = new class_640(new GameProfile(lastId, MeteorClient.mc.method_1548().method_1676()), false);
         needsNewEntry = false;
      }

      return playerListEntry;
   }
}
