package swarogi.actions;

import swarogi.common.Configuration;
import swarogi.game.Tile;
import swarogi.models.Unit;
import swarogi.interfaces.Action;
import java.util.Stack;

public class MovementAction implements Action {

    private final Unit unit;
    private boolean started;
    private boolean useActionPoints;

    public MovementAction(Unit unit, boolean useActionPoints) {
        this.unit = unit;
        this.useActionPoints = useActionPoints;
    }

    @Override
    public boolean canBeExecuted() {
        Stack<Tile> path = unit.getPath();
        // Jeśli jednostka zginęła lub już wykonała ruch, to nie może wykonać akcji.
        if (unit != null && unit.isAlive() && unit.getOwner().hasCommandPoints(Configuration.MOVEMENT_COMMAND_POINTS_COST)
                && unit.hasActionPoints(Configuration.MOVEMENT_ACTION_POINTS_COST)
                && unit.canMove() && path != null && !path.isEmpty()) {
            Tile tile = path.pop();
            // Jeśli jednostka nie znajduje się na pierwszym polu drogi, to nie można wykonać akcji.
            if (unit.getTile() != tile) {
                return false;
            }
            // Jeśli długość trasy przekracza zasięg jednostki, to nie można wykonać akcji.
            if (path.size() > unit.getSteps()) {
                return false;
            }
            // Jeśli któreś z pól trasy zostało zablokowane, to nie można wykonać akcji.
            while (!path.isEmpty()) {
                tile = path.pop();
                if (!tile.isMovementAllowed()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean hasStarted() {
        return started;
    }

    @Override
    public boolean isCompleted() {
        return !unit.isMoving();
    }

    @Override
    public void start() {
        unit.setConstructedBuilding(null); // TODO: Jakoś ładniej to obsługiwać?
        if (this.useActionPoints) {
            unit.useActionPoints(Configuration.MOVEMENT_ACTION_POINTS_COST);
            unit.getOwner().decreaseCommandPoints(Configuration.MOVEMENT_COMMAND_POINTS_COST);
        }
        started = true;
    }

    @Override
    public void update() {
        unit.updateMovement();
    }

    @Override
    public void finish() { }

    @Override
    public void abort() {
        unit.clearPath();
    }
}
