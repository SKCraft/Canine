package com.skcraft.alicefixes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.skcraft.alicefixes.jsongenerator.GeneratorFrame.PatchList;
import com.skcraft.alicefixes.jsongenerator.JsonHelperObject;
import com.skcraft.alicefixes.transformers.ClassTransformer;
import com.skcraft.alicefixes.util.Obfs;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class AliceTransformer implements IClassTransformer {

    private final JsonParser parser = new JsonParser();
    public static Map<String, String> primitives = new HashMap();

    private final List<IClassTransformer> transformers = new ArrayList<IClassTransformer>();

    public AliceTransformer() {
        try {
            File configDir = new File(LoadingPlugin.getMCDirectory(), "config");
            Blacklist.load(configDir);
            File configFile = new File(configDir, "AFPatches.json");

            if(!configFile.createNewFile()) {
                Gson gson = new Gson();
                JsonObject obj = parser.parse(FileUtils.readFileToString(configFile)).getAsJsonObject();
                PatchList patchList = gson.fromJson(obj, PatchList.class);
                Iterator<JsonHelperObject> i = patchList.getPatches().values().iterator();
                while(i.hasNext()) {
                    JsonHelperObject patch = i.next();
                    transformers.add(
                            new ClassTransformer(patch, buildDescriptor(patch.params, patch.returnType), sortVars(patch.params))
                    );
                    if(patch.blacklist) {
                        Blacklist.addBlacklist(patch.className, new int[] {-1});
                    }
                }
            }
        } catch(IOException e) {
            FMLLog.log("AliceFixes", Level.WARNING, "%s", "Error while loading the patches file: " + e);
            e.printStackTrace();
        }
    }

    private String buildDescriptor(String[] params, String returnType) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for(int i = 0; i < params.length; i++) {
            if(primitives.containsKey(params[i])) {
                sb.append(primitives.get(params[i]));
            } else if(!params[i].equals("")) {
                sb.append("L" + params[i] + ";");
            }
        }
        sb.append(")");
        if(primitives.containsKey(returnType)) {
            sb.append(primitives.get(returnType));
        } else {
            sb.append("L" + returnType + ";");
        }
        return sb.toString();
    }

    private List sortVars(String[] params) {
        List<Integer> desiredVars = new ArrayList<Integer>();
        boolean foundCoords = false;

        //Find the player
        for(int i = 0; i < params.length; i++) {
            if((params[i].equals("net/minecraft/entity/player/EntityPlayer") || params[i].equals(Obfs.get("EntityPlayer")) ||
                    params[i].equals("net/minecraft/entity/EntityLivingBase") || params[i].equals(Obfs.get("EntityLivingBase")))) {
                desiredVars.add(i + 1);
                break;
            }
        }

        //Check if the coords were indicated by user
        for(int i = 0; i < params.length; i++) {
            if(params[i].equals("intCOORD")) {
                desiredVars.add(i + 1);
                foundCoords = true;
            }
        }

        //Search for the xyz coords if they weren't indicated, THIS IS NOT VERY RELIABLE!
        //It will search for the first 3 parameters which are integers
        if(!foundCoords) {
            for(int i = 0; i < params.length; i++) {
                if(params[i].equals("int")) {
                    int foundInts = 1;
                    for(int j = 1; j < 3; j++) {
                        if(params.length <= i + j) {
                            break;
                        }
                        if(params[i + j].equals("int")) {
                            foundInts++;
                        } else {
                            break;
                        }
                    }
                    if(foundInts == 3) {
                        for(int j = 0; j < 3; j++) {
                            desiredVars.add(i + j + 1);
                        }
                        break;
                    }
                }
            }
        }
        return desiredVars;
    }

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

    static {
        primitives.put("byte", "B");
        primitives.put("int", "I");
        primitives.put("short", "S");
        primitives.put("long", "L");
        primitives.put("float", "F");
        primitives.put("double", "D");
        primitives.put("boolean", "Z");
        primitives.put("char", "C");
        primitives.put("void", "V");
        primitives.put("intCOORD", "I");
    }
}
