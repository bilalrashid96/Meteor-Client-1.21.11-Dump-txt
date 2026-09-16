package meteordevelopment.meteorclient.systems.modules.render;

import java.util.Objects;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_124;
import net.minecraft.class_1934;
import net.minecraft.class_2561;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import net.minecraft.class_640;

public class BetterTab extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<Integer> tabSize;
   public final Setting<Integer> tabHeight;
   private final Setting<Boolean> self;
   private final Setting<SettingColor> selfColor;
   private final Setting<Boolean> friends;
   public final Setting<Boolean> accurateLatency;
   private final Setting<Boolean> gamemode;

   public BetterTab() {
      super(Categories.Render, "better-tab", "Various improvements to the tab list.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.tabSize = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("tablist-size")).description("How many players in total to display in the tablist.")).defaultValue(100)).min(1).sliderRange(1, 1000).build());
      this.tabHeight = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("column-height")).description("How many players to display in each column.")).defaultValue(20)).min(1).sliderRange(1, 1000).build());
      this.self = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("highlight-self")).description("Highlights yourself in the tablist.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      ColorSetting.Builder var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("self-color")).description("The color to highlight your name with.")).defaultValue(new SettingColor(250, 130, 30));
      Setting var10003 = this.self;
      Objects.requireNonNull(var10003);
      this.selfColor = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      this.friends = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("highlight-friends")).description("Highlights friends in the tablist.")).defaultValue(true)).build());
      this.accurateLatency = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("accurate-latency")).description("Shows latency as a number in the tablist.")).defaultValue(true)).build());
      this.gamemode = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("gamemode")).description("Display gamemode next to the nick.")).defaultValue(false)).build());
   }

   public class_2561 getPlayerName(class_640 playerListEntry) {
      Color color = null;
      class_2561 name = playerListEntry.method_2971();
      if (name == null) {
         name = class_2561.method_43470(playerListEntry.method_2966().name());
      }

      if (playerListEntry.method_2966().id().toString().equals(this.mc.field_1724.method_7334().id().toString()) && (Boolean)this.self.get()) {
         color = this.selfColor.get();
      } else if ((Boolean)this.friends.get() && Friends.get().isFriend(playerListEntry)) {
         Friend friend = Friends.get().get(playerListEntry);
         if (friend != null) {
            color = Config.get().friendColor.get();
         }
      }

      if (color != null) {
         String nameString = name.getString();

         for(class_124 format : class_124.values()) {
            if (format.method_543()) {
               nameString = nameString.replace(format.toString(), "");
            }
         }

         name = class_2561.method_43470(nameString).method_10862(name.method_10866().method_27703(class_5251.method_27717(color.getPacked())));
      }

      if ((Boolean)this.gamemode.get()) {
         class_1934 gm = playerListEntry.method_2958();
         String gmText = "?";
         if (gm != null) {
            String var10000;
            switch (gm) {
               case field_9219 -> var10000 = "Sp";
               case field_9215 -> var10000 = "S";
               case field_9220 -> var10000 = "C";
               case field_9216 -> var10000 = "A";
               default -> throw new MatchException((String)null, (Throwable)null);
            }

            gmText = var10000;
         }

         class_5250 text = class_2561.method_43470("");
         text.method_10852(name);
         text.method_27693(" [" + gmText + "]");
         name = text;
      }

      return name;
   }
}
