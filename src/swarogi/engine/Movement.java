package swarogi.engine;

import swarogi.enums.PlacingType;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.interfaces.Destructible;
import swarogi.interfaces.Placeable;
import swarogi.interfaces.PlaceableData;
import swarogi.game.TilesSelection;
import swarogi.models.Building;
import swarogi.models.Unit;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Movement {

    public static boolean canPlace(PlaceableData placeable, Tile tile) {
        // Na tych polach fizycznie będzie się znajdował obiekt.
        TilesSelection placingSelection = TilesSelection.get(placeable.getPlacingTileGroup());
        List<Tile> placingTiles = placingSelection.applyTo(tile);
        if (placingTiles.size() != placingSelection.size()) { return false; }
        PlacingType placingType = placeable.getPlacingType();

        if (!placeable.isIgnoringPlacingRules() && placingTiles.stream()
                .anyMatch(t -> !t.isPlacingAllowed(placingType))) {
            return false;
        }

        if (!placeable.isIgnoringBuildingRules()) {

            if (placingTiles.stream().anyMatch(t -> !t.isBuildingAllowed(placingType))) {
                return false;
            }

            TilesSelection buildingSelection = TilesSelection.get(placeable.getBuildingTileGroup());
            List<Tile> buildingTiles = buildingSelection.applyTo(tile);
            if (buildingTiles.size() != buildingSelection.size()) { return false; }
            if (buildingTiles.stream().anyMatch(t -> !t.canBeAdjacentToBuilding(placingType))) {
                return false;
            }
        }

        return true;
    }

    public static boolean place(Placeable placeable, Tile newTile) {
        if (placeable == null || newTile == null) { return false; }

        PlaceableData placeableData = placeable.getPlaceableData();

        if (canPlace(placeableData, newTile)) {

            // TODO: Można usuwać tylko z tych, na których się nie znajdują.
            removeFromCurrentTiles(placeable);

            List<Tile> newPlacingTiles = TilesSelection.get(placeableData.getPlacingTileGroup(), newTile);
            if (newPlacingTiles != null) {
                for (Tile tile : newPlacingTiles) {
                    tile.addSelectable(placeable);
                }
            }
            List<Tile> newBuildingTiles = TilesSelection.get(placeableData.getBuildingTileGroup(), newTile);
            if (newBuildingTiles != null) {
                for (Tile tile : newBuildingTiles) {
                    tile.addBuildingObstacle(placeable);
                }
            }
            List<Tile> newMovementTiles = TilesSelection.get(placeableData.getMovementTileGroup(), newTile);
            if (newMovementTiles != null) {
                for (Tile tile : newMovementTiles) {
                    tile.addMovementObstacle(placeable);
                }
            }

            placeable.onPositionChanged(newTile);

            return true;
        }
        return false;
    }

    public static void destroy(Destructible destructible) {
        removeFromCurrentTiles(destructible);
        destructible.onDestroyed();
    }

    private static boolean removeFromCurrentTiles(Placeable placeable) {

        PlaceableData placeableData = placeable.getPlaceableData();
        Tile previousTile = placeable.getTile();

        if (previousTile != null) {
            List<Tile> previousPlacingTiles = TilesSelection.get(placeableData.getPlacingTileGroup(), previousTile);
            if (previousPlacingTiles != null) {
                for (Tile tile : previousPlacingTiles) {
                    tile.removeSelectable(placeable);
                }
            }

            List<Tile> previousBuildingTiles = TilesSelection.get(placeableData.getBuildingTileGroup(), previousTile);
            if (previousBuildingTiles != null) {
                for (Tile tile : previousBuildingTiles) {
                    tile.removeBuildingObstacle(placeable);
                }
            }

            List<Tile> previousMovementTiles = TilesSelection.get(placeableData.getMovementTileGroup(), previousTile);
            if (previousMovementTiles != null) {
                for (Tile tile : previousMovementTiles) {
                    tile.removeMovementObstacle(placeable);
                }
            }

            return true;
        }
        return false;
    }

    public static boolean tryPlaceUnitOnAssemblyPoint(Building building, Unit unit) {
        List<Tile> assemblyPoints = TilesSelection.get(building.getModel().getAssemblyPoints(), building.getTile());
        PlacingType unitPlacingType = unit.getUnitData().getPlacingType();
        GameMap map = building.getTile().getMap();
        boolean unitPlaced = false;
        for (Tile tile : assemblyPoints) {
            if (tile.isPlacingAllowed(unitPlacingType)) {
                if (map.tryPlace(unit, tile)) {
                    map.addDestructible(unit);
                    return true;
                }
            }
        }
        return false;
    }

    public static List<Tile> getClosestAdjacentTiles(Pathfinding pathfinding, List<Tile> area) {
        List<Tile> adjacentTiles = Tile.getAdjacentTiles(area);
        int minDistance = Integer.MAX_VALUE;
        //TODO: Zawsze wybierać pierwsze pole? (Wtedy tablica zbędna.) Losowe?
        ArrayList<Tile> closestTiles = new ArrayList<>();
        for (Tile tile : adjacentTiles) {
            if (pathfinding.canAccess(tile)) {
                int distance = pathfinding.getDistanceTo(tile);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestTiles.clear();
                    closestTiles.add(tile);
                } else if (distance == minDistance) {
                    closestTiles.add(tile);
                }
            }
        }
        return closestTiles;
    }
}
