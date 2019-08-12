package swarogi.playermodes;

import swarogi.common.Configuration;
import swarogi.enums.ActionButton;
import swarogi.enums.HorizontalAlign;
import swarogi.enums.TileSelectionTag;
import swarogi.enums.VerticalAlign;
import swarogi.game.GameCamera;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.gui.DiamondIcon;
import swarogi.gui.Icon;
import swarogi.gui.RenderingHelper;
import swarogi.interfaces.Placeable;
import swarogi.interfaces.PlayerModeChangeListener;
import swarogi.interfaces.PlayerUnit;
import swarogi.models.Building;
import swarogi.models.Player;
import swarogi.models.Unit;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SummaryPlayerMode extends SelectionPlayerMode {

    private PlayerUnit unit;
    private List<Tile> tiles;

    public SummaryPlayerMode(Player player, PlayerModeChangeListener listener, GameMap map, PlayerUnit playerUnit) {
        super(player, listener, map);
        this.unit = playerUnit;

        if (playerUnit instanceof Building) {
            tiles = ((Building)playerUnit).getAllTiles();
        }
        else if (playerUnit instanceof Unit) {
            tiles = new ArrayList<>(1);
            tiles.add(((Unit)playerUnit).getTile());
        }

        setIcons();
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void renderSelection(Graphics g, GameCamera camera) {

        HashMap<Tile, TileSelectionTag> tilesToDraw = new HashMap<>();

        if (!isGuiInteraction()) {
            List<Tile> hoveredTiles = getHoveredTiles();

            // Wskazywana jest jednostka:
            Placeable hoveredPlaceable = getHoveredPlaceable();
            if (hoveredPlaceable != null) {
                TileSelectionTag tileSelectionTag = getPlaceableSelectionTag(player, hoveredPlaceable, TileSelectionTag.INACTIVE_POSITIVE,
                        TileSelectionTag.INACTIVE_ALLIED, TileSelectionTag.INACTIVE_NEGATIVE, TileSelectionTag.HOVER_NEUTRAL);
                for (Tile tile : hoveredTiles) {
                    tilesToDraw.put(tile, tileSelectionTag);
                }
            }
            // Wskazywane jest pole:
            else {
                for (Tile tile : hoveredTiles) {
                    tilesToDraw.put(tile, TileSelectionTag.HOVER_NEUTRAL);
                }
            }
        }

        for (Tile tile : tiles) {
            tilesToDraw.put(tile, TileSelectionTag.ACTIVE_NEGATIVE);
        }

        // Narysuj wszystko
        for (Tile tile : tilesToDraw.keySet()) {
            renderTile(g, tile, camera, tilesToDraw.get(tile));
        }
    }

    @Override
    public void renderGui(Graphics g, Dimension size, Font font) {
        RenderingHelper.drawSummaryBox(g, size);
        if (unit instanceof Unit) {
            RenderingHelper.drawUnitSummary(g, size, (Unit)unit);
        }
        else if (unit instanceof Building) {
            RenderingHelper.drawBuildingSummary(g, size, (Building)unit);
        }
        RenderingHelper.drawTextArea(g, size, "");
        RenderingHelper.drawIcons(g, getIcons(), size);
    }

    private void setIcons() {
        // Wstecz
        {
            Point pos = RenderingHelper.getCancelIconPosition();
            Icon icon = new DiamondIcon();
            icon.hAlign = HorizontalAlign.RIGHT;
            icon.vAlign = VerticalAlign.TOP;
            icon.x = pos.x;
            icon.y = pos.y;
            icon.actionButton = ActionButton.CANCEL;
            icon.textureKey = Configuration.CANCEL_ICON_NAME;
            icon.hoverText = "Wstecz [PPM]";
            icon.clickAction = this::exitMode;
            icon.hoverAction = () -> setText(icon.hoverText);
            icon.unhoverAction = () -> setText(null);
            addIcon(icon);
        }
    }


    // TODO: Ale że jak to java nie ma pakietowych interfejsów?!
    @Override
    public PlayerModeChangeListener getListener() { return super.getListener(); }
    @Override
    public GameMap getMap() { return super.getMap(); }
    @Override
    public Player getPlayer() { return super.getPlayer(); }
    @Override
    public void changePlayerMode(PlayerMode playerMode) { super.changePlayerMode(playerMode); }

    private void exitMode() {
        changePlayerMode(new SelectionPlayerMode(getPlayer(), getListener(), getMap()));
    }
}
