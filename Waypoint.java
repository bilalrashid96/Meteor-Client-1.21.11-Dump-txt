package meteordevelopment.meteorclient.systems.waypoints;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.BlockPosSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ProvidedStringSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dimension;
import net.minecraft.class_1044;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_2520;
import net.minecraft.class_4844;

public class Waypoint implements ISerializable<Waypoint> {
   public final Settings settings = new Settings();
   private final SettingGroup sgVisual;
   private final SettingGroup sgPosition;
   public Setting<String> name;
   public Setting<String> icon;
   public Setting<SettingColor> color;
   public Setting<Boolean> visible;
   public Setting<Integer> maxVisible;
   public Setting<Double> scale;
   public Setting<class_2338> pos;
   public Setting<Dimension> dimension;
   public Setting<Boolean> opposite;
   public Setting<NearAction> actionWhenNear;
   public Setting<Integer> actionWhenNearDistance;
   public final UUID uuid;
   public final long createdAt;
   final int waypointActionCooldown;

   private Waypoint() {
      this.sgVisual = this.settings.createGroup("Visual");
      this.sgPosition = this.settings.createGroup("Position");
      this.name = this.sgVisual.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("name")).description("The name of the waypoint.")).defaultValue("Home")).build());
      this.icon = this.sgVisual.add(((ProvidedStringSetting.Builder)((ProvidedStringSetting.Builder)((ProvidedStringSetting.Builder)((ProvidedStringSetting.Builder)(new ProvidedStringSetting.Builder()).name("icon")).description("The icon of the waypoint.")).defaultValue("Square")).supplier(() -> Waypoints.BUILTIN_ICONS).onChanged((v) -> this.validateIcon())).build());
      this.color = this.sgVisual.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color")).description("The color of the waypoint.")).defaultValue(MeteorClient.ADDON.color.toSetting()).build());
      this.visible = this.sgVisual.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("visible")).description("Whether to show the waypoint.")).defaultValue(true)).build());
      this.maxVisible = this.sgVisual.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-visible-distance")).description("How far away to render the waypoint.")).defaultValue(5000)).build());
      this.scale = this.sgVisual.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("The scale of the waypoint.")).defaultValue((double)1.5F).build());
      this.pos = this.sgPosition.add(((BlockPosSetting.Builder)((BlockPosSetting.Builder)((BlockPosSetting.Builder)(new BlockPosSetting.Builder()).name("location")).description("The location of the waypoint.")).defaultValue(class_2338.field_10980)).build());
      this.dimension = this.sgPosition.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("dimension")).description("Which dimension the waypoint is in.")).defaultValue(Dimension.Overworld)).build());
      this.opposite = this.sgPosition.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("opposite-dimension")).description("Whether to show the waypoint in the opposite dimension.")).defaultValue(true)).visible(() -> this.dimension.get() != Dimension.End)).build());
      this.actionWhenNear = this.sgPosition.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("action-when-near")).description("Action to be performed when the player is near.")).defaultValue(Waypoint.NearAction.Disabled)).build());
      this.actionWhenNearDistance = this.sgPosition.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("action-when-near-distance")).description("How close (in blocks) the player has to be for the near action to be performed.")).defaultValue(8)).sliderRange(0, 32).visible(() -> this.actionWhenNear.get() != Waypoint.NearAction.Disabled)).build());
      this.waypointActionCooldown = 1000;
      this.uuid = UUID.randomUUID();
      this.createdAt = System.currentTimeMillis();
   }

   public Waypoint(class_2520 tag) {
      this.sgVisual = this.settings.createGroup("Visual");
      this.sgPosition = this.settings.createGroup("Position");
      this.name = this.sgVisual.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("name")).description("The name of the waypoint.")).defaultValue("Home")).build());
      this.icon = this.sgVisual.add(((ProvidedStringSetting.Builder)((ProvidedStringSetting.Builder)((ProvidedStringSetting.Builder)((ProvidedStringSetting.Builder)(new ProvidedStringSetting.Builder()).name("icon")).description("The icon of the waypoint.")).defaultValue("Square")).supplier(() -> Waypoints.BUILTIN_ICONS).onChanged((v) -> this.validateIcon())).build());
      this.color = this.sgVisual.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color")).description("The color of the waypoint.")).defaultValue(MeteorClient.ADDON.color.toSetting()).build());
      this.visible = this.sgVisual.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("visible")).description("Whether to show the waypoint.")).defaultValue(true)).build());
      this.maxVisible = this.sgVisual.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-visible-distance")).description("How far away to render the waypoint.")).defaultValue(5000)).build());
      this.scale = this.sgVisual.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("The scale of the waypoint.")).defaultValue((double)1.5F).build());
      this.pos = this.sgPosition.add(((BlockPosSetting.Builder)((BlockPosSetting.Builder)((BlockPosSetting.Builder)(new BlockPosSetting.Builder()).name("location")).description("The location of the waypoint.")).defaultValue(class_2338.field_10980)).build());
      this.dimension = this.sgPosition.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("dimension")).description("Which dimension the waypoint is in.")).defaultValue(Dimension.Overworld)).build());
      this.opposite = this.sgPosition.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("opposite-dimension")).description("Whether to show the waypoint in the opposite dimension.")).defaultValue(true)).visible(() -> this.dimension.get() != Dimension.End)).build());
      this.actionWhenNear = this.sgPosition.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("action-when-near")).description("Action to be performed when the player is near.")).defaultValue(Waypoint.NearAction.Disabled)).build());
      this.actionWhenNearDistance = this.sgPosition.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("action-when-near-distance")).description("How close (in blocks) the player has to be for the near action to be performed.")).defaultValue(8)).sliderRange(0, 32).visible(() -> this.actionWhenNear.get() != Waypoint.NearAction.Disabled)).build());
      this.waypointActionCooldown = 1000;
      class_2487 nbt = (class_2487)tag;
      this.uuid = (UUID)nbt.method_67491("uuid", class_4844.field_25122).orElse(UUID.randomUUID());
      this.createdAt = System.currentTimeMillis();
      this.fromTag(nbt);
   }

   public void renderIcon(double x, double y, double a, double size) {
      class_1044 texture = (class_1044)Waypoints.get().icons.get(this.icon.get());
      if (texture != null) {
         int preA = (this.color.get()).a;
         SettingColor var10000 = this.color.get();
         var10000.a = (int)((double)var10000.a * a);
         Renderer2D.TEXTURE.begin();
         Renderer2D.TEXTURE.texQuad(x, y, size, size, this.color.get());
         Renderer2D.TEXTURE.render(texture.method_71659(), texture.method_75484());
         (this.color.get()).a = preA;
      }
   }

   public class_2338 getPos() {
      Dimension dim = this.dimension.get();
      class_2338 pos = this.pos.get();
      Dimension currentDim = PlayerUtils.getDimension();
      if (dim != currentDim && !dim.equals(Dimension.End)) {
         class_2338 var10000;
         switch (dim) {
            case Overworld -> var10000 = new class_2338(pos.method_10263() / 8, pos.method_10264(), pos.method_10260() / 8);
            case Nether -> var10000 = new class_2338(pos.method_10263() * 8, pos.method_10264(), pos.method_10260() * 8);
            default -> var10000 = null;
         }

         return var10000;
      } else {
         return this.pos.get();
      }
   }

   public boolean actionWhenNearCheck(int distance) {
      if (System.currentTimeMillis() - this.createdAt < 1000L) {
         return false;
      } else {
         return (Integer)this.actionWhenNearDistance.get() >= distance;
      }
   }

   private void validateIcon() {
      Map<String, class_1044> icons = Waypoints.get().icons;
      class_1044 texture = (class_1044)icons.get(this.icon.get());
      if (texture == null && !icons.isEmpty()) {
         this.icon.set((String)icons.keySet().iterator().next());
      }

   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_67494("uuid", class_4844.field_25122, this.uuid);
      tag.method_10566("settings", this.settings.toTag());
      return tag;
   }

   public Waypoint fromTag(class_2487 tag) {
      if (tag.method_10545("settings")) {
         this.settings.fromTag(tag.method_68568("settings"));
      }

      return this;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Waypoint waypoint = (Waypoint)o;
         return Objects.equals(this.uuid, waypoint.uuid);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.uuid);
   }

   public String toString() {
      return this.name.get();
   }

   public static enum NearAction {
      Disabled,
      Hide,
      Delete;

      // $FF: synthetic method
      private static NearAction[] $values() {
         return new NearAction[]{Disabled, Hide, Delete};
      }
   }

   public static class Builder {
      private String name = "";
      private String icon = "";
      private class_2338 pos;
      private Dimension dimension;

      public Builder() {
         this.pos = class_2338.field_10980;
         this.dimension = Dimension.Overworld;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder icon(String icon) {
         this.icon = icon;
         return this;
      }

      public Builder pos(class_2338 pos) {
         this.pos = pos;
         return this;
      }

      public Builder dimension(Dimension dimension) {
         this.dimension = dimension;
         return this;
      }

      public Waypoint build() {
         Waypoint waypoint = new Waypoint();
         if (!this.name.equals(waypoint.name.getDefaultValue())) {
            waypoint.name.set(this.name);
         }

         if (!this.icon.equals(waypoint.icon.getDefaultValue())) {
            waypoint.icon.set(this.icon);
         }

         if (!this.pos.equals(waypoint.pos.getDefaultValue())) {
            waypoint.pos.set(this.pos);
         }

         if (!this.dimension.equals(waypoint.dimension.getDefaultValue())) {
            waypoint.dimension.set(this.dimension);
         }

         return waypoint;
      }
   }
}
