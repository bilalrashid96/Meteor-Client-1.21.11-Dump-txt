package meteordevelopment.meteorclient.systems.modules.render;

import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import meteordevelopment.meteorclient.events.game.ItemStackTooltipEvent;
import meteordevelopment.meteorclient.events.render.TooltipDataEvent;
import meteordevelopment.meteorclient.gui.screens.ContainerInventoryScreen;
import meteordevelopment.meteorclient.mixin.EntityAccessor;
import meteordevelopment.meteorclient.mixin.EntityBucketItemAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.ByteCountDataOutput;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.player.EChestMemory;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.tooltip.BannerTooltipComponent;
import meteordevelopment.meteorclient.utils.tooltip.BookTooltipComponent;
import meteordevelopment.meteorclient.utils.tooltip.BundleTooltipComponent;
import meteordevelopment.meteorclient.utils.tooltip.ContainerTooltipComponent;
import meteordevelopment.meteorclient.utils.tooltip.EntityTooltipComponent;
import meteordevelopment.meteorclient.utils.tooltip.MapTooltipComponent;
import meteordevelopment.meteorclient.utils.tooltip.TextTooltipComponent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_10124;
import net.minecraft.class_10132;
import net.minecraft.class_11907;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_1291;
import net.minecraft.class_1292;
import net.minecraft.class_1293;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1746;
import net.minecraft.class_1767;
import net.minecraft.class_1785;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2509;
import net.minecraft.class_2520;
import net.minecraft.class_2561;
import net.minecraft.class_3730;
import net.minecraft.class_3872;
import net.minecraft.class_4174;
import net.minecraft.class_465;
import net.minecraft.class_5250;
import net.minecraft.class_5537;
import net.minecraft.class_5632;
import net.minecraft.class_5761;
import net.minecraft.class_6862;
import net.minecraft.class_7924;
import net.minecraft.class_9209;
import net.minecraft.class_9262;
import net.minecraft.class_9276;
import net.minecraft.class_9279;
import net.minecraft.class_9298;
import net.minecraft.class_9301;
import net.minecraft.class_9302;
import net.minecraft.class_9307;
import net.minecraft.class_9334;
import net.minecraft.class_3872.class_3931;

public class BetterTooltips extends Module {
   public static final Color ECHEST_COLOR = new Color(0, 50, 50);
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPreviews;
   private final SettingGroup sgOther;
   private final SettingGroup sgHideFlags;
   private final Setting<DisplayWhen> displayWhen;
   private final Setting<Keybind> keybind;
   private final Setting<Boolean> openContents;
   private final Setting<Keybind> openContentsKey;
   private final Setting<Boolean> pauseInCreative;
   private final Setting<Boolean> shulkers;
   private final Setting<Boolean> shulkerCompactTooltip;
   private final Setting<Boolean> echest;
   private final Setting<Boolean> maps;
   public final Setting<Double> mapsScale;
   private final Setting<Boolean> books;
   private final Setting<Boolean> banners;
   private final Setting<Boolean> entitiesInBuckets;
   private final Setting<Boolean> bundles;
   private final Setting<Boolean> foodInfo;
   public final Setting<Boolean> byteSize;
   private final Setting<SortSize> sizeType;
   private final Setting<Boolean> statusEffects;
   public final Setting<Boolean> tooltip;
   public final Setting<Boolean> additional;
   private boolean updateTooltips;
   private static final class_1799[] PREVIEW = new class_1799[27];
   private static final class_1799[] PEEK_SCREEN = new class_1799[27];

