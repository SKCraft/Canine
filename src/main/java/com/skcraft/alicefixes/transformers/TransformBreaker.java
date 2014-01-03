package com.skcraft.alicefixes.transformers;

import com.skcraft.alicefixes.util.ASMHelper;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

public class TransformBreaker implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(name.equals("thermalexpansion.block.device.TileBreaker")) {
            ClassReader cr = new ClassReader(bytes);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            cr.accept(new BreakerVisitor(cw), 0);
            return cw.toByteArray();
        }
        return bytes;
    }

    class BreakerVisitor extends ClassVisitor {
        public BreakerVisitor(ClassVisitor cv) {
            super(ASM4, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

            if(name.equals("breakBlock")) {
                return new BreakBlockVisitor(super.visitMethod(access, name,
                        desc, signature, exceptions));
            }
            return super.visitMethod(access, name, desc, signature,
                    exceptions);
        }
    }

    class BreakBlockVisitor extends MethodVisitor {
        public BreakBlockVisitor(MethodVisitor mv) {
            super(ASM4, mv);
        }

        @Override
        public void visitCode() {
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD,
                    "thermalexpansion/block/device/TileBreaker",
                    "facing",
                    "B");
            mv.visitMethodInsn(INVOKESTATIC,
                    "com/skcraft/alicefixes/util/ASMHelper",
                    "canTileMine",
                    "(L" + ASMHelper.getObf("TileEntity") + ";B)Z");
            Label l1 = new Label();
            mv.visitJumpInsn(IFNE, l1);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitInsn(RETURN);
            mv.visitLabel(l1);
            mv.visitCode();
        }
    }
}
