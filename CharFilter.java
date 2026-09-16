package meteordevelopment.meteorclient.gui.utils;

public interface CharFilter {
   boolean filter(String var1, char var2);

   default boolean filter(String text, int i) {
      return this.filter(text, (char)i);
   }
}
