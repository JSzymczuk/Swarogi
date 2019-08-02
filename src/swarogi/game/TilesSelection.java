package swarogi.game;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class TilesSelection {

    public static final HashMap<String, TilesSelection> Selections;

    public static final TilesSelection NONE = new TilesSelection(new ArrayList<>());

    public static final TilesSelection SINGLE_TILE = new TilesSelection(new ArrayList<>(List.of(new Point(0, 0))));

    public static final TilesSelection RADIUS_2 = new TilesSelection(new ArrayList<>(
            List.of(new Point(0, 0), new Point(-1, -1), new Point(0, -1),
                    new Point(1, -1), new Point(1, 0), new Point(0, 1),
                    new Point(-1, 0))));

    public static final TilesSelection RADIUS_3 = new TilesSelection(new ArrayList<>(
            List.of(new Point(0, 0), new Point(-1, -1), new Point(0, -1),
                    new Point(1, -1), new Point(1, 0), new Point(0, 1),
                    new Point(-1, 0), new Point(-2, 0), new Point(-2, -1),
                    new Point(-1, -2), new Point(0, -2), new Point(1, -2),
                    new Point(2, -1), new Point(2, 0), new Point(2, 1),
                    new Point(1, 1), new Point(0, 2), new Point(-1, 1),
                    new Point(-2, 1))));

    public static final TilesSelection TRIANGLE_2 = new TilesSelection(new ArrayList<>(
            List.of(new Point(0, 0), new Point(1, -1), new Point(1, 0))));

    public static final TilesSelection TRIANGLE_3 = new TilesSelection(new ArrayList<>(
            List.of(new Point(-1, 0), new Point(0, 0), new Point(1, -1),
                    new Point(1, 0), new Point(1, 1), new Point(0, 1))));

    public static final TilesSelection TRIANGLE_2_PADDED_1 = new TilesSelection(new ArrayList<>(
            List.of(new Point(0, 0), new Point(1, -1), new Point(1, 0),
                    new Point(1, 1), new Point(0, 1), new Point(-1, 0),
                    new Point(-1, -1), new Point(0, -1), new Point(1, -2),
                    new Point(2, -1), new Point(2, 0), new Point(2, 1))));

    public static final TilesSelection TRIANGLE_2_PADDED_2 = new TilesSelection(new ArrayList<>(
            List.of(new Point(0, 0), new Point(1, -1), new Point(1, 0),
                    new Point(1, 1), new Point(0, 1), new Point(-1, 0),
                    new Point(-1, -1), new Point(0, -1), new Point(1, -2),
                    new Point(2, -1), new Point(2, 0), new Point(2, 1),
                    new Point(2, 2), new Point(1, 2), new Point(0, 2),
                    new Point(-1, 1), new Point(-2, 1), new Point(-2, 0),
                    new Point(-2, -1), new Point(-1, -2), new Point(0, -2),
                    new Point(1, -3), new Point(2, -2), new Point(3, -2),
                    new Point(3, -1), new Point(3, 0), new Point(3, 1)
                    )));

    public static final TilesSelection DIAMOND_2 = new TilesSelection(new ArrayList<>(
            List.of(new Point(0, 0), new Point(-1, -1), new Point(0, -1),
                    new Point(1, -1))));

    public static final TilesSelection DIAMOND_2_PADDED_1 = new TilesSelection(new ArrayList<>(
            List.of(new Point(0, 0), new Point(-1, -1), new Point(0, -1),
                    new Point(1, -1), new Point(1, 0), new Point(0, 1),
                    new Point(-1, 0), new Point(-2, 0), new Point(-2, -1),
                    new Point(-1, -2), new Point(0, -2), new Point(1, -2),
                    new Point(2, -1), new Point(2, 0))));

    public static final TilesSelection LINE_2_DIAG_1 = new TilesSelection(new ArrayList<>(
            List.of(new Point(0, 0), new Point(1, 0))));

    public static final TilesSelection GORD_ASSEMBLY_POINTS = new TilesSelection(new ArrayList<>(
            List.of(
                    new Point(-1, 1), new Point(0, 2), new Point(-2, 1),
                    new Point(1, 2), new Point(-2, 0), new Point(2, 2),
                    new Point(-2, -1), new Point(3, 1), new Point(-1, -2),
                    new Point(3, 0), new Point(0, -2), new Point(3, -1),
                    new Point(1, -3), new Point(3, -2), new Point(2, -2)
            )));

    public static final TilesSelection BARRACKS_ASSEMBLY_POINTS = new TilesSelection(new ArrayList<>(
            List.of(new Point(0, 1), new Point(-1, 0), new Point(1, 0),
                    new Point(-2, 0), new Point(2, 0), new Point(-2, -1),
                    new Point(2, -1), new Point(-1, -2), new Point(1, -2),
                    new Point(0, -2)
            )));

    public static final TilesSelection TOWER_ASSEMBLY_POINTS = new TilesSelection(new ArrayList<>(
            List.of(new Point(0, 1), new Point(-1, 0), new Point(1, 1),
                    new Point(-1, -1), new Point(2, 1), new Point(0, -1),
                    new Point(2, 0), new Point(1, -2), new Point(2, -1)
            )));

    public static final TilesSelection LINE_2_DIAG_1_PADDED_1 = new TilesSelection(new ArrayList<>(
            List.of(new Point(0, 0), new Point(1, 0), new Point(1, 1),
                    new Point(0, 1), new Point(-1, 0), new Point(-1, -1),
                    new Point(0, -1), new Point(1, -1), new Point(2, 0),
                    new Point(2, 1)
                )));

    static {
        Selections = new HashMap<>();
        Selections.put("NONE", NONE);
        Selections.put("SINGLE_TILE", SINGLE_TILE);
        Selections.put("RADIUS_2", RADIUS_2);
        Selections.put("RADIUS_3", RADIUS_3);
        Selections.put("TRIANGLE_2", TRIANGLE_2);
        Selections.put("TRIANGLE_3", TRIANGLE_3);
        Selections.put("TRIANGLE_2_PADDED_1", TRIANGLE_2_PADDED_1);
        Selections.put("TRIANGLE_2_PADDED_2", TRIANGLE_2_PADDED_2);
        Selections.put("DIAMOND_2", DIAMOND_2);
        Selections.put("DIAMOND_2_PADDED_1", DIAMOND_2_PADDED_1);
        Selections.put("LINE_2_DIAG_1", LINE_2_DIAG_1);
        Selections.put("LINE_2_DIAG_1_PADDED_1", LINE_2_DIAG_1_PADDED_1);
        Selections.put("GORD_ASSEMBLY_POINTS", GORD_ASSEMBLY_POINTS);
        Selections.put("BARRACKS_ASSEMBLY_POINTS", BARRACKS_ASSEMBLY_POINTS);
        Selections.put("TOWER_ASSEMBLY_POINTS", TOWER_ASSEMBLY_POINTS);
    }

    public static TilesSelection get(String selectionName) {
        if (Selections.containsKey(selectionName)) {
            return Selections.get(selectionName);
        }
        return null;
    }

    public static List<Tile> get(String selectionName, Tile tile) {
        if (Selections.containsKey(selectionName)) {
            return Selections.get(selectionName).applyTo(tile);
        }
        return null;
    }

    private TilesSelection(List<Point> coordinates) {
        this.coordinates = new ArrayList<>(coordinates);
    }

    public int size() { return coordinates.size(); }

    public List<Tile> applyTo(Tile tile) {
        return applyTo(tile.map, tile.xIdx, tile.yIdx);
    }

    private List<Tile> applyTo(GameMap map, int xIdx, int yIdx) {
        ArrayList<Tile> result = new ArrayList<>();

        if(xIdx % 2 == 1) {
            for (Point point : coordinates) {
                int x = xIdx + point.x;
                Tile tile;
                if (x % 2 == 1) {
                    tile = map.getTile(x, yIdx + point.y);
                }
                else {
                    tile = map.getTile(x, yIdx + point.y + 1);
                }
                if (tile != null) {
                    result.add(tile);
                }
            }
        }
        else {
            for (Point point : coordinates) {
                Tile tile = map.getTile(xIdx + point.x, yIdx + point.y);
                if (tile != null) {
                    result.add(tile);
                }
            }
        }

        return result;
    }

    private ArrayList<Point> coordinates;
}
