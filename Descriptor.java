package meteordevelopment.meteorclient.asm;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class Descriptor {
   private final String[] components;

   public Descriptor(String... components) {
      this.components = components;
   }

   public String toString(boolean method, boolean map) {
      MappingResolver mappings = FabricLoader.getInstance().getMappingResolver();
      StringBuilder sb = new StringBuilder();
      if (method) {
         sb.append('(');
      }

      for(int i = 0; i < this.components.length; ++i) {
         if (method && i == this.components.length - 1) {
            sb.append(')');
         }

         String component = this.components[i];
         if (map && component.startsWith("L") && component.endsWith(";")) {
            sb.append('L').append(mappings.mapClassName("intermediary", component.substring(1, component.length() - 1).replace('/', '.')).replace('.', '/')).append(';');
         } else {
            sb.append(component);
         }
      }

      return sb.toString();
   }
}
