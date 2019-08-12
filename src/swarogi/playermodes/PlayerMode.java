package swarogi.playermodes;

import swarogi.common.Configuration;
import swarogi.common.ContentManager;
import swarogi.common.WindowSize;
import swarogi.enums.ActionButton;
import swarogi.enums.TileSelectionTag;
import swarogi.game.GameCamera;
import swarogi.game.Tile;
import swarogi.gui.Icon;
import swarogi.interfaces.ControlsProvider;
import swarogi.interfaces.Placeable;
import swarogi.interfaces.PlayerModeChangeListener;
import swarogi.interfaces.PlayerUnit;
import swarogi.models.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class PlayerMode {

    protected final Player player;
    private final PlayerModeChangeListener listener;

    private List<Icon> icons;
    protected Icon hoveredIcon;
    private boolean guiInteraction;

    // TODO: Tymczasowe? Albo tekst rozwinąć do okienka
    private String currentText;

    PlayerMode(Player player, PlayerModeChangeListener listener) {
        this.player = player;
        this.listener = listener;
        this.icons = new ArrayList<>();
    }

    public abstract boolean isDebugOnly();
    public abstract boolean isPausingGameplay();
    public abstract boolean isLockingCamera();
    public abstract void update();
    public abstract void renderSelection(Graphics g, GameCamera camera);
    public void renderGui(Graphics g, Dimension dimension, Font font) { }


    public boolean isGuiInteraction() { return this.guiInteraction; }

    public void addIcon(Icon icon) { this.icons.add(icon); }

    protected void setText(String text) { this.currentText = text; }

    protected String getText() { return this.currentText; }

    public List<Icon> getIcons() { return this.icons; }

    protected ControlsProvider getControls() {
        return this.player.getControls();
    }

    protected GameCamera getCamera() {
        return this.player.getCamera();
    }

    Player getPlayer() { return player; }

    PlayerModeChangeListener getListener() { return listener; }

    protected boolean checkGuiInteraction() {
        ControlsProvider controls = getControls();

        guiInteraction = false;

        // Sprawdzenie skrótów klawiaturowych
        for (Icon icon : icons) {
            if (icon.actionButton != null && controls.isButtonDown(icon.actionButton)) {
                icon.onClick();
                guiInteraction = true;
            }
        }

        Point pos = controls.getPointerPosition();
        Dimension size = WindowSize.getSize();

        Icon newHoverIcon = null;

        // Sprawdzenie wskazywanej ikony
        for (Icon icon : icons) {
            if (icon.checkHover(size, pos)) {
                newHoverIcon = icon;
                break;
            }
        }

        if (hoveredIcon != null) {
            // Opuszczono poprzenio wskazywaną ikonę
            if (newHoverIcon == null) {
                hoveredIcon.hoveredFlag = false;
                hoveredIcon.onUnhover();
                hoveredIcon = null;
            }
            // Wskazywana jest inna ikona
            else if (newHoverIcon == hoveredIcon) {
                hoveredIcon.hoveredFlag = false;
                newHoverIcon.hoveredFlag = true;
                hoveredIcon.onUnhover();
                hoveredIcon = newHoverIcon;
                hoveredIcon.onHover();
            }
            // Nadal wskazywana jest ta sama ikona
        }
        // Wskazano nową ikonę
        else if (newHoverIcon != null) {
            newHoverIcon.hoveredFlag = true;
            hoveredIcon = newHoverIcon;
            hoveredIcon.onHover();
        }

        guiInteraction = hoveredIcon != null;

        if (hoveredIcon != null && controls.isButtonDown(ActionButton.CONFIRM)) {
            hoveredIcon.onClick();
        }

        return guiInteraction;
    }

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
