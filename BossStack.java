package meteordevelopment.meteorclient.systems.modules.render;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import meteordevelopment.meteorclient.events.render.RenderBossBarEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2561;
import net.minecraft.class_345;

public class BossStack extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<Boolean> stack;
   public final Setting<Boolean> hideName;
   private final Setting<Double> spacing;
   public static final Map<class_345, Integer> barMap = new WeakHashMap();

   public BossStack() {
      super(Categories.Render, "boss-stack", "Stacks boss bars to make your HUD less cluttered.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.stack = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("stack")).description("Stacks boss bars and adds a counter to the text.")).defaultValue(true)).build());
      this.hideName = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("hide-name")).description("Hides the names of boss bars.")).defaultValue(false)).build());
      this.spacing = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("bar-spacing")).description("The spacing reduction between each boss bar.")).defaultValue((double)10.0F).min((double)0.0F).build());
   }

   @EventHandler
   private void onFetchText(RenderBossBarEvent.BossText event) {
      if ((Boolean)this.hideName.get()) {
         event.name = class_2561.method_43473();
      } else if (!barMap.isEmpty() && (Boolean)this.stack.get()) {
         class_345 bar = event.bossBar;
         Integer integer = (Integer)barMap.get(bar);
         barMap.remove(bar);
         if (integer != null && !(Boolean)this.hideName.get()) {
            event.name = event.name.method_27661().method_27693(" x" + integer);
         }

      }
   }

   @EventHandler
   private void onSpaceBars(RenderBossBarEvent.BossSpacing event) {
      event.spacing = ((Double)this.spacing.get()).intValue();
   }

   @EventHandler
   private void onGetBars(RenderBossBarEvent.BossIterator event) {
      if ((Boolean)this.stack.get()) {
         HashMap<String, class_345> chosenBarMap = new HashMap();
         event.iterator.forEachRemaining((bar) -> {
            String name = bar.method_5414().getString();
            if (chosenBarMap.containsKey(name)) {
               barMap.compute((class_345)chosenBarMap.get(name), (clientBossBar, integer) -> integer == null ? 2 : integer + 1);
            } else {
               chosenBarMap.put(name, bar);
            }

         });
         event.iterator = chosenBarMap.values().iterator();
      }

   }
}
