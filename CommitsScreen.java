package meteordevelopment.meteorclient.gui.screens;

import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.class_156;

public class CommitsScreen extends WindowScreen {
   private final MeteorAddon addon;
   private Commit[] commits;
   private int statusCode;

   public CommitsScreen(GuiTheme theme, MeteorAddon addon) {
      super(theme, "Commits for " + addon.name);
      this.addon = addon;
      this.locked = true;
      this.lockedAllowClose = true;
      MeteorExecutor.execute(() -> {
         GithubRepo repo = addon.getRepo();
         if (addon.getCommit() != null && !addon.getCommit().equals("${commit}")) {
            Http.Request request = Http.get(String.format("https://api.github.com/repos/%s/compare/%s...%s", repo.getOwnerName(), addon.getCommit(), repo.branch()));
            repo.authenticate(request);
            HttpResponse<Response> res = request.<Response>sendJsonResponse(Response.class);
            if (res.statusCode() == 200) {
               this.commits = ((Response)res.body()).commits;
               this.taskAfterRender = this::populateCommits;
            } else {
               this.statusCode = res.statusCode();
               this.taskAfterRender = this::populateError;
            }

         } else {
            this.statusCode = 404;
            this.taskAfterRender = this::populateError;
         }
      });
   }

   public void initWidgets() {
   }

   private void populateHeader(String headerMessage) {
      WHorizontalList l = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
      l.add(this.theme.label(headerMessage)).expandX();
      String website = this.addon.getWebsite();
      if (website != null) {
         ((WButton)l.add(this.theme.button("Website")).widget()).action = () -> class_156.method_668().method_670(website);
      }

      ((WButton)l.add(this.theme.button("GitHub")).widget()).action = () -> {
         GithubRepo repo = this.addon.getRepo();
         class_156.method_668().method_670(String.format("https://github.com/%s/tree/%s", repo.getOwnerName(), repo.branch()));
      };
   }

   private void populateError() {
      String var10000;
      switch (this.statusCode) {
         case 400:
            var10000 = "Connection dropped";
            break;
         case 401:
            var10000 = "Unauthorized";
            break;
         case 402:
         default:
            var10000 = "Error Code: " + this.statusCode;
            break;
         case 403:
            var10000 = "Rate-limited";
            break;
         case 404:
            var10000 = "Invalid commit hash";
      }

      String errorMessage = var10000;
      this.populateHeader("There was an error fetching commits: " + errorMessage);
      if (this.statusCode == 401) {
         this.add(this.theme.horizontalSeparator()).padVertical(this.theme.scale((double)8.0F)).expandX();
         WHorizontalList l = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
         l.add(this.theme.label("Consider using an authentication token: ")).expandX();
         ((WButton)l.add(this.theme.button("Authorization Guide")).widget()).action = () -> class_156.method_668().method_670("https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens");
      }

      this.locked = false;
   }

   private void populateCommits() {
      String text = "There are %d new commits";
      if (this.commits.length == 1) {
         text = "There is %d new commit";
      }

      this.populateHeader(String.format(text, this.commits.length));
      if (this.commits.length > 0) {
         this.add(this.theme.horizontalSeparator()).padVertical(this.theme.scale((double)8.0F)).expandX();
         WTable t = (WTable)this.add(this.theme.table()).expandX().widget();
         t.horizontalSpacing = (double)0.0F;

         for(Commit commit : this.commits) {
            String date = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(commit.commit.committer.date));
            ((WLabel)t.add(this.theme.label(date)).top().right().widget()).color = this.theme.textSecondaryColor();
            ((WLabel)t.add(this.theme.label(getMessage(commit))).widget()).action = () -> class_156.method_668().method_670(String.format("https://github.com/%s/commit/%s", this.addon.getRepo().getOwnerName(), commit.sha));
            t.row();
         }
      }

      this.locked = false;
   }

   private static String getMessage(Commit commit) {
      StringBuilder sb = new StringBuilder(" - ");
      String message = commit.commit.message;

      for(int i = 0; i < message.length(); ++i) {
         if (i >= 80) {
            sb.append("...");
            break;
         }

         char c = message.charAt(i);
         if (c == '\n') {
            sb.append("...");
            break;
         }

         sb.append(c);
      }

      return sb.toString();
   }

   private static class Response {
      public Commit[] commits;
   }

   private static class Commit {
      public String sha;
      public CommitInner commit;
   }

   private static class CommitInner {
      public Committer committer;
      public String message;
   }

   private static class Committer {
      public String date;
   }
}
