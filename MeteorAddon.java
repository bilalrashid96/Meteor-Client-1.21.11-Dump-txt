package meteordevelopment.meteorclient.addons;

import meteordevelopment.meteorclient.utils.render.color.Color;

public abstract class MeteorAddon {
   public String name;
   public String[] authors;
   public final Color color = new Color(255, 255, 255);

   public abstract void onInitialize();

   public void onRegisterCategories() {
   }

   public abstract String getPackage();

   public String getWebsite() {
      return null;
   }

   public GithubRepo getRepo() {
      return null;
   }

   public String getCommit() {
      return null;
   }
}
