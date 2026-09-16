package meteordevelopment.meteorclient.gui.screens.settings;

import java.util.Map;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.screens.settings.base.CollectionMapSettingScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.BlockDataSetting;
import meteordevelopment.meteorclient.settings.IBlockData;
import meteordevelopment.meteorclient.utils.misc.IChangeable;
import meteordevelopment.meteorclient.utils.misc.ICopyable;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.misc.Names;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_332;
import net.minecraft.class_7923;
import org.jetbrains.annotations.Nullable;

public class BlockDataSettingScreen<T extends ICopyable<T> & ISerializable<T> & IChangeable & IBlockData<T>> extends CollectionMapSettingScreen<class_2248, T> {
   private final BlockDataSetting<T> setting;
   private boolean invalidate;

   public BlockDataSettingScreen(GuiTheme theme, BlockDataSetting<T> setting) {
      super(theme, "Configure Blocks", setting, (Map)setting.get(), class_7923.field_41175);
      this.setting = setting;
   }

   protected boolean includeValue(class_2248 value) {
      return value != class_2246.field_10124;
   }

   protected WWidget getValueWidget(class_2248 block) {
      return this.theme.itemWithLabel(block.method_8389().method_7854(), Names.get(block));
   }

   protected WWidget getDataWidget(class_2248 block, @Nullable T blockData) {
      WButton edit = this.theme.button(GuiRenderer.EDIT);
      edit.action = () -> {
         T data = blockData;
         if (blockData == null) {
            data = (this.setting.defaultData.get()).copy();
         }

         MeteorClient.mc.method_1507((data).createScreen(this.theme, block, this.setting));
         this.invalidate = true;
      };
      return edit;
   }

   protected void onRenderBefore(class_332 drawContext, float delta) {
      if (this.invalidate) {
         this.invalidateTable();
         this.invalidate = false;
      }

   }

   protected String[] getValueNames(class_2248 block) {
      return new String[]{Names.get(block), class_7923.field_41175.method_10221(block).toString()};
   }
}
