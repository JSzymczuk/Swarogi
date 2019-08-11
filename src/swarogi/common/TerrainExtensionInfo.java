package swarogi.common;

import java.awt.image.BufferedImage;

public class TerrainExtensionInfo {
    public BufferedImage texture;
    public int destinationX;
    public int destinationY;
    public int x1;
    public int x2;
    public int halfY;

    public TerrainExtensionInfo(BufferedImage image) {
        this.texture = image;
        int width = image.getWidth();
        int height = image.getHeight();
        int tempX = (width - Configuration.BASE_TILE_WIDTH) / 2;
        this.destinationX = (int)(tempX * Configuration.TILE_SCALE);
        this.destinationY = (int)((height - Configuration.BASE_TILE_HEIGHT) / 2 * Configuration.TILE_SCALE);
        this.x1 = tempX + Configuration.BASE_TILE_SLANT_WIDTH;
        this.x2 = tempX + Configuration.BASE_TILE_WIDTH - Configuration.BASE_TILE_SLANT_WIDTH;
        this.halfY = height / 2;
    }
}
