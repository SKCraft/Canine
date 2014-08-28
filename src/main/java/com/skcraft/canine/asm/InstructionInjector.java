package com.skcraft.canine.asm;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class InstructionInjector {

    private MethodVisitor mv;

    public InstructionInjector(MethodVisitor mv) {
        this.mv = mv;
    }

    public void begin() {
        Label l0 = new Label();
        mv.visitLabel(l0);
    }

    public void addInstanceVarToStack(String className, String varName, String varType) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, varName, varType);
    }

    public void addThisObjectToStack() {
        mv.visitVarInsn(ALOAD, 0);
    }

    public void addBooleanToStack(boolean value) {
        mv.visitInsn(value ? ICONST_1 : ICONST_0);
    }

    public void addLocalVarToStack(Class type, int index) {
        if(type == int.class) {
            mv.visitVarInsn(ILOAD, index);
        } else if(type == long.class) {
            mv.visitVarInsn(LLOAD, index);
        } else if(type == float.class) {
            mv.visitVarInsn(FLOAD, index);
        } else if(type == double.class) {
            mv.visitVarInsn(DLOAD, index);
        } else {
            mv.visitVarInsn(ALOAD, index);
        }
    }

    /**
     * Adds a call to a static method in the given class. Make sure all the proper
     * parameters are on the stack and in the right order before calling this.
     *
     * @param clazz the method's class
     * @param methodName the name of the method
     * @param descriptor the method's descriptor
     */
    public void addStaticMethodCall(String clazz, String methodName, MethodDescriptor descriptor) {
        mv.visitMethodInsn(INVOKESTATIC, clazz, methodName, descriptor.toString());
    }

    /**
     * Adds an instruction to check the the integer on the top of the stack is
     * non-zero.
     *
     * @return the label
     */
    public Label addIfNotEqual() {
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        return l1;
    }

    /**
     * Adds a return statement. Need to make this better... one day...
     *
     * @param type the return type of the method
     */
    public void addReturn(String type, Label label) {
        Label l2 = new Label();
        mv.visitLabel(l2);
        if(type.equals("boolean") || type.equals("int")) {
            mv.visitInsn(ICONST_0);
            mv.visitInsn(IRETURN);
        } else if(type.equals("void")) {
            mv.visitInsn(RETURN);
        } else {
            mv.visitVarInsn(ALOAD, ACONST_NULL);
            mv.visitInsn(ARETURN);
        }
        mv.visitLabel(label);
    }

    public void end() {
        mv.visitEnd();
    }
}
