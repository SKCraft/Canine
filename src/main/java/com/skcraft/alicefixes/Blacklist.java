package com.skcraft.alicefixes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class Blacklist {

    private static File config;
    private static Map<String, int[]> blacklists = new HashMap<String, int[]>();
    private static BlackLists lists = new BlackLists();

    public static void load(File configDir) {
        try {
            Gson gson = new Gson();
            config = new File(configDir, "Blacklists.json");
            BufferedReader br = new BufferedReader(new FileReader(config));
            BlackLists storedLists = gson.fromJson(br, BlackLists.class);
            if(storedLists != null) {
                blacklists.putAll(storedLists.blacklists);
            }
            br.close();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
    }

    public static void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            lists.setBlacklists(blacklists);
            FileUtils.writeStringToFile(config, gson.toJson(lists));
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    public static void addBlacklist(String key, int[] def) {
        if(!blacklists.containsKey(key)) {
            blacklists.put(key, def);
        }
    }

    public static int[] getBlacklist(String key) {
        return blacklists.get(key);
    }

    public static class BlackLists {
        private Map<String, int[]> blacklists = new HashMap();

        public void setBlacklists(Map lists) {
            blacklists = lists;
        }
    }
}
