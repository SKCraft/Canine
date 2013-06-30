package com.skcraft.alicefixes;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

//@Mod(modid = "aliceFixes", version = "0.1.0")
public class AliceFixes {
    
    @PreInit
    public void preInit(FMLPreInitializationEvent evt) {
        BreakerBlacklist.load();
    }

}
