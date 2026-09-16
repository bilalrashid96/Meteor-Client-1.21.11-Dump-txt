package meteordevelopment.meteorclient.systems.modules.world;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.Set;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1802;
import net.minecraft.class_3966;

public class AutoNametag extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<Double> range;
   private final Setting<SortPriority> priority;
   private final Setting<Boolean> renametag;
   private final Setting<Boolean> rotate;
   private final Object2IntMap<class_1297> entityCooldowns;
   private class_1297 target;
   private boolean offHand;

   public AutoNametag() {
      super(Categories.World, "auto-nametag", "Automatically uses nametags on entities without a nametag. WILL nametag ALL entities in the specified distance.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Which entities to nametag.")).build());
      this.range = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("The maximum range an entity can be to be nametagged.")).defaultValue((double)5.0F).min((double)0.0F).sliderMax((double)6.0F).build());
      this.priority = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("priority")).description("Priority sort")).defaultValue(SortPriority.LowestDistance)).build());
      this.renametag = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("renametag")).description("Allows already nametagged entities to be renamed.")).defaultValue(true)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Automatically faces towards the mob being nametagged.")).defaultValue(true)).build());
      this.entityCooldowns = new Object2IntOpenHashMap();
   }

   public void onDeactivate() {
      this.entityCooldowns.clear();
   }

   @EventHandler
   private void onTickPre(TickEvent.Pre event) {
      FindItemResult findNametag = InvUtils.findInHotbar(class_1802.field_8448);
      if (!findNametag.found()) {
         this.error("No Nametag in Hotbar", new Object[0]);
         this.toggle();
      } else {
         this.target = TargetUtils.get((entity) -> {
            if (!PlayerUtils.isWithin(entity, (Double)this.range.get())) {
               return false;
            } else if (!((Set)this.entities.get()).contains(entity.method_5864())) {
               return false;
            } else if (!entity.method_16914() || (Boolean)this.renametag.get() && !entity.method_5797().equals(this.mc.field_1724.method_31548().method_5438(findNametag.slot()).method_7964())) {
               return this.entityCooldowns.getInt(entity) <= 0;
            } else {
               return false;
            }
         }, this.priority.get());
         if (this.target != null) {
            InvUtils.swap(findNametag.slot(), true);
            this.offHand = findNametag.isOffhand();
            if ((Boolean)this.rotate.get()) {
               Rotations.rotate(Rotations.getYaw(this.target), Rotations.getPitch(this.target), -100, this::interact);
            } else {
               this.interact();
            }

         }
      }
   }

   @EventHandler
   private void onTickPost(TickEvent.Post event) {
      Iterator<class_1297> it = this.entityCooldowns.keySet().iterator();

      while(it.hasNext()) {
         class_1297 entity = (class_1297)it.next();
         int cooldown = this.entityCooldowns.getInt(entity) - 1;
         if (cooldown <= 0) {
            it.remove();
         } else {
            this.entityCooldowns.put(entity, cooldown);
         }
      }

   }

   private void interact() {
      class_1268 hand = this.offHand ? class_1268.field_5810 : class_1268.field_5808;
      class_3966 location = new class_3966(this.target, this.target.method_5829().method_1005());
      this.mc.field_1761.method_2917(this.mc.field_1724, this.target, location, hand);
      this.mc.field_1761.method_2905(this.mc.field_1724, this.target, hand);
      InvUtils.swapBack();
      this.entityCooldowns.put(this.target, 20);
   }
}
