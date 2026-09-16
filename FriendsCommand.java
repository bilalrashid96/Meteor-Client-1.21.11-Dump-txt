package meteordevelopment.meteorclient.commands.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.FriendArgumentType;
import meteordevelopment.meteorclient.commands.arguments.PlayerListEntryArgumentType;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.class_124;
import net.minecraft.class_2172;

public class FriendsCommand extends Command {
   public FriendsCommand() {
      super("friends", "Manages friends.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(literal("add").then(argument("player", PlayerListEntryArgumentType.create()).executes((context) -> {
         GameProfile profile = PlayerListEntryArgumentType.get(context).method_2966();
         Friend friend = new Friend(profile.name(), profile.id());
         if (Friends.get().add(friend)) {
            ChatUtils.sendMsg(friend.hashCode(), class_124.field_1080, "Added (highlight)%s (default)to friends.".formatted(friend.getName()));
         } else {
            this.error("Already friends with that player.", new Object[0]);
         }

         return 1;
      })));
      builder.then(literal("remove").then(argument("friend", FriendArgumentType.create()).executes((context) -> {
         Friend friend = FriendArgumentType.get(context);
         if (friend == null) {
            this.error("Not friends with that player.", new Object[0]);
            return 1;
         } else {
            if (Friends.get().remove(friend)) {
               ChatUtils.sendMsg(friend.hashCode(), class_124.field_1080, "Removed (highlight)%s (default)from friends.".formatted(friend.getName()));
            } else {
               this.error("Failed to remove that friend.", new Object[0]);
            }

            return 1;
         }
      })));
      builder.then(literal("list").executes((context) -> {
         this.info("--- Friends ((highlight)%s(default)) ---", new Object[]{Friends.get().count()});
         Friends.get().forEach((friend) -> ChatUtils.info("(highlight)%s".formatted(friend.getName())));
         return 1;
      }));
   }
}
