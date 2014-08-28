package com.skcraft.canine.forge;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.io.File;
import java.util.Map;

public class CaninePlugin implements IFMLLoadingPlugin {

    private static File mcDir;

    public static File getMCDirectory() {
        return mcDir;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "com.skcraft.canine.forge.CanineTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        mcDir = (File)data.get("mcLocation");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
