package com.skcraft.alicefixes;

import java.util.logging.Level;

import com.skcraft.alicefixes.transformers.TransformDislocWand;
import com.skcraft.alicefixes.transformers.TransformElemAxe;
import com.skcraft.alicefixes.transformers.TransformElemShovel;
import com.skcraft.alicefixes.transformers.TransformMiningLaser;
import com.skcraft.alicefixes.transformers.TransformExcWand;
import com.skcraft.alicefixes.transformers.TransformTradeWand;
import com.skcraft.alicefixes.transformers.TransformVajra;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.IClassTransformer;

public class AliceTransformer implements IClassTransformer {

    private final IClassTransformer[] transformers = { 
            new TransformMiningLaser(),
            new TransformExcWand(),
            new TransformTradeWand(),
            new TransformElemAxe(),
            new TransformElemShovel(),
            new TransformDislocWand(),
            new TransformVajra()};

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
