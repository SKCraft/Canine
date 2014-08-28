package com.skcraft.canine.forge;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skcraft.canine.Blacklist;
import com.skcraft.canine.asm.TransformInfo;
import com.skcraft.canine.asm.ClassTransformer;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.tileentity.TileEntity;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CanineTransformer implements IClassTransformer {

    private final List<IClassTransformer> transformers = new ArrayList<IClassTransformer>();

    public CanineTransformer() {
        try {
            File configDir = new File(CaninePlugin.getMCDirectory(), "config");
            Blacklist bl = new Blacklist(configDir);
            bl.load();

            File transforms = new File(configDir, "Canine-Transformers.json");
            BufferedReader br = new BufferedReader(new FileReader(transforms));
            Gson gson = new Gson();
            Type type = new TypeToken<List<TransformInfo>>(){}.getType();
            List<TransformInfo> infos = gson.fromJson(br, type);
            br.close();

            for(TransformInfo info : infos) {
                transformers.add(new ClassTransformer(info));
            }
        } catch(FileNotFoundException e) {
            FMLLog.log("Canine", Level.WARN, "%s", "Couldn't find the transformers file!");
        } catch(IOException e) {
            FMLLog.log("Canine", Level.WARN, "%s", "Error while loading the transformers file: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(bytes == null) return null;

        for(IClassTransformer transformer : transformers) {
            bytes = transformer.transform(name, transformedName, bytes);
            if(bytes == null)
                FMLLog.log(Level.FATAL, "Transformer " + transformer.getClass().getCanonicalName() + " has corrupted class " + name);
        }
        return bytes;
    }

}
