package meteordevelopment.meteorclient.gui.screens;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WConfirmedButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.proxies.Proxies;
import meteordevelopment.meteorclient.systems.proxies.Proxy;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class ProxiesScreen extends WindowScreen {
   private final List<WCheckbox> checkboxes = new ArrayList();
   private final WButton refreshButton;
   private final WConfirmedButton cleanButton;
   private Map<Proxy, WLabel> statuses;
   private int timer;

   public ProxiesScreen(GuiTheme theme) {
      super(theme, "Proxies");
      this.refreshButton = this.theme.button("Refresh");
      this.cleanButton = this.theme.confirmedButton("Cleanup", "Confirm");
      this.statuses = new HashMap();
      this.timer = 0;
   }

   public void initWidgets() {
      WTable table = (WTable)this.add(this.theme.table()).expandX().minWidth((double)400.0F).widget();
      this.initTable(table);
      this.add(this.theme.horizontalSeparator()).expandX();
      WHorizontalList l = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
      WButton newBtn = (WButton)l.add(this.theme.button("New")).expandX().widget();
      newBtn.action = () -> MeteorClient.mc.method_1507(new EditProxyScreen(this.theme, (Proxy)null, this::reload));
      PointerBuffer filters = BufferUtils.createPointerBuffer(1);
      ByteBuffer txtFilter = MemoryUtil.memASCII("*.txt");
      filters.put(txtFilter);
      filters.rewind();
      WButton importBtn = (WButton)l.add(this.theme.button("Import")).expandX().widget();
      importBtn.action = () -> {
         String selectedFile = TinyFileDialogs.tinyfd_openFileDialog("Import Proxies", (CharSequence)null, filters, (CharSequence)null, false);
         if (selectedFile != null) {
            File file = new File(selectedFile);
            MeteorClient.mc.method_1507(new ProxiesImportScreen(this.theme, file));
         }

      };
      l.add(this.refreshButton).expandX();
      this.refreshButton.action = () -> Proxies.get().checkProxies(true);
      l.add(this.cleanButton).expandX();
      this.cleanButton.action = () -> {
         if (!Proxies.get().refreshing) {
            Proxies.get().clean();
            this.initTable(table);
         }
      };
      WButton configButton = (WButton)l.add(this.theme.button(GuiRenderer.EDIT)).widget();
      configButton.action = () -> MeteorClient.mc.method_1507(new ConfigScreen(this.theme));
      configButton.tooltip = "Proxies Config";
   }

   private void initTable(WTable table) {
      table.clear();
      if (!Proxies.get().isEmpty()) {
         this.statuses = new HashMap(Proxies.get().size(), 1.0F);

         for(Proxy proxy : Proxies.get()) {
            WCheckbox enabled = (WCheckbox)table.add(this.theme.checkbox((Boolean)proxy.enabled.get())).widget();
            this.checkboxes.add(enabled);
            enabled.action = () -> {
               boolean checked = enabled.checked;
               Proxies.get().setEnabled(proxy, checked);

               for(WCheckbox checkbox : this.checkboxes) {
                  checkbox.checked = false;
               }

               enabled.checked = checked;
            };
            WLabel name = (WLabel)table.add(this.theme.label(proxy.name.get())).widget();
            name.color = this.theme.textColor();
            WLabel type = (WLabel)table.add(this.theme.label("(" + String.valueOf(proxy.type.get()) + ")")).widget();
            type.color = this.theme.textSecondaryColor();
            WHorizontalList ipList = (WHorizontalList)table.add(this.theme.horizontalList()).expandCellX().widget();
            ipList.spacing = (double)0.0F;
            ipList.add(this.theme.label(proxy.address.get()));
            ((WLabel)ipList.add(this.theme.label(":")).widget()).color = this.theme.textSecondaryColor();
            ipList.add(this.theme.label(Integer.toString((Integer)proxy.port.get())));
            String s = proxy.status == Proxy.Status.ALIVE ? proxy.latency + "ms" : proxy.status.toString();
            WLabel status = (WLabel)table.add(this.theme.label(s)).widget();
            status.color = proxy.status.getColor();
            this.statuses.put(proxy, status);
            WButton refresh = (WButton)table.add(this.theme.button(GuiRenderer.RESET)).widget();
            refresh.action = () -> {
               Objects.requireNonNull(proxy);
               MeteorExecutor.execute(proxy::checkStatus);
            };
            refresh.tooltip = "Refresh";
            WButton edit = (WButton)table.add(this.theme.button(GuiRenderer.EDIT)).widget();
            edit.action = () -> MeteorClient.mc.method_1507(new EditProxyScreen(this.theme, proxy, this::reload));
            WMinus remove = (WMinus)table.add(this.theme.minus()).widget();
            remove.action = () -> {
               Proxies.get().remove(proxy);
               this.reload();
            };
            table.row();
         }

      }
   }

   public void method_25393() {
      if (Proxies.get().refreshing) {
         if (this.cleanButton.getText().equals("Cleanup")) {
            this.cleanButton.set("---", "---");
         }

         if (this.timer > 2) {
            this.refreshButton.set(this.getNext(this.refreshButton));
            this.timer = 0;
         } else {
            ++this.timer;
         }
      } else {
         if (!this.refreshButton.getText().equals("Refresh")) {
            this.refreshButton.set("Refresh");
         }

         if (!this.cleanButton.getText().equals("Cleanup")) {
            this.cleanButton.set("Cleanup", "Confirm");
         }
      }

      for(Map.Entry<Proxy, WLabel> entry : this.statuses.entrySet()) {
         Proxy proxy = (Proxy)entry.getKey();
         WLabel label = (WLabel)entry.getValue();
         if (!label.get().equals(proxy.status.toString())) {
            label.set(proxy.status == Proxy.Status.ALIVE ? proxy.latency + "ms" : proxy.status.toString());
            label.color = proxy.status.getColor();
         }
      }

   }

   private String getNext(WButton b) {
      String var10000;
      switch (b.getText()) {
         case "Refresh":
         case "oo0":
            var10000 = "ooo";
            break;
         case "ooo":
            var10000 = "0oo";
            break;
         case "0oo":
            var10000 = "o0o";
            break;
         case "o0o":
            var10000 = "oo0";
            break;
         default:
            var10000 = "Refresh";
      }

      return var10000;
   }

   public boolean toClipboard() {
      return NbtUtils.toClipboard(Proxies.get());
   }

   public boolean fromClipboard() {
      return NbtUtils.fromClipboard(Proxies.get());
   }

   protected static class EditProxyScreen extends EditSystemScreen<Proxy> {
      public EditProxyScreen(GuiTheme theme, Proxy value, Runnable reload) {
         super(theme, value, reload);
      }

      public Proxy create() {
         return (new Proxy.Builder()).build();
      }

      public boolean save() {
         Proxy var10000 = this.value;
         Objects.requireNonNull(var10000);
         MeteorExecutor.execute(var10000::checkStatus);
         return ((Proxy)this.value).resolveAddress() && (!this.isNew || Proxies.get().add(this.value));
      }

      public Settings getSettings() {
         return (this.value).settings;
      }
   }

   private static class ConfigScreen extends WindowScreen {
      private WContainer settingsContainer;

      public ConfigScreen(GuiTheme theme) {
         super(theme, "Proxies Config");
      }

      public void initWidgets() {
         this.settingsContainer = (WContainer)this.add(this.theme.verticalList()).expandX().minWidth((double)400.0F).widget();
         this.settingsContainer.add(this.theme.settings(Proxies.get().settings)).expandX();
      }

      public void method_25393() {
         Proxies.get().settings.tick(this.settingsContainer, this.theme);
      }
   }
}
