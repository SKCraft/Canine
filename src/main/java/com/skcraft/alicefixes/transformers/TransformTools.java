package com.skcraft.alicefixes.transformers;

import com.skcraft.alicefixes.util.ASMHelper;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM4;

public class TransformTools implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {

        if(name.equals("thaumcraft.common.items.equipment.ItemElementalAxe") ||
                name.equals("thaumcraft.common.items.equipment.ItemElementalShovel") ||
                name.equals("gravisuite.ItemVajra")) {
            ClassReader cr = new ClassReader(bytes);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            cr.accept(new ToolVisitor(cw), 0);
            return cw.toByteArray();
        }
        return bytes;
    }

    class ToolVisitor extends ClassVisitor {
        public ToolVisitor(ClassVisitor cv) {
            super(ASM4, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

            if(name.equals("func_77648_a") || name.equals("func_77660_a") || (name.equals("a") &&
                    desc.equals("(L" + ASMHelper.getObf("ItemStack") + ";L" + ASMHelper.getObf("EntityPlayer") +
                            ";L" + ASMHelper.getObf("World") + ";IIIIFFF)Z"))) {
                return new ToolMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions), name.equals("func_77660_a") ? false : true);
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    class ToolMethodVisitor extends MethodVisitor {

        boolean onBlockDestroyed;

        public ToolMethodVisitor(MethodVisitor mv, boolean onBlockDestroyed) {
            super(ASM4, mv);
            this.onBlockDestroyed = onBlockDestroyed;
        }

        @Override
        public void visitCode() {
            if(onBlockDestroyed) {
                ASMHelper.injectCodeBoolXYZ(mv, 2, 4, 5, 6);
            } else {
                ASMHelper.injectCodeBoolXYZ(mv, 7, 4, 5, 6);
            }
        }
    }
}
