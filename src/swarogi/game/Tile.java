package swarogi.game;

import swarogi.common.Configuration;
import swarogi.enums.PlacingType;
import swarogi.enums.TerrainType;
import swarogi.interfaces.Destructible;
import swarogi.interfaces.Placeable;
import swarogi.interfaces.PlaceableData;
import swarogi.models.Decoration;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Tile {

    GameMap map;
    final int xIdx;
    final int yIdx;
    private TerrainType terrainType;
    private TerrainType virtualTerrainType; // Pomimo, że pole wygląda na jeden typ, traktowane jest jako drugi (np. gdy jest na nim most).

    public boolean isMovementAllowed() { return movementFlag; }

    public boolean isPlacingAllowed(PlacingType placingType) {
        // TODO: Na razie tak o, żeby było czytelnie co się dzieje.
        if (placingType == PlacingType.ONLY_LAND) {
            return placingFlag && !virtualTerrainType.isWater() && virtualTerrainType.isPlacingAllowed();
        }
        else if (placingType == PlacingType.ONLY_WATER) {
            return placingFlag && virtualTerrainType.isWater() && virtualTerrainType.isPlacingAllowed();
        }
        else if (placingType == PlacingType.LAND_OR_WATER) {
            return placingFlag && virtualTerrainType.isPlacingAllowed();
        }
        return false;
    }

    public boolean isBuildingAllowed(PlacingType placingType) {
        if (buildingObstacles.size() > 0) { return false; }
        if (placingType == PlacingType.ONLY_LAND) {
            return !virtualTerrainType.isWater() && virtualTerrainType.isBuildingAllowed();
        }
        else if (placingType == PlacingType.ONLY_WATER) {
            return virtualTerrainType.isWater() && virtualTerrainType.isBuildingAllowed();
        }
        else if (placingType == PlacingType.LAND_OR_WATER) {
            return virtualTerrainType.isBuildingAllowed();
        }
        return false;
    }

    public boolean canBeAdjacentToBuilding(PlacingType placingType) {
        if (!buildingAdjacencyFlag) { return false; }
        if (placingType == PlacingType.ONLY_LAND) {
            return !virtualTerrainType.isWater() && virtualTerrainType.isPlacingAllowed();
        }
        else if (placingType == PlacingType.ONLY_WATER) {
            return virtualTerrainType.isWater() && virtualTerrainType.isPlacingAllowed();
        }
        else if (placingType == PlacingType.LAND_OR_WATER) {
            return virtualTerrainType.isPlacingAllowed();
        }
        return false;
    }

    private HashSet<Placeable> movementObstacles;
    private HashSet<Placeable> buildingObstacles;
    private HashSet<Placeable> selectables;
    private boolean placingFlag;
    private boolean movementFlag;
    private boolean buildingAdjacencyFlag;

    public Tile(GameMap map, int xIdx, int yIdx) {
        this.map = map;
        this.xIdx = xIdx;
        this.yIdx = yIdx;
        this.placingFlag = true;
        this.buildingAdjacencyFlag = true;
        this.movementObstacles = new HashSet<>();
        this.buildingObstacles = new HashSet<>();
        this.selectables = new HashSet<>();
    }

    public Point getTopLeft() {
        int x = (Configuration.TILE_WIDTH - Configuration.TILE_SLANT_WIDTH) * xIdx;
        if (xIdx % 2 == 1) {
            int tileHeight = Configuration.TILE_HEIGHT;
            return new Point(x,yIdx * tileHeight + tileHeight / 2);
        }
        else {
            return new Point(x, yIdx * Configuration.TILE_HEIGHT);
        }
    }

    public Point getCenter() {
        int x = (Configuration.TILE_WIDTH - Configuration.TILE_SLANT_WIDTH) * xIdx + Configuration.TILE_WIDTH / 2;
        if (xIdx % 2 == 1) {
            return new Point(x,(yIdx + 1) * Configuration.TILE_HEIGHT);
        }
        else {
            int tileHeight = Configuration.TILE_HEIGHT;
            return new Point(x, yIdx * tileHeight + tileHeight / 2);
        }
    }

    public TerrainType getTerrainType() { return  this.terrainType; }
    public void setTerrainType(TerrainType terrainType) {
        this.terrainType = terrainType;
        this.movementFlag = terrainType.isPassable();
        this.virtualTerrainType = terrainType;
        // TODO: Uwzględnić to, co było już na polu.
    }

    // TODO: Refaktoryzacja - te metody chyba nie powinny być publiczne
    public void addMovementObstacle(Placeable placeable) {
        movementObstacles.add(placeable);
        this.movementFlag = virtualTerrainType.isPassable() && movementObstacles.size() == 0;
    }

    public void removeMovementObstacle(Placeable placeable) {
        movementObstacles.remove(placeable);
        this.movementFlag = virtualTerrainType.isPassable() && movementObstacles.size() == 0;
    }

    public void addBuildingObstacle(Placeable placeable) { buildingObstacles.add(placeable); }

    public void removeBuildingObstacle(Placeable placeable) { buildingObstacles.remove(placeable); }

    public void addSelectable(Placeable placeable) {
        selectables.add(placeable);
        PlaceableData model = placeable.getPlaceableData();
        placingFlag &= model.isIgnoringPlacingRules();
        buildingAdjacencyFlag &= model.isMovable() || model.isIgnoringPlacingRules();
        TerrainType newTerrainType = placeable.getPlaceableData().getInducedTerrainType();
        if (newTerrainType != null) {
            this.virtualTerrainType = newTerrainType;
            this.movementFlag = virtualTerrainType.isPassable() && movementObstacles.size() == 0;
        }
    }

    public void removeSelectable(Placeable placeable) {
        selectables.remove(placeable);
        placingFlag = selectables.stream().allMatch(s -> s.getPlaceableData().isIgnoringPlacingRules());
        buildingAdjacencyFlag = selectables.stream().allMatch(p ->
                p.getPlaceableData().isMovable() || p.getPlaceableData().isIgnoringPlacingRules());
        if (virtualTerrainType != terrainType && placeable.getPlaceableData().getInducedTerrainType() != null) {
            for (Placeable p : selectables) {
                TerrainType newTerrainType = p.getPlaceableData().getInducedTerrainType();
                if (newTerrainType != null) {
                    this.virtualTerrainType = newTerrainType;
                    this.movementFlag = virtualTerrainType.isPassable() && movementObstacles.size() == 0;
                    return;
                }
            }
        }
    }

    public HashSet<Placeable> getPlaceables() {
        return (HashSet<Placeable>)selectables.clone();
    }

    public List<Placeable> getSelectables() {
        return selectables.stream()
                .filter(x -> x.getPlaceableData().isSelectable())
                .collect(Collectors.toList());
    }

    public List<Destructible> getDestructibles() {
        return selectables.stream()
                .filter(s -> s instanceof Destructible)
                .map(s -> (Destructible)s)
                .collect(Collectors.toList());
    }

    public List<Placeable> removeDecorations() {
        // TODO: Dwa razy to samo. Jakoś ładniej, prościej.
        List<Placeable> result = this.selectables.stream()
                .filter(p -> p instanceof Decoration)
                .collect(Collectors.toList());
        this.selectables.removeIf(p -> p instanceof Decoration);
        return result;
    }

    public List<Tile> getNeighbors() { return map.getTileNeighbors(this); }

    public static boolean areNeighbors(Tile tile1, Tile tile2) {
        int x1 = tile1.xIdx, y1 = tile1.yIdx;
        int x2 = tile2.xIdx, y2 = tile2.yIdx;

        if (x1 % 2 == 0) {
            if (x2 == x1 - 1 || x2 == x1 + 1) {
                return y2 == y1 || y2 == y1 - 1;
            }
            else if (x2 == x1) {
                return y2 == y1 - 1 || y2 == y1 + 1;
            }
        }
        else {
            if (x2 == x1 - 1 || x2 == x1 + 1) {
                return y2 == y1 || y2 == y1 + 1;
            }
            else if (x2 == x1) {
                return y2 == y1 - 1 || y2 == y1 + 1;
            }
        }
        return false;
    }

    // TODO: Przenieść gdzie indziej.
    public static List<Tile> getAdjacentTiles(List<Tile> tiles) {
        ArrayList<Tile> result = new ArrayList<>();
        for (Tile tile : tiles) {
            for (Tile neighbor : tile.getNeighbors()) {
                if (!tiles.contains(neighbor) && !result.contains(neighbor)) {
                    result.add(neighbor);
                }
            }
        }
        return result;
    }

    // TODO: Powinno być prywatne?
    public GameMap getMap() { return map; }
    public int getIdX() { return xIdx; }
    public int getIdY() { return yIdx; }

}
