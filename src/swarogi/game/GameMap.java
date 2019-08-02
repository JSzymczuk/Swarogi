package swarogi.game;

import swarogi.common.Configuration;
import swarogi.common.ContentManager;
import swarogi.engine.Movement;
import swarogi.enums.Direction;
import swarogi.enums.TerrainType;
import swarogi.interfaces.Destructible;
import swarogi.interfaces.Placeable;
import swarogi.interfaces.PlaceableData;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class GameMap {

    private final Tile[][] tiles;
    private final List<Placeable> placeables;
    private final List<Destructible> destructibles;
    private int tilesX;
    private int tilesY;
    private List<Tile> playerPositions;

    public GameMap(int tilesX, int tilesY) {
        this.tilesX = tilesX;
        this.tilesY = tilesY;
        tiles = new Tile[tilesX][tilesY];
        placeables = new ArrayList<>();
        destructibles = new ArrayList<>();
        playerPositions = new ArrayList<>();

        for (int i = 0; i < tilesX; ++i) {
            for (int j = 0; j < tilesY; ++j) {
                Tile tile = new Tile(this, i, j);
                tile.setTerrainType(TerrainType.Grass);
                tiles[i][j] = tile;
            }
        }
    }

    public Tile getTile(int xIdx, int yIdx) {
        if (0 <= xIdx && xIdx < tilesX && 0 <= yIdx && yIdx < tilesY) {
            return tiles[xIdx][yIdx];
        }
        return null;
    }

    public Tile getTileNeighbor(Tile tile, Direction direction) {
        int x = tile.xIdx, y = tile.yIdx;

        if (x % 2 == 0) {
            switch (direction) {
                case TOP_LEFT: return getTile(x - 1, y - 1);
                case TOP: return getTile(x, y - 1);
                case TOP_RIGHT: return getTile(x + 1, y - 1);
                case BOTTOM_RIGHT: return getTile(x + 1, y);
                case BOTTOM: return getTile(x, y + 1);
                case BOTTOM_LEFT: return getTile(x - 1, y);
            }
        }
        else {
            switch (direction) {
                case TOP_LEFT: return getTile(x - 1, y);
                case TOP: return getTile(x, y - 1);
                case TOP_RIGHT: return getTile(x + 1, y);
                case BOTTOM_RIGHT: return getTile(x + 1, y + 1);
                case BOTTOM: return getTile(x, y + 1);
                case BOTTOM_LEFT: return getTile(x - 1, y + 1);
            }
        }
        return null;
    }

    public Tile getTileByCoordinates(int x, int y) {

        if (0 <= x && x < getMapWidth() && 0 <= y && y < getMapHeight()) {

            int tileWidth = Configuration.TILE_WIDTH;
            int tileHeight = Configuration.TILE_HEIGHT;
            int tileSlant = Configuration.TILE_SLANT_WIDTH;
            int tileWidthSubSlant = tileWidth - tileSlant;

            int xRel = x % (2 * tileWidthSubSlant);

            if (xRel >= tileWidth) {
                if (y % tileHeight >= tileHeight / 2) {
                    return getTile(x / tileWidthSubSlant, y / tileHeight);
                }
                else {
                    return getTile(x / tileWidthSubSlant, y / tileHeight - 1);
                }
            }
            else if (xRel >= tileWidthSubSlant) {
                int yRel = y % tileHeight;
                int tileHeight2 = tileHeight / 2;
                if (yRel >= tileHeight2) {
                    if (tileHeight2 * (tileSlant - xRel + tileWidthSubSlant) > (yRel - tileHeight2) * tileSlant) {
                        return getTile(x / tileWidthSubSlant - 1, y / tileHeight);
                    }
                    else {
                        return getTile(x / tileWidthSubSlant, y / tileHeight);
                    }
                }
                else {
                    if (yRel * tileSlant > tileHeight2 * (xRel - tileWidthSubSlant)) {
                        return getTile(x / tileWidthSubSlant - 1, y / tileHeight);
                    }
                    else {
                        return getTile(x / tileWidthSubSlant, y / tileHeight - 1);
                    }
                }
            }
            else if (xRel >= tileSlant) {
                return getTile(x / tileWidthSubSlant, y / tileHeight);
            }
            else {
                int yRel = y % tileHeight;
                int tileHeight2 = tileHeight / 2;
                if (yRel >= tileHeight2) {
                    if ((yRel - tileHeight2) * tileSlant > tileHeight2 * xRel) {
                        return getTile(x / tileWidthSubSlant - 1, y / tileHeight);
                    }
                    else {
                        return getTile(x / tileWidthSubSlant, y / tileHeight);
                    }
                }
                else {
                    if (tileHeight2 * (tileSlant - xRel) > yRel * tileSlant) {
                        return getTile(x / tileWidthSubSlant - 1, y / tileHeight - 1);
                    }
                    else {
                        return getTile(x / tileWidthSubSlant, y / tileHeight);
                    }
                }
            }
        }
        return null;
    }

    public int getTilesX() { return tilesX; }
    public int getTilesY() { return tilesY; }

    public int getMapWidth() { return (Configuration.TILE_WIDTH - Configuration.TILE_SLANT_WIDTH) * tilesX + Configuration.TILE_SLANT_WIDTH; }
    public int getMapHeight() { return Configuration.TILE_HEIGHT * tilesY + Configuration.TILE_HEIGHT / 2; }

    public void addPlayerPosition(Tile tile) { this.playerPositions.add(tile); }
    public List<Tile> getPlayerPositions() { return this.playerPositions; }

    public List<Tile> getTileNeighbors(Tile tile) { return getTileNeighbors(tile.xIdx, tile.yIdx); }

    public boolean tryPlace(Placeable placeable, Tile tile) {
        if (Movement.place(placeable, tile)) {
            placeables.add(placeable);
            placeables.sort(new PlaceablesYCoordinateComparator());
            return true;
        }
        return false;
    }
    public List<Placeable> getPlaceables() { return placeables; }

    public List<Destructible> getDestructibles() { return this.destructibles; }
    public void addDestructible(Destructible destructible) { this.destructibles.add(destructible); }
    public boolean removeDestructible(Destructible destructible) {
        return this.destructibles.remove(destructible) && this.placeables.remove(destructible);
    }
    public boolean removePlaceable(Placeable placeable) {
        return this.placeables.remove(placeable);
    }

    /* Zwraca sąsiadujące pola */
    public List<Tile> getTileNeighbors(int xIdx, int yIdx) {
        // TODO: Dodać sprawdzenie na to czy indeksy należą do tablicy?
        ArrayList<Tile> result = new ArrayList(6);
        if (xIdx % 2 == 0) { // Kolumna parzysta
            if (yIdx > 0) {
                if (xIdx > 0) { result.add(tiles[xIdx - 1][yIdx - 1]); }          // lewy górny
                result.add(tiles[xIdx][yIdx - 1]);                                // górny
                if (xIdx < tilesX - 1) { result.add(tiles[xIdx + 1][yIdx - 1]); } // prawy górny
            }
            if (xIdx < tilesX - 1) { result.add(tiles[xIdx + 1][yIdx]); }         // prawy dolny
            if (yIdx < tilesY - 1) { result.add(tiles[xIdx][yIdx + 1]); }         // dolny
            if (xIdx > 0) { result.add(tiles[xIdx - 1][yIdx]); }                  // lewy dolny
        }
        else { // Kolumna nieparzysta
            if (xIdx > 0) { result.add(tiles[xIdx - 1][yIdx]); }                  // lewy górny
            if (yIdx > 0) { result.add(tiles[xIdx][yIdx - 1]); }                  // górny
            if (xIdx < tilesX - 1) { result.add(tiles[xIdx + 1][yIdx]); }         // prawy górny
            if (yIdx < tilesY - 1) {
                if (xIdx < tilesX - 1) { result.add(tiles[xIdx + 1][yIdx + 1]); } // prawy dolny
                result.add(tiles[xIdx][yIdx + 1]);                                // dolny
                if (xIdx > 0) { result.add(tiles[xIdx - 1][yIdx + 1]); }          // lewy dolny
            }
        }
        return result;
    }

    /* Zwraca pola, znajdujące się w podanym pierścieniu */
    public List<Tile> getTilesInRing(Tile tile, int innerRadius, int outerRadius) {

        HashMap<Tile, Integer> minDistances = new HashMap<>();
        ArrayDeque<Tile> open = new ArrayDeque<>();

        open.push(tile);
        minDistances.put(tile, 0);

        // Przeszukiwanie wszerz (dla danego pola krótszy dystans zawsze będzie sprawdzony przed dłuższym)
        while (!open.isEmpty()) {
            Tile current = open.poll();
            int currentDistance = minDistances.get(current) + 1;

            if (currentDistance <= outerRadius) {
                for (Tile t : current.getNeighbors()) {
                    if (!minDistances.containsKey(t)) {
                        minDistances.put(t, currentDistance);
                        open.add(t);
                    }
                }
            }
        }

        ArrayList<Tile> result = new ArrayList<>();
        for (Tile t : minDistances.keySet()) {
            int distance = minDistances.get(t);
            if (innerRadius <= distance && distance <= outerRadius) {
                result.add(t);
            }
        }
        return result;
    }

    public HashMap<Destructible, Integer> getMinDistancesToDestructiblesInRange(Tile tile, int radius) {

        HashMap<Tile, Integer> minDistances = new HashMap<>();
        HashMap<Destructible, Integer> foundDestructibles = new HashMap<>();

        ArrayDeque<Tile> open = new ArrayDeque<>();

        open.push(tile);
        minDistances.put(tile, 0);

        while (!open.isEmpty()) {
            Tile current = open.poll();
            int currentDistance = minDistances.get(current);

            for (Destructible destructible : current.getDestructibles()) {
                if (!foundDestructibles.containsKey(destructible)) {
                    foundDestructibles.put(destructible, currentDistance);
                }
            }
            ++currentDistance;

            if (currentDistance <= radius) {
                for (Tile t : current.getNeighbors()) {
                    if (!minDistances.containsKey(t)) {
                        minDistances.put(t, currentDistance);
                        open.add(t);
                    }
                }
            }
        }

        return foundDestructibles;
    }

    class PlaceablesYCoordinateComparator implements Comparator<Placeable> {

        @Override
        public int compare(Placeable p1, Placeable p2) {
            return Integer.compare(getValueFor(p1), getValueFor(p2));
        }

        private int getValueFor(Placeable placeable) {
            PlaceableData placeableData = placeable.getPlaceableData();
            BufferedImage texture = ContentManager.getModel(placeableData.getTextureName());
            if (texture != null) {
                return placeable.getTile().getTopLeft().y +
                        (int)(texture.getHeight() * placeableData.getYScale()) / 2
                        + placeableData.getYTexturePosition();
            }
            return Integer.MIN_VALUE;
        }
    }
}