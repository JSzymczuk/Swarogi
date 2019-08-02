package swarogi.playermodes;

import swarogi.common.Configuration;
import swarogi.enums.ActionButton;
import swarogi.enums.TerrainType;
import swarogi.enums.TileSelectionTag;
import swarogi.game.GameCamera;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.interfaces.ControlsProvider;
import swarogi.interfaces.PlayerModeChangeListener;
import swarogi.models.Player;

import java.awt.*;


public class DebugChangeTerrainPlayerMode extends PlayerMode {

    private final GameMap map;
    private Tile hoverTile;

    public DebugChangeTerrainPlayerMode(Player player, PlayerModeChangeListener listener, GameMap map) {
        super(player, listener);
        this.map = map;
    }

    @Override
    public boolean isDebugOnly() { return true; }
    @Override
    public boolean isPausingGameplay() { return true; }
    @Override
    public boolean isLockingCamera() { return false; }

    @Override
    public void update() {

        ControlsProvider controlsProvider = getControls();
        Point mouseAbsolutePosition = getAbsoluteMousePosition();
        hoverTile = map.getTileByCoordinates(mouseAbsolutePosition.x, mouseAbsolutePosition.y);

        if (hoverTile != null) {
            ActionButton button = controlsProvider.getFirstSelectedOption();
            if (button != null) {
                int option = ActionButton.getOption(button);
                TerrainType[] terrainTypes = TerrainType.values();
                if (option < terrainTypes.length) {
                    TerrainType terrainType = terrainTypes[option];
                    hoverTile.setTerrainType(terrainType);

                    int x1 = hoverTile.getIdX();
                    int x2 = map.getTilesX() - hoverTile.getIdX() - 1;
                    int y1 = hoverTile.getIdY();
                    int y2 = map.getTilesY() - hoverTile.getIdY() - 1;

                    if (Configuration.mapBuildingXSymmetry) {
                        map.getTile(x2, y1).setTerrainType(terrainType);

                        if (Configuration.mapBuildingYSymmetry || Configuration.mapBuildingDiagSymmetry) {
                            map.getTile(x1, y2).setTerrainType(terrainType);
                            map.getTile(x2, y2).setTerrainType(terrainType);
                        }
                    } else if (Configuration.mapBuildingYSymmetry) {

                        map.getTile(x1, y2).setTerrainType(terrainType);

                        if (Configuration.mapBuildingDiagSymmetry) {
                            map.getTile(x2, y1).setTerrainType(terrainType);
                            map.getTile(x2, y2).setTerrainType(terrainType);
                        }
                    } else if (Configuration.mapBuildingDiagSymmetry) {
                        map.getTile(x2, y2).setTerrainType(terrainType);
                    }
                }
            }
        }
    }

    @Override
    public void renderSelection(Graphics g, GameCamera camera) {
        if (hoverTile != null) {
            renderTile(g, hoverTile, camera, TileSelectionTag.HOVER_NEUTRAL);
        }
    }
}
