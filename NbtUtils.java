package meteordevelopment.meteorclient.utils.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2505;
import net.minecraft.class_2507;
import net.minecraft.class_2520;

public class NbtUtils {
   public static <T extends ISerializable<?>> class_2499 listToTag(Iterable<T> list) {
      class_2499 tag = new class_2499();

      for(T item : list) {
         tag.add(item.toTag());
      }

      return tag;
   }

   public static <T> List<T> listFromTag(class_2499 tag, ToValue<T> toItem) {
      List<T> list = new ArrayList(tag.size());

      for(class_2520 itemTag : tag) {
         T value = toItem.toValue(itemTag);
         if (value != null) {
            list.add(value);
         }
      }

      return list;
   }

   public static <K, V extends ISerializable<?>> class_2487 mapToTag(Map<K, V> map) {
      class_2487 tag = new class_2487();

      for(K key : map.keySet()) {
         tag.method_10566(key.toString(), ((ISerializable)map.get(key)).toTag());
      }

      return tag;
   }

   public static <K, V> Map<K, V> mapFromTag(class_2487 tag, ToKey<K> toKey, ToValue<V> toValue) {
      Map<K, V> map = new HashMap(tag.method_10546());

      for(String key : tag.method_10541()) {
         map.put(toKey.toKey(key), toValue.toValue(tag.method_10580(key)));
      }

      return map;
   }

   public static boolean toClipboard(ISerializable<?> serializable) {
      return toClipboard(serializable.toTag());
   }

   public static boolean toClipboard(class_2487 tag) {
      String preClipboard = MeteorClient.mc.field_1774.method_1460();

      try {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         class_2507.method_10634(tag, byteArrayOutputStream);
         MeteorClient.mc.field_1774.method_1455(Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
         return true;
      } catch (Exception e) {
         MeteorClient.LOG.error("Error copying NBT to clipboard!", e);
         MeteorClient.mc.field_1774.method_1455(preClipboard);
         return false;
      }
   }

   public static boolean fromClipboard(ISerializable<?> serializable) {
      class_2487 tag = fromClipboard();
      if (tag == null) {
         return false;
      } else {
         class_2487 sourceTag = serializable.toTag();

         for(String key : sourceTag.method_10541()) {
            if (!tag.method_10545(key)) {
               return false;
            }
         }

         serializable.fromTag(tag);
         return true;
      }
   }

   public static class_2487 fromClipboard() {
      try {
         byte[] data = Base64.getDecoder().decode(MeteorClient.mc.field_1774.method_1460().trim());
         ByteArrayInputStream bis = new ByteArrayInputStream(data);
         return class_2507.method_10629(new DataInputStream(bis), class_2505.method_53898());
      } catch (Exception e) {
         MeteorClient.LOG.error("Invalid NBT data pasted!", e);
         return null;
      }
   }

   public interface ToKey<T> {
      T toKey(String var1);
   }

   public interface ToValue<T> {
      T toValue(class_2520 var1);
   }
}
