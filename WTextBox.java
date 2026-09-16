package meteordevelopment.meteorclient.gui.widgets.input;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiKeyEvents;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.utils.CharFilter;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_3532;
import net.minecraft.class_6417;
import org.apache.commons.lang3.SystemUtils;

public abstract class WTextBox extends WWidget {
   private static final Renderer DEFAULT_RENDERER = (renderer, x, y, text, color) -> renderer.text(text, x, y, color, false);
   public Runnable action;
   public Runnable actionOnUnfocused;
   protected String text;
   protected String placeholder;
   protected CharFilter filter;
   protected final Renderer renderer;
   protected DoubleList textWidths;
   protected int cursor;
   protected double textStart;
   protected boolean selecting;
   protected boolean doubleClick;
   protected int selectionStart;
   protected int selectionEnd;
   private int preSelectionCursor;
   private List<String> completions;
   private int completionsStart;
   private WContainer completionsW;

   public WTextBox(String text, CharFilter filter, Class<? extends Renderer> renderer) {
      this(text, (String)null, filter, renderer);
   }

   public WTextBox(String text, String placeholder, CharFilter filter, Class<? extends Renderer> renderer) {
      this.textWidths = new DoubleArrayList();
      this.text = text;
      this.placeholder = placeholder;
      this.filter = filter;

      try {
         this.renderer = renderer != null ? (Renderer)renderer.getDeclaredConstructor().newInstance() : DEFAULT_RENDERER;
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
         throw new RuntimeException(e);
      }
   }

   protected abstract WContainer createCompletionsRootWidget();

   protected abstract <T extends WWidget & ICompletionItem> T createCompletionsValueWidth(String var1, boolean var2);

   protected void onCalculateSize() {
      double pad = this.pad();
      double s = this.theme.textHeight();
      this.width = pad + s + pad;
      this.height = pad + s + pad;
      this.calculateTextWidths();
      if (this.completionsW != null) {
         this.completionsW.calculateSize();
      }

   }

   public void calculateWidgetPositions() {
      super.calculateWidgetPositions();
      if (this.completionsW != null) {
         this.completionsW.x = this.x;
         this.completionsW.y = this.y + this.height;
         this.completionsW.calculateWidgetPositions();
      }

   }

   public void move(double deltaX, double deltaY) {
      super.move(deltaX, deltaY);
      if (this.completionsW != null) {
         this.completionsW.move(deltaX, deltaY);
      }

   }

   protected double maxTextWidth() {
      return this.width - this.pad() * (double)2.0F;
   }

   public boolean onMouseClicked(class_11909 click, boolean doubled) {
      if (!this.mouseOver) {
         if (this.focused) {
            this.setFocused(false);
         }

         return false;
      } else {
         if (click.method_74245() == 1) {
            if (!this.text.isEmpty()) {
               this.text = "";
               this.cursor = 0;
               this.selectionStart = 0;
               this.selectionEnd = 0;
               this.runAction();
            }
         } else if (click.method_74245() == 0) {
            this.selecting = true;
            double overflowWidth = this.getOverflowWidthForRender();
            double relativeMouseX = click.comp_4798() - this.x + overflowWidth;
            double pad = this.pad();
            double smallestDifference = Double.MAX_VALUE;
            this.cursor = this.text.length();

            for(int i = 0; i < this.textWidths.size(); ++i) {
               double difference = Math.abs(this.textWidths.getDouble(i) + pad - relativeMouseX);
               if (difference < smallestDifference) {
                  smallestDifference = difference;
                  this.cursor = i;
               }
            }

            if (doubled && this.cursor == this.preSelectionCursor) {
               this.doubleClick = true;
               this.resetSelection();
               this.selectionStart = this.cursor - this.countToNextSpace(true);
               this.selectionEnd = this.cursor += this.countToNextSpace(false);
               return true;
            }

            this.preSelectionCursor = this.cursor;
            this.resetSelection();
            this.cursorChanged();
         }

         this.setFocused(true);
         return true;
      }
   }

