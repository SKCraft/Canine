package com.skcraft.alicefixes.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class ASMHelper {

    public static final Map<String, String> mappings = new HashMap<String, String>();

    public static boolean canMine(EntityLivingBase player, Object obj, int x, int y, int z) {
        if(player == null) return true;

        if(obj != null) {
            if(obj instanceof MovingObjectPosition) {
                MovingObjectPosition pos = (MovingObjectPosition)obj;
                x = pos.blockX;
                y = pos.blockY;
                z = pos.blockZ;
            }
        }

        if(!fireEvent(player, x, y, z)) {
            return false;
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

    /**
     * Should be used with methods that return a boolean value and have x, y, and z coordinates of the block readily available.
     *
     * @param mv
     * @param entityVar
     * @param xVar
     * @param yVar
     * @param zVar
     */
    public static void injectCodeBoolXYZ(MethodVisitor mv, int entityVar, int xVar, int yVar, int zVar) {
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, entityVar);
        mv.visitInsn(ACONST_NULL);
        mv.visitVarInsn(ILOAD, xVar);
        mv.visitVarInsn(ILOAD, yVar);
        mv.visitVarInsn(ILOAD, zVar);
        mv.visitMethodInsn(INVOKESTATIC, "com/skcraft/alicefixes/util/ASMHelper",
                "canMine", "(L" + ASMHelper.getObf("EntityLivingBase") + ";Ljava/lang/Object;III)Z");
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitInsn(ICONST_0);
        mv.visitInsn(IRETURN);
        mv.visitLabel(l1);
        mv.visitCode();
    }

    public static String getObf(String key) {
        return mappings.get(key);
    }

    static {
        mappings.put("EntityPlayer", "uf");
        mappings.put("Entity", "nn");
        mappings.put("MovingObjectPosition", "ata");
        mappings.put("World", "abw");
        mappings.put("ItemStack", "ye");
        mappings.put("EntityLivingBase", "of");
        mappings.put("WorldObj", "field_70170_p");
    }
}
