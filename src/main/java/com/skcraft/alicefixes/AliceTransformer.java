package com.skcraft.alicefixes;

import com.skcraft.alicefixes.transformers.TransformTools;
import com.skcraft.alicefixes.transformers.TransformExcavationWand;
import com.skcraft.alicefixes.transformers.TransformTradeWand;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.launchwrapper.IClassTransformer;

import java.util.logging.Level;

public class AliceTransformer implements IClassTransformer {

    private final IClassTransformer[] transformers = {
        new TransformExcavationWand(),
        new TransformTradeWand(),
        new TransformTools()
    };

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(bytes == null) {
            return bytes;
        }

        for(IClassTransformer transformer : transformers) {
            bytes = transformer.transform(name, transformedName, bytes);
            if(bytes == null)
                FMLLog.log(Level.SEVERE, "Transformer " + transformer.getClass().getCanonicalName() + " has corrupted class " + name);
        }

        return bytes;
    }
}
