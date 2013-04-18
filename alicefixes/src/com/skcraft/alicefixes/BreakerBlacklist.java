package com.skcraft.alicefixes;

import java.io.File;

import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.Configuration;

public class BreakerBlacklist {
    
    public static int[] forbiddenIds = new int[] {};
    
    public static void load() {
        File configDir = Loader.instance().getConfigDir();
        configDir = new File(configDir, "/redpower/");
        configDir.mkdir();
        configDir = new File(configDir, "blacklist.cfg");
        Configuration blacklist = new Configuration(configDir);
        
        try {
            blacklist.load();
            forbiddenIds = blacklist.get("Blacklist", "forbiddenBlocks", 
                    new int[] {1}, "List of block IDs that the block breaker cannot break. Add 1 ID per line.").getIntList();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            blacklist.save();
        }
    }

}
