package meteordevelopment.meteorclient.utils.misc.input;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_304;
import net.minecraft.class_304.class_11900;
import net.minecraft.class_3675.class_307;

public class KeyBinds {
   private static final class_304.class_11900 CATEGORY = class_11900.method_74698(MeteorClient.identifier("meteor-client"));
   public static class_304 OPEN_GUI;
   public static class_304 OPEN_COMMANDS;

   private KeyBinds() {
   }

   public static class_304[] apply(class_304[] binds) {
      class_304[] newBinds = new class_304[binds.length + 2];
      System.arraycopy(binds, 0, newBinds, 0, binds.length);
      newBinds[binds.length] = OPEN_GUI;
      newBinds[binds.length + 1] = OPEN_COMMANDS;
      return newBinds;
   }

   static {
      OPEN_GUI = new class_304("key.meteor-client.open-gui", class_307.field_1668, 344, CATEGORY);
      OPEN_COMMANDS = new class_304("key.meteor-client.open-commands", class_307.field_1668, 46, CATEGORY);
   }
}
