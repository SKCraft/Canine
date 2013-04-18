package com.skcraft.alicefixes;

public class CoordHelper {

    int x;
    int y;
    int z;

    public CoordHelper(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void addFacingAsOffset(int facing) {
        switch (facing)
        {
        case 0:
            this.y += 1;
            break;
        case 1:
            this.y -= 1;
            break;
        case 2:
            this.z += 1;
            break;
        case 3:
            this.z -= 1;
            break;
        case 4:
            this.x += 1;
            break;
        default:
            this.x -= 1;
        }
    }
}
