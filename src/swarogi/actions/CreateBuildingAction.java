package swarogi.actions;

import swarogi.common.Configuration;
import swarogi.datamodels.BuildingData;
import swarogi.engine.Movement;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.interfaces.Action;
import swarogi.interfaces.Placeable;
import swarogi.models.Building;
import swarogi.models.Decoration;
import swarogi.models.Player;
import swarogi.models.Unit;

public class CreateBuildingAction implements Action {

    private BuildingData model;
    private GameMap map;
    private Tile tile;
    private Unit unit;
    private Player player;
    private boolean started;

    public CreateBuildingAction(BuildingData model, GameMap map, Tile tile, Player player, Unit creator) {
        this.model = model;
        this.map = map;
        this.tile = tile;
        this.player = player;
        this.unit = creator;
    }

    @Override
    public boolean canBeExecuted() {
        return player.isActive() && unit != null && unit.hasActionPoints(Configuration.BUILD_ACTION_POINTS_COST)
                && player.hasCommandPoints(Configuration.BUILDING_COMMAND_POINTS_COST)
                && player.areRequirementsMet(model) && Movement.canPlace(model, tile);
    }

    @Override
    public boolean hasStarted() { return started; }

    @Override
    public boolean isCompleted() { return started; }

    @Override
    public void start() {
        player.payFor(model);
        player.decreaseCommandPoints(Configuration.BUILDING_COMMAND_POINTS_COST);

        Building building = new Building(model, player);
        if (map.tryPlace(building, tile)) {
            map.addDestructible(building);
            player.addBuilding(building);
            unit.useActionPoints(Configuration.BUILD_ACTION_POINTS_COST);
            unit.setConstructedBuilding(building);
            for (Tile tile : building.getAllTiles()) {
                for (Placeable decoration : tile.removeDecorations()) {
                    this.map.removePlaceable(decoration);
                }
            }
        }
        started = true;
    }

    @Override
    public void update() { }

    @Override
    public void finish() { }

    @Override
    public void abort() { }
}
