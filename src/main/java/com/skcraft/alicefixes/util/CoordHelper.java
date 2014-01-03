package com.skcraft.alicefixes.util;

public class CoordHelper {

    public int x, y, z;

    public CoordHelper(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void addFacingAsOffset(byte facing) {
        switch (facing)
        {
            case 0:
                y--;
                break;
            case 1:
                y++;
                break;
            case 2:
                z--;
                break;
            case 3:
                z++;
                break;
            case 4:
                x--;
                break;
            default:
                x++;
        }
    }
}
