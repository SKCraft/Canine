package com.skcraft.alicefixes.transformers;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

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

public class TransformExcWand implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(name.equals("thaumcraft.common.items.wands.ItemWandExcavation")) {
            ClassReader cr = new ClassReader(bytes);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            cr.accept(new WandVisitor(cw), 0);
            return cw.toByteArray();
        }
        return bytes;
    }
    
    public static boolean canMine(EntityPlayer player, MovingObjectPosition pos) {
        if(pos != null) {
            int xPos = pos.blockX;
            int yPos = pos.blockY;
            int zPos = pos.blockZ;

            try {
                Method m = player.getClass()
                        .getDeclaredMethod("getBukkitEntity", new Class[] {});
                org.bukkit.entity.Entity ent = 
                        (org.bukkit.entity.Entity)m.invoke(player);
                if ((ent instanceof Player)) {
                    Player bukkitPlayer = (Player)ent;
                    org.bukkit.World bukkitWorld = bukkitPlayer.getWorld();
                    BlockBreakEvent breakEv = new BlockBreakEvent(
                            bukkitWorld.getBlockAt(xPos, yPos, zPos), bukkitPlayer);
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
    
    class WandVisitor extends ClassVisitor {
        
        public WandVisitor(ClassVisitor cv) {
            super(ASM4, cv);
        }
        
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                String signature, String[] exceptions) {
            
            if(name.equals("onUsingItemTick")) {
                return new OnUsingItemTickVisitor(super.visitMethod(access, name, desc,
                        signature, exceptions));
            }
            return super.visitMethod(access, name, desc, signature, 
                    exceptions);
        }
    }
    
    class OnUsingItemTickVisitor extends MethodVisitor {
        
        public OnUsingItemTickVisitor(MethodVisitor mv) {
            super(ASM4, mv);
        }
        
        @Override
        public void visitCode() {
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitFieldInsn(GETFIELD, ObfNames.ENTITY_PLAYER, ObfNames.WORLD_OBJ, "L" 
                    + ObfNames.WORLD + ";");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ICONST_0);
            mv.visitMethodInsn(INVOKESTATIC, "thaumcraft/common/lib/Utils", 
                    "getTargetBlock", "(L" + ObfNames.WORLD + ";L" + ObfNames.ENTITY_PLAYER 
                    + ";Z)L" + ObfNames.MOV_OBJ_POS + ";");
            mv.visitMethodInsn(INVOKESTATIC, "com/skcraft/alicefixes/transformers/TransformExcWand", 
                    "canMine", "(L" + ObfNames.ENTITY_PLAYER + ";L" + ObfNames.MOV_OBJ_POS + ";)Z");
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
