package com.skcraft.alicefixes;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "com.skcraft.alicefixes", name = "AliceFixes", version = "1.2.0")
public class AliceFixes {

    @EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(new AFListener());
    }

}
