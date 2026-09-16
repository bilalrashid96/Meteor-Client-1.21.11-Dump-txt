package meteordevelopment.meteorclient.systems.hud.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnchantmentListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1304;
import net.minecraft.class_1657;
import net.minecraft.class_1748;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1887;
import net.minecraft.class_1890;
import net.minecraft.class_3489;
import net.minecraft.class_3532;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_9304;
import net.minecraft.class_9636;
import org.joml.Matrix4fStack;

public class CombatHud extends HudElement {
   private static final Color GREEN = new Color(15, 255, 15);
   private static final Color RED = new Color(255, 15, 15);
   private static final Color BLACK = new Color(0, 0, 0, 255);
   public static final HudElementInfo<CombatHud> INFO;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgEnchantments;
   private final SettingGroup sgHealth;
   private final SettingGroup sgDistance;
   private final SettingGroup sgPing;
   private final SettingGroup sgScale;
   private final SettingGroup sgBackground;
   private final Setting<Double> range;
   private final Setting<SettingColor> healthColor1;
   private final Setting<SettingColor> healthColor2;
   private final Setting<SettingColor> healthColor3;
   private final Setting<Set<class_5321<class_1887>>> displayedEnchantments;
   private final Setting<SettingColor> enchantmentTextColor;
   private final Setting<Boolean> displayPing;
   private final Setting<SettingColor> pingColor1;
   private final Setting<SettingColor> pingColor2;
   private final Setting<SettingColor> pingColor3;
   private final Setting<Boolean> displayDistance;
   private final Setting<SettingColor> distColor1;
   private final Setting<SettingColor> distColor2;
   private final Setting<SettingColor> distColor3;
   public final Setting<Boolean> customScale;
   public final Setting<Double> scale;
   public final Setting<Boolean> background;
   public final Setting<SettingColor> backgroundColor;
   private class_1657 playerEntity;

