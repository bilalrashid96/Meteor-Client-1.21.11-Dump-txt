package meteordevelopment.meteorclient.systems.macros;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2487;
import org.jetbrains.annotations.NotNull;

public class Macros extends System<Macros> implements Iterable<Macro> {
   private List<Macro> macros = new ArrayList();

   public Macros() {
      super("macros");
   }

   public static Macros get() {
      return (Macros)Systems.get(Macros.class);
   }

   public void add(Macro macro) {
      this.macros.add(macro);
      MeteorClient.EVENT_BUS.subscribe(macro);
      this.save();
   }

   public Macro get(String name) {
      for(Macro macro : this.macros) {
         if (((String)macro.name.get()).equalsIgnoreCase(name)) {
            return macro;
         }
      }

      return null;
   }

   public List<Macro> getAll() {
      return this.macros;
   }

   public void remove(Macro macro) {
      if (this.macros.remove(macro)) {
         MeteorClient.EVENT_BUS.unsubscribe(macro);
         this.save();
      }

   }

   @EventHandler(
      priority = 100
   )
   private void onKey(KeyEvent event) {
      if (event.action != KeyAction.Release) {
         for(Macro macro : this.macros) {
            if (macro.onAction(true, event.key(), event.modifiers())) {
               return;
            }
         }

      }
   }

   @EventHandler(
      priority = 100
   )
   private void onMouse(MouseClickEvent event) {
      if (event.action != KeyAction.Release) {
         for(Macro macro : this.macros) {
            if (macro.onAction(false, event.button(), 0)) {
               return;
            }
         }

      }
   }

   public boolean isEmpty() {
      return this.macros.isEmpty();
   }

   public @NotNull Iterator<Macro> iterator() {
      return this.macros.iterator();
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10566("macros", NbtUtils.listToTag(this.macros));
      return tag;
   }

   public Macros fromTag(class_2487 tag) {
      for(Macro macro : this.macros) {
         MeteorClient.EVENT_BUS.unsubscribe(macro);
      }

      this.macros = NbtUtils.<Macro>listFromTag(tag.method_68569("macros"), Macro::new);

      for(Macro macro : this.macros) {
         MeteorClient.EVENT_BUS.subscribe(macro);
      }

      return this;
   }
}
