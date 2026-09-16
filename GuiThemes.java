package meteordevelopment.meteorclient.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.PreInit;
import net.minecraft.class_2487;
import net.minecraft.class_2507;

public class GuiThemes {
   private static final File FOLDER;
   private static final File THEMES_FOLDER;
   private static final File FILE;
   private static final List<GuiTheme> themes;
   private static GuiTheme theme;

   private GuiThemes() {
   }

   @PreInit
   public static void init() {
      add(new MeteorGuiTheme());
   }

   @PostInit
   public static void postInit() {
      if (FILE.exists()) {
         try {
            class_2487 tag = class_2507.method_10633(FILE.toPath());
            if (tag != null) {
               select(tag.method_68564("currentTheme", ""));
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      if (theme == null) {
         select("Meteor");
      }

   }

   public static void add(GuiTheme theme) {
      ListIterator<GuiTheme> it = themes.listIterator();

      while(it.hasNext()) {
         if (((GuiTheme)it.next()).name.equals(theme.name)) {
            it.set(theme);
            MeteorClient.LOG.error("Theme with the name '{}' has already been added.", theme.name);
            return;
         }
      }

      themes.add(theme);
   }

   public static void select(String name) {
      GuiTheme theme = null;

      for(GuiTheme t : themes) {
         if (t.name.equals(name)) {
            theme = t;
            break;
         }
      }

      if (theme != null) {
         saveTheme();
         GuiThemes.theme = theme;

         try {
            File file = new File(THEMES_FOLDER, get().name + ".nbt");
            if (file.exists()) {
               class_2487 tag = class_2507.method_10633(file.toPath());
               if (tag != null) {
                  get().fromTag(tag);
               }
            }
         } catch (IOException e) {
            e.printStackTrace();
         }

         saveGlobal();
      }

   }

   public static GuiTheme get() {
      return theme;
   }

   public static String[] getNames() {
      String[] names = new String[themes.size()];

      for(int i = 0; i < themes.size(); ++i) {
         names[i] = ((GuiTheme)themes.get(i)).name;
      }

      return names;
   }

   private static void saveTheme() {
      if (get() != null) {
         try {
            class_2487 tag = get().toTag();
            THEMES_FOLDER.mkdirs();
            class_2507.method_10630(tag, (new File(THEMES_FOLDER, get().name + ".nbt")).toPath());
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

   }

   private static void saveGlobal() {
      try {
         class_2487 tag = new class_2487();
         tag.method_10582("currentTheme", get().name);
         FOLDER.mkdirs();
         class_2507.method_10630(tag, FILE.toPath());
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   public static void save() {
      saveTheme();
      saveGlobal();
   }

   static {
      FOLDER = new File(MeteorClient.FOLDER, "gui");
      THEMES_FOLDER = new File(FOLDER, "themes");
      FILE = new File(FOLDER, "gui.nbt");
      themes = new ArrayList();
   }
}