   public CombatHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgEnchantments = this.settings.createGroup("Enchantments");
      this.sgHealth = this.settings.createGroup("Health");
      this.sgDistance = this.settings.createGroup("Distance");
      this.sgPing = this.settings.createGroup("Ping");
      this.sgScale = this.settings.createGroup("Scale");
      this.sgBackground = this.settings.createGroup("Background");
      this.range = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("The range to target players.")).defaultValue((double)100.0F).min((double)1.0F).sliderMax((double)200.0F).build());
      this.healthColor1 = this.sgHealth.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("health-stage-1")).description("The color on the left of the health gradient.")).defaultValue(new SettingColor(255, 15, 15)).build());
      this.healthColor2 = this.sgHealth.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("health-stage-2")).description("The color in the middle of the health gradient.")).defaultValue(new SettingColor(255, 150, 15)).build());
      this.healthColor3 = this.sgHealth.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("health-stage-3")).description("The color on the right of the health gradient.")).defaultValue(new SettingColor(15, 255, 15)).build());
      this.displayedEnchantments = this.sgEnchantments.add(((EnchantmentListSetting.Builder)((EnchantmentListSetting.Builder)(new EnchantmentListSetting.Builder()).name("displayed-enchantments")).description("The enchantments that are shown on nametags.")).vanillaDefaults().build());
      this.enchantmentTextColor = this.sgEnchantments.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("enchantment-color")).description("Color of enchantment text.")).defaultValue(new SettingColor(255, 255, 255)).build());
      this.displayPing = this.sgPing.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ping")).description("Shows the player's ping.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgPing;
      ColorSetting.Builder var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("ping-stage-1")).description("Color of ping text when under 75.")).defaultValue(new SettingColor(15, 255, 15));
      Setting var10003 = this.displayPing;
      Objects.requireNonNull(var10003);
      this.pingColor1 = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgPing;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("ping-stage-2")).description("Color of ping text when between 75 and 200.")).defaultValue(new SettingColor(255, 150, 15));
      var10003 = this.displayPing;
      Objects.requireNonNull(var10003);
      this.pingColor2 = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgPing;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("ping-stage-3")).description("Color of ping text when over 200.")).defaultValue(new SettingColor(255, 15, 15));
      var10003 = this.displayPing;
      Objects.requireNonNull(var10003);
      this.pingColor3 = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      this.displayDistance = this.sgDistance.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("distance")).description("Shows the distance between you and the player.")).defaultValue(true)).build());
      var10001 = this.sgDistance;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("distance-stage-1")).description("The color when a player is within 10 blocks of you.")).defaultValue(new SettingColor(255, 15, 15));
      var10003 = this.displayDistance;
      Objects.requireNonNull(var10003);
      this.distColor1 = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgDistance;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("distance-stage-2")).description("The color when a player is within 50 blocks of you.")).defaultValue(new SettingColor(255, 150, 15));
      var10003 = this.displayDistance;
      Objects.requireNonNull(var10003);
      this.distColor2 = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgDistance;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("distance-stage-3")).description("The color when a player is greater then 50 blocks away from you.")).defaultValue(new SettingColor(15, 255, 15));
      var10003 = this.displayDistance;
      Objects.requireNonNull(var10003);
      this.distColor3 = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      this.customScale = this.sgScale.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-scale")).description("Applies a custom scale to this hud element.")).defaultValue(false)).onChanged((aBoolean) -> this.calculateSize())).build());
      var10001 = this.sgScale;
      DoubleSetting.Builder var13 = (DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("Custom scale.");
      var10003 = this.customScale;
      Objects.requireNonNull(var10003);
      this.scale = var10001.add(((DoubleSetting.Builder)((DoubleSetting.Builder)var13.visible(var10003::get)).defaultValue((double)2.0F).onChanged((aDouble) -> this.calculateSize())).min((double)0.5F).sliderRange((double)0.5F, (double)3.0F).build());
      this.background = this.sgBackground.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("background")).description("Displays background.")).defaultValue(false)).build());
      var10001 = this.sgBackground;
      ColorSetting.Builder var14 = (ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("Color used for the background.");
      var10003 = this.background;
      Objects.requireNonNull(var10003);
      this.backgroundColor = var10001.add(((ColorSetting.Builder)var14.visible(var10003::get)).defaultValue(new SettingColor(25, 25, 25, 50)).build());
      this.calculateSize();
   }

   private void calculateSize() {
      this.setSize((double)175.0F * this.getScale(), (double)95.0F * this.getScale());
   }

   public void render(HudRenderer renderer) {
      renderer.post(() -> {
         double x = (double)this.x;
         double y = (double)this.y;
         Color primaryColor = TextHud.getSectionColor(0);
         Color secondaryColor = TextHud.getSectionColor(1);
         if (this.isInEditor()) {
            this.playerEntity = MeteorClient.mc.field_1724;
         } else {
            this.playerEntity = TargetUtils.getPlayerTarget((Double)this.range.get(), SortPriority.LowestDistance);
         }

         if (this.playerEntity != null || this.isInEditor()) {
            if ((Boolean)this.background.get()) {
               Renderer2D.COLOR.begin();
               Renderer2D.COLOR.quad(x, y, (double)this.getWidth(), (double)this.getHeight(), this.backgroundColor.get());
            }

            if (this.playerEntity == null) {
               if (this.isInEditor()) {
                  renderer.line(x, y, x + (double)this.getWidth(), y + (double)this.getHeight(), Color.GRAY);
                  renderer.line(x + (double)this.getWidth(), y, x, y + (double)this.getHeight(), Color.GRAY);
                  Renderer2D.COLOR.render();
               }

            } else {
               Renderer2D.COLOR.render();
               renderer.entity(this.playerEntity, (int)(x + (double)5.0F * this.getScale()), (int)(y + (double)10.0F * this.getScale()), (int)((double)50.0F * this.getScale()), (int)((double)60.0F * this.getScale()), -class_3532.method_15393(this.playerEntity.field_5982 + (this.playerEntity.method_36454() - this.playerEntity.field_5982) * MeteorClient.mc.method_61966().method_60637(true)), -this.playerEntity.method_36455());
               x += (double)50.0F * this.getScale();
               y += (double)5.0F * this.getScale();
               String breakText = " | ";
               String nameText = this.playerEntity.method_5477().getString();
               Color nameColor = PlayerUtils.getPlayerColor(this.playerEntity, primaryColor);
               int ping = EntityUtils.getPing(this.playerEntity);
               String pingText = ping + "ms";
               Color pingColor;
               if (ping <= 75) {
                  pingColor = this.pingColor1.get();
               } else if (ping <= 200) {
                  pingColor = this.pingColor2.get();
               } else {
                  pingColor = this.pingColor3.get();
               }

               double dist = (double)0.0F;
               if (!this.isInEditor()) {
                  dist = (double)Math.round((double)MeteorClient.mc.field_1724.method_5739(this.playerEntity) * (double)100.0F) / (double)100.0F;
               }

               String distText = dist + "m";
               Color distColor;
               if (dist <= (double)10.0F) {
                  distColor = this.distColor1.get();
               } else if (dist <= (double)50.0F) {
                  distColor = this.distColor2.get();
               } else {
                  distColor = this.distColor3.get();
               }

               String friendText = "Unknown";
               Color friendColor = primaryColor;
               if (Friends.get().isFriend(this.playerEntity)) {
                  friendText = "Friend";
                  friendColor = Config.get().friendColor.get();
               } else {
                  boolean naked = true;

                  for(int position = 3; position >= 0; --position) {
                     class_1799 itemStack = this.getItem(position);
                     if (!itemStack.method_7960()) {
                        naked = false;
                     }
                  }

                  if (naked) {
                     friendText = "Naked";
                     friendColor = GREEN;
                  } else {
                     boolean threat = false;

                     for(int position = 5; position >= 0; --position) {
                        class_1799 itemStack = this.getItem(position);
                        if (itemStack.method_31573(class_3489.field_42611) || itemStack.method_7909() == class_1802.field_8301 || itemStack.method_7909() == class_1802.field_23141 || itemStack.method_7909() instanceof class_1748) {
                           threat = true;
                        }
                     }

                     if (threat) {
                        friendText = "Threat";
                        friendColor = RED;
                     }
                  }
               }

               TextRenderer.get().begin(0.45 * this.getScale(), false, true);
               double breakWidth = TextRenderer.get().getWidth(breakText);
               double pingWidth = TextRenderer.get().getWidth(pingText);
               double friendWidth = TextRenderer.get().getWidth(friendText);
               TextRenderer.get().render(nameText, x, y, nameColor != null ? nameColor : primaryColor);
               y += TextRenderer.get().getHeight();
               TextRenderer.get().render(friendText, x, y, friendColor);
               if ((Boolean)this.displayPing.get()) {
                  TextRenderer.get().render(breakText, x + friendWidth, y, secondaryColor);
                  TextRenderer.get().render(pingText, x + friendWidth + breakWidth, y, pingColor);
                  if ((Boolean)this.displayDistance.get()) {
                     TextRenderer.get().render(breakText, x + friendWidth + breakWidth + pingWidth, y, secondaryColor);
                     TextRenderer.get().render(distText, x + friendWidth + breakWidth + pingWidth + breakWidth, y, distColor);
                  }
               } else if ((Boolean)this.displayDistance.get()) {
                  TextRenderer.get().render(breakText, x + friendWidth, y, secondaryColor);
                  TextRenderer.get().render(distText, x + friendWidth + breakWidth, y, distColor);
               }

               TextRenderer.get().end();
               y += (double)10.0F * this.getScale();
               int slot = 5;
               Matrix4fStack matrices = RenderSystem.getModelViewStack();
               matrices.pushMatrix();
               matrices.scale((float)this.getScale(), (float)this.getScale(), 1.0F);
               TextRenderer.get().begin(0.35, false, true);

               for(int position = 0; position < 6; ++position) {
                  double armorX = x + (double)(position * 20) * this.getScale();
                  class_1799 itemStack = this.getItem(slot);
                  renderer.item(itemStack, (int)armorX, (int)y, (float)this.getScale(), true);
                  double armorY = y / this.getScale() + (double)18.0F;
                  class_9304 enchantments = class_1890.method_57532(itemStack);
                  List<ObjectIntPair<class_6880<class_1887>>> enchantmentsToShow = new ArrayList();

                  for(Object2IntMap.Entry<class_6880<class_1887>> entry : enchantments.method_57539()) {
                     class_6880 var10000 = (class_6880)entry.getKey();
                     Set var10001 = this.displayedEnchantments.get();
                     Objects.requireNonNull(var10001);
                     if (var10000.method_40224(var10001::contains)) {
                        enchantmentsToShow.add(new ObjectIntImmutablePair((class_6880)entry.getKey(), entry.getIntValue()));
                     }
                  }

                  for(ObjectIntPair<class_6880<class_1887>> entry : enchantmentsToShow) {
                     String var71 = Utils.getEnchantSimpleName((class_6880)entry.left(), 3);
                     String enchantName = var71 + " " + entry.rightInt();
                     double enchX = x / this.getScale() + (double)(position * 20) + (double)8.0F - TextRenderer.get().getWidth(enchantName) / (double)2.0F;
                     TextRenderer.get().render(enchantName, enchX, armorY, ((class_6880)entry.left()).method_40220(class_9636.field_51551) ? RED : (Color)this.enchantmentTextColor.get());
                     armorY += TextRenderer.get().getHeight();
                  }

                  --slot;
               }

               TextRenderer.get().end();
               y = (double)((int)((double)this.y + (double)75.0F * this.getScale()));
               x = (double)this.x;
               x /= this.getScale();
               y /= this.getScale();
               x += (double)5.0F;
               y += (double)5.0F;
               Renderer2D.COLOR.begin();
               Renderer2D.COLOR.boxLines(x, y, (double)165.0F, (double)11.0F, BLACK);
               Renderer2D.COLOR.render();
               x += (double)2.0F;
               y += (double)2.0F;
               float maxHealth = this.playerEntity.method_6063();
               int maxAbsorb = 16;
               int maxTotal = (int)(maxHealth + (float)maxAbsorb);
               int totalHealthWidth = (int)(161.0F * maxHealth / (float)maxTotal);
               int totalAbsorbWidth = 161 * maxAbsorb / maxTotal;
               float health = this.playerEntity.method_6032();
               float absorb = this.playerEntity.method_6067();
               double healthPercent = (double)(health / maxHealth);
               double absorbPercent = (double)(absorb / (float)maxAbsorb);
               int healthWidth = (int)((double)totalHealthWidth * healthPercent);
               int absorbWidth = (int)((double)totalAbsorbWidth * absorbPercent);
               Renderer2D.COLOR.begin();
               Renderer2D.COLOR.quad(x, y, (double)healthWidth, (double)7.0F, this.healthColor1.get(), this.healthColor2.get(), this.healthColor2.get(), this.healthColor1.get());
               Renderer2D.COLOR.quad(x + (double)healthWidth, y, (double)absorbWidth, (double)7.0F, this.healthColor2.get(), this.healthColor3.get(), this.healthColor3.get(), this.healthColor2.get());
               Renderer2D.COLOR.render();
               matrices.popMatrix();
            }
         }
      });
   }

   private class_1799 getItem(int i) {
      if (this.isInEditor()) {
         class_1799 var2;
         switch (i) {
            case 0 -> var2 = class_1802.field_22030.method_7854();
            case 1 -> var2 = class_1802.field_22029.method_7854();
            case 2 -> var2 = class_1802.field_22028.method_7854();
            case 3 -> var2 = class_1802.field_22027.method_7854();
            case 4 -> var2 = class_1802.field_8288.method_7854();
            case 5 -> var2 = class_1802.field_8301.method_7854();
            default -> var2 = class_1799.field_8037;
         }

         return var2;
      } else if (this.playerEntity == null) {
         return class_1799.field_8037;
      } else {
         class_1799 var10000;
         switch (i) {
            case 0 -> var10000 = this.playerEntity.method_6118(class_1304.field_6166);
            case 1 -> var10000 = this.playerEntity.method_6118(class_1304.field_6172);
            case 2 -> var10000 = this.playerEntity.method_6118(class_1304.field_6174);
            case 3 -> var10000 = this.playerEntity.method_6118(class_1304.field_6169);
            case 4 -> var10000 = this.playerEntity.method_6079();
            case 5 -> var10000 = this.playerEntity.method_6047();
            default -> var10000 = class_1799.field_8037;
         }

         return var10000;
      }
   }

   private double getScale() {
      return (Boolean)this.customScale.get() ? (Double)this.scale.get() : Hud.get().getTextScale();
   }

   static {
      INFO = new HudElementInfo<CombatHud>(Hud.GROUP, "combat", "Displays information about your combat target.", CombatHud::new);
   }
}
