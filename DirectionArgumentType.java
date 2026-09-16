package meteordevelopment.meteorclient.commands.arguments;

import net.minecraft.class_2350;
import net.minecraft.class_7485;

public class DirectionArgumentType extends class_7485<class_2350> {
   private static final DirectionArgumentType INSTANCE = new DirectionArgumentType();

   private DirectionArgumentType() {
      super(class_2350.field_29502, class_2350::values);
   }

   public static DirectionArgumentType create() {
      return INSTANCE;
   }
}
