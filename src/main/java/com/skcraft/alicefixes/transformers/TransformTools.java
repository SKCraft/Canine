package com.skcraft.alicefixes.transformers;

import com.skcraft.alicefixes.util.ASMHelper;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM4;

public class TransformTools implements IClassTransformer {

    private String className;
    private String method;
    private String desc;
    
    public TransformTools(String className, String method, String desc) {
        this.className = className;
        this.method = method;
        this.desc = desc;
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {

        if(name.equals(className)) {
            ClassReader cr = new ClassReader(bytes);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            cr.accept(new ToolVisitor(cw, method, desc), 0);
            return cw.toByteArray();
        }
        return bytes;
    }

    class ToolVisitor extends ClassVisitor {
    
        private String method;
        private String desc;
        
        public ToolVisitor(ClassVisitor cv, String method, String desc) {
            super(ASM4, cv);
            this.method = method;
            this.desc = desc;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

            if(name.equals(method) && desc.equals(this.desc)) {
                return new ToolMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions),
                        !name.equals("func_77660_a"));
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    class ToolMethodVisitor extends MethodVisitor {

        boolean alternate;

        public ToolMethodVisitor(MethodVisitor mv, boolean alternate) {
            super(ASM4, mv);
            this.alternate = alternate;
        }

        @Override
        public void visitCode() {
            if(alternate) {
                ASMHelper.injectCodeBoolXYZ(mv, 2, 4, 5, 6);
            } else {
                ASMHelper.injectCodeBoolXYZ(mv, 7, 4, 5, 6);
            }
        }
    }
}
