package com.skcraft.alicefixes.transformers;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import com.skcraft.alicefixes.BreakerBlacklist;
import com.skcraft.alicefixes.util.CoordHelper;
import com.skcraft.alicefixes.util.ObfNames;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.IClassTransformer;

// THIS MAY NOT WORK ANYMORE!
public class TransformBlockBreaker implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(name.equals("com.eloraam.redpower.machine.TileBreaker")) {
            return handleBreakerTransform(bytes);
        }
        return bytes;
    }

    private byte[] handleBreakerTransform(byte[] bytes) {
        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        
        cr.accept(new TileBreakerVisitor(cw), 0);
        return cw.toByteArray();
    }

    public static boolean canMine(TileEntity tile, int rotation) {
        
        CoordHelper facingCoords = new CoordHelper(tile.xCoord, tile.yCoord,
                tile.zCoord);
        facingCoords.addFacingAsOffset(rotation);

        int id = tile.worldObj.getBlockId(facingCoords.x, facingCoords.y, 
                facingCoords.z);

        for(int i = 0; i < BreakerBlacklist.forbiddenIds.length; i++) {
            if(id == BreakerBlacklist.forbiddenIds[i]) {
                return false;
            }
        }
        return true;
    }
    
    class TileBreakerVisitor extends ClassVisitor {
        
        public TileBreakerVisitor(ClassVisitor cv) {
            super(ASM4, cv);
        }
        
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                String signature, String[] exceptions) {
            
            if(name.equals("onBlockNeighborChange")) {
                return new OnNeighborChangeVisitor(super.visitMethod(access, 
                        name, desc, signature, exceptions));
            }
            return super.visitMethod(access, name, desc, signature,
                    exceptions);
        }
    }
    
    class OnNeighborChangeVisitor extends MethodVisitor {
        
        public OnNeighborChangeVisitor(MethodVisitor mv) {
            super(ASM4, mv);
        }
        
        @Override
        public void visitCode() {
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, 
                    "com/eloraam/redpower/machine/TileBreaker", 
                    "Rotation", 
                    "I");
            mv.visitMethodInsn(INVOKESTATIC, 
                    "com/skcraft/alicefixes/transformers/TransformBlockBreaker", 
                    "canMine", 
                    "(L" + ObfNames.TILE_ENTITY + ";I)Z");
            Label l1 = new Label();
            mv.visitJumpInsn(IFEQ, l1);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitInsn(RETURN);
            mv.visitLabel(l1);
            mv.visitCode();
        }
        
    }
}
