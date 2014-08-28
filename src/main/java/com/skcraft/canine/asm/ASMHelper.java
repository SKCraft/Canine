package com.skcraft.canine.asm;

import com.skcraft.canine.Blacklist;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.apache.logging.log4j.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ASMHelper {

    public static Map<String, String> primitives = new HashMap<String, String>();
    private static Method getEntity;

    public static boolean canMine(EntityLivingBase player, int x, int y, int z, Object obj) {
        return player == null || !isBlacklisted(player.worldObj.getBlock(x, y, z), obj.getClass().getCanonicalName())
                && fireEvent(player, x, y, z);
    }

    public static boolean canMine(EntityLivingBase player, Object obj) {
        if(player == null) return true;

        float var5 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch);
        float var6 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw);
        double x = player.prevPosX + (player.posX - player.prevPosX);
        double y = player.prevPosY + (player.posY - player.prevPosY) + 1.62D - player.yOffset;
        double z = player.prevPosZ + (player.posZ - player.prevPosZ);
        Vec3 vec1 = Vec3.createVectorHelper(x, y, z);
        float var14 = MathHelper.cos(-var6 * 0.017453292F - (float) Math.PI);
        float var15 = MathHelper.sin(-var6 * 0.017453292F - (float) Math.PI);
        float var16 = -MathHelper.cos(-var5 * 0.017453292F);
        float var17 = MathHelper.sin(-var5 * 0.017453292F);
        float var18 = var15 * var16;
        float var20 = var14 * var16;
        // Range of 16 blocks should cover most things... hopefully
        double range = 16.0D;
        Vec3 vec2 = vec1.addVector(var18 * range, var17 * range, var20 * range);
        MovingObjectPosition mop =  player.worldObj.rayTraceBlocks(vec1, vec2, true);
        return mop == null || !isBlacklisted(player.worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ),
                obj.getClass().getCanonicalName()) && fireEvent(player, mop.blockX, mop.blockY, mop.blockZ);
    }

    public static boolean canMine(TileEntity tile, byte facing, Object obj) {
        if(tile == null) return true;
        int x = tile.xCoord, y = tile.yCoord, z = tile.zCoord;
        switch (facing) {
            case 0: y--;
                break;
            case 1: y++;
                break;
            case 2: z--;
                break;
            case 3: z++;
                break;
            case 4: x--;
                break;
            case 5: x++;
        }
        return isBlacklisted(tile.getWorldObj().getBlock(x, y, z), obj.getClass().getCanonicalName());
    }

    private static boolean isBlacklisted(Block block, String className) {
        String blockName = Block.blockRegistry.getNameForObject(block);
        if(Blacklist.getInstance().getBlacklists().containsKey(className)) {
            for (String bl : Blacklist.getInstance().getBlacklist(className)) {
                if (blockName.equals(bl)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean fireEvent(EntityLivingBase player, int x, int y, int z) {
        try {
            if(getEntity == null) {
                getEntity = player.getClass().getDeclaredMethod("getBukkitEntity");
            }
            org.bukkit.entity.Entity ent = (org.bukkit.entity.Entity)getEntity.invoke(player);
            if(ent instanceof Player) {
                Player bukkitPlayer = (Player)ent;
                BlockBreakEvent breakEvt = new BlockBreakEvent(bukkitPlayer.getWorld().getBlockAt(x, y, z), bukkitPlayer);
                Bukkit.getPluginManager().callEvent(breakEvt);
                if(breakEvt.isCancelled()) {
                    return false;
                }
                breakEvt.setCancelled(true);
            }
        } catch(Throwable t) {
            FMLLog.log("Canine", Level.ERROR, "%s", "Error while firing Bukkit event: " + t);
            t.printStackTrace();
        }
        return true;
    }

    static {
        primitives.put("byte", "B");
        primitives.put("int", "I");
        primitives.put("short", "S");
        primitives.put("long", "J");
        primitives.put("float", "F");
        primitives.put("double", "D");
        primitives.put("boolean", "Z");
        primitives.put("char", "C");
        primitives.put("void", "V");
        primitives.put("XCOORD", "I");
        primitives.put("YCOORD", "I");
        primitives.put("ZCOORD", "I");
    }
}
