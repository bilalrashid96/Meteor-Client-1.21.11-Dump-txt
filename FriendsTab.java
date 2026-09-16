package meteordevelopment.meteorclient.gui.tabs.builtin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.utils.CharFilter;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.class_437;

public class FriendsTab extends Tab {
   public FriendsTab() {
      super("Friends");
   }

   public TabScreen createScreen(GuiTheme theme) {
      return new FriendsScreen(theme, this);
   }

   public boolean isScreen(class_437 screen) {
      return screen instanceof FriendsScreen;
   }

   private static class FriendsScreen extends WindowTabScreen {
      public FriendsScreen(GuiTheme theme, Tab tab) {
         super(theme, tab);
      }

      public void initWidgets() {
         WTable table = (WTable)this.add(this.theme.table()).expandX().minWidth((double)400.0F).widget();
         this.initTable(table);
         this.add(this.theme.horizontalSeparator()).expandX();
         WHorizontalList list = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
         WTextBox nameW = (WTextBox)list.add(this.theme.textBox("", (CharFilter)((text, c) -> c != ' '))).expandX().widget();
         nameW.setFocused(true);
         WPlus add = (WPlus)list.add(this.theme.plus()).widget();
         add.action = () -> {
            String name = nameW.get().trim();
            Friend friend = new Friend(name);
            if (Friends.get().add(friend)) {
               nameW.set("");
               this.initTable(table);
               nameW.setFocused(true);
               MeteorExecutor.execute(() -> {
                  friend.updateInfo();
                  MeteorClient.mc.execute(() -> {
                     this.initTable(table);
                     nameW.setFocused(true);
                  });
               });
            }

         };
         this.enterAction = add.action;
      }

      private void initTable(WTable table) {
         table.clear();
         if (!Friends.get().isEmpty()) {
            Friends.get().forEach((friendx) -> MeteorExecutor.execute(() -> {
                  if (friendx.headTextureNeedsUpdate()) {
                     friendx.updateInfo();
                  }

               }));

            for(Friend friend : Friends.get()) {
               table.add(this.theme.texture((double)32.0F, (double)32.0F, friend.getHead().needsRotate() ? (double)90.0F : (double)0.0F, friend.getHead()));
               table.add(this.theme.label(friend.getName()));
               WMinus remove = (WMinus)table.add(this.theme.minus()).expandCellX().right().widget();
               remove.action = () -> {
                  Friends.get().remove(friend);
                  this.initTable(table);
               };
               table.row();
            }

         }
      }

      public boolean toClipboard() {
         return NbtUtils.toClipboard(Friends.get());
      }

      public boolean fromClipboard() {
         return NbtUtils.fromClipboard(Friends.get());
      }
   }
}
