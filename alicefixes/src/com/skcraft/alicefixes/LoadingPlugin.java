package com.skcraft.alicefixes;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions("com.skcraft.alicefixes")
public class LoadingPlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getLibraryRequestClass() {
		return null;
	}

	@Override
	public String[] getASMTransformerClass() {
		System.out.println("getASMTransformerClass()");
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
	public void injectData(Map<String, Object> data) {}

	public static String[] getTransformers() {
		return new String[] { "com.skcraft.alicefixes.TransformMiningLaser",
				              /*"com.skcraft.alicefixes.TransformIC2Explosions",
				              "com.skcraft.alicefixes.TransformTCExcWand",
				              "com.skcraft.alicefixes.TransformTCEquWand",
				              "com.skcraft.alicefixes.TransformTCFrostWand",
				              "com.skcraft.alicefixes.TransformTCAxe",
				              "com.skcraft.alicefixes.TransformTCShovel"*/};
	}

}
