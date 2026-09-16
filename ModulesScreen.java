package meteordevelopment.meteorclient.gui.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import net.minecraft.class_11908;
import net.minecraft.class_1802;
import net.minecraft.class_3545;
import net.minecraft.class_6417;

public class ModulesScreen extends TabScreen {
   private WCategoryController controller;
   private WWindow searchWindow;
   private WTextBox searchTextBox;

   public ModulesScreen(GuiTheme theme) {
      super(theme, (Tab)Tabs.get().getFirst());
   }

   public void initWidgets() {
      this.controller = (WCategoryController)this.add(new WCategoryController()).widget();
      WVerticalList help = (WVerticalList)this.add(this.theme.verticalList()).pad((double)4.0F).bottom().widget();
      help.add(this.theme.label("Left click - Toggle module"));
      help.add(this.theme.label("Right click - Open module settings"));
   }

   protected void method_25426() {
      super.method_25426();
      this.controller.refresh();
   }

   protected WWindow createCategory(WContainer c, Category category, List<Module> moduleList) {
      WWindow w = this.theme.window(category.name);
      w.id = category.name;
      w.padding = (double)0.0F;
      w.spacing = (double)0.0F;
      if (this.theme.categoryIcons()) {
         w.beforeHeaderInit = (wContainer) -> wContainer.add(this.theme.item(category.icon)).pad((double)2.0F);
      }

      c.add(w);
      w.view.scrollOnlyWhenMouseOver = true;
      w.view.hasScrollBar = false;
      w.view.spacing = (double)0.0F;

      for(Module module : moduleList) {
         w.add(this.theme.module(module)).expandX();
      }

      return w;
   }

   protected void createSearchW(WContainer w, String text) {
      if (!text.isEmpty()) {
         List<class_3545<Module, String>> modules = Modules.get().searchTitles(text);
         if (!modules.isEmpty()) {
            WSection section = (WSection)w.add(this.theme.section("Modules")).expandX().widget();
            section.spacing = (double)0.0F;
            int count = 0;

            for(class_3545<Module, String> p : modules) {
               if (count >= (Integer)Config.get().moduleSearchCount.get() || count >= modules.size()) {
                  break;
               }

               section.add(this.theme.module((Module)p.method_15442(), (String)p.method_15441())).expandX();
               ++count;
            }
         }

         Set<Module> settings = Modules.get().searchSettingTitles(text);
         if (!settings.isEmpty()) {
            WSection section = (WSection)w.add(this.theme.section("Settings")).expandX().widget();
            section.spacing = (double)0.0F;
            int count = 0;

            for(Module module : settings) {
               if (count >= (Integer)Config.get().moduleSearchCount.get() || count >= settings.size()) {
                  break;
               }

               section.add(this.theme.module(module)).expandX();
               ++count;
            }
         }
      }

   }

   protected WWindow createSearch(WContainer c) {
      WWindow w = this.theme.window("Search");
      w.id = "search";
      this.searchWindow = w;
      if (this.theme.categoryIcons()) {
         w.beforeHeaderInit = (wContainer) -> wContainer.add(this.theme.item(class_1802.field_8251.method_7854())).pad((double)2.0F);
      }

      c.add(w);
      w.view.scrollOnlyWhenMouseOver = true;
      w.view.hasScrollBar = false;
      WView var10000 = w.view;
      var10000.maxHeight -= (double)20.0F;
      WVerticalList l = this.theme.verticalList();
      WTextBox text = (WTextBox)w.add(this.theme.textBox("")).minWidth((double)140.0F).expandX().widget();
      text.setFocused(true);
      this.searchTextBox = text;
      text.action = () -> {
         l.clear();
         this.createSearchW(l, text.get());
      };
      w.add(l).expandX();
      this.createSearchW(l, text.get());
      return w;
   }

