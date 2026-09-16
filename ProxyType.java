package meteordevelopment.meteorclient.systems.proxies;

import org.jetbrains.annotations.Nullable;

public enum ProxyType {
   Socks4,
   Socks5;

   public static @Nullable ProxyType parse(String group) {
      for(ProxyType type : values()) {
         if (type.name().equalsIgnoreCase(group)) {
            return type;
         }
      }

      return null;
   }

   // $FF: synthetic method
   private static ProxyType[] $values() {
      return new ProxyType[]{Socks4, Socks5};
   }
}
