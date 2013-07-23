package com.skcraft.alicefixes.transformers;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Method;

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

public class TransformTradeWand implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        
        if(name.equals("thaumcraft.common.items.wands.ItemWandTrade")) {
            ClassReader cr = new ClassReader(bytes);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            cr.accept(new WandVisitor(cw), 0);
            return cw.toByteArray();
        }
        return bytes;
    }
    
    public static boolean canMine(EntityPlayer player, int x, int y, int z) {
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
        return true;
    }
    
    class WandVisitor extends ClassVisitor {
        
        public WandVisitor(ClassVisitor cv) {
            super(ASM4, cv);
        }
        
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                String signature, String[] exceptions) {
            
            if(name.equals("onItemUseFirst")) {
                return new OnItemUseFirstVisitor(super.visitMethod(access, name,
                        desc, signature, exceptions));
            } else if(name.equals("onBlockStartBreak")) {
                return new OnBlockStartBreakVisitor(super.visitMethod(access, name,
                        desc, signature, exceptions));
            }
            return super.visitMethod(access, name, desc, signature, 
                    exceptions);
        }
    }
    
    class OnItemUseFirstVisitor extends MethodVisitor {
        
        public OnItemUseFirstVisitor(MethodVisitor mv) {
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
            mv.visitMethodInsn(INVOKESTATIC, "com/skcraft/alicefixes/transformers/TransformTradeWand",
                    "canMine", "(L" + ObfNames.ENTITY_PLAYER + ";III)Z");
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
    
    class OnBlockStartBreakVisitor extends MethodVisitor {
        
        public OnBlockStartBreakVisitor(MethodVisitor mv) {
            super(ASM4, mv);
        }
        
        @Override
        public void visitCode() {
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, "com/skcraft/alicefixes/transformers/TransformTradeWand",
                    "canMine", "(L" + ObfNames.ENTITY_PLAYER + ";III)Z");
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
