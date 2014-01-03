package com.skcraft.alicefixes;

import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.Configuration;

import java.io.File;

public class BreakerBlacklist {

    public static int[] blacklist = new int[] {};

    public static void load() {
        File configDir = Loader.instance().getConfigDir();
        configDir = new File(configDir, "/cofh/");
        configDir.mkdir();
        configDir = new File(configDir, "breakerblacklist.cfg");
        Configuration list = new Configuration(configDir);

        try {
            list.load();
            blacklist = list.get("Blacklist", "blacklist", new int[] {-1},
                    "List of block IDs that the block breaker cannot break. Add 1 ID per line.").getIntList();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            list.save();
        }
    }

    public static int[] getBlacklist() {
        return blacklist;
    }
}
