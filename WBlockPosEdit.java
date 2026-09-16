package meteordevelopment.meteorclient.gui.widgets.input;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.InteractBlockEvent;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.marker.Marker;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2338;
import net.minecraft.class_437;
import net.minecraft.class_239.class_240;

public class WBlockPosEdit extends WHorizontalList {
   public Runnable action;
   public Runnable actionOnRelease;
   private WTextBox textBoxX;
   private WTextBox textBoxY;
   private WTextBox textBoxZ;
   private class_437 previousScreen;
   private class_2338 value;
   private class_2338 lastValue;
   private boolean clicking;

   public WBlockPosEdit(class_2338 value) {
      this.value = value;
   }

   public void init() {
      this.addTextBox();
      if (Utils.canUpdate()) {
         WButton click = (WButton)this.add(this.theme.button("Click")).expandX().widget();
         click.action = () -> {
            String sb = "Click!\nRight click to pick a new position.\nLeft click to cancel.";
            ((Marker)Modules.get().get(Marker.class)).info(sb, new Object[0]);
            this.clicking = true;
            MeteorClient.EVENT_BUS.subscribe(this);
            this.previousScreen = MeteorClient.mc.field_1755;
            MeteorClient.mc.method_1507((class_437)null);
         };
         WButton here = (WButton)this.add(this.theme.button("Set Here")).expandX().widget();
         here.action = () -> {
            this.lastValue = this.value;
            this.set(new class_2338(MeteorClient.mc.field_1724.method_24515()));
            this.newValueCheck();
            this.clear();
            this.init();
         };
      }

   }

   @EventHandler
   private void onStartBreakingBlock(StartBreakingBlockEvent event) {
      if (this.clicking) {
         this.clicking = false;
         event.cancel();
         MeteorClient.EVENT_BUS.unsubscribe(this);
         MeteorClient.mc.method_1507(this.previousScreen);
      }

   }

   @EventHandler
   private void onInteractBlock(InteractBlockEvent event) {
      if (this.clicking) {
         if (event.result.method_17783() == class_240.field_1333) {
            return;
         }

         this.lastValue = this.value;
         this.set(event.result.method_17777());
         this.newValueCheck();
         this.clear();
         this.init();
         this.clicking = false;
         event.cancel();
         MeteorClient.EVENT_BUS.unsubscribe(this);
         MeteorClient.mc.method_1507(this.previousScreen);
      }

   }

   private boolean filter(String text, char c) {
      boolean validate = true;
      boolean good;
      if (c == '-' && text.isEmpty()) {
         good = true;
         validate = false;
      } else {
         good = Character.isDigit(c);
      }

      if (good && validate) {
         try {
            Integer.parseInt(text + c);
         } catch (NumberFormatException var6) {
            good = false;
         }
      }

      return good;
   }

   public class_2338 get() {
      return this.value;
   }

   public void set(class_2338 value) {
      this.value = value;
   }

   private void addTextBox() {
      this.textBoxX = (WTextBox)this.add(this.theme.textBox(Integer.toString(this.value.method_10263()), this::filter)).minWidth((double)75.0F).widget();
      this.textBoxY = (WTextBox)this.add(this.theme.textBox(Integer.toString(this.value.method_10264()), this::filter)).minWidth((double)75.0F).widget();
      this.textBoxZ = (WTextBox)this.add(this.theme.textBox(Integer.toString(this.value.method_10260()), this::filter)).minWidth((double)75.0F).widget();
      this.textBoxX.actionOnUnfocused = () -> {
         this.lastValue = this.value;
         if (this.textBoxX.get().isEmpty()) {
            this.set(new class_2338(0, 0, 0));
         } else {
            try {
               this.set(new class_2338(Integer.parseInt(this.textBoxX.get()), this.value.method_10264(), this.value.method_10260()));
            } catch (NumberFormatException var2) {
            }
         }

         this.newValueCheck();
      };
      this.textBoxY.actionOnUnfocused = () -> {
         this.lastValue = this.value;
         if (this.textBoxY.get().isEmpty()) {
            this.set(new class_2338(0, 0, 0));
         } else {
            try {
               this.set(new class_2338(this.value.method_10263(), Integer.parseInt(this.textBoxY.get()), this.value.method_10260()));
            } catch (NumberFormatException var2) {
            }
         }

         this.newValueCheck();
      };
      this.textBoxZ.actionOnUnfocused = () -> {
         this.lastValue = this.value;
         if (this.textBoxZ.get().isEmpty()) {
            this.set(new class_2338(0, 0, 0));
         } else {
            try {
               this.set(new class_2338(this.value.method_10263(), this.value.method_10264(), Integer.parseInt(this.textBoxZ.get())));
            } catch (NumberFormatException var2) {
            }
         }

         this.newValueCheck();
      };
   }

   private void newValueCheck() {
      if (this.value != this.lastValue) {
         if (this.action != null) {
            this.action.run();
         }

         if (this.actionOnRelease != null) {
            this.actionOnRelease.run();
         }
      }

   }
}
