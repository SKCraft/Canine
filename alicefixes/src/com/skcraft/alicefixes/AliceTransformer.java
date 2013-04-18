package com.skcraft.alicefixes;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.IClassTransformer;

public class AliceTransformer implements IClassTransformer {
    
    private final List<IClassTransformer> transformers;
    
    public AliceTransformer() {
        String[] names = LoadingPlugin.getTransformers();
        transformers = new ArrayList(names.length);
        for(String transformer : names) {
            try {
                transformers.add((IClassTransformer)Class.forName(transformer).newInstance());
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
        }
    }
    
    @Override
    public byte[] transform(String name, byte[] bytes) {
        
        if(bytes == null) {
            return bytes;
        }
        
        for(IClassTransformer transformer : transformers) {
            try {
                bytes = transformer.transform(name, bytes);
                if(bytes == null)
                    FMLLog.log(Level.SEVERE, "Transformer " + transformer + " has corrupted class " + name);
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
        }
        
        return bytes;
    }

}
