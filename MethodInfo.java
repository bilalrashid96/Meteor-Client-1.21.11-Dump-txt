package meteordevelopment.meteorclient.asm;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class MethodInfo {
   private String owner;
   private String name;
   private String descriptor;

   public MethodInfo(String owner, String name, Descriptor descriptor, boolean map) {
      if (map) {
         MappingResolver mappings = FabricLoader.getInstance().getMappingResolver();
         String ownerDot = owner.replace('/', '.');
         if (owner != null) {
            this.owner = mappings.mapClassName("intermediary", ownerDot).replace('.', '/');
         }

         if (name != null && descriptor != null) {
            this.name = mappings.mapMethodName("intermediary", ownerDot, name, descriptor.toString(true, false));
         }
      } else {
         this.owner = owner;
         this.name = name;
      }

      if (descriptor != null) {
         this.descriptor = descriptor.toString(true, map);
      }

   }

   public boolean equals(MethodNode method) {
      return (this.name == null || method.name.equals(this.name)) && (this.descriptor == null || method.desc.equals(this.descriptor));
   }

   public boolean equals(MethodInsnNode insn) {
      return (this.owner == null || insn.owner.equals(this.owner)) && (this.name == null || insn.name.equals(this.name)) && (this.descriptor == null || insn.desc.equals(this.descriptor));
   }
}
