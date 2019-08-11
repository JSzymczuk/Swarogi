package swarogi.playermodes;

import swarogi.data.Database;
import swarogi.datamodels.BuildingData;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.interfaces.PlaceableData;
import swarogi.interfaces.PlayerModeChangeListener;
import swarogi.models.Building;
import swarogi.models.Player;

import java.util.ArrayList;
import java.util.List;

public class DebugBuildingsPlacingPlayerMode extends DebugPlacingPlayerMode {

    private static final List<BuildingData> models;

    private int currentModel;

    static {
        models = new ArrayList<>();
        models.add(Database.Gord);
        models.add(Database.Barracks);
        models.add(Database.Farm);
        models.add(Database.Tower);
        models.add(Database.Chram);
    }

    public DebugBuildingsPlacingPlayerMode(Player player, PlayerModeChangeListener listener, GameMap map) {
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
        BuildingData model = 0 <= currentModel && currentModel < models.size() ? models.get(currentModel) : null;
        if (model != null) {
            Building building = new Building(model, player, true);
            GameMap map = getMap();
            if (map.tryPlace(building, tile)) {
                map.addDestructible(building);
                player.addBuilding(building);
            }
        }
    }
}