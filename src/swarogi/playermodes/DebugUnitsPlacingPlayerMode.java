package swarogi.playermodes;

import swarogi.data.Database;
import swarogi.datamodels.UnitData;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.interfaces.PlaceableData;
import swarogi.interfaces.PlayerModeChangeListener;
import swarogi.models.Player;
import swarogi.models.Unit;

import java.util.ArrayList;
import java.util.List;

public class DebugUnitsPlacingPlayerMode extends DebugPlacingPlayerMode {

    private static final List<UnitData> models;

    private int currentModel;

    static {
        models = new ArrayList<>();
        models.add(Database.Worker);
        models.add(Database.Warrior);
        models.add(Database.Bowman);
        models.add(Database.Rider);
        models.add(Database.Volkhv);
        models.add(Database.Hero);
    }

    public DebugUnitsPlacingPlayerMode(Player player, PlayerModeChangeListener listener, GameMap map) {
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
        UnitData model = 0 <= currentModel && currentModel < models.size() ? models.get(currentModel) : null;
        if (model != null) {
            Unit unit = new Unit(model, player);
            GameMap map = getMap();
            if (map.tryPlace(unit, tile)) {
                map.addDestructible(unit);
                player.addUnit(unit);
            }
        }
    }
}