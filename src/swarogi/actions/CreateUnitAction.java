package swarogi.actions;

import swarogi.common.Configuration;
import swarogi.datamodels.UnitData;
import swarogi.engine.Movement;
import swarogi.enums.PlacingType;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.game.TilesSelection;
import swarogi.interfaces.Action;
import swarogi.models.Building;
import swarogi.models.Player;
import swarogi.models.Unit;

import java.util.List;

public class CreateUnitAction implements Action {

    private UnitData model;
    private Building building;
    private GameMap map;
    private Player player;
    private boolean started;

    public CreateUnitAction(UnitData model, Building building, GameMap map, Player player) {
        this.model = model;
        this.building = building;
        this.map = map;
        this.player = player;
    }

    @Override
    public boolean canBeExecuted() {
        if (building.isReady() && player.areRequirementsMet(model)
                && player.hasCommandPoints(Configuration.UNIT_CREATION_COMMAND_POINTS_COST)) {
            List<Tile> assemblyPoints = TilesSelection.get(building.getModel().getAssemblyPoints(), building.getTile());
            PlacingType unitPlacingType = model.getPlacingType();
            for (Tile tile : assemblyPoints) {
                if (tile.isPlacingAllowed(unitPlacingType)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasStarted() { return started; }

    @Override
    public boolean isCompleted() { return started; }

    @Override
    public void start() {
        Unit unit = new Unit(model, player);
        if (Movement.tryPlaceUnitOnAssemblyPoint(building, unit)) {
            unit.replenishActionPoints();
            player.addUnit(unit);
            player.payFor(model);
            player.decreaseCommandPoints(Configuration.UNIT_CREATION_COMMAND_POINTS_COST);
        }
        // TODO: Else - zachować jednostkę w budynku?
        started = true;
    }

    @Override
    public void update() { }

    @Override
    public void finish() { }

    @Override
    public void abort() { }
}
