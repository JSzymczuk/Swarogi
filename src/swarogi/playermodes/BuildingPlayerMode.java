package swarogi.playermodes;

import swarogi.actions.CreateBuildingAction;
import swarogi.actions.MovementAction;
import swarogi.common.Configuration;
import swarogi.common.ContentManager;
import swarogi.datamodels.BuildingData;
import swarogi.engine.Movement;
import swarogi.engine.Pathfinding;
import swarogi.enums.ActionButton;
import swarogi.enums.HorizontalAlign;
import swarogi.enums.TileSelectionTag;
import swarogi.enums.VerticalAlign;
import swarogi.game.GameCamera;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.game.TilesSelection;
import swarogi.gui.DiamondIcon;
import swarogi.gui.Icon;
import swarogi.gui.RenderingHelper;
import swarogi.interfaces.ControlsProvider;
import swarogi.interfaces.PlayerModeChangeListener;
import swarogi.models.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BuildingPlayerMode extends SelectionPlayerMode {

    Unit unit;
    Pathfinding pathfinding;
    Set<Tile> pathfindingTiles;

    BuildingData plannedBuilding;
    List<Tile> buildingTiles;
    boolean isPositionValid;

    public BuildingPlayerMode(Player player, PlayerModeChangeListener listener, GameMap map, Unit unit) {
        super(player, listener, map);
        this.unit = unit;
        this.pathfinding = new Pathfinding(unit.getTile(), unit.getSteps());
        this.pathfindingTiles = pathfinding.getAccessibleTiles();
        this.buildingTiles = new ArrayList<>();
        setIcons();
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

        Point pos = RenderingHelper.getRegularIconPosition(true);
        int x = pos.x;
        int y = pos.y;
        int dx = Configuration.ICON_MARGIN + Configuration.ICON_SIZE;

        List<BuildingData> createdBuildings = unit.getUnitData().getCreatedBuildings();
        for (int i = 0; i < createdBuildings.size(); ++i) {
            BuildingData building = createdBuildings.get(i);

            Icon icon = new DiamondIcon();
            icon.hAlign = HorizontalAlign.LEFT;
            icon.vAlign = VerticalAlign.BOTTOM;
            icon.x = x + i * dx;
            icon.y = y;
            icon.actionButton = ActionButton.toOption(i);
            icon.textureKey = building.getTextureName();

            if (building.getRequiredTribeLevel() > player.getTribeLevel()) {
                icon.lockedFlag = true;
                icon.hoverText = building.getName() + " (wymagany poziom " + building.getRequiredTribeLevel()
                        + ") " + building.getDescription();
            }
            else if (player.getCommandPoints() < Configuration.BUILD_ACTION_POINTS_COST) {
                icon.lockedFlag = true;
                icon.hoverText = building.getName() + " (żywność: " + building.getFoodCost()
                        + " drewno: " + building.getWoodCost()
                        + ") " + building.getDescription();
            }
            else if (player.areRequirementsMet(building)) {
                icon.hoverText = building.getName() + " [" + Integer.toString(i + 1) + "] (żywność: " + building.getFoodCost()
                        + " drewno: " + building.getWoodCost()
                        + ") " + building.getDescription();
                icon.clickAction = () -> changeBuilding(building);
            }
            else {
                icon.noFundsFlag = true;
                icon.hoverText = building.getName() + " (żywność: " + building.getFoodCost()
                        + " drewno: " + building.getWoodCost()
                        + ") " + building.getDescription();
            }

            icon.hoverAction = () -> setText(icon.hoverText);
            icon.unhoverAction = () -> setText(null);
            addIcon(icon);
        }
    }

    @Override
    public void update() {

        if (checkGuiInteraction()) { return; }

        ControlsProvider controlsProvider = getControls();

        if (plannedBuilding != null) {
            if (controlsProvider.hasMousePositionChanged() || getCamera().hasPositionChanged()) {
                updateHover();
            }

            if (controlsProvider.isButtonDown(ActionButton.CONFIRM)) {
                if (isPositionValid) {
                    List<Tile> closestTiles = Movement.getClosestAdjacentTiles(pathfinding, buildingTiles);
                    Tile closestTile = closestTiles.get(0);
                    // Wydaj polecenie budowy
                    if (pathfinding.getDistanceTo(closestTile) <= unit.getSteps()) {
                        PlayerModeChangeListener listener = getListener();
                        GameMap map = getMap();

                        unit.setPath(pathfinding.getPathTo(closestTile));
                        listener.addAction(new MovementAction(unit, false)); // Nie zużywaj punktu akcji na ten ruch
                        listener.addAction(new CreateBuildingAction(plannedBuilding, map, getHoverTile(), player, unit));
                        changePlayerMode(new SelectionPlayerMode(getPlayer(), listener, map));
                    }
                }
            }
        }
    }

    private void updateHover() {
        Tile hoverTile = getHoverTile();
        if (hoverTile != null) {
            buildingTiles = TilesSelection.get(plannedBuilding.getPlacingTileGroup(), hoverTile);
            isPositionValid = false;
            if (Movement.canPlace(plannedBuilding, hoverTile)) {
                List<Tile> adjacentTiles = Tile.getAdjacentTiles(buildingTiles);
                for (Tile tile : adjacentTiles) {
                    if (pathfinding.canAccess(tile)) {
                        isPositionValid = true;
                        break;
                    }
                }
            }
        }
        else {
            buildingTiles.clear();
            isPositionValid = false;
        }
    }

    @Override
    public void renderSelection(Graphics g, GameCamera camera) {

        if (!isGuiInteraction()) {
            TileSelectionTag buildingTag = isPositionValid ? TileSelectionTag.INACTIVE_POSITIVE : TileSelectionTag.INACTIVE_NEGATIVE;
            for (Tile tile : buildingTiles) {
                renderTile(g, tile, camera, buildingTag);
            }
        }

        for (Tile tile : pathfindingTiles) {
            if (!buildingTiles.contains(tile)) {
                renderTile(g, tile, camera, TileSelectionTag.HOVER_NEUTRAL);
            }
        }
    }

    @Override
    public void renderGui(Graphics g, Dimension size, Font font) {
        RenderingHelper.drawSummaryBox(g, size);
        RenderingHelper.drawUnitSummary(g, size, unit);
        RenderingHelper.drawTextArea(g, size, getText());
        RenderingHelper.drawIcons(g, getIcons(), size);
    }

    private void changeBuilding(BuildingData building) {
        if (building != plannedBuilding && player.areRequirementsMet(building)) {
            plannedBuilding = building;
        }
    }

    private void exitMode() {
        changePlayerMode(new UnitCommandPlayerAction(getPlayer(), getListener(), getMap(), unit));
    }


}