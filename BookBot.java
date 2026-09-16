package meteordevelopment.meteorclient.systems.modules.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2487;
import net.minecraft.class_2558;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2820;
import net.minecraft.class_5250;
import net.minecraft.class_5348;
import net.minecraft.class_9262;
import net.minecraft.class_9301;
import net.minecraft.class_9302;
import net.minecraft.class_9334;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class BookBot extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Mode> mode;
   private final Setting<RandomType> randomType;
   private final Setting<Integer> pages;
   private final Setting<Integer> characters;
   private final Setting<Integer> delay;
   private final Setting<Boolean> sign;
   private final Setting<String> name;
   private final Setting<Boolean> count;
   private final Setting<Boolean> wordWrap;
   private File file;
   private final PointerBuffer filters;
   private int delayTimer;
   private int bookCount;
   private Random random;

   public BookBot() {
      super(Categories.Misc, "book-bot", "Automatically writes in books.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("What kind of text to write.")).defaultValue(BookBot.Mode.Random)).build());
      this.randomType = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("random-type")).description("What kind of random to use.")).defaultValue(BookBot.RandomType.Utf8)).visible(() -> this.mode.get() == BookBot.Mode.Random)).build());
      this.pages = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("pages")).description("The number of pages to write per book.")).defaultValue(50)).range(1, 100).sliderRange(1, 100).visible(() -> this.mode.get() != BookBot.Mode.File && this.randomType.get() != BookBot.RandomType.PaperMC)).build());
      this.characters = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("characters")).description("How many characters to write per page.")).defaultValue(128)).range(1, 1024).sliderRange(1, 1024).visible(() -> this.mode.get() == BookBot.Mode.Random && this.randomType.get() != BookBot.RandomType.PaperMC)).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("The amount of delay between writing books.")).defaultValue(20)).min(1).sliderRange(1, 200).build());
      this.sign = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sign")).description("Whether to sign the book.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      StringSetting.Builder var10002 = (StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("name")).description("The name you want to give your books.")).defaultValue("Meteor on Crack!");
      Setting var10003 = this.sign;
      Objects.requireNonNull(var10003);
      this.name = var10001.add(((StringSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      BoolSetting.Builder var3 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("append-count")).description("Whether to append the number of the book to the title.")).defaultValue(true);
      var10003 = this.sign;
      Objects.requireNonNull(var10003);
      this.count = var10001.add(((BoolSetting.Builder)var3.visible(var10003::get)).build());
      this.wordWrap = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("word-wrap")).description("Prevents words from being cut in the middle of lines.")).defaultValue(true)).visible(() -> this.mode.get() == BookBot.Mode.File)).build());
      this.file = new File(MeteorClient.FOLDER, "bookbot.txt");
      if (!this.file.exists()) {
         this.file = null;
      }

      this.filters = BufferUtils.createPointerBuffer(1);
      ByteBuffer txtFilter = MemoryUtil.memASCII("*.txt");
      this.filters.put(txtFilter);
      this.filters.rewind();
   }

   public WWidget getWidget(GuiTheme theme) {
      WHorizontalList list = theme.horizontalList();
      WButton selectFile = (WButton)list.add(theme.button("Select File")).widget();
      WLabel fileName = (WLabel)list.add(theme.label(this.file != null && this.file.exists() ? this.file.getName() : "No file selected.")).widget();
      selectFile.action = () -> {
         String path = TinyFileDialogs.tinyfd_openFileDialog("Select File", (new File(MeteorClient.FOLDER, "bookbot.txt")).getAbsolutePath(), this.filters, (CharSequence)null, false);
         if (path != null) {
            this.file = new File(path);
            fileName.set(this.file.getName());
         }

      };
      return list;
   }

   public void onActivate() {
      if ((this.file == null || !this.file.exists()) && this.mode.get() == BookBot.Mode.File) {
         this.info("No file selected, please select a file in the GUI.", new Object[0]);
         this.toggle();
      } else {
         this.random = new Random();
         this.delayTimer = (Integer)this.delay.get();
         this.bookCount = 0;
      }
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      Predicate<class_1799> bookPredicate = (i) -> {
         class_9301 component = (class_9301)i.method_58694(class_9334.field_49653);
         return i.method_7909() == class_1802.field_8674 && (component == null || component.comp_2422().isEmpty());
      };
      FindItemResult writableBook = InvUtils.find(bookPredicate);
      if (!writableBook.found()) {
         this.toggle();
      } else if (!InvUtils.testInMainHand(bookPredicate)) {
         InvUtils.move().from(writableBook.slot()).toHotbar(this.mc.field_1724.method_31548().method_67532());
      } else if (this.delayTimer > 0) {
         --this.delayTimer;
      } else {
         this.delayTimer = (Integer)this.delay.get();
         if (this.mode.get() == BookBot.Mode.Random) {
            switch (((RandomType)this.randomType.get()).ordinal()) {
               case 0 -> this.writeBook(this.random.ints(33, 128).filter((i) -> !Character.isWhitespace(i) && i != 13 && i != 10).iterator());
               case 1 -> this.writeBook(this.random.ints(33, 55296).filter((i) -> !Character.isWhitespace(i) && i != 13 && i != 10).iterator());
               case 2 -> this.writePaperMcBook();
            }
         } else if (this.mode.get() == BookBot.Mode.File) {
            if ((this.file == null || !this.file.exists()) && this.mode.get() == BookBot.Mode.File) {
               this.info("No file selected, please select a file in the GUI.", new Object[0]);
               this.toggle();
               return;
            }

            if (this.file.length() == 0L) {
               class_5250 message = class_2561.method_43470("");
               message.method_10852(class_2561.method_43470("The bookbot file is empty! ").method_27692(class_124.field_1061));
               message.method_10852(class_2561.method_43470("Click here to edit it.").method_10862(class_2583.field_24360.method_27705(new class_124[]{class_124.field_1073, class_124.field_1061}).method_10958(new class_2558.class_10607(this.file.getAbsolutePath()))));
               this.info(message);
               this.toggle();
               return;
            }

            try {
               BufferedReader reader = new BufferedReader(new FileReader(this.file));

               try {
                  StringBuilder file = new StringBuilder();

                  String line;
                  while((line = reader.readLine()) != null) {
                     file.append(line).append('\n');
                  }

                  reader.close();
                  this.writeBook(file.toString().chars().iterator());
               } catch (Throwable var8) {
                  try {
                     reader.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }

                  throw var8;
               }

               reader.close();
            } catch (IOException var9) {
               this.error("Failed to read the file.", new Object[0]);
            }
         }

      }
   }

   private void writeBook(PrimitiveIterator.OfInt chars) {
      ArrayList<String> pages = new ArrayList();
      ArrayList<class_9262<class_2561>> filteredPages = new ArrayList();
      int maxPages = this.mode.get() == BookBot.Mode.File ? 100 : (Integer)this.pages.get();
      if ((Boolean)this.wordWrap.get() && this.mode.get() == BookBot.Mode.File) {
         StringBuilder text = new StringBuilder();

         while(chars.hasNext()) {
            text.appendCodePoint(chars.nextInt());
         }

         List<class_5348> wrappedLines = this.mc.field_1772.method_72101(class_2561.method_43470(text.toString()), 114);
         this.processLinesToPages(wrappedLines, pages, filteredPages, maxPages);
      } else {
         int pageIndex = 0;

         for(StringBuilder page = new StringBuilder(); pageIndex != maxPages; ++pageIndex) {
            for(int i = 0; i < (Integer)this.characters.get() && chars.hasNext(); ++i) {
               page.appendCodePoint(chars.nextInt());
            }

            if (!page.isEmpty()) {
               String builtPage = page.toString();
               filteredPages.add(class_9262.method_57137(class_2561.method_30163(builtPage)));
               pages.add(builtPage);
               page.setLength(0);
            }
         }
      }

      this.createBook(pages, filteredPages);
   }

   private void writePaperMcBook() {
      ArrayList<String> pages = new ArrayList();
      ArrayList<class_9262<class_2561>> filteredPages = new ArrayList();
      StringBuilder page = new StringBuilder();
      PrimitiveIterator.OfInt oneByte = this.random.ints(33, 128).iterator();
      PrimitiveIterator.OfInt twoBytes = this.random.ints(128, 2048).iterator();
      PrimitiveIterator.OfInt threeBytes = this.random.ints(2048, 55296).iterator();

      for(int pageIndex = 0; pageIndex < 100; ++pageIndex) {
         if (pageIndex < 50) {
            page.appendCodePoint(threeBytes.nextInt());

            for(int i = 1; i < 1024; ++i) {
               page.appendCodePoint(oneByte.nextInt());
            }
         } else if (pageIndex != 50) {
            for(int i = 0; i < 1024; ++i) {
               page.appendCodePoint(threeBytes.nextInt());
            }
         } else {
            for(int i = 0; i < 110; ++i) {
               page.appendCodePoint(threeBytes.nextInt());
            }

            page.appendCodePoint(twoBytes.nextInt());

            for(int i = 0; i < 913; ++i) {
               page.appendCodePoint(oneByte.nextInt());
            }
         }

         String builtPage = page.toString();
         filteredPages.add(class_9262.method_57137(class_2561.method_30163(builtPage)));
         pages.add(builtPage);
         page.setLength(0);
      }

      this.createBook(pages, filteredPages);
   }

   private void processLinesToPages(List<class_5348> lines, ArrayList<String> pages, ArrayList<class_9262<class_2561>> filteredPages, int maxPages) {
      int pageIndex = 0;
      int lineIndex = 0;
      StringBuilder currentPage = new StringBuilder();

      for(class_5348 line : lines) {
         String lineText = line.getString();
         if (!currentPage.isEmpty()) {
            currentPage.append('\n');
         }

         currentPage.append(lineText);
         ++lineIndex;
         if (lineIndex == 14) {
            filteredPages.add(class_9262.method_57137(class_2561.method_30163(currentPage.toString())));
            pages.add(currentPage.toString());
            currentPage.setLength(0);
            ++pageIndex;
            lineIndex = 0;
            if (pageIndex == maxPages) {
               break;
            }
         }
      }

      if (!currentPage.isEmpty() && pageIndex < maxPages) {
         filteredPages.add(class_9262.method_57137(class_2561.method_30163(currentPage.toString())));
         pages.add(currentPage.toString());
      }

   }

   private void createBook(ArrayList<String> pages, ArrayList<class_9262<class_2561>> filteredPages) {
      String title = this.name.get();
      if ((Boolean)this.count.get() && this.bookCount != 0) {
         title = title + " #" + this.bookCount;
      }

      this.mc.field_1724.method_6047().method_57379(class_9334.field_49606, new class_9302(class_9262.method_57137(title), this.mc.field_1724.method_7334().name(), 0, filteredPages, true));
      this.mc.field_1724.field_3944.method_52787(new class_2820(this.mc.field_1724.method_31548().method_67532(), pages, (Boolean)this.sign.get() ? Optional.of(title) : Optional.empty()));
      ++this.bookCount;
   }

   public class_2487 toTag() {
      class_2487 tag = super.toTag();
      if (this.file != null && this.file.exists()) {
         tag.method_10582("file", this.file.getAbsolutePath());
      }

      return tag;
   }

   public Module fromTag(class_2487 tag) {
      if (tag.method_10545("file")) {
         this.file = new File(tag.method_68564("file", ""));
      }

      return super.fromTag(tag);
   }

   public static enum Mode {
      File,
      Random;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{File, Random};
      }
   }

   public static enum RandomType {
      Ascii,
      Utf8,
      PaperMC;

      // $FF: synthetic method
      private static RandomType[] $values() {
         return new RandomType[]{Ascii, Utf8, PaperMC};
      }
   }
}
