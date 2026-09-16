package meteordevelopment.meteorclient.systems.hud;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.hud.screens.HudEditorScreen;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.other.Snapper;
import net.minecraft.class_2487;

public abstract class HudElement implements Snapper.Element, ISerializable<HudElement> {
   public final HudElementInfo<?> info;
   private boolean active;
   public final Settings settings = new Settings();
   public final HudBox box = new HudBox(this);
   public boolean autoAnchors = true;
   public int x;
   public int y;

   public HudElement(HudElementInfo<?> info) {
      this.info = info;
      this.active = true;
   }

   public boolean isActive() {
      return this.active;
   }

   public void toggle() {
      this.active = !this.active;
   }

   public void setSize(double width, double height) {
      this.box.setSize(width, height);
   }

   public void setPos(int x, int y) {
      if (this.autoAnchors) {
         this.box.setPos(x, y);
         this.box.xAnchor = XAnchor.Left;
         this.box.yAnchor = YAnchor.Top;
         this.box.updateAnchors();
      } else {
         this.box.setPos(this.box.x + (x - this.x), this.box.y + (y - this.y));
      }

      this.updatePos();
   }

   public void move(int deltaX, int deltaY) {
      this.box.move(deltaX, deltaY);
      this.updatePos();
   }

   public void updatePos() {
      this.x = this.box.getRenderX();
      this.y = this.box.getRenderY();
   }

   protected double alignX(double width, Alignment alignment) {
      return this.box.alignX((double)this.getWidth(), width, alignment);
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getWidth() {
      return this.box.width;
   }

   public int getHeight() {
      return this.box.height;
   }

   protected boolean isInEditor() {
      return !Utils.canUpdate() || HudEditorScreen.isOpen();
   }

   public void remove() {
      Hud.get().remove(this);
   }

   public void tick(HudRenderer renderer) {
   }

   public void render(HudRenderer renderer) {
   }

   public void onFontChanged() {
   }

   public WWidget getWidget(GuiTheme theme) {
      return null;
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10582("name", this.info.name);
      tag.method_10556("active", this.active);
      tag.method_10566("settings", this.settings.toTag());
      tag.method_10566("box", this.box.toTag());
      tag.method_10556("autoAnchors", this.autoAnchors);
      return tag;
   }

   public HudElement fromTag(class_2487 tag) {
      this.settings.reset();
      tag.method_10577("active").ifPresent((active1) -> this.active = active1);
      this.settings.fromTag(tag.method_68568("settings"));
      this.box.fromTag(tag.method_68568("box"));
      tag.method_10577("autoAnchors").ifPresent((autoAnchors1) -> this.autoAnchors = autoAnchors1);
      this.x = this.box.getRenderX();
      this.y = this.box.getRenderY();
      return this;
   }
}
