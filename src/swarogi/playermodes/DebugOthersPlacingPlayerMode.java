package swarogi.playermodes;

import swarogi.common.Configuration;
import swarogi.data.Database;
import swarogi.datamodels.DecorationData;
import swarogi.datamodels.ObstacleData;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.interfaces.PlaceableData;
import swarogi.interfaces.PlayerModeChangeListener;
import swarogi.models.Decoration;
import swarogi.models.Obstacle;
import swarogi.models.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DebugOthersPlacingPlayerMode extends DebugPlacingPlayerMode {

    private static final List<PlaceableData> models;

    private int currentModel;

    static {
        models = new ArrayList<>();
        models.add(Database.LimeTree);
        models.add(Database.OakTree);
        models.add(Database.PineTree);
        models.add(Database.WillowTree);
        models.add(Database.Rock);
        models.add(Database.Bridge);
        models.add(Database.Daisy);
        models.add(Database.Cabbage);
        models.add(Database.Thickets);
        models.add(Database.Grain);
    }

    public DebugOthersPlacingPlayerMode(Player player, PlayerModeChangeListener listener, GameMap map) {
        super(player, listener, map);
    }

    @Override
    protected PlaceableData getPlaceable() {
        return 0 <= currentModel && currentModel < models.size() ? models.get(currentModel) : null;
    }

    @Override
    protected PlaceableData setPlaceable(int option) {
        currentModel = option;
        return getPlaceable();
    }

    @Override
    protected void place(Tile tile) {
        PlaceableData model = 0 <= currentModel && currentModel < models.size() ? models.get(currentModel) : null;
        if (model != null) {
            if (model instanceof DecorationData) {
                Point hoverTilePosition = tile.getCenter();
                Point mouseAbsolutePosition = getAbsoluteMousePosition();
                getMap().tryPlace(new Decoration((DecorationData)model,
                        mouseAbsolutePosition.x - hoverTilePosition.x,
                        mouseAbsolutePosition.y - hoverTilePosition.y),
                        tile);
            }
            else if (model instanceof ObstacleData) {
                GameMap map = getMap();
                map.tryPlace(new Obstacle((ObstacleData)model), tile);

                int x1 = tile.getIdX();
                int x2 = map.getTilesX() - tile.getIdX() - 1;
                int y1 = tile.getIdY();
                int y2 = map.getTilesY() - tile.getIdY() - 1;

                if(Configuration.mapBuildingXSymmetry) {
                    map.tryPlace(new Obstacle((ObstacleData)model), map.getTile(x2, y1));
                    if(Configuration.mapBuildingYSymmetry || Configuration.mapBuildingDiagSymmetry) {

                        map.tryPlace(new Obstacle((ObstacleData)model),map.getTile(x1, y2));
                        map.tryPlace(new Obstacle((ObstacleData)model),map.getTile(x2, y2));
                    }
                }
                else if(Configuration.mapBuildingYSymmetry) {
                    map.tryPlace(new Obstacle((ObstacleData)model), map.getTile(x1, y2));
                    if(Configuration.mapBuildingDiagSymmetry) {
                        map.tryPlace(new Obstacle((ObstacleData)model),map.getTile(x2, y1));
                        map.tryPlace(new Obstacle((ObstacleData)model),map.getTile(x2, y2));
                    }
                }
                else if(Configuration.mapBuildingDiagSymmetry) {
                    map.tryPlace(new Obstacle((ObstacleData)model),map.getTile(x2, y2));
                }
            }
        }
    }
}