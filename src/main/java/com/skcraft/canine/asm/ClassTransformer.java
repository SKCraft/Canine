package com.skcraft.canine.asm;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.tileentity.TileEntity;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.*;

import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class ClassTransformer implements IClassTransformer {

    private TransformInfo info;

    public ClassTransformer(TransformInfo info) {
        this.info = info;
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        try {
            if (name.equals(info.className)) {
                ClassReader cr = new ClassReader(bytes);
                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
                cr.accept(new TargetClassVisitor(cw, info, TileEntity.class.isAssignableFrom(Class.forName(cr.getSuperName().replace("/", ".")))), 0);
                return cw.toByteArray();
            }
        } catch(ClassNotFoundException e) {
            FMLLog.log("Canine", Level.WARN, "%s", "Failed to transform class: " + info.className);
        }
        return bytes;
    }

    class TargetClassVisitor extends ClassVisitor {

        private TransformInfo info;
        private boolean tileEntity;

        public TargetClassVisitor(ClassVisitor cv, TransformInfo info, boolean tileEntity) {
            super(ASM4, cv);
            this.info = info;
            this.tileEntity = tileEntity;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

            if(name.equals(info.methodName) && desc.equals(info.desc.toString())) {
                return new TargetMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions), info, tileEntity);
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    class TargetMethodVisitor extends MethodVisitor {

        private TransformInfo info;
        private boolean tileEntity;

        public TargetMethodVisitor(MethodVisitor mv, TransformInfo info, boolean tileEntity) {
            super(ASM4, mv);
            this.info = info;
            this.tileEntity = tileEntity;
        }

        // TODO: make this method less bad
        @Override
        public void visitCode() {
            try {
                InstructionInjector injector = new InstructionInjector(mv);
                if (tileEntity) {
                    injector.begin();
                    injector.addThisObjectToStack();
                    injector.addThisObjectToStack();
                    injector.addInstanceVarToStack(info.className.replace(".", "/"), info.facingVar, info.varType);
                    injector.addThisObjectToStack();
                    injector.addStaticMethodCall(ASMHelper.class.getCanonicalName().replace(".", "/"), "canMine",
                            new MethodDescriptor("boolean", new String[] {
                                    TileEntity.class.getCanonicalName().replace(".", "/"),
                                    byte.class.getCanonicalName(),
                                    Object.class.getCanonicalName().replace(".", "/")
                            }));
                    Label l1 = injector.addIfNotEqual();
                    injector.addReturn(info.desc.getReturnType(), l1);
                    injector.end();
                } else {
                    Map<String, Integer> indexes = info.desc.getParamIndexes(new String[] {
                            EntityLivingBase.class.getCanonicalName().replace(".", "/"),
                            "XCOORD",
                            "YCOORD",
                            "ZCOORD"
                    });
                    if(indexes.containsKey("XCOORD") && indexes.containsKey("YCOORD") && indexes.containsKey("ZCOORD")) {
                        // We can pass coordinates to use...
                        injector.begin();
                        injector.addLocalVarToStack(Object.class, indexes.get(EntityLivingBase.class.getCanonicalName().replace(".", "/")));
                        injector.addLocalVarToStack(int.class, indexes.get("XCOORD"));
                        injector.addLocalVarToStack(int.class, indexes.get("YCOORD"));
                        injector.addLocalVarToStack(int.class, indexes.get("ZCOORD"));
                        injector.addThisObjectToStack();
                        injector.addStaticMethodCall(ASMHelper.class.getCanonicalName().replace(".", "/"), "canMine",
                                new MethodDescriptor("boolean", new String[] {
                                        EntityLivingBase.class.getCanonicalName().replace(".", "/"),
                                        int.class.getCanonicalName(),
                                        int.class.getCanonicalName(),
                                        int.class.getCanonicalName(),
                                        Object.class.getCanonicalName().replace(".", "/")
                                }));
                        Label l1 = injector.addIfNotEqual();
                        injector.addReturn(info.desc.getReturnType(), l1);
                        injector.end();
                    } else if(indexes.containsKey(EntityLivingBase.class.getCanonicalName().replace(".", "/"))) {
                        // We'll have to figure out coordinates on our own based on where the player is looking...
                        injector.begin();
                        injector.addLocalVarToStack(Object.class, indexes.get(EntityLivingBase.class.getCanonicalName().replace(".", "/")));
                        injector.addThisObjectToStack();
                        injector.addStaticMethodCall(ASMHelper.class.getCanonicalName().replace(".", "/"), "canMine",
                                new MethodDescriptor("boolean", new String[] {
                                        EntityLivingBase.class.getCanonicalName().replace(".", "/"),
                                        Object.class.getCanonicalName().replace(".", "/")
                                }));
                        Label l1 = injector.addIfNotEqual();
                        injector.addReturn(info.desc.getReturnType(), l1);
                        injector.end();
                    }
                }
            } catch (Throwable e) {
                FMLLog.log("Canine", Level.WARN, "%s", "Failed to find or patch class: " + info.className);
                e.printStackTrace();
            }
        }
    }
}
