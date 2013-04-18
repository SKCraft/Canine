package com.skcraft.alicefixes;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;
import java.util.logging.Level;

import net.minecraft.tileentity.TileEntity;

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

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.IClassTransformer;

public class TransformBlockBreaker implements IClassTransformer {

    @Override
    public byte[] transform(String name, byte[] bytes) {
        if(name.equals("com.eloraam.redpower.machine.TileBreaker")) {
            return handleBreakerTransform(bytes);
        }
        return bytes;
    }

    private byte[] handleBreakerTransform(byte[] bytes) {

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        while(methods.hasNext()) {
            MethodNode method = methods.next();
            if(method.name.equals("onBlockNeighborChange")) {
                LabelNode l0 = new LabelNode();
                LabelNode l1 = new LabelNode();
                LabelNode l2 = new LabelNode();
                InsnList toInject = new InsnList();
                toInject.add(l0);
                toInject.add(new VarInsnNode(ALOAD, 0));  //this
                toInject.add(new VarInsnNode(ALOAD, 0));  //this
                toInject.add(new FieldInsnNode(GETFIELD, "com/eloraam/redpower/machine/TileBreaker", "Rotation", "I"));
                toInject.add(new MethodInsnNode(INVOKESTATIC, 
                        "com/skcraft/alicefixes/TransformBlockBreaker", 
                        "canMine", 
                        "(L" + ObfNames.TILE_ENTITY + ";I)Z"));  //Invoke canMine() in this class
                toInject.add(new JumpInsnNode(IFNE, l1));  //if statement
                toInject.add(l2);
                toInject.add(new InsnNode(RETURN));
                toInject.add(l1);
                
                method.instructions.insertBefore(method.instructions.getFirst(), toInject); //insert before first instruction
                FMLLog.log(Level.INFO, "Block Breaker successfully patched!");
                break;
            }
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public static boolean canMine(TileEntity tile, int rotation) {
        
        CoordHelper facingCoords = new CoordHelper(tile.xCoord, tile.yCoord, tile.zCoord);
        facingCoords.addFacingAsOffset(rotation);

        int id = tile.worldObj.getBlockId(facingCoords.x, facingCoords.y, facingCoords.z);

        for(int i = 0; i < BreakerBlacklist.forbiddenIds.length; i++) {
            if(id == BreakerBlacklist.forbiddenIds[i]) {
                return false;
            }
        }
        return true;
    }
}
