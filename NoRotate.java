package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class NoRotate extends Module {
   public NoRotate() {
      super(Categories.Player, "no-rotate", "Attempts to block rotations sent from server to client.");
   }
}
