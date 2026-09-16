package meteordevelopment.meteorclient.systems.modules.render;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_10583;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1321;
import net.minecraft.class_1657;
import net.minecraft.class_1684;
import org.joml.Vector3d;

public class EntityOwner extends Module {
   private static final Color BACKGROUND = new Color(0, 0, 0, 75);
   private static final Color TEXT = new Color(255, 255, 255);
   private final SettingGroup sgGeneral;
   private final Setting<Double> scale;
   private final Vector3d pos;
   private final Map<UUID, String> uuidToName;

   public EntityOwner() {
      super(Categories.Render, "entity-owner", "Displays the name of the player who owns the entity you're looking at.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("The scale of the text.")).defaultValue((double)1.0F).min((double)0.0F).build());
      this.pos = new Vector3d();
      this.uuidToName = new HashMap();
   }

   public void onDeactivate() {
      this.uuidToName.clear();
   }

   @EventHandler
   private void onRender2D(Render2DEvent event) {
      for(class_1297 entity : this.mc.field_1687.method_18112()) {
         class_10583<class_1309> owner;
         if (entity instanceof class_1321 tameable) {
            owner = tameable.method_66287();
         } else {
            if (!(entity instanceof class_1684)) {
               continue;
            }

            class_1684 pearl = (class_1684)entity;
            owner = class_10583.method_73299((class_1309)pearl.method_24921());
         }

         if (owner != null) {
            Utils.set(this.pos, entity, (double)event.tickDelta);
            this.pos.add((double)0.0F, (double)entity.method_18381(entity.method_18376()) + (double)0.75F, (double)0.0F);
            if (NametagUtils.to2D(this.pos, (Double)this.scale.get())) {
               this.renderNametag(this.getOwnerName(owner));
            }
         }
      }

   }

   private void renderNametag(String name) {
      TextRenderer text = TextRenderer.get();
      NametagUtils.begin(this.pos);
      text.beginBig();
      double w = text.getWidth(name);
      double x = -w / (double)2.0F;
      double y = -text.getHeight();
      Renderer2D.COLOR.begin();
      Renderer2D.COLOR.quad(x - (double)1.0F, y - (double)1.0F, w + (double)2.0F, text.getHeight() + (double)2.0F, BACKGROUND);
      Renderer2D.COLOR.render();
      text.render(name, x, y, TEXT);
      text.end();
      NametagUtils.end();
   }

   private String getOwnerName(class_10583<class_1309> owner) {
      class_1309 ownerEntity = (class_1309)class_10583.method_66254(owner, this.mc.field_1687, class_1309.class);
      if (ownerEntity instanceof class_1657 playerEntity) {
         return playerEntity.method_5477().getString();
      } else {
         UUID uuid = owner.method_66263();
         String name = (String)this.uuidToName.get(uuid);
         if (name != null) {
            return name;
         } else {
            MeteorExecutor.execute(() -> {
               if (this.isActive()) {
                  String var10000 = uuid.toString();
                  ProfileResponse res = (ProfileResponse)Http.get("https://sessionserver.mojang.com/session/minecraft/profile/" + var10000.replace("-", "")).sendJson(ProfileResponse.class);
                  if (this.isActive()) {
                     if (res == null) {
                        this.uuidToName.put(uuid, "Failed to get name");
                     } else {
                        this.uuidToName.put(uuid, res.name);
                     }
                  }
               }

            });
            name = "Retrieving";
            this.uuidToName.put(uuid, name);
            return name;
         }
      }
   }

   private static class ProfileResponse {
      public String name;
   }
}
