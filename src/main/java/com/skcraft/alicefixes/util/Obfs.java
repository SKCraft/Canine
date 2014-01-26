package com.skcraft.alicefixes.util;

import java.util.HashMap;
import java.util.Map;

public class Obfs {

    public static final Map<String, String> mappings = new HashMap<String, String>();

    public static String get(String key) {
        return mappings.get(key);
    }

    static {
        mappings.put("EntityPlayer", "uf");
        mappings.put("Entity", "nn");
        mappings.put("MovingObjectPosition", "ata");
        mappings.put("World", "abw");
        mappings.put("ItemStack", "ye");
        mappings.put("EntityLivingBase", "of");
        mappings.put("TileEntity", "asp");
        mappings.put("WorldObj", "field_70170_p");
    }

}