   public BetterTooltips() {
      super(Categories.Render, "better-tooltips", "Displays more useful tooltips for certain items.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPreviews = this.settings.createGroup("Previews");
      this.sgOther = this.settings.createGroup("Other");
      this.sgHideFlags = this.settings.createGroup("Hide Flags");
      this.displayWhen = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("display-when")).description("When to display previews.")).defaultValue(BetterTooltips.DisplayWhen.Keybind)).onChanged((value) -> this.updateTooltips = true)).build());
      this.keybind = this.sgGeneral.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("keybind")).description("The bind for keybind mode.")).defaultValue(Keybind.fromKey(342))).visible(() -> this.displayWhen.get() == BetterTooltips.DisplayWhen.Keybind)).onChanged((value) -> this.updateTooltips = true)).build());
      this.openContents = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("open-contents")).description("Opens a GUI window with the inventory of the storage block or book when you click the item.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      KeybindSetting.Builder var10002 = (KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("keybind")).description("Key to open contents (containers, books, etc.) when pressed on items.")).defaultValue(Keybind.fromButton(2));
      Setting var10003 = this.openContents;
      Objects.requireNonNull(var10003);
      this.openContentsKey = var10001.add(((KeybindSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      BoolSetting.Builder var4 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-in-creative")).description("Pauses middle click open while the player is in creative mode.")).defaultValue(true);
      var10003 = this.openContents;
      Objects.requireNonNull(var10003);
      this.pauseInCreative = var10001.add(((BoolSetting.Builder)var4.visible(var10003::get)).build());
      this.shulkers = this.sgPreviews.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("containers")).description("Shows a preview of a containers when hovering over it in an inventory.")).defaultValue(true)).onChanged((value) -> this.updateTooltips = true)).build());
      this.shulkerCompactTooltip = this.sgPreviews.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("compact-shulker-tooltip")).description("Compacts the lines of the shulker tooltip.")).defaultValue(true)).build());
      this.echest = this.sgPreviews.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("echests")).description("Shows a preview of your echest when hovering over it in an inventory.")).defaultValue(true)).onChanged((value) -> this.updateTooltips = true)).build());
      this.maps = this.sgPreviews.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("maps")).description("Shows a preview of a map when hovering over it in an inventory.")).defaultValue(true)).onChanged((value) -> this.updateTooltips = true)).build());
      var10001 = this.sgPreviews;
      DoubleSetting.Builder var5 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("map-scale")).description("The scale of the map preview.")).defaultValue((double)1.0F).min(0.001).sliderMax((double)1.0F);
      var10003 = this.maps;
      Objects.requireNonNull(var10003);
      this.mapsScale = var10001.add(((DoubleSetting.Builder)var5.visible(var10003::get)).build());
      this.books = this.sgPreviews.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("books")).description("Shows contents of a book when hovering over it in an inventory.")).defaultValue(true)).onChanged((value) -> this.updateTooltips = true)).build());
      this.banners = this.sgPreviews.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("banners")).description("Shows banners' patterns when hovering over it in an inventory. Also works with shields.")).defaultValue(true)).onChanged((value) -> this.updateTooltips = true)).build());
      this.entitiesInBuckets = this.sgPreviews.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("entities-in-buckets")).description("Shows entities in buckets when hovering over it in an inventory.")).defaultValue(true)).onChanged((value) -> this.updateTooltips = true)).build());
      this.bundles = this.sgPreviews.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("bundles")).description("Shows a preview of bundle contents when hovering over it in an inventory.")).defaultValue(true)).onChanged((value) -> this.updateTooltips = true)).build());
      this.foodInfo = this.sgPreviews.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("food-info")).description("Shows hunger and saturation values for food items.")).defaultValue(true)).onChanged((value) -> this.updateTooltips = true)).build());
      this.byteSize = this.sgOther.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("byte-size")).description("Displays an item's size in bytes in the tooltip.")).defaultValue(true)).onChanged((value) -> this.updateTooltips = true)).build());
      var10001 = this.sgOther;
      EnumSetting.Builder var6 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("byte-size-format")).description("The format by which to display the item's byte size.")).defaultValue(BetterTooltips.SortSize.Dynamic);
      var10003 = this.byteSize;
      Objects.requireNonNull(var10003);
      this.sizeType = var10001.add(((EnumSetting.Builder)var6.visible(var10003::get)).build());
      this.statusEffects = this.sgOther.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("status-effects")).description("Adds list of status effects to tooltips of food items.")).defaultValue(true)).onChanged((value) -> this.updateTooltips = true)).build());
      this.tooltip = this.sgHideFlags.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("tooltip")).description("Show the tooltip when it's hidden.")).defaultValue(false)).build());
      this.additional = this.sgHideFlags.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("tooltip-components")).description("Shows tooltip components when they're hidden - e.g. enchantments, attributes, lore, etc.")).defaultValue(false)).build());
      this.updateTooltips = false;
   }

   @EventHandler
   private void appendTooltip(ItemStackTooltipEvent event) {
      if (!(Boolean)this.tooltip.get() && event.list().isEmpty()) {
         this.appendPreviewTooltipText(event, false);
      } else {
         if ((Boolean)this.statusEffects.get()) {
            if (event.itemStack().method_7909() == class_1802.field_8766) {
               class_9298 stewEffectsComponent = (class_9298)event.itemStack().method_58694(class_9334.field_49652);
               if (stewEffectsComponent != null) {
                  for(class_9298.class_8751 effectTag : stewEffectsComponent.comp_2416()) {
                     class_1293 effect = new class_1293(effectTag.comp_1838(), effectTag.comp_1839(), 0);
                     event.appendStart(this.getStatusText(effect));
                  }
               }
            } else {
               class_10124 consumable = (class_10124)event.itemStack().method_58694(class_9334.field_53964);
               if (consumable != null) {
                  Stream var10000 = consumable.comp_3089().stream();
                  Objects.requireNonNull(class_10132.class);
                  var10000 = var10000.filter(class_10132.class::isInstance);
                  Objects.requireNonNull(class_10132.class);
                  var10000.map(class_10132.class::cast).flatMap((apply) -> apply.comp_3094().stream()).forEach((effectx) -> event.appendStart(this.getStatusText(effectx)));
               }
            }
         }

         if ((Boolean)this.foodInfo.get() && event.itemStack().method_57826(class_9334.field_50075)) {
            class_4174 food = (class_4174)event.itemStack().method_58694(class_9334.field_50075);
            event.appendStart(class_2561.method_43470(String.format("\ud83c\udf56 %d (\ud83d\udc9b %.1f)", food.comp_2491(), food.comp_2492())).method_27692(class_124.field_1080));
         }

         if ((Boolean)this.byteSize.get()) {
            DataResult var16 = class_1799.field_24671.encodeStart(this.mc.field_1724.method_56673().method_57093(class_2509.field_11560), event.itemStack());
            Objects.requireNonNull(var16);
            DataResult var10 = var16;
            byte var11 = 0;
            //$FF: var11->value
            //0->com/mojang/serialization/DataResult$Success
            //1->com/mojang/serialization/DataResult$Error
            switch (var10.typeSwitch<invokedynamic>(var10, var11)) {
               case 0:
                  DataResult.Success<class_2520> success = (DataResult.Success)var10;

                  try {
                     ((class_2520)success.value()).method_10713(ByteCountDataOutput.INSTANCE);
                     int byteCount = ByteCountDataOutput.INSTANCE.getCount();
                     String var17;
                     switch (((SortSize)this.sizeType.get()).ordinal()) {
                        case 0 -> var17 = String.format("%d bytes", byteCount);
                        case 1 -> var17 = String.format("%.2f kB", (float)byteCount / 1024.0F);
                        case 2 -> var17 = String.format("%.4f MB", (float)byteCount / 1048576.0F);
                        case 3 -> var17 = byteCount >= 1048576 ? String.format("%.2f MB", (float)byteCount / 1048576.0F) : (byteCount >= 1024 ? String.format("%.2f kB", (float)byteCount / 1024.0F) : String.format("%d bytes", byteCount));
                        default -> throw new MatchException((String)null, (Throwable)null);
                     }

                     String count = var17;
                     ByteCountDataOutput.INSTANCE.reset();
                     event.appendEnd(class_2561.method_43470(count).method_27692(class_124.field_1063));
                  } catch (Exception var7) {
                     event.appendEnd(class_2561.method_43470("Error getting bytes.").method_27692(class_124.field_1061));
                  }
                  break;
               case 1:
                  DataResult.Error<class_2520> ignored = (DataResult.Error)var10;
                  event.appendEnd(class_2561.method_43470("Error getting bytes.").method_27692(class_124.field_1061));
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         }

         this.appendPreviewTooltipText(event, true);
      }
   }

   @EventHandler
   private void getTooltipData(TooltipDataEvent event) {
      if (this.previewShulkers() && Utils.hasItems(event.itemStack)) {
         Utils.getItemsInContainerItem(event.itemStack, PREVIEW);
         event.tooltipData = new ContainerTooltipComponent(PREVIEW, Utils.getShulkerColor(event.itemStack));
      } else if (event.itemStack.method_7909() == class_1802.field_8466 && this.previewEChest()) {
         event.tooltipData = (class_5632)(EChestMemory.isKnown() ? new ContainerTooltipComponent((class_1799[])EChestMemory.ITEMS.toArray(new class_1799[27]), ECHEST_COLOR) : new TextTooltipComponent(class_2561.method_43470("Unknown inventory.").method_27692(class_124.field_1079)));
      } else if (event.itemStack.method_7909() == class_1802.field_8204 && this.previewMaps()) {
         class_9209 mapIdComponent = (class_9209)event.itemStack.method_58694(class_9334.field_49646);
         if (mapIdComponent != null) {
            event.tooltipData = new MapTooltipComponent(mapIdComponent.comp_2315());
         }
      } else if ((event.itemStack.method_7909() == class_1802.field_8674 || event.itemStack.method_7909() == class_1802.field_8360) && this.previewBooks()) {
         class_2561 page = this.getFirstPage(event.itemStack);
         if (page != null) {
            int pageCount = this.getBookPageCount(event.itemStack);
            class_2561 pageWithCount = page.method_27661().method_10852(class_2561.method_43470(String.format(" (%d pages)", pageCount)).method_27692(class_124.field_1080));
            event.tooltipData = new BookTooltipComponent(pageWithCount);
         }
      } else if (event.itemStack.method_7909() instanceof class_1746 && this.previewBanners()) {
         event.tooltipData = new BannerTooltipComponent(event.itemStack);
      } else if (event.itemStack.method_57826(class_9334.field_56398) && this.previewBanners()) {
         event.tooltipData = this.createBannerFromBannerPatternItem(event.itemStack);
      } else if (event.itemStack.method_7909() == class_1802.field_8255 && this.previewBanners()) {
         if (!((class_9307)event.itemStack.method_58695(class_9334.field_49619, class_9307.field_49404)).comp_2428().isEmpty()) {
            event.tooltipData = this.createBannerFromShield(event.itemStack);
         }
      } else {
         class_1792 mapIdComponent = event.itemStack.method_7909();
         if (mapIdComponent instanceof class_1785) {
            class_1785 bucketItem = (class_1785)mapIdComponent;
            if (this.previewEntities()) {
               class_1299<?> type = ((EntityBucketItemAccessor)bucketItem).meteor$getEntityType();
               class_1309 entity = (class_1309)type.method_5883(this.mc.field_1687, class_3730.field_16459);
               if (entity != null) {
                  class_9279 nbtComponent = (class_9279)event.itemStack.method_58695(class_9334.field_49610, (Object)null);
                  if (nbtComponent == null) {
                     return;
                  }

                  entity.method_66652(event.itemStack);
                  ((class_5761)entity).method_35170(nbtComponent.method_57461());
                  ((EntityAccessor)entity).meteor$setInWater(true);
                  event.tooltipData = new EntityTooltipComponent(entity);
               }

               return;
            }
         }

         if (event.itemStack.method_7909() instanceof class_5537 && this.previewBundles() && event.itemStack.method_57826(class_9334.field_49650)) {
            class_9276 bundleContents = (class_9276)event.itemStack.method_58694(class_9334.field_49650);
            if (bundleContents != null && !bundleContents.method_57429()) {
               class_1799[] bundleItems = new class_1799[bundleContents.method_57426()];
               int index = 0;

               for(class_1799 stack : bundleContents.method_57421()) {
                  bundleItems[index++] = stack;
               }

               event.tooltipData = new BundleTooltipComponent(bundleItems, bundleContents);
            }
         }
      }

   }

   public void applyCompactShulkerTooltip(List<class_1799> stacks, Consumer<class_2561> textConsumer) {
      Object2IntMap<class_1792> counts = new Object2IntOpenHashMap();

      for(class_1799 item : stacks) {
         if (!item.method_7960()) {
            int count = counts.getInt(item.method_7909());
            counts.put(item.method_7909(), count + item.method_7947());
         }
      }

      counts.keySet().stream().sorted(Comparator.comparingInt((value) -> -counts.getInt(value))).limit(5L).forEach((itemx) -> {
         class_5250 mutableText = itemx.method_63680().method_27662();
         mutableText.method_10852(class_2561.method_43470(" x").method_27693(String.valueOf(counts.getInt(itemx))).method_27692(class_124.field_1080));
         textConsumer.accept(mutableText);
      });
      if (counts.size() > 5) {
         textConsumer.accept(class_2561.method_43469("container.shulkerBox.more", new Object[]{counts.size() - 5}).method_27692(class_124.field_1056));
      }

   }

   private void appendPreviewTooltipText(ItemStackTooltipEvent event, boolean spacer) {
      boolean showPreviewText = !this.isPressed() && ((Boolean)this.shulkers.get() && Utils.hasItems(event.itemStack()) || event.itemStack().method_7909() == class_1802.field_8466 && (Boolean)this.echest.get() || event.itemStack().method_7909() == class_1802.field_8204 && (Boolean)this.maps.get() || event.itemStack().method_7909() == class_1802.field_8674 && (Boolean)this.books.get() || event.itemStack().method_7909() == class_1802.field_8360 && (Boolean)this.books.get() || event.itemStack().method_7909() instanceof class_1785 && (Boolean)this.entitiesInBuckets.get() || event.itemStack().method_7909() instanceof class_5537 && (Boolean)this.bundles.get() || event.itemStack().method_7909() instanceof class_1746 && (Boolean)this.banners.get() || event.itemStack().method_57826(class_9334.field_56398) && (Boolean)this.banners.get() || event.itemStack().method_7909() == class_1802.field_8255 && (Boolean)this.banners.get());
      if (showPreviewText) {
         if (spacer) {
            event.appendEnd(class_2561.method_43470(""));
         }

         String var10001 = String.valueOf(class_124.field_1054);
         event.appendEnd(class_2561.method_43470("Hold " + var10001 + String.valueOf(this.keybind) + String.valueOf(class_124.field_1070) + " to preview"));
      }

   }

   private class_5250 getStatusText(class_1293 effect) {
      class_5250 text = class_2561.method_43471(effect.method_5586());
      if (effect.method_5578() != 0) {
         text.method_27693(String.format(" %d (%s)", effect.method_5578() + 1, class_1292.method_5577(effect, 1.0F, this.mc.field_1687.method_54719().method_54748()).getString()));
      } else {
         text.method_27693(String.format(" (%s)", class_1292.method_5577(effect, 1.0F, this.mc.field_1687.method_54719().method_54748()).getString()));
      }

      return ((class_1291)effect.method_5579().comp_349()).method_5573() ? text.method_27692(class_124.field_1078) : text.method_27692(class_124.field_1061);
   }

   private class_2561 getFirstPage(class_1799 bookItem) {
      if (bookItem.method_58694(class_9334.field_49653) != null) {
         List<class_9262<String>> pages = ((class_9301)bookItem.method_58694(class_9334.field_49653)).comp_2422();
         return pages.isEmpty() ? null : class_2561.method_43470((String)((class_9262)pages.getFirst()).method_57140(false));
      } else if (bookItem.method_58694(class_9334.field_49606) != null) {
         List<class_9262<class_2561>> pages = ((class_9302)bookItem.method_58694(class_9334.field_49606)).comp_2422();
         return pages.isEmpty() ? null : (class_2561)((class_9262)pages.getFirst()).method_57140(false);
      } else {
         return null;
      }
   }

   private int getBookPageCount(class_1799 bookItem) {
      if (bookItem.method_58694(class_9334.field_49653) != null) {
         return ((class_9301)bookItem.method_58694(class_9334.field_49653)).comp_2422().size();
      } else {
         return bookItem.method_58694(class_9334.field_49606) != null ? ((class_9302)bookItem.method_58694(class_9334.field_49606)).comp_2422().size() : 0;
      }
   }

   private BannerTooltipComponent createBannerFromBannerPatternItem(class_1799 item) {
      class_9307 component = (new class_9307.class_3750()).method_16376(this.mc.field_1724.method_56673().method_30530(class_7924.field_41252).method_46735((class_6862)item.method_58694(class_9334.field_56398)).method_40240(0), class_1767.field_7952).method_57573();
      return new BannerTooltipComponent(class_1767.field_7944, component);
   }

   private BannerTooltipComponent createBannerFromShield(class_1799 shieldItem) {
      class_1767 dyeColor2 = (class_1767)shieldItem.method_58695(class_9334.field_49620, class_1767.field_7952);
      class_9307 bannerPatternsComponent = (class_9307)shieldItem.method_58695(class_9334.field_49619, class_9307.field_49404);
      return new BannerTooltipComponent(dyeColor2, bannerPatternsComponent);
   }

   public boolean openContents() {
      return this.isActive() && (Boolean)this.openContents.get() && (!(Boolean)this.pauseInCreative.get() || !this.mc.field_1724.method_56992());
   }

   public boolean shouldOpenContents(class_11907 input) {
      if (input instanceof class_11909 click) {
         return this.openContents() && ((Keybind)this.openContentsKey.get()).matches(click.comp_4800());
      } else if (!(input instanceof class_11908 keyInput)) {
         return false;
      } else {
         return this.openContents() && ((Keybind)this.openContentsKey.get()).matches(keyInput);
      }
   }

   public boolean openContent(class_1799 itemStack) {
      if (this.openContents() && !itemStack.method_7960()) {
         if (itemStack.method_7909() instanceof class_5537) {
            if (this.mc.field_1755 instanceof class_465) {
               this.mc.field_1755.method_25419();
            }

            this.mc.method_1507(new ContainerInventoryScreen(itemStack));
            return true;
         } else if (!Utils.hasItems(itemStack) && itemStack.method_7909() != class_1802.field_8466) {
            if (itemStack.method_7909() != class_1802.field_8674 && itemStack.method_7909() != class_1802.field_8360) {
               return false;
            } else {
               if (this.mc.field_1755 instanceof class_465) {
                  this.mc.field_1755.method_25419();
               }

               this.mc.method_1507(new class_3872(class_3931.method_17562(itemStack)));
               return true;
            }
         } else {
            Utils.openContainer(itemStack, PEEK_SCREEN, false);
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean previewShulkers() {
      return this.isActive() && this.isPressed() && (Boolean)this.shulkers.get();
   }

   public boolean shulkerCompactTooltip() {
      return this.isActive() && (Boolean)this.shulkerCompactTooltip.get();
   }

   private boolean previewEChest() {
      return this.isPressed() && (Boolean)this.echest.get();
   }

   private boolean previewMaps() {
      return this.isPressed() && (Boolean)this.maps.get();
   }

   private boolean previewBooks() {
      return this.isPressed() && (Boolean)this.books.get();
   }

   private boolean previewBanners() {
      return this.isPressed() && (Boolean)this.banners.get();
   }

   private boolean previewEntities() {
      return this.isPressed() && (Boolean)this.entitiesInBuckets.get();
   }

   public boolean previewBundles() {
      return this.isPressed() && (Boolean)this.bundles.get();
   }

   private boolean isPressed() {
      return ((Keybind)this.keybind.get()).isPressed() && this.displayWhen.get() == BetterTooltips.DisplayWhen.Keybind || this.displayWhen.get() == BetterTooltips.DisplayWhen.Always;
   }

   public boolean updateTooltips() {
      if (this.updateTooltips && this.isActive()) {
         this.updateTooltips = false;
         return true;
      } else {
         return false;
      }
   }

   public static enum DisplayWhen {
      Keybind,
      Always;

      // $FF: synthetic method
      private static DisplayWhen[] $values() {
         return new DisplayWhen[]{Keybind, Always};
      }
   }

   public static enum SortSize {
      Bytes,
      Kilobytes,
      Megabytes,
      Dynamic;

      // $FF: synthetic method
      private static SortSize[] $values() {
         return new SortSize[]{Bytes, Kilobytes, Megabytes, Dynamic};
      }
   }
}
