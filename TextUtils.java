package meteordevelopment.meteorclient.utils.misc.text;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_2561;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import net.minecraft.class_5481;

public class TextUtils {
   private TextUtils() {
   }

   public static List<ColoredText> toColoredTextList(class_2561 text) {
      Deque<ColoredText> stack = new ArrayDeque();
      List<ColoredText> coloredTexts = new ArrayList();
      preOrderTraverse(text, stack, coloredTexts);
      coloredTexts.removeIf((e) -> e.text().isEmpty());
      return coloredTexts;
   }

   public static class_5250 parseOrderedText(class_5481 orderedText) {
      class_5250 parsedText = class_2561.method_43473();
      orderedText.accept((i, style, codePoint) -> {
         parsedText.method_10852(class_2561.method_43470(new String(Character.toChars(codePoint))).method_10862(style));
         return true;
      });
      return parsedText;
   }

   public static Color getMostPopularColor(class_2561 text) {
      Object2IntMap.Entry<Color> biggestEntry = null;
      ObjectIterator var2 = getColoredCharacterCount(toColoredTextList(text)).object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         Object2IntMap.Entry<Color> entry = (Object2IntMap.Entry)var2.next();
         if (biggestEntry == null) {
            biggestEntry = entry;
         } else if (entry.getIntValue() > biggestEntry.getIntValue()) {
            biggestEntry = entry;
         }
      }

      return biggestEntry == null ? new Color(255, 255, 255) : (Color)biggestEntry.getKey();
   }

   public static Object2IntMap<Color> getColoredCharacterCount(List<ColoredText> coloredTexts) {
      Object2IntMap<Color> colorCount = new Object2IntOpenHashMap();

      for(ColoredText coloredText : coloredTexts) {
         if (colorCount.containsKey(coloredText.color())) {
            colorCount.put(coloredText.color(), colorCount.getInt(coloredText.color()) + coloredText.text().length());
         } else {
            colorCount.put(coloredText.color(), coloredText.text().length());
         }
      }

      return colorCount;
   }

   private static void preOrderTraverse(class_2561 text, Deque<ColoredText> stack, List<ColoredText> coloredTexts) {
      if (text != null) {
         String textString = text.getString();
         class_5251 mcTextColor = text.method_10866().method_10973();
         Color textColor;
         if (mcTextColor == null) {
            if (stack.isEmpty()) {
               textColor = new Color(255, 255, 255);
            } else {
               textColor = ((ColoredText)stack.peek()).color();
            }
         } else {
            textColor = new Color(text.method_10866().method_10973().method_27716() | -16777216);
         }

         ColoredText coloredText = new ColoredText(textString, textColor);
         coloredTexts.add(coloredText);
         stack.push(coloredText);

         for(class_2561 child : text.method_10855()) {
            preOrderTraverse(child, stack, coloredTexts);
         }

         stack.pop();
      }
   }
}
