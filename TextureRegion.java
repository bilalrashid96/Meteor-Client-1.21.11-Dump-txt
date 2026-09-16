package meteordevelopment.meteorclient.gui.renderer.packer;

public class TextureRegion {
   public double x1;
   public double y1;
   public double x2;
   public double y2;
   public double diagonal;

   public TextureRegion(double width, double height) {
      this.diagonal = Math.sqrt(width * width + height * height);
   }
}