   public void onMouseMoved(double mouseX, double mouseY, double lastMouseX, double lastMouseY) {
      if (this.selecting) {
         double overflowWidth = this.getOverflowWidthForRender();
         double relativeMouseX = mouseX - this.x + overflowWidth;
         double pad = this.pad();
         double smallestDifference = Double.MAX_VALUE;
         int best = 0;

         for(int i = 0; i < this.textWidths.size(); ++i) {
            double difference = Math.abs(this.textWidths.getDouble(i) + pad - relativeMouseX);
            if (difference < smallestDifference) {
               best = i;
               smallestDifference = difference;
               if (!this.doubleClick) {
                  if (i < this.preSelectionCursor) {
                     this.selectionStart = i;
                     this.cursor = i;
                  } else if (i > this.preSelectionCursor) {
                     this.selectionEnd = i;
                     this.cursor = i;
                  } else {
                     this.cursor = this.preSelectionCursor;
                     this.resetSelection();
                  }
               }
            }
         }

         if (this.doubleClick) {
            if (best < this.selectionStart) {
               this.selectionStart = best - this.countToNextSpace(true, best);
               this.cursor = this.selectionStart;
            } else if (best > this.selectionEnd) {
               this.selectionEnd = best + this.countToNextSpace(false, best);
               this.cursor = this.selectionEnd;
            } else if (this.cursor == this.selectionStart) {
               int nextRight = this.countToNextSpace(false);
               if (best > this.cursor + nextRight) {
                  this.selectionStart = this.cursor = this.cursor + nextRight + 1;
                  if (this.selectionStart <= this.preSelectionCursor && this.selectionStart + this.countToNextSpace(false) >= this.preSelectionCursor) {
                     this.cursor = this.selectionEnd;
                  }
               }
            } else if (this.cursor == this.selectionEnd) {
               int nextLeft = this.countToNextSpace(true);
               if (best < this.cursor - nextLeft) {
                  this.selectionEnd = this.cursor = this.cursor - nextLeft - 1;
               }
            }
         }

      }
   }

   public boolean onMouseReleased(class_11909 click) {
      this.selecting = false;
      this.doubleClick = false;
      if (this.selectionStart < this.preSelectionCursor && this.preSelectionCursor == this.selectionEnd) {
         this.cursor = this.selectionStart;
      } else if (this.selectionEnd > this.preSelectionCursor && this.preSelectionCursor == this.selectionStart) {
         this.cursor = this.selectionEnd;
      }

      return false;
   }

   public boolean onKeyPressed(class_11908 input) {
      if (!this.focused) {
         return false;
      } else {
         boolean control = class_6417.field_52734 ? input.comp_4797() == 8 : input.comp_4797() == 2;
         if (control && input.comp_4795() == 67) {
            if (this.cursor != this.selectionStart || this.cursor != this.selectionEnd) {
               MeteorClient.mc.field_1774.method_1455(this.text.substring(this.selectionStart, this.selectionEnd));
            }

            return true;
         } else if (control && input.comp_4795() == 88) {
            if (this.cursor != this.selectionStart || this.cursor != this.selectionEnd) {
               MeteorClient.mc.field_1774.method_1455(this.text.substring(this.selectionStart, this.selectionEnd));
               this.clearSelection();
            }

            return true;
         } else {
            if (control && input.comp_4795() == 65) {
               this.cursor = this.text.length();
               this.selectionStart = 0;
               this.selectionEnd = this.cursor;
            } else if (input.comp_4797() == ((class_6417.field_52734 ? 8 : 2) | 1) && input.comp_4795() == 65) {
               this.resetSelection();
            } else {
               if (input.comp_4795() == 257 || input.comp_4795() == 335) {
                  this.setFocused(false);
                  if (this.actionOnUnfocused != null) {
                     this.actionOnUnfocused.run();
                  }

                  return true;
               }

               if (input.comp_4795() == 258 && this.completionsW != null) {
                  String completion = ((ICompletionItem)((Cell)this.completionsW.cells.get(this.getSelectedCompletion())).widget()).getCompletion();
                  StringBuilder sb = new StringBuilder(this.text.length() + completion.length() + 1);
                  String a = this.text.substring(0, this.cursor);
                  sb.append(a);

                  for(int i = 0; i < completion.length() - 1; ++i) {
                     if (a.endsWith(completion.substring(0, completion.length() - i - 1))) {
                        completion = completion.substring(completion.length() - i - 1);
                        break;
                     }
                  }

                  sb.append(completion);
                  if (completion.endsWith("(")) {
                     sb.append(')');
                  }

                  sb.append(this.text, this.cursor, this.text.length());
                  this.text = sb.toString();
                  this.cursor += completion.length();
                  this.resetSelection();
                  this.runAction();
                  return true;
               }
            }

            return this.onKeyRepeated(input);
         }
      }
   }

