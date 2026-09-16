package meteordevelopment.meteorclient.asm.transformers;

import java.util.ListIterator;
import meteordevelopment.meteorclient.asm.AsmTransformer;
import meteordevelopment.meteorclient.asm.Descriptor;
import meteordevelopment.meteorclient.asm.MethodInfo;
import meteordevelopment.meteorclient.systems.modules.misc.AntiPacketKick;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class PacketInflaterTransformer extends AsmTransformer {
   private final MethodInfo decodeMethod = new MethodInfo("net/minecraft/class_2532", "decode", new Descriptor(new String[]{"Lio/netty/channel/ChannelHandlerContext;", "Lio/netty/buffer/ByteBuf;", "Ljava/util/List;", "V"}), true);

   public PacketInflaterTransformer() {
      super(mapClassName("net/minecraft/class_2532"));
   }

   public void transform(ClassNode klass) {
      MethodNode method = this.getMethod(klass, this.decodeMethod);
      if (method == null) {
         error("[Meteor Client] Could not find method PacketInflater.decode()");
      }

      int newCount = 0;
      LabelNode label = new LabelNode(new Label());
      ListIterator var5 = method.instructions.iterator();

      while(var5.hasNext()) {
         AbstractInsnNode insn = (AbstractInsnNode)var5.next();
         if (insn instanceof TypeInsnNode typeInsn) {
            if (typeInsn.getOpcode() == 187 && typeInsn.desc.equals("io/netty/handler/codec/DecoderException")) {
               ++newCount;
               if (newCount == 2) {
                  InsnList list = new InsnList();
                  list.add(new MethodInsnNode(184, "meteordevelopment/meteorclient/systems/modules/Modules", "get", "()Lmeteordevelopment/meteorclient/systems/modules/Modules;", false));
                  list.add(new LdcInsnNode(Type.getType(AntiPacketKick.class)));
                  list.add(new MethodInsnNode(182, "meteordevelopment/meteorclient/systems/modules/Modules", "isActive", "(Ljava/lang/Class;)Z", false));
                  list.add(new JumpInsnNode(154, label));
                  method.instructions.insertBefore(insn, list);
               }
               continue;
            }
         }

         if (newCount == 2 && insn.getOpcode() == 191) {
            method.instructions.insert(insn, label);
            return;
         }
      }

      error("[Meteor Client] Failed to modify PacketInflater.decode()");
   }
}
