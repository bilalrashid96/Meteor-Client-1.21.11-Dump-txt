package meteordevelopment.meteorclient.systems.friends;

import com.mojang.util.UndashedUuid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.class_1657;
import net.minecraft.class_2487;
import net.minecraft.class_2520;
import net.minecraft.class_640;
import org.jetbrains.annotations.NotNull;

public class Friends extends System<Friends> implements Iterable<Friend> {
   private final List<Friend> friends = new ArrayList();

   public Friends() {
      super("friends");
   }

   public static Friends get() {
      return (Friends)Systems.get(Friends.class);
   }

   public boolean add(Friend friend) {
      if (!friend.name.isEmpty() && !friend.name.contains(" ")) {
         if (this.get(friend.name) != null) {
            return false;
         } else {
            this.friends.add(friend);
            this.save();
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean remove(Friend friend) {
      if (this.friends.remove(friend)) {
         this.save();
         return true;
      } else {
         return false;
      }
   }

   public Friend get(String name) {
      for(Friend friend : this.friends) {
         if (friend.name.equalsIgnoreCase(name)) {
            return friend;
         }
      }

      return null;
   }

   public Friend get(class_1657 player) {
      return this.get(player.method_5477().getString());
   }

   public Friend get(class_640 player) {
      return this.get(player.method_2966().name());
   }

   public boolean isFriend(class_1657 player) {
      return player != null && this.get(player) != null;
   }

   public boolean isFriend(class_640 player) {
      return this.get(player) != null;
   }

   public boolean shouldAttack(class_1657 player) {
      return !this.isFriend(player);
   }

   public int count() {
      return this.friends.size();
   }

   public boolean isEmpty() {
      return this.friends.isEmpty();
   }

   public @NotNull Iterator<Friend> iterator() {
      return this.friends.iterator();
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10566("friends", NbtUtils.listToTag(this.friends));
      return tag;
   }

   public Friends fromTag(class_2487 tag) {
      this.friends.clear();

      for(class_2520 itemTag : tag.method_68569("friends")) {
         class_2487 friendTag = (class_2487)itemTag;
         if (friendTag.method_10545("name")) {
            String name = friendTag.method_68564("name", "");
            if (this.get(name) == null) {
               String uuid = friendTag.method_68564("id", "");
               Friend friend = !uuid.isBlank() ? new Friend(name, UndashedUuid.fromStringLenient(uuid)) : new Friend(name);
               this.friends.add(friend);
            }
         }
      }

      Collections.sort(this.friends);
      MeteorExecutor.execute(() -> this.friends.forEach(Friend::updateInfo));
      return this;
   }
}
