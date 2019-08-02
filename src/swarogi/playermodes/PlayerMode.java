package swarogi.playermodes;

import swarogi.common.Configuration;
import swarogi.common.ContentManager;
import swarogi.enums.TileSelectionTag;
import swarogi.game.GameCamera;
import swarogi.game.Tile;
import swarogi.interfaces.ControlsProvider;
import swarogi.interfaces.Placeable;
import swarogi.interfaces.PlayerModeChangeListener;
import swarogi.interfaces.PlayerUnit;
import swarogi.models.Player;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class PlayerMode {

    protected final Player player;
    private final PlayerModeChangeListener listener;

    PlayerMode(Player player, PlayerModeChangeListener listener) {
        this.player = player;
        this.listener = listener;
    }

    public abstract boolean isDebugOnly();
    public abstract boolean isPausingGameplay();
    public abstract boolean isLockingCamera();
    public abstract void update();
    public abstract void renderSelection(Graphics g, GameCamera camera);
    public void renderGui(Graphics g, Dimension dimension, Font font) { }

    protected void renderTextBack(Graphics g, Dimension dimension) {
        BufferedImage textBack = ContentManager.bottomTextShadow;
        int y = dimension.height - textBack.getHeight();
        int w = dimension.width;
        int dx = textBack.getWidth();
        for (int x = 0; x < w; x += dx) {
            g.drawImage(textBack, x, y, null);
        }
    }

    protected ControlsProvider getControls() {
        return this.player.getControls();
    }

    protected GameCamera getCamera() {
        return this.player.getCamera();
    }

    Player getPlayer() { return player; }

    PlayerModeChangeListener getListener() { return listener; }

    Point getAbsoluteMousePosition() {
        Point position = this.player.getControls().getPointerPosition();
        GameCamera camera = this.player.getCamera();
        return new Point(position.x + camera.x, position.y + camera.y);
    }

    void renderTile(Graphics g, Tile tile, GameCamera camera, TileSelectionTag tag) {
        Point pos = tile.getTopLeft();
        g.drawImage(ContentManager.getTileSelection(tag),
                pos.x - camera.x, pos.y - camera.y,
                Configuration.TILE_WIDTH, Configuration.TILE_HEIGHT, null);
    }

    void changePlayerMode(PlayerMode playerMode) {
        listener.onPlayerModeChanged(playerMode);
    }

    TileSelectionTag getPlaceableSelectionTag(Player player, Placeable placeable, TileSelectionTag owned,
                                                      TileSelectionTag ally, TileSelectionTag enemy, TileSelectionTag neutral) {
        if (placeable instanceof PlayerUnit) {
            Player unitOwner = ((PlayerUnit) placeable).getOwner();
            return unitOwner == player ? owned : unitOwner.getTeam() != player.getTeam() ? enemy : ally;
        }
        else {
            return neutral;
        }
    }
}
