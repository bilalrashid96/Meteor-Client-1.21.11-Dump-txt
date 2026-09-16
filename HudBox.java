package meteordevelopment.meteorclient.systems.hud;

import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import net.minecraft.class_2487;

public class HudBox implements ISerializable<HudBox> {
   private final HudElement element;
   public XAnchor xAnchor;
   public YAnchor yAnchor;
   public int x;
   public int y;
   int width;
   int height;

   public HudBox(HudElement element) {
      this.xAnchor = XAnchor.Left;
      this.yAnchor = YAnchor.Top;
      this.element = element;
   }

   public void setSize(double width, double height) {
      if (width >= (double)0.0F) {
         this.width = (int)Math.ceil(width);
      }

      if (height >= (double)0.0F) {
         this.height = (int)Math.ceil(height);
      }

   }

   public void setPos(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void setXAnchor(XAnchor anchor) {
      if (this.xAnchor != anchor) {
         int renderX = this.getRenderX();
         switch (anchor) {
            case Left -> this.x = renderX;
            case Center -> this.x = renderX + this.width / 2 - Utils.getWindowWidth() / 2;
            case Right -> this.x = renderX + this.width - Utils.getWindowWidth();
         }

         this.xAnchor = anchor;
      }

   }

   public void setYAnchor(YAnchor anchor) {
      if (this.yAnchor != anchor) {
         int renderY = this.getRenderY();
         switch (anchor) {
            case Top -> this.y = renderY;
            case Center -> this.y = renderY + this.height / 2 - Utils.getWindowHeight() / 2;
            case Bottom -> this.y = renderY + this.height - Utils.getWindowHeight();
         }

         this.yAnchor = anchor;
      }

   }

   public void updateAnchors() {
      this.setXAnchor(this.getXAnchor((double)this.getRenderX()));
      this.setYAnchor(this.getYAnchor((double)this.getRenderY()));
   }

   public void move(int deltaX, int deltaY) {
      this.x += deltaX;
      this.y += deltaY;
      if (this.element.autoAnchors) {
         this.updateAnchors();
      }

      int border = (Integer)Hud.get().border.get();
      if (this.xAnchor == XAnchor.Left && this.x < border) {
         this.x = border;
      } else if (this.xAnchor == XAnchor.Right && this.x > border) {
         this.x = border;
      }

      if (this.yAnchor == YAnchor.Top && this.y < border) {
         this.y = border;
      } else if (this.yAnchor == YAnchor.Bottom && this.y > border) {
         this.y = border;
      }

   }

   public XAnchor getXAnchor(double x) {
      double splitLeft = (double)Utils.getWindowWidth() / (double)3.0F;
      double splitRight = splitLeft * (double)2.0F;
      boolean left = x <= splitLeft;
      boolean right = x + (double)this.width >= splitRight;
      if ((!left || !right) && (left || right)) {
         return left ? XAnchor.Left : XAnchor.Right;
      } else {
         return XAnchor.Center;
      }
   }

   public YAnchor getYAnchor(double y) {
      double splitTop = (double)Utils.getWindowHeight() / (double)3.0F;
      double splitBottom = splitTop * (double)2.0F;
      boolean top = y <= splitTop;
      boolean bottom = y + (double)this.height >= splitBottom;
      if ((!top || !bottom) && (top || bottom)) {
         return top ? YAnchor.Top : YAnchor.Bottom;
      } else {
         return YAnchor.Center;
      }
   }

   public int getRenderX() {
      int var10000;
      switch (this.xAnchor) {
         case Left -> var10000 = this.x;
         case Center -> var10000 = Utils.getWindowWidth() / 2 - this.width / 2 + this.x;
         case Right -> var10000 = Utils.getWindowWidth() - this.width + this.x;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public int getRenderY() {
      int var10000;
      switch (this.yAnchor) {
         case Top -> var10000 = this.y;
         case Center -> var10000 = Utils.getWindowHeight() / 2 - this.height / 2 + this.y;
         case Bottom -> var10000 = Utils.getWindowHeight() - this.height + this.y;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public double alignX(double selfWidth, double width, Alignment alignment) {
      XAnchor anchor = this.xAnchor;
      if (alignment == Alignment.Left) {
         anchor = XAnchor.Left;
      } else if (alignment == Alignment.Center) {
         anchor = XAnchor.Center;
      } else if (alignment == Alignment.Right) {
         anchor = XAnchor.Right;
      }

      double var10000;
      switch (anchor) {
         case Left -> var10000 = (double)0.0F;
         case Center -> var10000 = selfWidth / (double)2.0F - width / (double)2.0F;
         case Right -> var10000 = selfWidth - width;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10582("x-anchor", this.xAnchor.name());
      tag.method_10582("y-anchor", this.yAnchor.name());
      tag.method_10569("x", this.x);
      tag.method_10569("y", this.y);
      return tag;
   }

   public HudBox fromTag(class_2487 tag) {
      if (tag.method_10558("x-anchor").isPresent()) {
         this.xAnchor = XAnchor.valueOf((String)tag.method_10558("x-anchor").get());
      }

      if (tag.method_10558("y-anchor").isPresent()) {
         this.yAnchor = YAnchor.valueOf((String)tag.method_10558("y-anchor").get());
      }

      if (tag.method_10550("x").isPresent()) {
         this.x = (Integer)tag.method_10550("x").get();
      }

      if (tag.method_10550("y").isPresent()) {
         this.y = (Integer)tag.method_10550("y").get();
      }

      return this;
   }
}
