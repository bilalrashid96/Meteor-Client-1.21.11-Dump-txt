package meteordevelopment.meteorclient.systems.modules.world;

import java.util.Iterator;
import java.util.Set;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1308;
import net.minecraft.class_1452;
import net.minecraft.class_1501;
import net.minecraft.class_1506;
import net.minecraft.class_1507;
import net.minecraft.class_1826;
import net.minecraft.class_3966;
import net.minecraft.class_4985;

public class AutoMount extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> checkSaddle;
   private final Setting<Boolean> rotate;
   private final Setting<Set<class_1299<?>>> entities;

   public AutoMount() {
      super(Categories.World, "auto-mount", "Automatically mounts entities.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.checkSaddle = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("check-saddle")).description("Checks if the entity contains a saddle before mounting.")).defaultValue(false)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Faces the entity you mount.")).defaultValue(true)).build());
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Rideable entities.")).filter(EntityUtils::isRideable).build());
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (!this.mc.field_1724.method_5765()) {
         if (!this.mc.field_1724.method_5715()) {
            if (!(this.mc.field_1724.method_6047().method_7909() instanceof class_1826)) {
               Iterator var2 = this.mc.field_1687.method_18112().iterator();

               class_1297 entity;
               while(true) {
                  if (!var2.hasNext()) {
                     return;
                  }

                  entity = (class_1297)var2.next();
                  if (((Set)this.entities.get()).contains(entity.method_5864()) && PlayerUtils.isWithin(entity, (double)4.0F) && (!(entity instanceof class_1452) && !(entity instanceof class_1506) && !(entity instanceof class_4985) && !(entity instanceof class_1507) || ((class_1308)entity).method_66672())) {
                     if (entity instanceof class_1501 || !(entity instanceof class_1308)) {
                        break;
                     }

                     class_1308 mobEntity = (class_1308)entity;
                     if (!(Boolean)this.checkSaddle.get() || mobEntity.method_66672()) {
                        break;
                     }
                  }
               }

               this.interact(entity, (Boolean)this.rotate.get());
            }
         }
      }
   }

   private void interact(class_1297 entity, boolean rotate) {
      if (rotate) {
         Rotations.rotate(Rotations.getYaw(entity), Rotations.getPitch(entity), -100, () -> this.interact(entity));
      } else {
         this.interact(entity);
      }

   }

   private void interact(class_1297 entity) {
      class_3966 location = new class_3966(entity, entity.method_5829().method_1005());
      this.mc.field_1761.method_2917(this.mc.field_1724, entity, location, class_1268.field_5808);
      this.mc.field_1761.method_2905(this.mc.field_1724, entity, class_1268.field_5808);
   }
}
