package com.skcraft.alicefixes.transformers;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Method;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

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

public class TransformMiningLaser implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(name.equals("ic2.core.item.tool.EntityMiningLaser")) {
            return handleLaserTransform(bytes);
        }
        return bytes;
    }

    private byte[] handleLaserTransform(byte[] bytes) {
        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        
        cr.accept(new EntityLaserVisitor(cw), 0);
        return cw.toByteArray();
    }
    
    public static boolean canMine(Entity laser, EntityLiving owner) {
        Vec3 currentPos = Vec3.createVectorHelper(laser.posX, laser.posY,
                laser.posZ);
        Vec3 heading = Vec3.createVectorHelper(laser.posX + laser.motionX,
                laser.posY + laser.motionY, laser.posZ + laser.motionZ);
        MovingObjectPosition pos = laser.worldObj.rayTraceBlocks_do_do(
                currentPos, heading, false, true);

        if(pos != null) {
            int xPos = pos.blockX;
            int yPos = pos.blockY;
            int zPos = pos.blockZ;

            try {
                Method m = owner.getClass()
                        .getDeclaredMethod("getBukkitEntity", new Class[] {});
                org.bukkit.entity.Entity ent = 
                        (org.bukkit.entity.Entity)m.invoke(owner);
                if ((ent instanceof Player)) {
                    Player player = (Player)ent;
                    org.bukkit.World bukkitWorld = player.getWorld();
                    BlockBreakEvent breakEv = new BlockBreakEvent(
                            bukkitWorld.getBlockAt(xPos, yPos, zPos), player);
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
    
    class EntityLaserVisitor extends ClassVisitor {
        
        public EntityLaserVisitor(ClassVisitor cv) {
            super(ASM4, cv);
        }
        
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                String signature, String[] exceptions) {
            
            if(name.equals("canMine")) {
                return new CanMineVisitor(super.visitMethod(access, name, desc,
                        signature, exceptions));
            }
            return super.visitMethod(access, name, desc, signature, 
                    exceptions);
        }
    }
    
    class CanMineVisitor extends MethodVisitor {
        
        public CanMineVisitor(MethodVisitor mv) {
            super(ASM4, mv);
        }
        
        @Override
        public void visitCode() {
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "ic2/core/item/tool/EntityMiningLaser",
                    "owner", "L" + ObfNames.ENTITY_LIVING + ";");
            mv.visitMethodInsn(INVOKESTATIC,
                    "com/skcraft/alicefixes/transformers/TransformMiningLaser",
                    "canMine", 
                    "(L" + ObfNames.ENTITY + ";L" 
                    + ObfNames.ENTITY_LIVING + ";)Z");
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
