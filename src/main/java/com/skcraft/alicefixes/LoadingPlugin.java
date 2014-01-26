package com.skcraft.alicefixes;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.io.File;
import java.util.Map;

public class LoadingPlugin implements IFMLLoadingPlugin {

    private static File mcDir;

    public static File getMCDirectory() {
        return mcDir;
    }

    @Override
    public String[] getLibraryRequestClass() {
        return null;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "com.skcraft.alicefixes.AliceTransformer" };
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
}
