package meteordevelopment.meteorclient.systems.hud.elements;

import java.util.List;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.ModuleListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Alignment;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.AnchorAura;
import meteordevelopment.meteorclient.systems.modules.combat.BedAura;
import meteordevelopment.meteorclient.systems.modules.combat.CrystalAura;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.systems.modules.combat.Surround;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class ModuleInfosHud extends HudElement {
   public static final HudElementInfo<ModuleInfosHud> INFO;
   private final SettingGroup sgGeneral;
   private final Setting<List<Module>> modules;
   private final Setting<Boolean> additionalInfo;
   private final Setting<Boolean> textShadow;
   private final Setting<SettingColor> moduleColor;
   private final Setting<SettingColor> onColor;
   private final Setting<SettingColor> offColor;
   private final Setting<Alignment> alignment;

   public ModuleInfosHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.modules = this.sgGeneral.add(((ModuleListSetting.Builder)((ModuleListSetting.Builder)(new ModuleListSetting.Builder()).name("modules")).description("Which modules to display")).defaultValue(KillAura.class, CrystalAura.class, AnchorAura.class, BedAura.class, Surround.class).build());
      this.additionalInfo = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("additional-info")).description("Shows additional info from the module next to the name in the module info list.")).defaultValue(true)).build());
      this.textShadow = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("text-shadow")).description("Renders shadow behind text.")).defaultValue(true)).build());
      this.moduleColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("module-color")).description("Module color.")).defaultValue(new SettingColor()).build());
      this.onColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("on-color")).description("Color when module is on.")).defaultValue(new SettingColor(25, 225, 25)).build());
      this.offColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("off-color")).description("Color when module is off.")).defaultValue(new SettingColor(225, 25, 25)).build());
      this.alignment = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("alignment")).description("Horizontal alignment.")).defaultValue(Alignment.Auto)).build());
   }

   public void render(HudRenderer renderer) {
      if (Modules.get() != null && !((List)this.modules.get()).isEmpty()) {
         double y = (double)this.y;
         double width = (double)0.0F;
         double height = (double)0.0F;
         int i = 0;

         for(Module module : this.modules.get()) {
            double moduleWidth = renderer.textWidth(module.title) + renderer.textWidth(" ");
            String text = null;
            if (module.isActive()) {
               if ((Boolean)this.additionalInfo.get()) {
                  String info = module.getInfoString();
                  if (info != null) {
                     text = info;
                  }
               }

               if (text == null) {
                  text = "ON";
               }
            } else {
               text = "OFF";
            }

            moduleWidth += renderer.textWidth(text);
            double x = (double)this.x + this.alignX(moduleWidth, this.alignment.get());
            x = renderer.text(module.title, x, y, this.moduleColor.get(), (Boolean)this.textShadow.get());
            renderer.text(text, x + renderer.textWidth(" "), y, module.isActive() ? (Color)this.onColor.get() : (Color)this.offColor.get(), (Boolean)this.textShadow.get());
            y += renderer.textHeight() + (double)2.0F;
            width = Math.max(width, moduleWidth);
            height += renderer.textHeight();
            if (i > 0) {
               height += (double)2.0F;
            }

            ++i;
         }

         this.setSize(width, height);
      } else {
         renderer.text("Module Info", (double)this.x, (double)this.y, this.moduleColor.get(), (Boolean)this.textShadow.get());
         this.setSize(renderer.textWidth("Module Info"), renderer.textHeight());
      }
   }

   static {
      INFO = new HudElementInfo<ModuleInfosHud>(Hud.GROUP, "module-infos", "Displays if selected modules are enabled or disabled.", ModuleInfosHud::new);
   }
}
