package swarogi.playermodes;

import swarogi.enums.ActionButton;
import swarogi.enums.TileSelectionTag;
import swarogi.game.GameCamera;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.game.TilesSelection;
import swarogi.interfaces.Placeable;
import swarogi.interfaces.PlayerModeChangeListener;
import swarogi.interfaces.PlayerUnit;
import swarogi.models.Building;
import swarogi.models.Player;
import swarogi.models.Unit;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectionPlayerMode extends PlayerMode {

    private GameMap map;
    private Placeable hoveredPlaceable;
    private List<Tile> hoveredTiles;

    public SelectionPlayerMode(Player player, PlayerModeChangeListener listener, GameMap map) {
        super(player, listener);
        this.map = map;
        hoveredTiles = new ArrayList<>();
    }

    @Override
    public boolean isDebugOnly() { return false; }
    @Override
    public boolean isPausingGameplay() { return false; }
    @Override
    public boolean isLockingCamera() { return false; }

    @Override
    public void update() {
        if (getControls().isButtonDown(ActionButton.TRIBE_PATHS_MENU)) {
            changePlayerMode(new TribePathsPlayerMode(player, getListener(), this));
            return;
        }

        updateHoverable();

        if (getControls().isButtonDown(ActionButton.NEXT_UNIT)) {
            List<Unit> units = player.getUnits().stream().filter(u -> u.hasActionPoints(1)).collect(Collectors.toList());
            if (units.size() > 0) {
                changePlayerMode(new UnitCommandPlayerAction(player, getListener(), getMap(), units.get(0)));
                return;
            }
        }

        if (getControls().isButtonDown(ActionButton.CONFIRM)) {
            onSelect();
        }
    }

    @Override
    public void renderSelection(Graphics g, GameCamera camera) {
        if (hoveredTiles.size() > 0) {
            TileSelectionTag tileSelectionTag = getPlaceableSelectionTag(player, hoveredPlaceable, TileSelectionTag.INACTIVE_POSITIVE,
                    TileSelectionTag.INACTIVE_ALLIED, TileSelectionTag.INACTIVE_NEGATIVE, TileSelectionTag.HOVER_NEUTRAL);

            for (Tile tile : hoveredTiles) {
                renderTile(g, tile, camera, tileSelectionTag);
            }
        }
    }

    protected void updateHoverable() {
        Point mouseAbsolutePosition = getAbsoluteMousePosition();

        if (player.getCamera().hasPositionChanged() || getControls().hasMousePositionChanged()) {
            Tile hoverTile = map.getTileByCoordinates(mouseAbsolutePosition.x, mouseAbsolutePosition.y);

            if (hoverTile != null) {
                List<Placeable> tileObjects = hoverTile.getSelectables().stream()
                        .filter(p -> p.getPlaceableData().isSelectable())
                        .collect(Collectors.toList());

                if (tileObjects.size() == 1) {
                    hoveredPlaceable = tileObjects.get(0);

                    if (hoveredPlaceable instanceof Unit && ((Unit) hoveredPlaceable).isMoving()) {
                        hoveredPlaceable = null;
                        hoveredTiles.clear();
                        hoveredTiles.add(hoverTile);
                    }
                    else {
                        hoveredTiles = TilesSelection.get(
                                hoveredPlaceable.getPlaceableData().getPlacingTileGroup(),
                                hoveredPlaceable.getTile());
                    }
                }
                else {
                    hoveredPlaceable = null;
                    hoveredTiles.clear();
                    hoveredTiles.add(hoverTile);
                }
            }
            else {
                hoveredPlaceable = null;
                hoveredTiles.clear();
            }
        }
    }

    void onSelect() {
        Player player = getPlayer();
        if (hoveredPlaceable instanceof PlayerUnit) {
            if (hoveredPlaceable instanceof Unit) {
                Unit unit = (Unit)hoveredPlaceable;
                if (unit.getOwner() == player) {
                    changePlayerMode(new UnitCommandPlayerAction(player, getListener(), getMap(), unit));
                }
            }
            else if (hoveredPlaceable instanceof Building) {
                Building building = (Building)hoveredPlaceable;
                if (building.getOwner() == player) {
                    changePlayerMode(new BuildingCommandPlayerMode(player, getListener(), getMap(), building));
                }
            }
        }
    }

    GameMap getMap() { return map; }
    Placeable getHoveredPlaceable() { return hoveredPlaceable; }
    List<Tile> getHoveredTiles() { return hoveredTiles; }

    Tile getHoverTile() {
        Point mouseAbsolutePosition = getAbsoluteMousePosition();
        return map.getTileByCoordinates(mouseAbsolutePosition.x, mouseAbsolutePosition.y);
    }
}
