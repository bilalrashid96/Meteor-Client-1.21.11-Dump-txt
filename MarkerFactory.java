package meteordevelopment.meteorclient.systems.modules.render.marker;

import java.util.HashMap;
import java.util.Map;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class MarkerFactory {
   private final Map<String, Factory> factories = new HashMap();
   private final String[] names;

   public MarkerFactory() {
      this.factories.put("Cuboid", CuboidMarker::new);
      this.factories.put("Sphere-2D", Sphere2dMarker::new);
      this.names = new String[this.factories.size()];
      int i = 0;

      for(String key : this.factories.keySet()) {
         this.names[i++] = key;
      }

   }

   public String[] getNames() {
      return this.names;
   }

   public BaseMarker createMarker(String name) {
      if (this.factories.containsKey(name)) {
         BaseMarker marker = ((Factory)this.factories.get(name)).create();
         marker.settings.registerColorSettings(Modules.get().get(Marker.class));
         return marker;
      } else {
         return null;
      }
   }

   private interface Factory {
      BaseMarker create();
   }
}