   public boolean onKeyRepeated(class_11908 input) {
      if (!this.focused) {
         return false;
      } else {
         boolean control = class_6417.field_52734 ? input.comp_4797() == 8 : input.comp_4797() == 2;
         boolean shift = input.comp_4797() == 1;
         boolean controlShift = input.comp_4797() == ((SystemUtils.IS_OS_WINDOWS ? 4 : (class_6417.field_52734 ? 8 : 2)) | 1);
         boolean altShift = input.comp_4797() == ((SystemUtils.IS_OS_WINDOWS ? 2 : 4) | 1);
         if (control && input.comp_4795() == 86) {
            this.clearSelection();
            String preText = this.text;
            String clipboard = MeteorClient.mc.field_1774.method_1460();
            int addedChars = 0;
            StringBuilder sb = new StringBuilder(this.text.length() + clipboard.length());
            sb.append(this.text);

            for(int i = 0; i < clipboard.length(); ++i) {
               char c = clipboard.charAt(i);
               if (this.filter.filter(sb.toString(), c)) {
                  sb.insert(this.cursor + addedChars, c);
                  ++addedChars;
               }
            }

            this.text = sb.toString();
            this.cursor += addedChars;
            this.resetSelection();
            if (!this.text.equals(preText)) {
               this.runAction();
            }

            return true;
         } else if (input.comp_4795() == 259) {
            if (this.cursor > 0 && this.cursor == this.selectionStart && this.cursor == this.selectionEnd) {
               String preText = this.text;
               int count = input.comp_4797() == (SystemUtils.IS_OS_WINDOWS ? 4 : (class_6417.field_52734 ? 8 : 2)) ? this.cursor : (input.comp_4797() == (SystemUtils.IS_OS_WINDOWS ? 2 : 4) ? this.countToNextSpace(true) : 1);
               String var18 = this.text.substring(0, this.cursor - count);
               this.text = var18 + this.text.substring(this.cursor);
               this.cursor -= count;
               this.resetSelection();
               if (!this.text.equals(preText)) {
                  this.runAction();
               }
            } else if (this.cursor != this.selectionStart || this.cursor != this.selectionEnd) {
               this.clearSelection();
            }

            return true;
         } else if (input.comp_4795() == 261) {
            if (this.cursor == this.selectionStart && this.cursor == this.selectionEnd) {
               if (this.cursor < this.text.length()) {
                  String preText = this.text;
                  int count = input.comp_4797() == (SystemUtils.IS_OS_WINDOWS ? 4 : (class_6417.field_52734 ? 8 : 2)) ? this.text.length() - this.cursor : (input.comp_4797() == (SystemUtils.IS_OS_WINDOWS ? 2 : 4) ? this.countToNextSpace(false) : 1);
                  String var10001 = this.text.substring(0, this.cursor);
                  this.text = var10001 + this.text.substring(this.cursor + count);
                  if (!this.text.equals(preText)) {
                     this.runAction();
                  }
               }
            } else {
               this.clearSelection();
            }

            return true;
         } else if (input.comp_4795() == 263) {
            if (this.cursor > 0) {
               if (input.comp_4797() == (SystemUtils.IS_OS_WINDOWS ? 2 : 4)) {
                  this.cursor -= this.countToNextSpace(true);
                  this.resetSelection();
               } else if (input.comp_4797() == (SystemUtils.IS_OS_WINDOWS ? 4 : (class_6417.field_52734 ? 8 : 2))) {
                  this.cursor = 0;
                  this.resetSelection();
               } else if (altShift) {
                  if (this.cursor == this.selectionEnd && this.cursor != this.selectionStart) {
                     this.cursor -= this.countToNextSpace(true);
                     if (this.cursor >= this.selectionStart) {
                        this.selectionEnd = this.cursor;
                     } else {
                        this.selectionEnd = this.selectionStart;
                        this.selectionStart = this.cursor;
                     }
                  } else {
                     this.cursor -= this.countToNextSpace(true);
                     this.selectionStart = this.cursor;
                  }
               } else if (controlShift) {
                  if (this.cursor == this.selectionEnd && this.cursor != this.selectionStart) {
                     this.selectionEnd = this.selectionStart;
                  }

                  this.selectionStart = 0;
                  this.cursor = 0;
               } else if (shift) {
                  if (this.cursor == this.selectionEnd && this.cursor != this.selectionStart) {
                     this.selectionEnd = this.cursor - 1;
                  } else {
                     this.selectionStart = this.cursor - 1;
                  }

                  --this.cursor;
               } else {
                  if (this.cursor == this.selectionEnd && this.cursor != this.selectionStart) {
                     this.cursor = this.selectionStart;
                  } else {
                     --this.cursor;
                  }

                  this.resetSelection();
               }

               this.cursorChanged();
            } else if (this.selectionStart != this.selectionEnd && this.selectionStart == 0 && input.comp_4797() == 0) {
               this.cursor = 0;
               this.resetSelection();
               this.cursorChanged();
            }

            return true;
         } else if (input.comp_4795() != 262) {
            if (input.comp_4795() == 264 && this.completionsW != null) {
               int currentI = this.getSelectedCompletion();
               if (currentI == Math.min(5, this.completions.size() - 1)) {
                  if (this.completionsStart + 6 < this.completions.size()) {
                     ++this.completionsStart;
                     this.createCompletions(this.completionsStart + currentI);
                  }
               } else {
                  ((ICompletionItem)((Cell)this.completionsW.cells.get(currentI)).widget()).setSelected(false);
                  ((ICompletionItem)((Cell)this.completionsW.cells.get(currentI + 1)).widget()).setSelected(true);
               }

               return true;
            } else if (input.comp_4795() == 265 && this.completionsW != null) {
               int currentI = this.getSelectedCompletion();
               if (currentI == 0) {
                  if (this.completionsStart > 0) {
                     --this.completionsStart;
                     this.createCompletions(this.completionsStart + currentI);
                  }
               } else {
                  ((ICompletionItem)((Cell)this.completionsW.cells.get(currentI)).widget()).setSelected(false);
                  ((ICompletionItem)((Cell)this.completionsW.cells.get(currentI - 1)).widget()).setSelected(true);
               }

               return true;
            } else {
               return false;
            }
         } else {
            if (this.cursor < this.text.length()) {
               if (input.comp_4797() == (SystemUtils.IS_OS_WINDOWS ? 2 : 4)) {
                  this.cursor += this.countToNextSpace(false);
                  this.resetSelection();
               } else if (input.comp_4797() == (SystemUtils.IS_OS_WINDOWS ? 4 : (class_6417.field_52734 ? 8 : 2))) {
                  this.cursor = this.text.length();
                  this.resetSelection();
               } else if (altShift) {
                  if (this.cursor == this.selectionStart && this.cursor != this.selectionEnd) {
                     this.cursor += this.countToNextSpace(false);
                     if (this.cursor <= this.selectionEnd) {
                        this.selectionStart = this.cursor;
                     } else {
                        this.selectionStart = this.selectionEnd;
                        this.selectionEnd = this.cursor;
                     }
                  } else {
                     this.cursor += this.countToNextSpace(false);
                     this.selectionEnd = this.cursor;
                  }
               } else if (controlShift) {
                  if (this.cursor == this.selectionStart && this.cursor != this.selectionEnd) {
                     this.selectionStart = this.selectionEnd;
                  }

                  this.cursor = this.text.length();
                  this.selectionEnd = this.cursor;
               } else if (shift) {
                  if (this.cursor == this.selectionStart && this.cursor != this.selectionEnd) {
                     this.selectionStart = this.cursor + 1;
                  } else {
                     this.selectionEnd = this.cursor + 1;
                  }

                  ++this.cursor;
               } else {
                  if (this.cursor == this.selectionStart && this.cursor != this.selectionEnd) {
                     this.cursor = this.selectionEnd;
                  } else {
                     ++this.cursor;
                  }

                  this.resetSelection();
               }

               this.cursorChanged();
            } else if (this.selectionStart != this.selectionEnd && this.selectionEnd == this.text.length() && input.comp_4797() == 0) {
               this.cursor = this.text.length();
               this.resetSelection();
               this.cursorChanged();
            }

            return true;
         }
      }
   }

