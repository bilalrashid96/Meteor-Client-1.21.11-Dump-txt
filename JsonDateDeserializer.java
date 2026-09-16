package meteordevelopment.meteorclient.utils.other;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Date;

public class JsonDateDeserializer implements JsonDeserializer<Date> {
   public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      try {
         return Date.from(Instant.parse(jsonElement.getAsString()));
      } catch (Exception var5) {
         return null;
      }
   }
}
