package meteordevelopment.meteorclient.gui.tabs.builtin;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WConfirmedButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WConfirmedMinus;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.profiles.Profile;
import meteordevelopment.meteorclient.systems.profiles.Profiles;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.render.prompts.OkPrompt;
import net.minecraft.class_2487;
import net.minecraft.class_2507;
import net.minecraft.class_2520;
import net.minecraft.class_437;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class ProfilesTab extends Tab {
   private static final PointerBuffer filters = BufferUtils.createPointerBuffer(1);

   public ProfilesTab() {
      super("Profiles");
   }

   public TabScreen createScreen(GuiTheme theme) {
      return new ProfilesScreen(theme, this);
   }

   public boolean isScreen(class_437 screen) {
      return screen instanceof ProfilesScreen;
   }

   static {
      ByteBuffer pngFilter = MemoryUtil.memASCII("*.nbt");
      filters.put(pngFilter);
      filters.rewind();
   }

   private static class ProfilesScreen extends WindowTabScreen {
      public ProfilesScreen(GuiTheme theme, Tab tab) {
         super(theme, tab);
      }

      public void initWidgets() {
         WTable table = (WTable)this.add(this.theme.table()).expandX().minWidth((double)400.0F).widget();
         this.initTable(table);
         this.add(this.theme.horizontalSeparator()).expandX();
         WHorizontalList l = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
         WButton create = (WButton)l.add(this.theme.button("Create")).expandX().widget();
         create.tooltip = "Create new profile";
         create.action = () -> MeteorClient.mc.method_1507(new EditProfileScreen(this.theme, (Profile)null, this::reload));
         WButton importBtn = (WButton)l.add(this.theme.button("Import")).expandX().widget();
         importBtn.tooltip = "Import profile";
         importBtn.action = () -> {
            try {
               Profile imported = this.importProfile();
               if (imported != null) {
                  MeteorClient.LOG.info("Successfully imported profile '{}'.", imported.name.get());
               }

               this.reload();
            } catch (IOException e) {
               MeteorClient.LOG.error("Error importing profile", e);
               ((OkPrompt)((OkPrompt)((OkPrompt)((OkPrompt)OkPrompt.create().title("Failure importing profile")).message("There was an error importing the profile.")).message("Error: %d", new Object[]{e.getMessage()})).dontShowAgainCheckboxVisible(false)).show();
            }

         };
      }

      private void initTable(WTable table) {
         table.clear();
         if (!Profiles.get().isEmpty()) {
            for(Profile profile : Profiles.get()) {
               table.add(this.theme.label(profile.name.get())).expandCellX();
               WConfirmedButton save = this.theme.confirmedButton("Save", "Confirm");
               Objects.requireNonNull(profile);
               save.action = profile::save;
               table.add(save).right();
               WButton load = (WButton)table.add(this.theme.button("Load")).widget();
               Objects.requireNonNull(profile);
               load.action = profile::load;
               WButton export = (WButton)table.add(this.theme.button("Export")).widget();
               export.action = () -> MeteorClient.mc.method_1507(new ExportProfileScreen(this.theme, profile));
               WButton edit = (WButton)table.add(this.theme.button(GuiRenderer.EDIT)).widget();
               edit.action = () -> MeteorClient.mc.method_1507(new EditProfileScreen(this.theme, profile, this::reload));
               WConfirmedMinus remove = (WConfirmedMinus)table.add(this.theme.confirmedMinus()).widget();
               remove.action = () -> {
                  Profiles.get().remove(profile);
                  this.reload();
               };
               table.row();
            }

         }
      }

      private Profile importProfile() throws IOException {
         String file = TinyFileDialogs.tinyfd_openFileDialog("Select profile to import", (CharSequence)null, ProfilesTab.filters, (CharSequence)null, false);
         if (file == null) {
            return null;
         } else {
            File profileFile = new File(file);
            class_2487 nbt = class_2507.method_10633(profileFile.toPath());
            Profile p = new Profile();
            if (!p.name.set(nbt.method_68564("name", profileFile.getName()))) {
               return null;
            } else {
               File profileFolder = p.getSafeFile();
               if (profileFolder == null) {
                  return null;
               } else {
                  profileFolder.mkdirs();
                  nbt.method_10551("name");

                  for(Map.Entry<String, class_2520> entry : nbt.method_59874()) {
                     String filename = (String)entry.getKey();
                     if (filename.endsWith(".nbt") && !filename.contains("/") && !filename.contains("\\") && !(new File(filename)).isAbsolute()) {
                        switch (filename) {
                           case "hud.nbt":
                              p.hud.set(true);
                              break;
                           case "macros.nbt":
                              p.macros.set(true);
                              break;
                           case "modules.nbt":
                              p.modules.set(true);
                              break;
                           default:
                              if (filename.endsWith(".nbt")) {
                                 p.waypoints.set(true);
                              }
                        }

                        File f = (new File(profileFolder, filename)).getCanonicalFile();
                        if (f.toPath().startsWith(profileFolder.toPath())) {
                           class_2507.method_55324((class_2520)entry.getValue(), new DataOutputStream(new FileOutputStream(f)));
                        }
                     }
                  }

                  Profiles.get().getAll().add(p);
                  Profiles.get().save();
                  return p;
               }
            }
         }
      }

      public boolean toClipboard() {
         return NbtUtils.toClipboard(Profiles.get());
      }

      public boolean fromClipboard() {
         return NbtUtils.fromClipboard(Profiles.get());
      }
   }

   private static class EditProfileScreen extends WindowScreen {
      private WContainer settingsContainer;
      private final Profile profile;
      private final boolean isNew;
      private final Runnable action;

      public EditProfileScreen(GuiTheme theme, Profile profile, Runnable action) {
         super(theme, profile == null ? "New Profile" : "Edit Profile");
         this.isNew = profile == null;
         this.profile = this.isNew ? new Profile() : profile;
         this.action = action;
      }

      public void initWidgets() {
         this.settingsContainer = (WContainer)this.add(this.theme.verticalList()).expandX().minWidth((double)400.0F).widget();
         this.settingsContainer.add(this.theme.settings(this.profile.settings)).expandX();
         this.add(this.theme.horizontalSeparator()).expandX();
         WButton save = (WButton)this.add(this.theme.button(this.isNew ? "Create" : "Save")).expandX().widget();
         save.action = () -> {
            if (this.profile.getSafeFile() != null) {
               if (this.isNew) {
                  for(Profile p : Profiles.get()) {
                     if (this.profile.equals(p)) {
                        return;
                     }
                  }
               }

               List<String> valid = new ArrayList();

               for(String address : this.profile.loadOnJoin.get()) {
                  if (Utils.resolveAddress(address)) {
                     valid.add(address);
                  }
               }

               this.profile.loadOnJoin.set(valid);
               if (this.isNew) {
                  Profiles.get().add(this.profile);
               } else {
                  Profiles.get().save();
               }

               this.method_25419();
            }
         };
         this.enterAction = save.action;
      }

      public void method_25393() {
         super.method_25393();
         this.profile.settings.tick(this.settingsContainer, this.theme);
      }

      protected void onClosed() {
         if (this.action != null) {
            this.action.run();
         }

      }
   }

   private static class ExportProfileScreen extends WindowScreen {
      private final Profile profile;

      public ExportProfileScreen(GuiTheme theme, Profile profile) {
         super(theme, "Export Profile");
         this.profile = profile;
      }

      public void initWidgets() {
         this.add(this.theme.label("Select which profile settings to export."));
         WContainer settingsContainer = (WContainer)this.add(this.theme.verticalList()).expandX().minWidth((double)400.0F).widget();
         settingsContainer.add(this.theme.horizontalSeparator()).expandX().widget();
         WCheckbox hud = this.addBool(settingsContainer, this.profile.settings.get("hud", Boolean.class));
         WCheckbox macros = this.addBool(settingsContainer, this.profile.settings.get("macros", Boolean.class));
         WCheckbox modules = this.addBool(settingsContainer, this.profile.settings.get("modules", Boolean.class));
         WCheckbox waypoints = this.addBool(settingsContainer, this.profile.settings.get("waypoints", Boolean.class));
         this.add(this.theme.horizontalSeparator()).expandX().widget();
         WButton export = (WButton)this.add(this.theme.button("Export profile")).expandX().widget();
         export.action = () -> {
            this.exportProfile(this.profile, hud.checked, macros.checked, modules.checked, waypoints.checked);
            this.method_25419();
         };
      }

      private WCheckbox addBool(WContainer container, Setting<Boolean> setting) {
         WHorizontalList boolList = (WHorizontalList)container.add(this.theme.horizontalList()).expandX().widget();
         ((WLabel)boolList.add(this.theme.label(setting.title)).widget()).tooltip = setting.description;
         WCheckbox c = this.theme.checkbox((Boolean)setting.get());
         boolList.add(c).expandCellX().right();
         return c;
      }

      private void exportProfile(Profile profile, boolean hud, boolean macros, boolean modules, boolean waypoints) {
         String path = TinyFileDialogs.tinyfd_saveFileDialog("Save profile", (CharSequence)profile.name.get(), ProfilesTab.filters, (CharSequence)null);
         if (path != null) {
            Path p = Path.of(path.endsWith(".nbt") ? path : path + ".nbt");
            class_2487 nbt = new class_2487();
            nbt.method_10582("name", profile.name.get());

            try {
               for(File f : profile.getFile().listFiles()) {
                  if ((!f.getName().equals("hud.nbt") || !hud) && (!f.getName().equals("macros.nbt") || !macros) && (!f.getName().equals("modules.nbt") || !modules)) {
                     if (f.getName().endsWith(".nbt") && waypoints) {
                        nbt.method_10566(f.getName(), class_2507.method_10633(f.toPath()));
                     }
                  } else {
                     nbt.method_10566(f.getName(), class_2507.method_10633(f.toPath()));
                  }
               }

               class_2507.method_10630(nbt, p);
            } catch (IOException e) {
               MeteorClient.LOG.error("Error serialising profile {} to a file", profile.name.get(), e);
               ((OkPrompt)((OkPrompt)((OkPrompt)((OkPrompt)OkPrompt.create().title("Failure exporting profile")).message("There was an error serialising or exporting the profile %d.", new Object[]{profile.name.get()})).message("Error: %d", new Object[]{e.getMessage()})).dontShowAgainCheckboxVisible(false)).show();
            }

         }
      }
   }
}
