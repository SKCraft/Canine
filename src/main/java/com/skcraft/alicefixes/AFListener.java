package com.skcraft.alicefixes;

import com.skcraft.alicefixes.util.ASMHelper;
import ic2.api.event.LaserEvent.LaserExplodesEvent;
import ic2.api.event.LaserEvent.LaserHitsBlockEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class AFListener {

    @ForgeSubscribe
    public void onLaserHitBlock(LaserHitsBlockEvent evt) {
        if(!ASMHelper.canMine(evt.owner, evt.x, evt.y, evt.z, false, this)) {
            evt.lasershot.setDead();
            evt.setCanceled(true);
        }
    }

    @ForgeSubscribe
    public void onLaserExplode(LaserExplodesEvent evt) {
        if(!ASMHelper.canMine(evt.owner, (int)evt.lasershot.posX, (int)evt.lasershot.posY, (int)evt.lasershot.posZ, false, this)) {
            evt.lasershot.setDead();
            evt.setCanceled(true);
        }
    }
}
