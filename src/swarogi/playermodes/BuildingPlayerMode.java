package swarogi.playermodes;

import swarogi.actions.CreateBuildingAction;
import swarogi.actions.MovementAction;
import swarogi.common.Configuration;
import swarogi.common.ContentManager;
import swarogi.datamodels.BuildingData;
import swarogi.engine.Movement;
import swarogi.engine.Pathfinding;
import swarogi.enums.ActionButton;
import swarogi.enums.TileSelectionTag;
import swarogi.game.GameCamera;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.game.TilesSelection;
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
    }

    @Override
    public void update() {

        ControlsProvider controlsProvider = getControls();

        // Wyjdź z trybu budowania
        if (controlsProvider.isButtonDown(ActionButton.CANCEL)) {
            changePlayerMode(new UnitCommandPlayerAction(getPlayer(), getListener(), getMap(), unit));
            return;
        }

        boolean updateNeeded = controlsProvider.hasMousePositionChanged() || getCamera().hasPositionChanged();

        // Wybierz budynek
        if (player.getCommandPoints() > 0 && player.hasCommandPoints(Configuration.BUILDING_COMMAND_POINTS_COST)
                && unit.hasActionPoints(Configuration.BUILD_ACTION_POINTS_COST)) {
            ActionButton selectedButton = controlsProvider.getFirstSelectedOption();
            if (selectedButton != null) {
                int option = ActionButton.getOption(selectedButton);
                List<BuildingData> buildings = unit.getUnitData().getCreatedBuildings();
                if (option < buildings.size() && buildings.get(option) != plannedBuilding) {
                    BuildingData buildingSelected = buildings.get(option);
                    if (player.areRequirementsMet(buildingSelected)) {
                        plannedBuilding = buildingSelected;
                        updateNeeded = true;
                    }
                }
            }
        }

        Tile hoverTile = getHoverTile();

        if (plannedBuilding != null) {
            if (updateNeeded) {
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
                        listener.addAction(new CreateBuildingAction(plannedBuilding, map, hoverTile, player, unit));
                        changePlayerMode(new SelectionPlayerMode(getPlayer(), listener, map));
                    }
                }
            }
        }
    }

    @Override
    public void renderSelection(Graphics g, GameCamera camera) {

        TileSelectionTag buildingTag = isPositionValid ? TileSelectionTag.INACTIVE_POSITIVE : TileSelectionTag.INACTIVE_NEGATIVE;
        for (Tile tile : buildingTiles) {
            renderTile(g, tile, camera, buildingTag);
        }

        for (Tile tile : pathfindingTiles) {
            if (!buildingTiles.contains(tile)) {
                renderTile(g, tile, camera, TileSelectionTag.HOVER_NEUTRAL);
            }
        }
    }

    @Override
    public void renderGui(Graphics g, Dimension dimension, Font font) {
        renderTextBack(g, dimension);
        List<BuildingData> createdBuildings = unit.getUnitData().getCreatedBuildings();
        int n = createdBuildings.size();
        int dx = dimension.width / n;
        int paddingLeft = 20;
        int y = dimension.height - ContentManager.bottomTextShadow.getHeight() / 2;
        for (int i = 0; i < n; ++i) {
            BuildingData building = createdBuildings.get(i);

            if (building.getRequiredTribeLevel() > player.getTribeLevel()) {
                g.setColor(Color.black);
                g.drawString(displayString(building)
                        + "\n(wym. poz. " + Integer.toString(building.getRequiredTribeLevel()) + ")", i * dx + paddingLeft, y);
            }
            else if (player.getCommandPoints() == 0) {
                g.setColor(Color.black);
                g.drawString(displayString(building), i * dx + paddingLeft, y);
            }
            else if (player.areRequirementsMet(building)) {
                g.setColor(Color.white);
                g.drawString("["  + Integer.toString(i + 1) + "] " + displayString(building), i * dx + paddingLeft, y);
            }
            else {
                g.setColor(Color.red);
                g.drawString(displayString(building), i * dx + paddingLeft, y);
            }
        }
    }

    private String displayString(BuildingData building) {
        return building.getName()
                + " - Ż: " + Integer.toString(building.getFoodCost())
                + " | D: " + Integer.toString(building.getWoodCost());
    }
}