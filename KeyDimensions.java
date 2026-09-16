package meteordevelopment.meteorclient.systems.hud.elements.keyboard;

enum KeyDimensions {
   UNIT_1U((double)1.0F),
   UNIT_1_25U((double)1.25F),
   UNIT_1_5U((double)1.5F),
   UNIT_1_75U((double)1.75F),
   UNIT_2U((double)2.0F),
   UNIT_2_25U((double)2.25F),
   UNIT_2_75U((double)2.75F),
   UNIT_6_25U((double)6.25F);

   public final double units;
   public static final KeyDimensions STANDARD = UNIT_1U;
   public static final KeyDimensions TAB = UNIT_1_5U;
   public static final KeyDimensions CAPS_LOCK = UNIT_1_75U;
   public static final KeyDimensions ENTER_ANSI = UNIT_2_25U;
   public static final KeyDimensions LEFT_SHIFT_ANSI = UNIT_2_25U;
   public static final KeyDimensions RIGHT_SHIFT = UNIT_2_75U;
   public static final KeyDimensions BACKSPACE = UNIT_2U;
   public static final KeyDimensions LEFT_SHIFT_ISO = UNIT_1_25U;
   public static final KeyDimensions ENTER_ISO_WIDTH = UNIT_1_25U;
   public static final KeyDimensions ENTER_ISO_HEIGHT = UNIT_2U;
   public static final KeyDimensions CTRL = UNIT_1_25U;
   public static final KeyDimensions ALT = UNIT_1_25U;
   public static final KeyDimensions GUI = UNIT_1_25U;
   public static final KeyDimensions MENU = UNIT_1_25U;
   public static final KeyDimensions SPACEBAR = UNIT_6_25U;

   private KeyDimensions(double units) {
      this.units = units;
   }

   public double toPixels(double baseUnit, double gap) {
      return this.units * baseUnit + (this.units - (double)1.0F) * gap;
   }

   public double toPixels(double baseUnit) {
      return this.units * baseUnit;
   }

   // $FF: synthetic method
   private static KeyDimensions[] $values() {
      return new KeyDimensions[]{UNIT_1U, UNIT_1_25U, UNIT_1_5U, UNIT_1_75U, UNIT_2U, UNIT_2_25U, UNIT_2_75U, UNIT_6_25U};
   }
}
