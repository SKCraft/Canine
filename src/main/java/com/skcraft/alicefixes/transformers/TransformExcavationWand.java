package com.skcraft.alicefixes.transformers;

import static org.objectweb.asm.Opcodes.*;

import com.skcraft.alicefixes.util.ASMHelper;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.ASM4;

public class TransformExcavationWand implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {

        if(name.equals("thaumcraft.common.items.wands.foci.ItemFocusExcavation")) {
            ClassReader cr = new ClassReader(bytes);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            cr.accept(new WandVisitor(cw), 0);
            return cw.toByteArray();
        }
        return bytes;
    }

    class WandVisitor extends ClassVisitor {
        public WandVisitor(ClassVisitor cv) {
            super(ASM4, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

            if(name.equals("onUsingFocusTick")) {
                return new OnUsingFocusTickVisitor(super.visitMethod(access, name,
                        desc, signature, exceptions));
            }
            return super.visitMethod(access, name, desc, signature,
                    exceptions);
        }
    }

    class OnUsingFocusTickVisitor extends MethodVisitor {
        public OnUsingFocusTickVisitor(MethodVisitor mv) {
            super(ASM4, mv);
        }

        @Override
        public void visitCode() {
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitFieldInsn(GETFIELD, ASMHelper.getObf("EntityPlayer"), ASMHelper.getObf("WorldObj"), "L" + ASMHelper.getObf("World") + ";");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ICONST_0);
            mv.visitMethodInsn(INVOKESTATIC, "thaumcraft/common/lib/Utils", "getTargetBlock", "(L" + ASMHelper.getObf("World") + ";L" + ASMHelper.getObf("Entity") + ";Z)L" + ASMHelper.getObf("MovingObjectPosition") + ";");
            mv.visitInsn(ICONST_0);
            mv.visitInsn(ICONST_0);
            mv.visitInsn(ICONST_0);
            mv.visitMethodInsn(INVOKESTATIC, "com/skcraft/alicefixes/util/ASMHelper", "canMine", "(L" + ASMHelper.getObf("EntityLivingBase") + ";Ljava/lang/Object;III)Z");
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
