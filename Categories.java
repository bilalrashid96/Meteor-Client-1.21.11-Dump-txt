package meteordevelopment.meteorclient.systems.modules;

import meteordevelopment.meteorclient.addons.AddonManager;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import net.minecraft.class_1802;

public class Categories {
   public static final Category Combat;
   public static final Category Player;
   public static final Category Movement;
   public static final Category Render;
   public static final Category World;
   public static final Category Misc;
   public static boolean REGISTERING;

   public static void init() {
      REGISTERING = true;
      Modules.registerCategory(Combat);
      Modules.registerCategory(Player);
      Modules.registerCategory(Movement);
      Modules.registerCategory(Render);
      Modules.registerCategory(World);
      Modules.registerCategory(Misc);
      AddonManager.ADDONS.forEach(MeteorAddon::onRegisterCategories);
      REGISTERING = false;
   }

   static {
      Combat = new Category("Combat", class_1802.field_8845.method_7854());
      Player = new Category("Player", class_1802.field_8694.method_7854());
      Movement = new Category("Movement", class_1802.field_8285.method_7854());
      Render = new Category("Render", class_1802.field_8280.method_7854());
      World = new Category("World", class_1802.field_8270.method_7854());
      Misc = new Category("Misc", class_1802.field_8187.method_7854());
   }
}