   public boolean method_25404(class_11908 value) {
      if (this.locked) {
         return false;
      } else {
         boolean cntrl = class_6417.field_52734 ? value.comp_4797() == 8 : value.comp_4797() == 2;
         if (cntrl && value.comp_4795() == 70) {
            if (this.searchWindow != null) {
               this.searchWindow.setExpanded(true);
            }

            if (this.searchTextBox != null) {
               this.searchTextBox.setFocused(true);
               this.searchTextBox.setCursorMax();
            }

            return true;
         } else {
            return super.method_25404(value);
         }
      }
   }

   protected Cell<WWindow> createFavorites(WContainer c) {
      boolean hasFavorites = Modules.get().getAll().stream().anyMatch((module) -> module.favorite);
      if (!hasFavorites) {
         return null;
      } else {
         WWindow w = this.theme.window("Favorites");
         w.id = "favorites";
         w.padding = (double)0.0F;
         w.spacing = (double)0.0F;
         if (this.theme.categoryIcons()) {
            w.beforeHeaderInit = (wContainer) -> wContainer.add(this.theme.item(class_1802.field_8137.method_7854())).pad((double)2.0F);
         }

         Cell<WWindow> cell = c.<WWindow>add(w);
         w.view.scrollOnlyWhenMouseOver = true;
         w.view.hasScrollBar = false;
         w.view.spacing = (double)0.0F;
         this.createFavoritesW(w);
         return cell;
      }
   }

   protected boolean createFavoritesW(WWindow w) {
      List<Module> modules = new ArrayList();

      for(Module module : Modules.get().getAll()) {
         if (module.favorite) {
            modules.add(module);
         }
      }

      modules.sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.name, o2.name));

      for(Module module : modules) {
         w.add(this.theme.module(module)).expandX();
      }

      return !modules.isEmpty();
   }

   public boolean toClipboard() {
      return NbtUtils.toClipboard(Modules.get());
   }

   public boolean fromClipboard() {
      return NbtUtils.fromClipboard(Modules.get());
   }

   public void reload() {
   }

   protected class WCategoryController extends WContainer {
      public final List<WWindow> windows = new ArrayList();
      private Cell<WWindow> favorites;

      public void init() {
         List<Module> moduleList = new ArrayList();

         for(Category category : Modules.loopCategories()) {
            for(Module module : Modules.get().getGroup(category)) {
               if (!((List)Config.get().hiddenModules.get()).contains(module)) {
                  moduleList.add(module);
               }
            }

            if (!moduleList.isEmpty()) {
               this.windows.add(ModulesScreen.this.createCategory(this, category, moduleList));
               moduleList.clear();
            }
         }

         this.windows.add(ModulesScreen.this.createSearch(this));
         this.refresh();
      }

      protected void refresh() {
         if (this.favorites == null) {
            this.favorites = ModulesScreen.this.createFavorites(this);
            if (this.favorites != null) {
               this.windows.add(this.favorites.widget());
            }
         } else {
            ((WWindow)this.favorites.widget()).clear();
            if (!ModulesScreen.this.createFavoritesW(this.favorites.widget())) {
               this.remove(this.favorites);
               this.windows.remove(this.favorites.widget());
               this.favorites = null;
            }
         }

      }

      protected void onCalculateWidgetPositions() {
         double pad = this.theme.scale((double)4.0F);
         double h = this.theme.scale((double)40.0F);
         double x = this.x + pad;
         double y = this.y;

         for(Cell<?> cell : this.cells) {
            double windowWidth = (double)Utils.getWindowWidth();
            double windowHeight = (double)Utils.getWindowHeight();
            if (x + cell.width > windowWidth) {
               x += pad;
               y += h;
            }

            if (x > windowWidth) {
               x = windowWidth / (double)2.0F - cell.width / (double)2.0F;
               if (x < (double)0.0F) {
                  x = (double)0.0F;
               }
            }

            if (y > windowHeight) {
               y = windowHeight / (double)2.0F - cell.height / (double)2.0F;
               if (y < (double)0.0F) {
                  y = (double)0.0F;
               }
            }

            cell.x = x;
            cell.y = y;
            cell.width = cell.widget().width;
            cell.height = cell.widget().height;
            cell.alignWidget();
            x += cell.width + pad;
         }

      }
   }
}
