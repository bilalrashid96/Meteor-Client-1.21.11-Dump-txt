package meteordevelopment.meteorclient.systems.proxies;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.class_2487;
import org.jetbrains.annotations.NotNull;

public class Proxies extends System<Proxies> implements Iterable<Proxy> {
   public final Settings settings = new Settings();
   private final SettingGroup sgRefreshing;
   private final SettingGroup sgCleanup;
   private final Setting<Integer> threads;
   public final Setting<Integer> timeout;
   private final Setting<Integer> tries;
   private final Setting<Boolean> sort;
   private final Setting<Boolean> pruneDead;
   private final Setting<Integer> pruneLatency;
   private final Setting<Integer> pruneExcess;
   public static final Pattern PROXY_PATTERN = Pattern.compile("^(?:([\\w\\s]+)=)?((?:0*(?:\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])(?:\\.(?!:)|)){4}):(?!0)(\\d{1,4}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])(?i:@(socks[45]))?$", 8);
   public static final Pattern PROXY_PATTERN_WEBSHARE = Pattern.compile("^((?:0*(?:\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])(?:\\.(?!:)|)){4}):(?!0)(\\d{1,4}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5]):([^:]+)(?::(.+))?$", 8);
   public static final Pattern PROXY_PATTERN_URI = Pattern.compile("^(?:(socks|socks4|socks5)://)?(?:(?<user>[\\w~-]+)(:(?<pass>[\\w~-]+))?@)?(?<addr>(?:0*(?:\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])(?:\\.(?!:)|)){4}):(?!0)(?<port>\\d{1,4}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$", 8);
   private List<Proxy> proxies;
   public boolean refreshing;

