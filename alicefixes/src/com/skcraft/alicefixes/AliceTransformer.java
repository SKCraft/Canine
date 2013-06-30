package com.skcraft.alicefixes;

import java.util.logging.Level;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.IClassTransformer;

public class AliceTransformer implements IClassTransformer {

    private final IClassTransformer[] transformers = { 
            new TransformMiningLaser()};
            //new TransformBlockBreaker()
            //new TransformIC2Explosions(),
            //new TransformTCExcWand(),
            //new TransformTCEquWand(),
            //new TransformTCFrostWand(),
            //new TransformTCAxe(),
            //new TransformTCShovel()};

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {

        if(bytes == null) {
            return bytes;
        }

        for(IClassTransformer transformer : transformers) {
            bytes = transformer.transform(name, transformedName, bytes);
            if(bytes == null)
                FMLLog.log(Level.SEVERE, "Transformer " + transformer + " has corrupted class " + name);
        }

        return bytes;
    }
}
