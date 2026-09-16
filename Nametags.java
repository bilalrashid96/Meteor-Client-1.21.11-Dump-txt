package meteordevelopment.meteorclient.systems.modules.render;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnchantmentListSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.NameProtect;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.misc.Names;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1533;
import net.minecraft.class_1541;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1701;
import net.minecraft.class_1799;
import net.minecraft.class_1887;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_1934;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_9304;
import net.minecraft.class_9636;
import org.joml.Vector3d;

public class Nametags extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPlayers;
   private final SettingGroup sgItems;
   private final SettingGroup sgRender;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<Double> scale;
   private final Setting<Boolean> ignoreSelf;
   private final Setting<Boolean> ignoreFriends;
   private final Setting<Boolean> ignoreBots;
   private final Setting<Boolean> culling;
   private final Setting<Double> maxCullRange;
   private final Setting<Integer> maxCullCount;
   private final Setting<Boolean> displayHealth;
   private final Setting<Boolean> displayPrefix;
   private final Setting<Boolean> displayGameMode;
   private final Setting<Boolean> displayDistance;
   private final Setting<Boolean> displayPing;
   private final Setting<Boolean> displayItems;
   private final Setting<Double> itemSpacing;
   private final Setting<Boolean> ignoreEmpty;
   private final Setting<Durability> itemDurability;
   private final Setting<Boolean> displayEnchants;
   private final Setting<Set<class_5321<class_1887>>> shownEnchantments;
   private final Setting<Position> enchantPos;
   private final Setting<Integer> enchantLength;
   private final Setting<Double> enchantTextScale;
   private final Setting<Boolean> itemCount;
   private final Setting<SettingColor> background;
   private final Setting<SettingColor> nameColor;
   private final Setting<SettingColor> pingColor;
   private final Setting<SettingColor> gamemodeColor;
   private final Setting<DistanceColorMode> distanceColorMode;
   private final Setting<SettingColor> distanceColor;
   private final Color WHITE;
   private final Color RED;
   private final Color AMBER;
   private final Color GREEN;
   private final Color GOLD;
   private final Vector3d pos;
   private final double[] itemWidths;
   private final List<class_1297> entityList;

   public Nametags() {
      super(Categories.Render, "nametags", "Displays customizable nametags above players, items and other entities.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPlayers = this.settings.createGroup("Players");
      this.sgItems = this.settings.createGroup("Items");
      this.sgRender = this.settings.createGroup("Render");
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Select entities to draw nametags on.")).defaultValue(class_1299.field_6097, class_1299.field_6052).build());
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("The scale of the nametag.")).defaultValue(1.1).min(0.1).build());
      this.ignoreSelf = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-self")).description("Ignore yourself when in third person or freecam.")).defaultValue(true)).build());
      this.ignoreFriends = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-friends")).description("Ignore rendering nametags for friends.")).defaultValue(false)).build());
      this.ignoreBots = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-bots")).description("Only render non-bot nametags.")).defaultValue(true)).build());
      this.culling = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("culling")).description("Only render a certain number of nametags at a certain distance.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      DoubleSetting.Builder var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("culling-range")).description("Only render nametags within this distance of your player.")).defaultValue((double)20.0F).min((double)0.0F).sliderMax((double)200.0F);
      Setting var10003 = this.culling;
      Objects.requireNonNull(var10003);
      this.maxCullRange = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      IntSetting.Builder var9 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("culling-count")).description("Only render this many nametags.")).defaultValue(50)).min(1).sliderRange(1, 100);
      var10003 = this.culling;
      Objects.requireNonNull(var10003);
      this.maxCullCount = var10001.add(((IntSetting.Builder)var9.visible(var10003::get)).build());
      this.displayHealth = this.sgPlayers.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("health")).description("Shows the player's health.")).defaultValue(true)).build());
      this.displayPrefix = this.sgPlayers.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("use-display-name")).description("Uses the players server display name instead of their account name.")).defaultValue(false)).build());
      this.displayGameMode = this.sgPlayers.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("gamemode")).description("Shows the player's GameMode.")).defaultValue(false)).build());
      this.displayDistance = this.sgPlayers.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("distance")).description("Shows the distance between you and the player.")).defaultValue(false)).build());
      this.displayPing = this.sgPlayers.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ping")).description("Shows the player's ping.")).defaultValue(true)).build());
      this.displayItems = this.sgPlayers.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("items")).description("Displays armor and hand items above the name tags.")).defaultValue(true)).build());
      var10001 = this.sgPlayers;
      DoubleSetting.Builder var10 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("item-spacing")).description("The spacing between items.")).defaultValue((double)2.0F).range((double)0.0F, (double)10.0F);
      var10003 = this.displayItems;
      Objects.requireNonNull(var10003);
      this.itemSpacing = var10001.add(((DoubleSetting.Builder)var10.visible(var10003::get)).build());
      var10001 = this.sgPlayers;
      BoolSetting.Builder var11 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-empty-slots")).description("Doesn't add spacing where an empty item stack would be.")).defaultValue(true);
      var10003 = this.displayItems;
      Objects.requireNonNull(var10003);
      this.ignoreEmpty = var10001.add(((BoolSetting.Builder)var11.visible(var10003::get)).build());
      var10001 = this.sgPlayers;
      EnumSetting.Builder var12 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("durability")).description("Displays item durability as either a total, percentage, or neither.")).defaultValue(Nametags.Durability.None);
      var10003 = this.displayItems;
      Objects.requireNonNull(var10003);
      this.itemDurability = var10001.add(((EnumSetting.Builder)var12.visible(var10003::get)).build());
      var10001 = this.sgPlayers;
      BoolSetting.Builder var13 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("display-enchants")).description("Displays item enchantments on the items.")).defaultValue(false);
      var10003 = this.displayItems;
      Objects.requireNonNull(var10003);
      this.displayEnchants = var10001.add(((BoolSetting.Builder)var13.visible(var10003::get)).build());
      this.shownEnchantments = this.sgPlayers.add(((EnchantmentListSetting.Builder)((EnchantmentListSetting.Builder)((EnchantmentListSetting.Builder)(new EnchantmentListSetting.Builder()).name("shown-enchantments")).description("The enchantments that are shown on nametags.")).visible(() -> (Boolean)this.displayItems.get() && (Boolean)this.displayEnchants.get())).defaultValue(class_1893.field_9111, class_1893.field_9107, class_1893.field_9095, class_1893.field_9096).build());
      this.enchantPos = this.sgPlayers.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("enchantment-position")).description("Where the enchantments are rendered.")).defaultValue(Nametags.Position.Above)).visible(() -> (Boolean)this.displayItems.get() && (Boolean)this.displayEnchants.get())).build());
      this.enchantLength = this.sgPlayers.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("enchant-name-length")).description("The length enchantment names are trimmed to.")).defaultValue(3)).range(1, 5).sliderRange(1, 5).visible(() -> (Boolean)this.displayItems.get() && (Boolean)this.displayEnchants.get())).build());
      this.enchantTextScale = this.sgPlayers.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("enchant-text-scale")).description("The scale of the enchantment text.")).defaultValue((double)1.0F).range(0.1, (double)2.0F).sliderRange(0.1, (double)2.0F).visible(() -> (Boolean)this.displayItems.get() && (Boolean)this.displayEnchants.get())).build());
      this.itemCount = this.sgItems.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-count")).description("Displays the number of items in the stack.")).defaultValue(true)).build());
      this.background = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("The color of the nametag background.")).defaultValue(new SettingColor(0, 0, 0, 75)).build());
      this.nameColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("name-color")).description("The color of the nametag names.")).defaultValue(new SettingColor()).build());
      var10001 = this.sgRender;
      ColorSetting.Builder var14 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("ping-color")).description("The color of the nametag ping.")).defaultValue(new SettingColor(20, 170, 170));
      var10003 = this.displayPing;
      Objects.requireNonNull(var10003);
      this.pingColor = var10001.add(((ColorSetting.Builder)var14.visible(var10003::get)).build());
      var10001 = this.sgRender;
      var14 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("gamemode-color")).description("The color of the nametag gamemode.")).defaultValue(new SettingColor(232, 185, 35));
      var10003 = this.displayGameMode;
      Objects.requireNonNull(var10003);
      this.gamemodeColor = var10001.add(((ColorSetting.Builder)var14.visible(var10003::get)).build());
      var10001 = this.sgRender;
      EnumSetting.Builder var16 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("distance-color-mode")).description("The mode to color the nametag distance with.")).defaultValue(Nametags.DistanceColorMode.Gradient);
      var10003 = this.displayDistance;
      Objects.requireNonNull(var10003);
      this.distanceColorMode = var10001.add(((EnumSetting.Builder)var16.visible(var10003::get)).build());
      this.distanceColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("distance-color")).description("The color of the nametag distance.")).defaultValue(new SettingColor(150, 150, 150)).visible(() -> (Boolean)this.displayDistance.get() && this.distanceColorMode.get() == Nametags.DistanceColorMode.Flat)).build());
      this.WHITE = new Color(255, 255, 255);
      this.RED = new Color(255, 25, 25);
      this.AMBER = new Color(255, 105, 25);
      this.GREEN = new Color(25, 252, 25);
      this.GOLD = new Color(232, 185, 35);
      this.pos = new Vector3d();
      this.itemWidths = new double[6];
      this.entityList = new ArrayList();
   }

   private static String ticksToTime(int ticks) {
      if (ticks > 72000) {
         int h = ticks / 20 / 3600;
         return h + " h";
      } else if (ticks > 1200) {
         int m = ticks / 20 / 60;
         return m + " m";
      } else {
         int s = ticks / 20;
         int ms = ticks % 20 / 2;
         return s + "." + ms + " s";
      }
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      this.entityList.clear();
      boolean freecamNotActive = !Modules.get().isActive(Freecam.class);
      boolean notThirdPerson = this.mc.field_1690.method_31044().method_31034();
      class_243 cameraPos = this.mc.field_1773.method_19418().method_71156();

      for(class_1297 entity : this.mc.field_1687.method_18112()) {
         class_1299<?> type = entity.method_5864();
         if (((Set)this.entities.get()).contains(type) && (type != class_1299.field_6097 || (!(Boolean)this.ignoreSelf.get() && (!freecamNotActive || !notThirdPerson) || entity != this.mc.field_1724) && (EntityUtils.getGameMode((class_1657)entity) != null || !(Boolean)this.ignoreBots.get()) && (!Friends.get().isFriend((class_1657)entity) || !(Boolean)this.ignoreFriends.get())) && (!(Boolean)this.culling.get() || PlayerUtils.isWithinCamera(entity, (Double)this.maxCullRange.get()))) {
            this.entityList.add(entity);
         }
      }

      this.entityList.sort(Comparator.comparing((e) -> e.method_5707(cameraPos)));
   }

   @EventHandler
   private void onRender2D(Render2DEvent event) {
      int count = this.getRenderCount();
      boolean shadow = (Boolean)Config.get().customFont.get();

      for(int i = count - 1; i > -1; --i) {
         class_1297 entity = (class_1297)this.entityList.get(i);
         Utils.set(this.pos, entity, (double)event.tickDelta);
         this.pos.add((double)0.0F, this.getHeight(entity), (double)0.0F);
         class_1299<?> type = entity.method_5864();
         if (NametagUtils.to2D(this.pos, (Double)this.scale.get())) {
            if (type == class_1299.field_6097) {
               this.renderNametagPlayer(event, (class_1657)entity, shadow);
            } else if (type == class_1299.field_6052) {
               this.renderNametagItem(((class_1542)entity).method_6983(), shadow);
            } else if (type != class_1299.field_6043 && type != class_1299.field_28401) {
               if (type == class_1299.field_6063) {
                  this.renderTntNametag(ticksToTime(((class_1541)entity).method_6969()), shadow);
               } else if (type == class_1299.field_6053 && ((class_1701)entity).method_7578()) {
                  this.renderTntNametag(ticksToTime(((class_1701)entity).method_7577()), shadow);
               } else if (entity instanceof class_1309) {
                  this.renderGenericLivingNametag((class_1309)entity, shadow);
               } else {
                  this.renderGenericNametag(entity, shadow);
               }
            } else {
               this.renderNametagItem(((class_1533)entity).method_6940(), shadow);
            }
         }
      }

   }

   private int getRenderCount() {
      int count = (Boolean)this.culling.get() ? (Integer)this.maxCullCount.get() : this.entityList.size();
      count = class_3532.method_15340(count, 0, this.entityList.size());
      return count;
   }

   public String getInfoString() {
      return Integer.toString(this.getRenderCount());
   }

   private double getHeight(class_1297 entity) {
      double height = (double)entity.method_18381(entity.method_18376());
      if (entity.method_5864() != class_1299.field_6052 && entity.method_5864() != class_1299.field_6043 && entity.method_5864() != class_1299.field_28401) {
         height += (double)0.5F;
      } else {
         height += 0.2;
      }

      return height;
   }

   private void renderNametagPlayer(Render2DEvent event, class_1657 player, boolean shadow) {
      TextRenderer text = TextRenderer.get();
      NametagUtils.begin(this.pos, event.drawContext);
      class_1934 gm = EntityUtils.getGameMode(player);
      String gmText = "BOT";
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

      gmText = "[" + gmText + "] ";
      Color nameColor = PlayerUtils.getPlayerColor(player, this.nameColor.get());
      String name;
      if (player == this.mc.field_1724) {
         name = ((NameProtect)Modules.get().get(NameProtect.class)).getName(player.method_5477().getString());
      } else if ((Boolean)this.displayPrefix.get()) {
         name = player.method_5476().getString();
      } else {
         name = player.method_5477().getString();
      }

      float absorption = player.method_6067();
      int health = Math.round(player.method_6032() + absorption);
      double healthPercentage = (double)((float)health / (player.method_6063() + absorption));
      String healthText = " " + health;
      Color healthColor;
      if (healthPercentage <= 0.333) {
         healthColor = this.RED;
      } else if (healthPercentage <= 0.666) {
         healthColor = this.AMBER;
      } else {
         healthColor = this.GREEN;
      }

      int ping = EntityUtils.getPing(player);
      String pingText = " [" + ping + "ms]";
      double dist = (double)Math.round(PlayerUtils.distanceToCamera(player) * (double)10.0F) / (double)10.0F;
      String distText = " " + dist + "m";
      double gmWidth = text.getWidth(gmText, shadow);
      double nameWidth = text.getWidth(name, shadow);
      double healthWidth = text.getWidth(healthText, shadow);
      double pingWidth = text.getWidth(pingText, shadow);
      double distWidth = text.getWidth(distText, shadow);
      double width = nameWidth;
      boolean renderPlayerDistance = player != this.mc.method_1560() || Modules.get().isActive(Freecam.class);
      if ((Boolean)this.displayHealth.get()) {
         width = nameWidth + healthWidth;
      }

      if ((Boolean)this.displayGameMode.get()) {
         width += gmWidth;
      }

      if ((Boolean)this.displayPing.get()) {
         width += pingWidth;
      }

      if ((Boolean)this.displayDistance.get() && renderPlayerDistance) {
         width += distWidth;
      }

      double widthHalf = width / (double)2.0F;
      double heightDown = text.getHeight(shadow);
      this.drawBg(-widthHalf, -heightDown, width, heightDown);
      text.beginBig();
      double hX = -widthHalf;
      double hY = -heightDown;
      if ((Boolean)this.displayGameMode.get()) {
         hX = text.render(gmText, hX, hY, this.gamemodeColor.get(), shadow);
      }

      hX = text.render(name, hX, hY, nameColor, shadow);
      if ((Boolean)this.displayHealth.get()) {
         hX = text.render(healthText, hX, hY, healthColor, shadow);
      }

      if ((Boolean)this.displayPing.get()) {
         hX = text.render(pingText, hX, hY, this.pingColor.get(), shadow);
      }

      if ((Boolean)this.displayDistance.get() && renderPlayerDistance) {
         switch (((DistanceColorMode)this.distanceColorMode.get()).ordinal()) {
            case 0 -> text.render(distText, hX, hY, EntityUtils.getColorFromDistance(player), shadow);
            case 1 -> text.render(distText, hX, hY, this.distanceColor.get(), shadow);
         }
      }

      text.end();
      if ((Boolean)this.displayItems.get()) {
         Arrays.fill(this.itemWidths, (double)0.0F);
         boolean hasItems = false;
         int maxEnchantCount = 0;

         for(int i = 0; i < 6; ++i) {
            class_1799 itemStack = this.getItem(player, i);
            if (this.itemWidths[i] == (double)0.0F && (!(Boolean)this.ignoreEmpty.get() || !itemStack.method_7960())) {
               this.itemWidths[i] = (double)32.0F + (Double)this.itemSpacing.get();
            }

            if (!itemStack.method_7960()) {
               hasItems = true;
            }

            if ((Boolean)this.displayEnchants.get()) {
               class_9304 enchantments = class_1890.method_57532(itemStack);
               int size = 0;

               for(class_6880<class_1887> enchantment : enchantments.method_57534()) {
                  if (!enchantment.method_40230().isPresent() || ((Set)this.shownEnchantments.get()).contains(enchantment.method_40230().get())) {
                     String var81 = Utils.getEnchantSimpleName(enchantment, (Integer)this.enchantLength.get());
                     String enchantName = var81 + " " + enchantments.method_57536(enchantment);
                     this.itemWidths[i] = Math.max(this.itemWidths[i], text.getWidth(enchantName, shadow) / (double)2.0F);
                     ++size;
                  }
               }

               maxEnchantCount = Math.max(maxEnchantCount, size);
            }
         }

         double itemsHeight = (double)(hasItems ? 32 : 0);
         double itemWidthTotal = (double)0.0F;

         for(double w : this.itemWidths) {
            itemWidthTotal += w;
         }

         double itemWidthHalf = itemWidthTotal / (double)2.0F;
         double y = -heightDown - (double)7.0F - itemsHeight;
         double x = -itemWidthHalf;

         for(int i = 0; i < 6; ++i) {
            class_1799 stack = this.getItem(player, i);
            RenderUtils.drawItem(event.drawContext, stack, (int)x, (int)y, 2.0F, true, (String)null, false);
            if (stack.method_7963() && this.itemDurability.get() != Nametags.Durability.None) {
               text.begin((double)0.75F, false, true);
               String var82;
               switch (((Durability)this.itemDurability.get()).ordinal()) {
                  case 1 -> var82 = Integer.toString(stack.method_7936() - stack.method_7919());
                  case 2 -> var82 = String.format("%.0f%%", (float)(stack.method_7936() - stack.method_7919()) * 100.0F / (float)stack.method_7936());
                  default -> var82 = "err";
               }

               String damageText = var82;
               Color damageColor = new Color(stack.method_31580());
               text.render(damageText, (double)((int)x), (double)((int)y), damageColor.a(255), true);
               text.end();
            }

            if (maxEnchantCount > 0 && (Boolean)this.displayEnchants.get()) {
               text.begin((double)0.5F * (Double)this.enchantTextScale.get(), false, true);
               class_9304 enchantments = class_1890.method_57532(stack);
               Object2IntMap<class_6880<class_1887>> enchantmentsToShow = new Object2IntOpenHashMap();

               for(class_6880<class_1887> enchantment : enchantments.method_57534()) {
                  Set var10001 = this.shownEnchantments.get();
                  Objects.requireNonNull(var10001);
                  if (enchantment.method_40224(var10001::contains)) {
                     enchantmentsToShow.put(enchantment, enchantments.method_57536(enchantment));
                  }
               }

               double aW = this.itemWidths[i];
               double enchantY = (double)0.0F;
               double var83;
               switch (((Position)this.enchantPos.get()).ordinal()) {
                  case 0 -> var83 = -((double)(enchantmentsToShow.size() + 1) * text.getHeight(shadow));
                  case 1 -> var83 = (itemsHeight - (double)enchantmentsToShow.size() * text.getHeight(shadow)) / (double)2.0F;
                  default -> throw new MatchException((String)null, (Throwable)null);
               }

               double addY = var83;

               for(ObjectIterator var65 = Object2IntMaps.fastIterable(enchantmentsToShow).iterator(); var65.hasNext(); enchantY += text.getHeight(shadow)) {
                  Object2IntMap.Entry<class_6880<class_1887>> entry = (Object2IntMap.Entry)var65.next();
                  String var84 = Utils.getEnchantSimpleName((class_6880)entry.getKey(), (Integer)this.enchantLength.get());
                  String enchantName = var84 + " " + entry.getIntValue();
                  Color enchantColor = this.WHITE;
                  if (((class_6880)entry.getKey()).method_40220(class_9636.field_51551)) {
                     enchantColor = this.RED;
                  }

                  double var85;
                  switch (((Position)this.enchantPos.get()).ordinal()) {
                     case 0 -> var85 = x + aW / (double)2.0F - text.getWidth(enchantName, shadow) / (double)2.0F;
                     case 1 -> var85 = x + (aW - text.getWidth(enchantName, shadow)) / (double)2.0F;
                     default -> throw new MatchException((String)null, (Throwable)null);
                  }

                  double enchantX = var85;
                  text.render(enchantName, enchantX, y + addY + enchantY, enchantColor, shadow);
               }

               text.end();
            }

            x += this.itemWidths[i];
         }
      } else if ((Boolean)this.displayEnchants.get()) {
         this.displayEnchants.set(false);
      }

      NametagUtils.end(event.drawContext);
   }

   private void renderNametagItem(class_1799 stack, boolean shadow) {
      if (!stack.method_7960()) {
         TextRenderer text = TextRenderer.get();
         NametagUtils.begin(this.pos);
         String name = Names.get(stack);
         String count = " x" + stack.method_7947();
         double nameWidth = text.getWidth(name, shadow);
         double countWidth = text.getWidth(count, shadow);
         double heightDown = text.getHeight(shadow);
         double width = nameWidth;
         if ((Boolean)this.itemCount.get()) {
            width = nameWidth + countWidth;
         }

         double widthHalf = width / (double)2.0F;
         this.drawBg(-widthHalf, -heightDown, width, heightDown);
         text.beginBig();
         double hX = -widthHalf;
         double hY = -heightDown;
         hX = text.render(name, hX, hY, this.nameColor.get(), shadow);
         if ((Boolean)this.itemCount.get()) {
            text.render(count, hX, hY, this.GOLD, shadow);
         }

         text.end();
         NametagUtils.end();
      }
   }

   private void renderGenericLivingNametag(class_1309 entity, boolean shadow) {
      TextRenderer text = TextRenderer.get();
      NametagUtils.begin(this.pos);
      String nameText = entity.method_5864().method_5897().getString();
      nameText = nameText + " ";
      float absorption = entity.method_6067();
      int health = Math.round(entity.method_6032() + absorption);
      double healthPercentage = (double)((float)health / (entity.method_6063() + absorption));
      String healthText = String.valueOf(health);
      Color healthColor;
      if (healthPercentage <= 0.333) {
         healthColor = this.RED;
      } else if (healthPercentage <= 0.666) {
         healthColor = this.AMBER;
      } else {
         healthColor = this.GREEN;
      }

      double nameWidth = text.getWidth(nameText, shadow);
      double healthWidth = text.getWidth(healthText, shadow);
      double heightDown = text.getHeight(shadow);
      double width = nameWidth + healthWidth;
      double widthHalf = width / (double)2.0F;
      this.drawBg(-widthHalf, -heightDown, width, heightDown);
      text.beginBig();
      double hX = -widthHalf;
      double hY = -heightDown;
      hX = text.render(nameText, hX, hY, this.nameColor.get(), shadow);
      text.render(healthText, hX, hY, healthColor, shadow);
      text.end();
      NametagUtils.end();
   }

   private void renderGenericNametag(class_1297 entity, boolean shadow) {
      TextRenderer text = TextRenderer.get();
      NametagUtils.begin(this.pos);
      String nameText = entity.method_5864().method_5897().getString();
      double nameWidth = text.getWidth(nameText, shadow);
      double heightDown = text.getHeight(shadow);
      double widthHalf = nameWidth / (double)2.0F;
      this.drawBg(-widthHalf, -heightDown, nameWidth, heightDown);
      text.beginBig();
      double hX = -widthHalf;
      double hY = -heightDown;
      text.render(nameText, hX, hY, this.nameColor.get(), shadow);
      text.end();
      NametagUtils.end();
   }

   private void renderTntNametag(String fuseText, boolean shadow) {
      TextRenderer text = TextRenderer.get();
      NametagUtils.begin(this.pos);
      double width = text.getWidth(fuseText, shadow);
      double heightDown = text.getHeight(shadow);
      double widthHalf = width / (double)2.0F;
      this.drawBg(-widthHalf, -heightDown, width, heightDown);
      text.beginBig();
      double hX = -widthHalf;
      double hY = -heightDown;
      text.render(fuseText, hX, hY, this.nameColor.get(), shadow);
      text.end();
      NametagUtils.end();
   }

   private class_1799 getItem(class_1657 entity, int index) {
      class_1799 var10000;
      switch (index) {
         case 0 -> var10000 = entity.method_6047();
         case 1 -> var10000 = entity.method_6118(class_1304.field_6169);
         case 2 -> var10000 = entity.method_6118(class_1304.field_6174);
         case 3 -> var10000 = entity.method_6118(class_1304.field_6172);
         case 4 -> var10000 = entity.method_6118(class_1304.field_6166);
         case 5 -> var10000 = entity.method_6079();
         default -> var10000 = class_1799.field_8037;
      }

      return var10000;
   }

   private void drawBg(double x, double y, double width, double height) {
      Renderer2D.COLOR.begin();
      Renderer2D.COLOR.quad(x - (double)1.0F, y - (double)1.0F, width + (double)2.0F, height + (double)2.0F, this.background.get());
      Renderer2D.COLOR.render();
   }

   public boolean excludeBots() {
      return (Boolean)this.ignoreBots.get();
   }

   public boolean playerNametags() {
      return this.isActive() && ((Set)this.entities.get()).contains(class_1299.field_6097);
   }

   public static enum Position {
      Above,
      OnTop;

      // $FF: synthetic method
      private static Position[] $values() {
         return new Position[]{Above, OnTop};
      }
   }

   public static enum Durability {
      None,
      Total,
      Percentage;

      // $FF: synthetic method
      private static Durability[] $values() {
         return new Durability[]{None, Total, Percentage};
      }
   }

   public static enum DistanceColorMode {
      Gradient,
      Flat;

      // $FF: synthetic method
      private static DistanceColorMode[] $values() {
         return new DistanceColorMode[]{Gradient, Flat};
      }
   }
}