   public Proxies() {
      super("proxies");
      this.sgRefreshing = this.settings.createGroup("Refreshing");
      this.sgCleanup = this.settings.createGroup("Cleanup");
      this.threads = this.sgRefreshing.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("threads")).description("The number of concurrent threads to check proxies with.")).defaultValue(8)).min(0).sliderRange(0, 32).build());
      this.timeout = this.sgRefreshing.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("timeout")).description("The timeout in milliseconds for checking proxies.")).defaultValue(5000)).min(0).sliderRange(0, 15000).build());
      this.tries = this.sgRefreshing.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("retries-on-timeout")).description("How many additional times to check a proxy if the check times out.")).defaultValue(1)).min(0).sliderRange(0, 5).build());
      this.sort = this.sgCleanup.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sort-by-latency")).description("Whether to sort the proxy list by latency.")).defaultValue(true)).build());
      this.pruneDead = this.sgCleanup.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("prune-dead")).description("Whether to prune dead proxies.")).defaultValue(true)).build());
      this.pruneLatency = this.sgCleanup.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("prune-by-latency")).description("Prune proxies at or above this latency in ms. 0 to disable.")).defaultValue(2000)).min(0).sliderRange(0, 10000).build());
      this.pruneExcess = this.sgCleanup.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("prune-to-count")).description("If in excess, prune the number of proxies to this count. 0 to disable. Prioritises by latency.")).defaultValue(0)).sliderRange(0, 25).build());
      this.proxies = new ArrayList();
   }

   public static Proxies get() {
      return (Proxies)Systems.get(Proxies.class);
   }

   public boolean add(Proxy proxy) {
      for(Proxy p : this.proxies) {
         if (((ProxyType)p.type.get()).equals(proxy.type.get()) && ((String)p.address.get()).equals(proxy.address.get()) && Objects.equals(p.port.get(), proxy.port.get())) {
            return false;
         }
      }

      if (this.proxies.isEmpty()) {
         proxy.enabled.set(true);
      }

      this.proxies.add(proxy);
      this.save();
      return true;
   }

   public void remove(Proxy proxy) {
      if (this.proxies.remove(proxy)) {
         this.save();
      }

   }

   public Proxy getEnabled() {
      for(Proxy proxy : this.proxies) {
         if ((Boolean)proxy.enabled.get()) {
            return proxy;
         }
      }

      return null;
   }

   public void setEnabled(Proxy proxy, boolean enabled) {
      for(Proxy p : this.proxies) {
         p.enabled.set(false);
      }

      proxy.enabled.set(enabled);
      this.save();
   }

   public void checkProxies(boolean all) {
      if (!this.refreshing && !this.isEmpty()) {
         this.refreshing = true;
         MeteorExecutor.execute(() -> {
            BlockingQueue<Proxy> toCheck = new ArrayBlockingQueue(this.proxies.size());
            this.proxies.forEach((proxy) -> {
               if (all || proxy.status == Proxy.Status.UNCHECKED) {
                  toCheck.add(proxy);
               }

            });
            ConcurrentHashMap<Proxy, Integer> checked = new ConcurrentHashMap(this.proxies.size(), 1.0F);
            this.proxies.forEach((proxy) -> checked.put(proxy, 0));
            ExecutorService executor = Executors.newFixedThreadPool((Integer)this.threads.get());

            try {
               for(int i = 0; i < (Integer)this.threads.get(); ++i) {
                  executor.execute(() -> {
                     try {
                        this.check(toCheck, checked);
                     } catch (InterruptedException var4) {
                     }

                  });
               }

               try {
                  executor.shutdown();
                  executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
               } catch (InterruptedException var8) {
               }

               this.refreshing = false;
            } catch (Throwable var9) {
               if (executor != null) {
                  try {
                     executor.close();
                  } catch (Throwable var7) {
                     var9.addSuppressed(var7);
                  }
               }

               throw var9;
            }

            if (executor != null) {
               executor.close();
            }

         });
      }
   }

   private void check(BlockingQueue<Proxy> queue, ConcurrentHashMap<Proxy, Integer> checks) throws InterruptedException {
      while(!queue.isEmpty()) {
         Proxy proxy = (Proxy)queue.take();
         if (proxy.checkStatus() == 3 && (Integer)checks.get(proxy) <= (Integer)this.tries.get()) {
            checks.put(proxy, (Integer)checks.get(proxy) + 1);
            queue.put(proxy);
         }
      }

   }

   public void clean() {
      if (!this.refreshing) {
         this.proxies.removeIf((proxy) -> {
            if ((Boolean)this.pruneDead.get() && proxy.status == Proxy.Status.DEAD) {
               return true;
            } else {
               return (Integer)this.pruneLatency.get() != 0 && proxy.status == Proxy.Status.ALIVE && proxy.latency >= (long)(Integer)this.pruneLatency.get();
            }
         });
         List<Proxy> p = (List<Proxy>)((Boolean)this.sort.get() ? this.proxies : new ArrayList(this.proxies));
         p.sort(Comparator.comparingLong((proxy) -> proxy.status == Proxy.Status.ALIVE ? proxy.latency : Long.MAX_VALUE));
         if ((Integer)this.pruneExcess.get() != 0 && (Integer)this.pruneExcess.get() < p.size()) {
            p.subList((Integer)this.pruneExcess.get(), p.size()).clear();
            if (!(Boolean)this.sort.get()) {
               this.proxies.removeIf((proxy) -> !p.contains(proxy));
            }

         }
      }
   }

   public boolean isEmpty() {
      return this.proxies.isEmpty();
   }

   public int size() {
      return this.proxies.size();
   }

   public @NotNull Iterator<Proxy> iterator() {
      return this.proxies.iterator();
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10566("settings", this.settings.toTag());
      tag.method_10566("proxies", NbtUtils.listToTag(this.proxies));
      return tag;
   }

   public Proxies fromTag(class_2487 tag) {
      if (tag.method_10545("settings")) {
         this.settings.fromTag(tag.method_68568("settings"));
      }

      this.proxies = NbtUtils.<Proxy>listFromTag(tag.method_68569("proxies"), Proxy::new);
      return this;
   }
}
