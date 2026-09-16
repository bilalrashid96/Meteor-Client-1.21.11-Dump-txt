package meteordevelopment.meteorclient.systems.proxies;

import com.google.common.net.InetAddresses;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_2487;
import net.minecraft.class_2520;

public class Proxy implements ISerializable<Proxy> {
   public final Settings settings = new Settings();
   private final SettingGroup sgGeneral;
   private final SettingGroup sgOptional;
   public Setting<String> name;
   public Setting<ProxyType> type;
   public Setting<String> address;
   public Setting<Integer> port;
   public Setting<Boolean> enabled;
   public Setting<String> username;
   public Setting<String> password;
   public Status status;
   public long latency;

   private Proxy() {
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgOptional = this.settings.createGroup("Optional");
      this.name = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("name")).description("The name of the proxy.")).build());
      this.type = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("type")).description("The type of proxy.")).defaultValue(ProxyType.Socks5)).build());
      this.address = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("address")).description("The ip address of the proxy.")).filter(Utils::ipFilter).build());
      this.port = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("port")).description("The port of the proxy.")).defaultValue(0)).range(0, 65535).sliderMax(65535).noSlider().build());
      this.enabled = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("enabled")).description("Whether the proxy is enabled.")).defaultValue(true)).build());
      this.username = this.sgOptional.add(((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("username")).description("The username of the proxy.")).build());
      this.password = this.sgOptional.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("password")).description("The password of the proxy.")).visible(() -> ((ProxyType)this.type.get()).equals(ProxyType.Socks5))).build());
      this.status = Proxy.Status.UNCHECKED;
   }

   public Proxy(class_2520 tag) {
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgOptional = this.settings.createGroup("Optional");
      this.name = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("name")).description("The name of the proxy.")).build());
      this.type = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("type")).description("The type of proxy.")).defaultValue(ProxyType.Socks5)).build());
      this.address = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("address")).description("The ip address of the proxy.")).filter(Utils::ipFilter).build());
      this.port = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("port")).description("The port of the proxy.")).defaultValue(0)).range(0, 65535).sliderMax(65535).noSlider().build());
      this.enabled = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("enabled")).description("Whether the proxy is enabled.")).defaultValue(true)).build());
      this.username = this.sgOptional.add(((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("username")).description("The username of the proxy.")).build());
      this.password = this.sgOptional.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("password")).description("The password of the proxy.")).visible(() -> ((ProxyType)this.type.get()).equals(ProxyType.Socks5))).build());
      this.status = Proxy.Status.UNCHECKED;
      this.fromTag((class_2487)tag);
   }

   public boolean resolveAddress() {
      return Utils.resolveAddress(this.address.get(), (Integer)this.port.get());
   }

   public int checkStatus() {
      if (this.status == Proxy.Status.CHECKING) {
         return 0;
      } else {
         this.status = Proxy.Status.CHECKING;
         boolean timeout = false;

         try {
            Instant before = Instant.now();
            if (this.isSocks4()) {
               this.status = Proxy.Status.ALIVE;
               this.latency = Duration.between(before, Instant.now()).toMillis();
               return 1;
            }
         } catch (SocketTimeoutException var5) {
            timeout = true;
         } catch (IOException var6) {
         }

         try {
            Instant before = Instant.now();
            if (this.isSocks5()) {
               this.status = Proxy.Status.ALIVE;
               this.latency = Duration.between(before, Instant.now()).toMillis();
               return 1;
            }
         } catch (SocketTimeoutException var3) {
            timeout = true;
         } catch (IOException var4) {
         }

         this.status = Proxy.Status.DEAD;
         return timeout ? 3 : 2;
      }
   }

   private boolean isSocks4() throws IOException {
      byte[] u = ((String)this.username.get()).getBytes();
      ByteBuffer bb;
      if (InetAddresses.isInetAddress(this.address.get())) {
         bb = ByteBuffer.allocate(9 + u.length).put((byte)4).put((byte)1).putShort(((Integer)this.port.get()).shortValue()).putInt(InetAddress.getByName(this.address.get()).hashCode()).put(u).put((byte)0);
      } else {
         byte[] addr = ((String)this.address.get()).getBytes();
         bb = ByteBuffer.allocate(10 + u.length + addr.length).put((byte)4).put((byte)1).putShort(((Integer)this.port.get()).shortValue()).put(new byte[]{0, 0, 0, 1}).put(u).put((byte)0).put(addr).put((byte)0);
      }

      byte[] data = this.sendData(bb.array(), 8);
      if (data.length < 2) {
         return false;
      } else {
         return data[0] == 0 && data[1] == 90;
      }
   }

   private boolean isSocks5() throws IOException {
      ByteBuffer bb = ByteBuffer.allocate(4).put((byte)5).put((byte)2).put((byte)0).put((byte)2);
      byte[] data = this.sendData(bb.array(), 2);
      if (data.length < 2) {
         return false;
      } else {
         return data[0] == 5 && (data[1] == 0 || data[1] == 2);
      }
   }

   private byte[] sendData(byte[] data, int read) throws IOException {
      Socket s = new Socket();

      byte[] var5;
      try {
         s.setSoTimeout((Integer)Proxies.get().timeout.get());
         s.connect(new InetSocketAddress(this.address.get(), (Integer)this.port.get()), (Integer)Proxies.get().timeout.get());
         OutputStream out = s.getOutputStream();
         out.write(data);
         var5 = s.getInputStream().readNBytes(read);
      } catch (Throwable var7) {
         try {
            s.close();
         } catch (Throwable var6) {
            var7.addSuppressed(var6);
         }

         throw var7;
      }

      s.close();
      return var5;
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10566("settings", this.settings.toTag());
      return tag;
   }

   public Proxy fromTag(class_2487 tag) {
      Optional var10000 = tag.method_10562("settings");
      Settings var10001 = this.settings;
      Objects.requireNonNull(var10001);
      var10000.ifPresent(var10001::fromTag);
      return this;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Proxy proxy = (Proxy)o;
         return Objects.equals(proxy.address.get(), this.address.get()) && Objects.equals(proxy.port.get(), this.port.get());
      } else {
         return false;
      }
   }

   public static class Builder {
      protected ProxyType type;
      protected String address;
      protected int port;
      protected String name;
      protected String username;
      protected String password;
      protected boolean enabled;

      public Builder() {
         this.type = ProxyType.Socks5;
         this.address = "";
         this.port = 0;
         this.name = "";
         this.username = "";
         this.password = "";
         this.enabled = false;
      }

      public Builder type(ProxyType type) {
         this.type = type;
         return this;
      }

      public Builder address(String address) {
         this.address = address;
         return this;
      }

      public Builder port(int port) {
         this.port = port;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder username(String username) {
         this.username = username;
         return this;
      }

      public Builder password(String password) {
         this.password = password;
         return this;
      }

      public Builder enabled(boolean enabled) {
         this.enabled = enabled;
         return this;
      }

      public Proxy build() {
         Proxy proxy = new Proxy();
         if (!this.type.equals(proxy.type.getDefaultValue())) {
            proxy.type.set(this.type);
         }

         if (!this.address.equals(proxy.address.getDefaultValue())) {
            proxy.address.set(this.address);
         }

         if (this.port != (Integer)proxy.port.getDefaultValue()) {
            proxy.port.set(this.port);
         }

         if (!this.name.equals(proxy.name.getDefaultValue())) {
            proxy.name.set(this.name);
         }

         if (!this.username.equals(proxy.username.getDefaultValue())) {
            proxy.username.set(this.username);
         }

         if (!this.password.equals(proxy.password.getDefaultValue())) {
            proxy.password.set(this.password);
         }

         if (this.enabled != (Boolean)proxy.enabled.getDefaultValue()) {
            proxy.enabled.set(this.enabled);
         }

         return proxy;
      }
   }

   public static enum Status {
      UNCHECKED,
      CHECKING,
      DEAD,
      ALIVE;

      public String toString() {
         String var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = "";
            case 1 -> var10000 = "...";
            case 2 -> var10000 = "X";
            case 3 -> var10000 = "O";
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public Color getColor() {
         Color var10000;
         switch (this.ordinal()) {
            case 0:
            case 1:
               var10000 = Color.GRAY;
               break;
            case 2:
               var10000 = Color.RED;
               break;
            case 3:
               var10000 = Color.GREEN;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      // $FF: synthetic method
      private static Status[] $values() {
         return new Status[]{UNCHECKED, CHECKING, DEAD, ALIVE};
      }
   }
}
