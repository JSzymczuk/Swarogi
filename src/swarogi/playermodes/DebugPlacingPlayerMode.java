package swarogi.playermodes;

import swarogi.engine.Movement;
import swarogi.enums.ActionButton;
import swarogi.enums.TileSelectionTag;
import swarogi.game.GameCamera;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.game.TilesSelection;
import swarogi.interfaces.ControlsProvider;
import swarogi.interfaces.PlaceableData;
import swarogi.interfaces.PlayerModeChangeListener;
import swarogi.models.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class DebugPlacingPlayerMode extends PlayerMode {

    private GameMap map;
    private List<Tile> selectedTiles;
    private boolean isPositionValid;

    public DebugPlacingPlayerMode(Player player, PlayerModeChangeListener listener, GameMap map) {
        super(player, listener);
        this.map = map;
        this.selectedTiles = new ArrayList<>();

        PlaceableData currentPlaceableData = getPlaceable();
        if (currentPlaceableData != null) {
            System.out.printf("Wybrany model: %s\n", currentPlaceableData.getName());
        }
    }

    protected abstract PlaceableData getPlaceable();
    protected abstract PlaceableData setPlaceable(int option);
    protected abstract void place(Tile tile);

    @Override
    public void update() {

        ControlsProvider controlsProvider = getControls();
        GameCamera camera = getCamera();
        PlaceableData currentPlaceableData = getPlaceable();

        // Sprawdź czy wybrany obiekt może zostać umieszczony na wskazanym polu.
        Point mouseAbsolutePosition = getAbsoluteMousePosition();
        Tile hoverTile = map.getTileByCoordinates(mouseAbsolutePosition.x, mouseAbsolutePosition.y);

        if (hoverTile != null) {
            selectedTiles = TilesSelection.get(getTileSelectionNameForPlaceable(currentPlaceableData)).applyTo(hoverTile);
            isPositionValid = currentPlaceableData != null && Movement.canPlace(currentPlaceableData, hoverTile);
        }
        else {
            this.selectedTiles.clear();
            isPositionValid = false;
        }

        // Umieść, jeśli zatwierdzono
        if (currentPlaceableData != null && controlsProvider.isButtonDown(ActionButton.CONFIRM) && isPositionValid) {
            place(hoverTile);
        }

        // Sprawdź zmianę modelu
        ActionButton optionButton = controlsProvider.getFirstSelectedOption();
        if (optionButton != null) {
            currentPlaceableData = setPlaceable(ActionButton.getOption(optionButton));
            if (currentPlaceableData != null) {
                System.out.printf("Wybrany model: %s\n", currentPlaceableData.getName());
            }
        }
    }

    @Override
    public void renderSelection(Graphics g, GameCamera camera) {
        if (selectedTiles.size() > 0) {
            TileSelectionTag tileSelectionTag = isPositionValid ?
                    TileSelectionTag.INACTIVE_POSITIVE
                    : TileSelectionTag.INACTIVE_NEGATIVE;

            for (Tile tile : selectedTiles) {
                renderTile(g, tile, camera, tileSelectionTag);
            }
        }
    }

    @Override
    public boolean isDebugOnly() { return true; }
    @Override
    public boolean isPausingGameplay() { return true; }
    @Override
    public boolean isLockingCamera() { return false; }

    protected GameMap getMap() { return map; }

    private static String getTileSelectionNameForPlaceable(PlaceableData model) {
        if (model == null) { return "NONE"; }
        else {
            if (model.isIgnoringBuildingRules()) {
                return model.getPlacingTileGroup();
            }
            else {
                return model.getBuildingTileGroup();
            }
        }
    }
}