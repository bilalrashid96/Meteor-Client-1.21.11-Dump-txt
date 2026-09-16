package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1747;
import net.minecraft.class_1839;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2828;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_4184;
import net.minecraft.class_2350.class_2351;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class ClickTP extends Module {
   public ClickTP() {
      super(Categories.Movement, "click-tp", "Teleports you to the block you click on.");
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.mc.field_1724.method_31548().method_7391().method_7976() == class_1839.field_8952) {
         if (this.mc.field_1690.field_1904.method_1434()) {
            if (this.mc.field_1765 != null) {
               if (this.mc.field_1765.method_17783() == class_240.field_1331 && this.mc.field_1724.method_7287(((class_3966)this.mc.field_1765).method_17782(), class_1268.field_5808) != class_1269.field_5811) {
                  return;
               }

               if (this.mc.field_1765.method_17783() == class_240.field_1332 && this.mc.field_1724.method_6047().method_7909() instanceof class_1747) {
                  return;
               }
            }

            class_4184 camera = this.mc.field_1773.method_19418();
            class_243 cameraPos = camera.method_71156();
            class_243 direction = class_243.method_1030(camera.method_19329(), camera.method_19330()).method_1021((double)210.0F);
            class_243 targetPos = cameraPos.method_1019(direction);
            class_3959 context = new class_3959(cameraPos, targetPos, class_3960.field_17559, class_242.field_1348, this.mc.field_1724);
            class_3965 hitResult = this.mc.field_1687.method_17742(context);
            if (hitResult.method_17783() == class_240.field_1332) {
               class_2338 pos = hitResult.method_17777();
               class_2350 side = hitResult.method_17780();
               if (this.mc.field_1687.method_8320(pos).method_55781(this.mc.field_1687, this.mc.field_1724, hitResult) != class_1269.field_5811) {
                  return;
               }

               class_2680 state = this.mc.field_1687.method_8320(pos);
               class_265 shape = state.method_26220(this.mc.field_1687, pos);
               if (shape.method_1110()) {
                  shape = state.method_26218(this.mc.field_1687, pos);
               }

               double height = shape.method_1110() ? (double)1.0F : shape.method_1105(class_2351.field_11052);
               class_243 newPos = new class_243((double)pos.method_10263() + (double)0.5F + (double)side.method_10148(), (double)pos.method_10264() + height, (double)pos.method_10260() + (double)0.5F + (double)side.method_10165());
               int packetsRequired = (int)Math.ceil(this.mc.field_1724.method_73189().method_1022(newPos) / (double)10.0F) - 1;
               if (packetsRequired > 19) {
                  packetsRequired = 0;
               }

               for(int packetNumber = 0; packetNumber < packetsRequired; ++packetNumber) {
                  this.mc.field_1724.field_3944.method_52787(new class_2828.class_5911(true, true));
               }

               this.mc.field_1724.field_3944.method_52787(new class_2828.class_2829(newPos.field_1352, newPos.field_1351, newPos.field_1350, true, true));
               this.mc.field_1724.method_33574(newPos);
            }

         }
      }
   }
}
