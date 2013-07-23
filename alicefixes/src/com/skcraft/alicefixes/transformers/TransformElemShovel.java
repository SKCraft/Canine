package com.skcraft.alicefixes.transformers;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Method;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import com.skcraft.alicefixes.util.ObfNames;

import cpw.mods.fml.relauncher.IClassTransformer;

public class TransformElemShovel implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {

        if(name.equals("thaumcraft.common.items.equipment.ItemElementalShovel")) {
            ClassReader cr = new ClassReader(bytes);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            cr.accept(new ShovelVisitor(cw), 0);
            return cw.toByteArray();
        }
        return bytes;
    }

    public static boolean canMine(EntityLiving entity, int x, int y, int z) {
        if(entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            try {
                Method m = player.getClass()
                        .getDeclaredMethod("getBukkitEntity", new Class[] {});
                org.bukkit.entity.Entity ent = 
                        (org.bukkit.entity.Entity)m.invoke(player);
                if ((ent instanceof Player)) {
                    Player bukkitPlayer = (Player)ent;
                    org.bukkit.World bukkitWorld = bukkitPlayer.getWorld();
                    BlockBreakEvent breakEv = new BlockBreakEvent(
                            bukkitWorld.getBlockAt(x, y, z), bukkitPlayer);
                    Bukkit.getPluginManager().callEvent(breakEv);
                    if (breakEv.isCancelled()) {
                        return false;
                    }
                    breakEv.setCancelled(true);
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    class ShovelVisitor extends ClassVisitor {

        public ShovelVisitor(ClassVisitor cv) {
            super(ASM4, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                String signature, String[] exceptions) {

            if(name.equals("func_77648_a")) {
                return new OnItemUseVisitor(super.visitMethod(access, name,
                        desc, signature, exceptions));
            } else if(name.equals("func_77660_a")) {
                return new OnBlockDestroyedVisitor(super.visitMethod(access, name,
                        desc, signature, exceptions));
            }
            return super.visitMethod(access, name, desc, signature, 
                    exceptions);
        }
    }

    class OnItemUseVisitor extends MethodVisitor {

        public OnItemUseVisitor(MethodVisitor mv) {
            super(ASM4, mv);
        }

        @Override
        public void visitCode() {
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitMethodInsn(INVOKESTATIC, "com/skcraft/alicefixes/transformers/TransformElemShovel",
                    "canMine", "(L" + ObfNames.ENTITY_LIVING +";III)Z");
            Label l1 = new Label();
            mv.visitJumpInsn(IFNE, l1);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitInsn(ICONST_0);
            mv.visitInsn(IRETURN);
            mv.visitLabel(l1);
            mv.visitCode();
        }
    }

    class OnBlockDestroyedVisitor extends MethodVisitor {

        public OnBlockDestroyedVisitor(MethodVisitor mv) {
            super(ASM4, mv);
        }

        @Override
        public void visitCode() {
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitMethodInsn(INVOKESTATIC, "com/skcraft/alicefixes/transformers/TransformElemShovel",
                    "canMine", "(L" + ObfNames.ENTITY_LIVING + ";III)Z");
            Label l1 = new Label();
            mv.visitJumpInsn(IFNE, l1);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitInsn(ICONST_0);
            mv.visitInsn(IRETURN);
            mv.visitLabel(l1);
            mv.visitCode();
        }
    }

}
