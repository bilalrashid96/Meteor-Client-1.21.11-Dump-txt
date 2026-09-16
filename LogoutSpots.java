package meteordevelopment.meteorclient.systems.modules.render;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2874;
import net.minecraft.class_640;
import org.joml.Vector3d;

public class LogoutSpots extends Module {
   private static final Color GREEN = new Color(25, 225, 25);
   private static final Color ORANGE = new Color(225, 105, 25);
   private static final Color RED = new Color(225, 25, 25);
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Double> scale;
   private final Setting<Boolean> fullHeight;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<SettingColor> nameColor;
   private final Setting<SettingColor> nameBackgroundColor;
   private final List<Entry> players;
   private final List<class_640> lastPlayerList;
   private final List<class_1657> lastPlayers;
   private int timer;
   private class_2874 lastDimension;
   private static final Vector3d pos = new Vector3d();

   public LogoutSpots() {
      super(Categories.Render, "logout-spots", "Displays a box where another player has logged out at.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("The scale.")).defaultValue((double)1.0F).min((double)0.0F).build());
      this.fullHeight = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("full-height")).description("Displays the height as the player's full height.")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color.")).defaultValue(new SettingColor(255, 0, 255, 55)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color.")).defaultValue(new SettingColor(255, 0, 255)).build());
      this.nameColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("name-color")).description("The name color.")).defaultValue(new SettingColor(255, 255, 255)).build());
      this.nameBackgroundColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("name-background-color")).description("The name background color.")).defaultValue(new SettingColor(0, 0, 0, 75)).build());
      this.players = new ArrayList();
      this.lastPlayerList = new ArrayList();
      this.lastPlayers = new ArrayList();
      this.lineColor.onChanged();
   }

   public void onActivate() {
      this.lastPlayerList.addAll(this.mc.method_1562().method_2880());
      this.updateLastPlayers();
      this.timer = 10;
      this.lastDimension = this.mc.field_1687.method_8597();
   }

   public void onDeactivate() {
      this.players.clear();
      this.lastPlayerList.clear();
   }

   private void updateLastPlayers() {
      this.lastPlayers.clear();

      for(class_1297 entity : this.mc.field_1687.method_18112()) {
         if (entity instanceof class_1657) {
            this.lastPlayers.add((class_1657)entity);
         }
      }

   }

   @EventHandler
   private void onEntityAdded(EntityAddedEvent event) {
      if (event.entity instanceof class_1657) {
         int toRemove = -1;

         for(int i = 0; i < this.players.size(); ++i) {
            if (((Entry)this.players.get(i)).uuid.equals(event.entity.method_5667())) {
               toRemove = i;
               break;
            }
         }

         if (toRemove != -1) {
            this.players.remove(toRemove);
         }
      }

   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.mc.method_1562().method_2880().size() != this.lastPlayerList.size()) {
         for(class_640 entry : this.lastPlayerList) {
            if (!this.mc.method_1562().method_2880().stream().anyMatch((playerListEntry) -> playerListEntry.method_2966().equals(entry.method_2966()))) {
               for(class_1657 player : this.lastPlayers) {
                  if (player.method_5667().equals(entry.method_2966().id())) {
                     this.add(new Entry(player));
                  }
               }
            }
         }

         this.lastPlayerList.clear();
         this.lastPlayerList.addAll(this.mc.method_1562().method_2880());
         this.updateLastPlayers();
      }

      if (this.timer <= 0) {
         this.updateLastPlayers();
         this.timer = 10;
      } else {
         --this.timer;
      }

      class_2874 dimension = this.mc.field_1687.method_8597();
      if (dimension != this.lastDimension) {
         this.players.clear();
      }

      this.lastDimension = dimension;
   }

   private void add(Entry entry) {
      this.players.removeIf((player) -> player.uuid.equals(entry.uuid));
      this.players.add(entry);
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      for(Entry player : this.players) {
         player.render3D(event);
      }

   }

   @EventHandler
   private void onRender2D(Render2DEvent event) {
      for(Entry player : this.players) {
         player.render2D();
      }

   }

   public String getInfoString() {
      return Integer.toString(this.players.size());
   }

   private class Entry {
      public final double x;
      public final double y;
      public final double z;
      public final double xWidth;
      public final double zWidth;
      public final double halfWidth;
      public final double height;
      public final UUID uuid;
      public final String name;
      public final int health;
      public final int maxHealth;
      public final String healthText;

      public Entry(class_1657 entity) {
         this.halfWidth = (double)(entity.method_17681() / 2.0F);
         this.x = entity.method_23317() - this.halfWidth;
         this.y = entity.method_23318();
         this.z = entity.method_23321() - this.halfWidth;
         this.xWidth = entity.method_5829().method_17939();
         this.zWidth = entity.method_5829().method_17941();
         this.height = entity.method_5829().method_17940();
         this.uuid = entity.method_5667();
         this.name = entity.method_5477().getString();
         this.health = Math.round(entity.method_6032() + entity.method_6067());
         this.maxHealth = Math.round(entity.method_6063() + entity.method_6067());
         this.healthText = " " + this.health;
      }

      public void render3D(Render3DEvent event) {
         if ((Boolean)LogoutSpots.this.fullHeight.get()) {
            event.renderer.box(this.x, this.y, this.z, this.x + this.xWidth, this.y + this.height, this.z + this.zWidth, LogoutSpots.this.sideColor.get(), LogoutSpots.this.lineColor.get(), LogoutSpots.this.shapeMode.get(), 0);
         } else {
            event.renderer.sideHorizontal(this.x, this.y, this.z, this.x + this.xWidth, this.z, LogoutSpots.this.sideColor.get(), LogoutSpots.this.lineColor.get(), LogoutSpots.this.shapeMode.get());
         }

      }

      public void render2D() {
         if (PlayerUtils.isWithinCamera(this.x, this.y, this.z, (double)((Integer)LogoutSpots.this.mc.field_1690.method_42503().method_41753() * 16))) {
            TextRenderer text = TextRenderer.get();
            double scale = (Double)LogoutSpots.this.scale.get();
            LogoutSpots.pos.set(this.x + this.halfWidth, this.y + this.height + (double)0.5F, this.z + this.halfWidth);
            if (NametagUtils.to2D(LogoutSpots.pos, scale)) {
               NametagUtils.begin(LogoutSpots.pos);
               double healthPercentage = (double)this.health / (double)this.maxHealth;
               Color healthColor;
               if (healthPercentage <= 0.333) {
                  healthColor = LogoutSpots.RED;
               } else if (healthPercentage <= 0.666) {
                  healthColor = LogoutSpots.ORANGE;
               } else {
                  healthColor = LogoutSpots.GREEN;
               }

               double i = text.getWidth(this.name) / (double)2.0F + text.getWidth(this.healthText) / (double)2.0F;
               Renderer2D.COLOR.begin();
               Renderer2D.COLOR.quad(-i, (double)0.0F, i * (double)2.0F, text.getHeight(), LogoutSpots.this.nameBackgroundColor.get());
               Renderer2D.COLOR.render();
               text.beginBig();
               double hX = text.render(this.name, -i, (double)0.0F, LogoutSpots.this.nameColor.get());
               text.render(this.healthText, hX, (double)0.0F, healthColor);
               text.end();
               NametagUtils.end();
            }
         }
      }
   }
}
