package com.skcraft.alicefixes.util;

import com.skcraft.alicefixes.AliceTransformer;
import com.skcraft.alicefixes.Blacklist;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;

public class ASMHelper {

    public static boolean canMine(EntityLivingBase player, int x, int y, int z, boolean blacklist, Object clazz) {
        if(player == null) return true;

        if(blacklist) {
            int id = player.worldObj.getBlockId(x, y, z);
            for(int blacklisted : Blacklist.getBlacklist(clazz.getClass().getCanonicalName())) {
                if(id == blacklisted) {
                    return false;
                }
            }
        }

        if(!fireEvent(player, x, y, z)) {
            return false;
        }
        return true;
    }

    public static boolean canMine(EntityLivingBase player, boolean blacklist, Object clazz) {
        if(player == null) return true;

        MovingObjectPosition mop = LocationUtils.getTargetBlock(player.worldObj, player, false);

        if(mop == null) {
            return true;
        }

        if(blacklist) {
            int id = player.worldObj.getBlockId(mop.blockX, mop.blockY, mop.blockZ);
            for(int blacklisted : Blacklist.getBlacklist(clazz.getClass().getCanonicalName())) {
                if(id == blacklisted) {
                    return false;
                }
            }
        }

        if(!fireEvent(player, mop.blockX, mop.blockY, mop.blockZ)) {
            return false;
        }
        return true;
    }

    public static boolean canMine(TileEntity tile, byte facing, boolean blacklist, Object clazz) {
        if(tile == null) return true;

        CoordHelper target = new CoordHelper(tile.xCoord, tile.yCoord, tile.zCoord);
        target.addFacingAsOffset(facing);
        if(blacklist) {
            int id = tile.worldObj.getBlockId(target.x, target.y, target.z);
            for(int blacklisted : Blacklist.getBlacklist(clazz.getClass().getCanonicalName())) {
                if(id == blacklisted) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean fireEvent(EntityLivingBase player, int x, int y, int z) {
        try {
            Method m = player.getClass().getDeclaredMethod("getBukkitEntity");
            org.bukkit.entity.Entity ent = (org.bukkit.entity.Entity)m.invoke(player);
            if(ent instanceof Player) {
                Player bukkitPlayer = (Player)ent;
                org.bukkit.World bukkitWorld = bukkitPlayer.getWorld();
                BlockBreakEvent breakEvt = new BlockBreakEvent(bukkitWorld.getBlockAt(x, y, z), bukkitPlayer);
                Bukkit.getPluginManager().callEvent(breakEvt);
                if(breakEvt.isCancelled()) {
                    return false;
                }
                breakEvt.setCancelled(true);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void injectCode(MethodVisitor mv, int entityVar, int xVar, int yVar, int zVar, String returnType) {
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, entityVar);
        mv.visitVarInsn(ILOAD, xVar);
        mv.visitVarInsn(ILOAD, yVar);
        mv.visitVarInsn(ILOAD, zVar);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, "com/skcraft/alicefixes/util/ASMHelper",
                "canMine", "(L" + Obfs.get("EntityLivingBase") + ";IIIZLjava/lang/Object;)Z");
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv = injectReturn(mv, returnType);
        mv.visitLabel(l1);
        mv.visitCode();
    }

    public static void injectCode(MethodVisitor mv, int entityVar, String returnType) {
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, entityVar);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, "com/skcraft/alicefixes/util/ASMHelper",
                "canMine", "(L" + Obfs.get("EntityLivingBase") + ";ZLjava/lang/Object;)Z");
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv = injectReturn(mv, returnType);
        mv.visitLabel(l1);
        mv.visitCode();
    }

    public static void injectCode(MethodVisitor mv, String className, String facingName, String facingType, String returnType) {
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className.replace('.', '/'), facingName, AliceTransformer.primitives.get(facingType));
        mv.visitInsn(ICONST_1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC,
                "com/skcraft/alicefixes/util/ASMHelper",
                "canMine",
                "(L" + Obfs.get("TileEntity") + ";BZLjava/lang/Object;)Z");
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv = injectReturn(mv, returnType);
        mv.visitLabel(l1);
        mv.visitCode();
    }

    private static MethodVisitor injectReturn(MethodVisitor mv, String returnType) {
        if(returnType.equals("boolean")) {
            mv.visitInsn(ICONST_0);
            mv.visitInsn(IRETURN);
        } else if(returnType.equals("void")) {
            mv.visitInsn(RETURN);
        } else {
            //Not sure if this will turn out so well in every case...
            mv.visitVarInsn(ALOAD, ACONST_NULL);
            mv.visitInsn(ARETURN);
        }
        return mv;
    }
}
