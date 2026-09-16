package meteordevelopment.meteorclient.systems.modules.render;

import java.util.ArrayDeque;
import java.util.Queue;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2874;

public class Breadcrumbs extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<SettingColor> color;
   private final Setting<Integer> maxSections;
   private final Setting<Double> sectionLength;
   private final Pool<Section> sectionPool;
   private final Queue<Section> sections;
   private Section section;
   private class_2874 lastDimension;

   public Breadcrumbs() {
      super(Categories.Render, "breadcrumbs", "Displays a trail behind where you have walked.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.color = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color")).description("The color of the Breadcrumbs trail.")).defaultValue(new SettingColor(225, 25, 25)).build());
      this.maxSections = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-sections")).description("The maximum number of sections.")).defaultValue(1000)).min(1).sliderRange(1, 5000).build());
      this.sectionLength = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("section-length")).description("The section length in blocks.")).defaultValue((double)0.5F).min((double)0.0F).sliderMax((double)1.0F).build());
      this.sectionPool = new Pool<Section>(() -> new Section());
      this.sections = new ArrayDeque();
   }

   public void onActivate() {
      this.section = this.sectionPool.get();
      this.section.set1();
      this.lastDimension = this.mc.field_1687.method_8597();
   }

   public void onDeactivate() {
      this.sectionPool.freeAll(this.sections);
      this.sections.clear();
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.mc.method_1562().method_76760()) {
         if (this.lastDimension != this.mc.field_1687.method_8597()) {
            this.sectionPool.freeAll(this.sections);
            this.sections.clear();
         }

         if (this.isFarEnough((double)this.section.x1, (double)this.section.y1, (double)this.section.z1)) {
            this.section.set2();
            if (this.sections.size() >= (Integer)this.maxSections.get()) {
               Section section = (Section)this.sections.poll();
               if (section != null) {
                  this.sectionPool.free(section);
               }
            }

            this.sections.add(this.section);
            this.section = this.sectionPool.get();
            this.section.set1();
         }

         this.lastDimension = this.mc.field_1687.method_8597();
      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      int iLast = -1;

      for(Section section : this.sections) {
         event.renderer.lines.ensureLineCapacity();
         if (iLast == -1) {
            iLast = event.renderer.lines.vec3((double)section.x1, (double)section.y1, (double)section.z1).color(this.color.get()).next();
         }

         int i = event.renderer.lines.vec3((double)section.x2, (double)section.y2, (double)section.z2).color(this.color.get()).next();
         event.renderer.lines.line(iLast, i);
         iLast = i;
      }

   }

   private boolean isFarEnough(double x, double y, double z) {
      return Math.abs(this.mc.field_1724.method_23317() - x) >= (Double)this.sectionLength.get() || Math.abs(this.mc.field_1724.method_23318() - y) >= (Double)this.sectionLength.get() || Math.abs(this.mc.field_1724.method_23321() - z) >= (Double)this.sectionLength.get();
   }

   private class Section {
      public float x1;
      public float y1;
      public float z1;
      public float x2;
      public float y2;
      public float z2;

      public void set1() {
         this.x1 = (float)Breadcrumbs.this.mc.field_1724.method_23317();
         this.y1 = (float)Breadcrumbs.this.mc.field_1724.method_23318();
         this.z1 = (float)Breadcrumbs.this.mc.field_1724.method_23321();
      }

      public void set2() {
         this.x2 = (float)Breadcrumbs.this.mc.field_1724.method_23317();
         this.y2 = (float)Breadcrumbs.this.mc.field_1724.method_23318();
         this.z2 = (float)Breadcrumbs.this.mc.field_1724.method_23321();
      }
   }
}
