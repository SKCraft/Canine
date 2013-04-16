package com.skcraft.alicefixes;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import cpw.mods.fml.relauncher.IClassTransformer;

public class TransformMiningLaser implements IClassTransformer{

	private final String LASER_CLASS_NAME = "ic2.core.item.tool.EntityMiningLaser";
	private final String MINE_METHOD_NAME = "canMine";

	@Override
	public byte[] transform(String name, byte[] bytes) {
		if(name.equals(LASER_CLASS_NAME)) {
			return handleLaserTransform(bytes);
		}
		return bytes;
	}

	private byte[] handleLaserTransform(byte[] bytes) {

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext()) {
			MethodNode method = methods.next();
			if(method.name.equals(MINE_METHOD_NAME)) {
				LabelNode l0 = new LabelNode();
				LabelNode l1 = new LabelNode();
				LabelNode l2 = new LabelNode();
				InsnList toInject = new InsnList();
				toInject.add(l0);
				toInject.add(new VarInsnNode(ALOAD, 0));  //"this"
				toInject.add(new VarInsnNode(ALOAD, 0));  //"this"
				toInject.add(new FieldInsnNode(GETFIELD, "ic2/core/item/tool/EntityMiningLaser", "owner",
						"L" + ObfNames.ENTITY_LIVING + ";"));  //owner of laser
				//invokes canMine() in this class
				toInject.add(new MethodInsnNode(INVOKESTATIC,
						"com/skcraft/alicefixes/TransformMiningLaser",
						"canMine",
						"(L" + ObfNames.ENTITY + ";L" + ObfNames.ENTITY_LIVING + ";)Z"));
				toInject.add(new JumpInsnNode(IFNE, l1));  //if statement
				toInject.add(l2);
				toInject.add(new InsnNode(ICONST_0));  //false
				toInject.add(new InsnNode(IRETURN));  //return
				toInject.add(l1);
				//Insert these instructions at the start of the method's
				method.instructions.insertBefore(method.instructions.getFirst(), toInject);

				System.out.println("Mining laser successfully patched!");
				break;
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	
	public static boolean canMine(Entity laser, EntityLiving owner) {
		Vec3 currentPos = Vec3.createVectorHelper(laser.posX, laser.posY, laser.posZ);
		Vec3 heading = Vec3.createVectorHelper(laser.posX + laser.motionX, laser.posY + laser.motionY, laser.posZ + laser.motionZ);
		MovingObjectPosition pos = laser.worldObj.rayTraceBlocks_do_do(currentPos, heading, false, true);

		if(pos != null) {
			int xPos = pos.blockX;
			int yPos = pos.blockY;
			int zPos = pos.blockZ;

			try {
				Method m = owner.getClass().getDeclaredMethod("getBukkitEntity", new Class[] {});
				org.bukkit.entity.Entity ent = (org.bukkit.entity.Entity)m.invoke(owner);
				if ((ent instanceof Player)) {
					Player player = (Player)ent;
					org.bukkit.World bukkitWorld = player.getWorld();
					BlockBreakEvent breakEv = new BlockBreakEvent(bukkitWorld.getBlockAt(xPos, yPos, zPos), player);
					Bukkit.getPluginManager().callEvent(breakEv);
					if (breakEv.isCancelled()) {
						laser.setDead();
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
}
