package meteordevelopment.meteorclient.systems.accounts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.accounts.types.CrackedAccount;
import meteordevelopment.meteorclient.systems.accounts.types.MicrosoftAccount;
import meteordevelopment.meteorclient.systems.accounts.types.SessionAccount;
import meteordevelopment.meteorclient.systems.accounts.types.TheAlteningAccount;
import meteordevelopment.meteorclient.utils.misc.NbtException;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.class_2487;
import org.jetbrains.annotations.NotNull;

public class Accounts extends System<Accounts> implements Iterable<Account<?>> {
   private List<Account<?>> accounts = new ArrayList();

   public Accounts() {
      super("accounts");
   }

   public static Accounts get() {
      return (Accounts)Systems.get(Accounts.class);
   }

   public void add(Account<?> account) {
      this.accounts.add(account);
      this.save();
   }

   public boolean exists(Account<?> account) {
      return this.accounts.contains(account);
   }

   public void remove(Account<?> account) {
      if (this.accounts.remove(account)) {
         this.save();
      }

   }

   public int size() {
      return this.accounts.size();
   }

   public @NotNull Iterator<Account<?>> iterator() {
      return this.accounts.iterator();
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10566("accounts", NbtUtils.listToTag(this.accounts));
      return tag;
   }

   public Accounts fromTag(class_2487 tag) {
      MeteorExecutor.execute(() -> this.accounts = NbtUtils.<Account<?>>listFromTag(tag.method_68569("accounts"), (tag1) -> {
            class_2487 t = (class_2487)tag1;
            if (!t.method_10545("type")) {
               return null;
            } else {
               AccountType type = AccountType.valueOf(t.method_68564("type", ""));

               try {
                  Object var10000;
                  switch (type) {
                     case Cracked -> var10000 = (CrackedAccount)(new CrackedAccount((String)null)).fromTag(t);
                     case Microsoft -> var10000 = (MicrosoftAccount)(new MicrosoftAccount((String)null)).fromTag(t);
                     case TheAltening -> var10000 = (new TheAlteningAccount((String)null)).fromTag(t);
                     case Session -> var10000 = (new SessionAccount((String)null)).fromTag(t);
                     default -> throw new MatchException((String)null, (Throwable)null);
                  }

                  return (Account)var10000;
               } catch (NbtException var4) {
                  return null;
               }
            }
         }));
      return this;
   }
}
