package meteordevelopment.meteorclient.utils.notebot.instrumentdetect;

import net.minecraft.class_2428;
import net.minecraft.class_2766;
import net.minecraft.class_310;

public enum InstrumentDetectMode {
   BlockState((noteBlock, blockPos) -> (class_2766)noteBlock.method_11654(class_2428.field_11325)),
   BelowBlock((noteBlock, blockPos) -> class_310.method_1551().field_1687.method_8320(blockPos.method_10074()).method_51364());

   private final InstrumentDetectFunction instrumentDetectFunction;

   private InstrumentDetectMode(InstrumentDetectFunction instrumentDetectFunction) {
      this.instrumentDetectFunction = instrumentDetectFunction;
   }

   public InstrumentDetectFunction getInstrumentDetectFunction() {
      return this.instrumentDetectFunction;
   }

   // $FF: synthetic method
   private static InstrumentDetectMode[] $values() {
      return new InstrumentDetectMode[]{BlockState, BelowBlock};
   }
}
