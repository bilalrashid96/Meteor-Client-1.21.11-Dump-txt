package meteordevelopment.meteorclient.gui.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_2820;
import net.minecraft.class_3872;
import net.minecraft.class_9262;
import net.minecraft.class_9302;
import net.minecraft.class_9334;

public class EditBookTitleAndAuthorScreen extends WindowScreen {
   private final class_1799 itemStack;
   private final class_1268 hand;

   public EditBookTitleAndAuthorScreen(GuiTheme theme, class_1799 itemStack, class_1268 hand) {
      super(theme, "Edit title & author");
      this.itemStack = itemStack;
      this.hand = hand;
   }

   public void initWidgets() {
      WTable t = (WTable)this.add(this.theme.table()).expandX().widget();
      t.add(this.theme.label("Title"));
      WTextBox title = (WTextBox)t.add(this.theme.textBox((String)((class_9302)this.itemStack.method_58694(class_9334.field_49606)).comp_2419().method_57140(MeteorClient.mc.method_33883()))).minWidth((double)220.0F).expandX().widget();
      t.row();
      t.add(this.theme.label("Author"));
      WTextBox author = (WTextBox)t.add(this.theme.textBox(((class_9302)this.itemStack.method_58694(class_9334.field_49606)).comp_2420())).minWidth((double)220.0F).expandX().widget();
      t.row();
      ((WButton)t.add(this.theme.button("Done")).expandX().widget()).action = () -> {
         class_9302 component = (class_9302)this.itemStack.method_58694(class_9334.field_49606);
         class_9302 newComponent = new class_9302(class_9262.method_57137(title.get()), author.get(), component.comp_2421(), component.comp_2422(), component.comp_2423());
         this.itemStack.method_57379(class_9334.field_49606, newComponent);
         class_3872.class_3931 contents = new class_3872.class_3931(((class_9302)this.itemStack.method_58694(class_9334.field_49606)).method_57525(MeteorClient.mc.method_33883()));
         List<String> pages = new ArrayList(contents.method_17560());

         for(int i = 0; i < contents.method_17560(); ++i) {
            pages.add(contents.method_17563(i).getString());
         }

         MeteorClient.mc.method_1562().method_52787(new class_2820(this.hand == class_1268.field_5808 ? MeteorClient.mc.field_1724.method_31548().method_67532() : 40, pages, Optional.of(title.get())));
         this.method_25419();
      };
   }
}