   private int getSelectedCompletion() {
      for(int i = 0; i < this.completionsW.cells.size(); ++i) {
         ICompletionItem item = (ICompletionItem)((Cell)this.completionsW.cells.get(i)).widget();
         if (item.isSelected()) {
            return i;
         }
      }

      return -1;
   }

   public boolean onCharTyped(class_11905 input) {
      if (!this.focused) {
         return false;
      } else if (this.filter.filter(this.text, input.comp_4793())) {
         this.clearSelection();
         String var10001 = this.text.substring(0, this.cursor);
         this.text = var10001 + input.method_74226() + this.text.substring(this.cursor);
         ++this.cursor;
         this.resetSelection();
         this.runAction();
         return true;
      } else {
         return false;
      }
   }

   public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      if (this.isFocused()) {
         GuiKeyEvents.canUseKeys = false;
      }

      if (this.completionsW != null && this.focused) {
         renderer.absolutePost(() -> {
            renderer.beginRender();
            this.completionsW.render(renderer, mouseX, mouseY, delta);
            renderer.endRender();
         });
      }

      return super.render(renderer, mouseX, mouseY, delta);
   }

   private void clearSelection() {
      if (this.selectionStart != this.selectionEnd) {
         String preText = this.text;
         String var10001 = this.text.substring(0, this.selectionStart);
         this.text = var10001 + this.text.substring(this.selectionEnd);
         this.cursor = this.selectionStart;
         this.selectionEnd = this.cursor;
         if (!this.text.equals(preText)) {
            this.runAction();
         }

      }
   }

   private void resetSelection() {
      this.selectionStart = this.cursor;
      this.selectionEnd = this.cursor;
   }

   private int countToNextSpace(boolean toLeft) {
      return this.countToNextSpace(toLeft, this.cursor);
   }

   private int countToNextSpace(boolean toLeft, int startPos) {
      int count = 0;
      boolean hadNonSpace = false;
      int i = startPos;

      while(true) {
         if (toLeft) {
            if (i < 0) {
               break;
            }
         } else if (i >= this.text.length()) {
            break;
         }

         int j = i;
         if (toLeft) {
            j = i - 1;
         }

         if (j < this.text.length()) {
            if (j < 0 || hadNonSpace && Character.isWhitespace(this.text.charAt(j))) {
               break;
            }

            if (!Character.isWhitespace(this.text.charAt(j))) {
               hadNonSpace = true;
            }

            ++count;
         }

         i += toLeft ? -1 : 1;
      }

      return count;
   }

   private void calculateTextWidths() {
      this.textWidths.clear();

      for(int i = 0; i <= this.text.length(); ++i) {
         this.textWidths.add(this.theme.textWidth(this.text, i, false));
      }

   }

   private void runAction() {
      this.calculateTextWidths();
      this.cursorChanged();
      if (this.action != null) {
         this.action.run();
      }

   }

   private double textWidth() {
      return this.textWidths.isEmpty() ? (double)0.0F : this.textWidths.getDouble(this.textWidths.size() - 1);
   }

   private void cursorChanged() {
      double cursor = this.getCursorTextWidth(-2);
      if (cursor < this.textStart) {
         this.textStart -= this.textStart - cursor;
      }

      cursor = this.getCursorTextWidth(2);
      if (cursor > this.textStart + this.maxTextWidth()) {
         this.textStart += cursor - (this.textStart + this.maxTextWidth());
      }

      this.textStart = class_3532.method_15350(this.textStart, (double)0.0F, Math.max(this.textWidth() - this.maxTextWidth(), (double)0.0F));
      this.onCursorChanged();
      this.completions = this.renderer.getCompletions(this.text, this.cursor);
      this.completionsStart = 0;
      this.completionsW = null;
      if (this.completions != null && !this.completions.isEmpty()) {
         this.createCompletions(0);
      }

   }

   protected void onCursorChanged() {
   }

   private void createCompletions(int selected) {
      this.completionsW = this.createCompletionsRootWidget();
      this.completionsW.theme = this.theme;
      int max = Math.min(this.completions.size(), this.completionsStart + 6);

      for(int i = this.completionsStart; i < max; ++i) {
         WWidget widget = this.createCompletionsValueWidth((String)this.completions.get(i), i == selected);
         widget.theme = this.theme;
         Cell<?> cell = this.completionsW.add(widget).expandX().padHorizontal((double)4.0F);
         if (i == max - 1) {
            cell.padBottom((double)4.0F);
         }
      }

      this.completionsW.calculateSize();
      this.completionsW.x = Math.min(Math.max(this.x - this.pad() * (double)2.0F + this.getTextWidth(this.cursor) - this.getOverflowWidthForRender(), this.x), this.x + this.width - this.completionsW.width);
      this.completionsW.y = this.y + this.height;
      this.completionsW.calculateWidgetPositions();
   }

   protected double getTextWidth(int pos) {
      if (this.textWidths.isEmpty()) {
         return (double)0.0F;
      } else {
         if (pos < 0) {
            pos = 0;
         } else if (pos >= this.textWidths.size()) {
            pos = this.textWidths.size() - 1;
         }

         return this.textWidths.getDouble(pos);
      }
   }

   protected double getCursorTextWidth(int offset) {
      return this.getTextWidth(this.cursor + offset);
   }

   protected double getOverflowWidthForRender() {
      return this.textStart;
   }

   public String get() {
      return this.text;
   }

   public void set(String text) {
      this.text = text;
      this.cursor = class_3532.method_15340(this.cursor, 0, text.length());
      this.selectionStart = this.cursor;
      this.selectionEnd = this.cursor;
      this.calculateTextWidths();
      this.cursorChanged();
   }

   public void setFocused(boolean focused) {
      if (this.focused && !focused && this.actionOnUnfocused != null) {
         this.actionOnUnfocused.run();
      }

      boolean wasJustFocused = focused && !this.focused;
      this.focused = focused;
      this.resetSelection();
      if (wasJustFocused) {
         this.onCursorChanged();
      }

   }

   public void setCursorMax() {
      this.cursor = this.text.length();
   }

   public interface Renderer {
      void render(GuiRenderer var1, double var2, double var4, String var6, Color var7);

      default List<String> getCompletions(String text, int position) {
         return null;
      }
   }

   public interface ICompletionItem {
      boolean isSelected();

      void setSelected(boolean var1);

      String getCompletion();
   }
}
