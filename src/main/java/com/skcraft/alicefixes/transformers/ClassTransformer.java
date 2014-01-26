package com.skcraft.alicefixes.transformers;

import com.skcraft.alicefixes.jsongenerator.JsonHelperObject;
import com.skcraft.alicefixes.util.ASMHelper;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

import static org.objectweb.asm.Opcodes.ASM4;

public class ClassTransformer implements IClassTransformer {

    private JsonHelperObject patch;
    private String desc;
    private List<Integer> vars;
    
    public ClassTransformer(JsonHelperObject patch, String desc, List vars) {
        this.patch = patch;
        this.desc = desc;
        this.vars = vars;
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {

        if(name.equals(patch.className)) {
            ClassReader cr = new ClassReader(bytes);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            cr.accept(new ToolVisitor(cw, patch, desc, vars), 0);
            return cw.toByteArray();
        }
        return bytes;
    }

    class ToolVisitor extends ClassVisitor {
    
        private JsonHelperObject patch;
        private String desc;
        private List<Integer> vars;
        
        public ToolVisitor(ClassVisitor cv, JsonHelperObject patch, String desc, List vars) {
            super(ASM4, cv);
            this.patch = patch;
            this.desc = desc;
            this.vars = vars;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

            if(name.equals(patch.methodName) && desc.equals(this.desc)) {
                return new ToolMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions), vars, patch);
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    class ToolMethodVisitor extends MethodVisitor {

        List<Integer> vars;
        JsonHelperObject patch;

        public ToolMethodVisitor(MethodVisitor mv, List vars, JsonHelperObject patch) {
            super(ASM4, mv);
            this.vars = vars;
            this.patch = patch;
        }

        @Override
        public void visitCode() {
            if(!patch.blacklist) {
                if(vars.size() == 1) {
                    ASMHelper.injectCode(mv, vars.get(0), patch.returnType);
                } else if(vars.size() == 4) {
                    ASMHelper.injectCode(mv, vars.get(0), vars.get(1), vars.get(2), vars.get(3), patch.returnType);
                }
            } else {
                if(patch.tileEntity && patch.blacklist) {
                    ASMHelper.injectCode(mv, patch.className, patch.facingVar, patch.varType, patch.returnType);
                }
            }
        }
    }
}
