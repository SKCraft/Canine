package com.skcraft.canine;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cpw.mods.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Blacklist {

    private File configDir;
    private Map<String, String[]> blacklists = new HashMap<String, String[]>();
    private static Blacklist instance;

    public Blacklist(File dir) {
        configDir = dir;
        instance = this;
    }

    public void load() {
        try {
            File file = new File(configDir, "Canine-Blacklists.json");
            BufferedReader br = new BufferedReader(new FileReader(file));
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String[]>>(){}.getType();
            blacklists = gson.fromJson(br, type);
            br.close();
        } catch(FileNotFoundException e) {
            FMLLog.log("Canine", Level.WARN, "%s", "Couldn't find the blacklists file!");
        } catch(IOException e) {
            FMLLog.log("Canine", Level.WARN, "%s", "Error while loading the blacklists file: " + e);
            e.printStackTrace();
        }
    }

    public Map<String, String[]> getBlacklists() {
        return blacklists;
    }

    public String[] getBlacklist(String key) {
        return blacklists.get(key);
    }

    public static Blacklist getInstance() {
        return instance;
    }
}
