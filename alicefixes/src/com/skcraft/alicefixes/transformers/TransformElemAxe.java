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

public class TransformElemAxe implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        
        if(name.equals("thaumcraft.common.items.equipment.ItemElementalAxe")) {
            ClassReader cr = new ClassReader(bytes);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            cr.accept(new AxeVisitor(cw), 0);
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
    
    class AxeVisitor extends ClassVisitor {
        
        public AxeVisitor(ClassVisitor cv) {
            super(ASM4, cv);
        }
        
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                String signature, String[] exceptions) {
            
            if(name.equals("breakFurthestBlock")) {
                return new BreakFurthestBlockVisitor(super.visitMethod(access, name,
                        desc, signature, exceptions));
            }
            return super.visitMethod(access, name, desc, signature, 
                    exceptions);
        }
    }
    
    class BreakFurthestBlockVisitor extends MethodVisitor {
        
        public BreakFurthestBlockVisitor(MethodVisitor mv) {
            super(ASM4, mv);
        }
        
        @Override
        public void visitCode() {
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, "com/skcraft/alicefixes/transformers/TransformElemAxe",
                    "canMine", "(L" + ObfNames.ENTITY_PLAYER +";III)Z");
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
