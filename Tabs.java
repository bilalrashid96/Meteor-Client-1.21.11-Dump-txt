package meteordevelopment.meteorclient.gui.tabs;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.gui.tabs.builtin.ConfigTab;
import meteordevelopment.meteorclient.gui.tabs.builtin.FriendsTab;
import meteordevelopment.meteorclient.gui.tabs.builtin.GuiTab;
import meteordevelopment.meteorclient.gui.tabs.builtin.HudTab;
import meteordevelopment.meteorclient.gui.tabs.builtin.MacrosTab;
import meteordevelopment.meteorclient.gui.tabs.builtin.ModulesTab;
import meteordevelopment.meteorclient.gui.tabs.builtin.PathManagerTab;
import meteordevelopment.meteorclient.gui.tabs.builtin.ProfilesTab;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.utils.PreInit;

public class Tabs {
   private static final List<Tab> tabs = new ArrayList();
   private static final Reference2ReferenceOpenHashMap<Class<? extends Tab>, Tab> tabInstances = new Reference2ReferenceOpenHashMap();

   private Tabs() {
   }

   @PreInit(
      dependencies = {PathManagers.class}
   )
   public static void init() {
      add(new ModulesTab());
      add(new ConfigTab());
      add(new GuiTab());
      add(new HudTab());
      add(new FriendsTab());
      add(new MacrosTab());
      add(new ProfilesTab());
      if (PathManagers.get().getSettings().get().sizeGroups() > 0) {
         add(new PathManagerTab());
      }

   }

   public static void add(Tab tab) {
      tabs.add(tab);
      tabInstances.put(tab.getClass(), tab);
   }

   public static List<Tab> get() {
      return tabs;
   }

   public static Tab get(Class<? extends Tab> klass) {
      return (Tab)tabInstances.get(klass);
   }
}
