package meteordevelopment.meteorclient.settings;

import java.util.function.Consumer;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.gui.widgets.WKeybind;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2487;

public class KeybindSetting extends Setting<Keybind> {
   private final Runnable action;
   public WKeybind widget;

   public KeybindSetting(String name, String description, Keybind defaultValue, Consumer<Keybind> onChanged, Consumer<Setting<Keybind>> onModuleActivated, IVisible visible, Runnable action) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
      this.action = action;
      MeteorClient.EVENT_BUS.subscribe(this);
   }

   @EventHandler(
      priority = 200
   )
   private void onKeyBinding(KeyEvent event) {
      if (this.widget != null) {
         if (event.action == KeyAction.Press && event.key() == 256 && this.widget.onClear()) {
            event.cancel();
         } else if (event.action == KeyAction.Release && this.widget.onAction(true, event.key(), event.modifiers())) {
            event.cancel();
         }

      }
   }

   @EventHandler(
      priority = 200
   )
   private void onMouseClickBinding(MouseClickEvent event) {
      if (event.action == KeyAction.Press && this.widget != null && this.widget.onAction(false, event.button(), 0)) {
         event.cancel();
      }

   }

   @EventHandler(
      priority = 100
   )
   private void onKey(KeyEvent event) {
      if (event.action == KeyAction.Release && ((Keybind)this.get()).matches(event.input) && (this.module == null || this.module.isActive()) && this.action != null) {
         this.action.run();
      }

   }

   @EventHandler(
      priority = 100
   )
   private void onMouseClick(MouseClickEvent event) {
      if (event.action == KeyAction.Release && ((Keybind)this.get()).matches(event.input) && (this.module == null || this.module.isActive()) && this.action != null) {
         this.action.run();
      }

   }

   public void resetImpl() {
      if (this.value == null) {
         this.value = ((Keybind)this.defaultValue).copy();
      } else {
         ((Keybind)this.value).set(this.defaultValue);
      }

      if (this.widget != null) {
         this.widget.reset();
      }

   }

   protected Keybind parseImpl(String str) {
      try {
         return Keybind.fromKey(Integer.parseInt(str.trim()));
      } catch (NumberFormatException var3) {
         return null;
      }
   }

   protected boolean isValueValid(Keybind value) {
      return true;
   }

   public class_2487 save(class_2487 tag) {
      tag.method_10566("value", ((Keybind)this.get()).toTag());
      return tag;
   }

   public Keybind load(class_2487 tag) {
      ((Keybind)this.get()).fromTag(tag.method_68568("value"));
      return (Keybind)this.get();
   }

   public static class Builder extends Setting.SettingBuilder<Builder, Keybind, KeybindSetting> {
      private Runnable action;

      public Builder() {
         super(Keybind.none());
      }

      public Builder action(Runnable action) {
         this.action = action;
         return this;
      }

      public KeybindSetting build() {
         return new KeybindSetting(this.name, this.description, this.defaultValue, this.onChanged, this.onModuleActivated, this.visible, this.action);
      }
   }
}
