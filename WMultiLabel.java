package meteordevelopment.meteorclient.gui.widgets;

import java.util.ArrayList;
import java.util.List;

public abstract class WMultiLabel extends WLabel {
   protected List<String> lines = new ArrayList(2);
   protected double maxWidth;

   public WMultiLabel(String text, boolean title, double maxWidth) {
      super(text, title);
      this.maxWidth = maxWidth;
   }

   protected void onCalculateSize() {
      this.lines.clear();
      String[] textLines = this.text.split("\n");
      double maxLineWidth = (double)0.0F;
      if (this.maxWidth == (double)0.0F) {
         for(String line : textLines) {
            this.lines.add(line);
            double lineWidth = this.theme.textWidth(line, line.length(), this.title);
            maxLineWidth = Math.max(maxLineWidth, lineWidth);
         }
      } else {
         StringBuilder sb = new StringBuilder();
         double lineWidth = (double)0.0F;
         double spaceWidth = this.theme.textWidth(" ", 1, this.title);
         double maxWidth = this.theme.scale(this.maxWidth);
         int iInLine = 0;

         for(String line : textLines) {
            for(String word : line.split(" ")) {
               double wordWidth = this.theme.textWidth(word, word.length(), this.title);
               double toAdd = wordWidth;
               if (iInLine > 0) {
                  toAdd = wordWidth + spaceWidth;
               }

               if (lineWidth + toAdd > maxWidth) {
                  this.lines.add(sb.toString());
                  sb.setLength(0);
                  sb.append(word);
                  lineWidth = wordWidth;
                  iInLine = 1;
               } else {
                  if (iInLine > 0) {
                     sb.append(' ');
                     lineWidth += spaceWidth;
                  }

                  sb.append(word);
                  lineWidth += wordWidth;
                  ++iInLine;
               }

               maxLineWidth = Math.max(maxLineWidth, lineWidth);
            }

            this.lines.add(sb.toString());
            sb.setLength(0);
            lineWidth = (double)0.0F;
            iInLine = 0;
         }

         if (!sb.isEmpty()) {
            this.lines.add(sb.toString());
         }
      }

      this.width = maxLineWidth;
      this.height = this.theme.textHeight(this.title) * (double)this.lines.size();
   }

   public void set(String text) {
      if (!text.equals(this.text)) {
         this.invalidate();
      }

      this.text = text;
   }
}
