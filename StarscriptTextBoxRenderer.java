package meteordevelopment.meteorclient.gui.utils;

import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.systems.hud.elements.TextHud;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.meteordev.starscript.utils.SemanticToken;
import org.meteordev.starscript.utils.SemanticTokenProvider;
import org.meteordev.starscript.utils.SemanticTokenType;

public class StarscriptTextBoxRenderer implements WTextBox.Renderer {
   private static final Color RED = new Color(225, 25, 25);
   private final List<SemanticToken> tokens = new ArrayList();
   private final List<Section> sections = new ArrayList();
   private String lastText;

   public void render(GuiRenderer renderer, double x, double y, String text, Color color) {
      if (this.lastText == null || !this.lastText.equals(text)) {
         this.lastText = text;
         SemanticTokenProvider.get(text, this.tokens);
         this.convertTokensToSections(renderer.theme);
      }

      for(Section section : this.sections) {
         renderer.text(section.text, x, y, section.color, false);
         x += renderer.theme.textWidth(section.text);
      }

   }

   public List<String> getCompletions(String text, int position) {
      List<String> completions = new ArrayList();
      MeteorStarscript.ss.getCompletions(text, position, (completion, function) -> completions.add(function ? completion + "(" : completion));
      completions.sort(String::compareToIgnoreCase);
      return completions;
   }

   private void convertTokensToSections(GuiTheme theme) {
      this.sections.clear();
      int start = 0;

      for(SemanticToken token : this.tokens) {
         if (start != token.start) {
            this.sections.add(new Section(this.lastText.substring(start, token.start), theme.starscriptTextColor()));
         }

         String text = this.lastText.substring(token.start, token.end);
         this.sections.add(new Section(text, getColorForToken(theme, token.type, text)));
         start = token.end;
      }

      if (start < this.lastText.length()) {
         this.sections.add(new Section(this.lastText.substring(start), theme.starscriptTextColor()));
      }

   }

   private static Color getColorForToken(GuiTheme theme, SemanticTokenType type, String text) {
      Color var10000;
      switch (type) {
         case Dot:
            Color var17 = theme.starscriptDotColor();
            var10000 = var17;
            break;
         case Comma:
            Color var16 = theme.starscriptCommaColor();
            var10000 = var16;
            break;
         case Operator:
            Color var15 = theme.starscriptOperatorColor();
            var10000 = var15;
            break;
         case String:
            Color var14 = theme.starscriptStringColor();
            var10000 = var14;
            break;
         case Number:
            Color var13 = theme.starscriptNumberColor();
            var10000 = var13;
            break;
         case Keyword:
            Color var12 = theme.starscriptKeywordColor();
            var10000 = var12;
            break;
         case Paren:
            Color var11 = theme.starscriptParenthesisColor();
            var10000 = var11;
            break;
         case Brace:
            Color var10 = theme.starscriptBraceColor();
            var10000 = var10;
            break;
         case Identifier:
            Color var9 = theme.starscriptTextColor();
            var10000 = var9;
            break;
         case Map:
            Color var8 = theme.starscriptAccessedObjectColor();
            var10000 = var8;
            break;
         case Section:
            if (text.startsWith("#")) {
               text = text.substring(1);
            }

            Color var7;
            try {
               var7 = TextHud.getSectionColor(Integer.parseInt(text));
            } catch (NumberFormatException var5) {
               var7 = theme.starscriptTextColor();
               var10000 = var7;
               break;
            }

            var10000 = var7;
            break;
         case Error:
            Color var3 = RED;
            var10000 = var3;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private static record Section(String text, Color color) {
   }
}
