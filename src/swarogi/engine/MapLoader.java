package swarogi.engine;

import swarogi.data.Database;
import swarogi.datamodels.ObstacleData;
import swarogi.enums.TerrainType;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.interfaces.Placeable;
import swarogi.models.Decoration;
import swarogi.models.Obstacle;

import java.io.*;

public final class MapLoader {

    public static GameMap loadMap(String path) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            line = reader.readLine();
            String[] lineSplit = line.split(";");
            int tilesx = Integer.parseInt(lineSplit[1]);
            int tilesy = Integer.parseInt(lineSplit[2]);
            GameMap map = new GameMap(tilesx, tilesy);

            TerrainType[] terrainTypes = TerrainType.values();

            while (line != null) {
                lineSplit = line.split(";");

                if (lineSplit[0].equals("Player")) {
                    map.addPlayerPosition(map.getTile(Integer.parseInt(lineSplit[1]), Integer.parseInt(lineSplit[2])));
                }
                else if (lineSplit[0].equals("Terrain")) {
                    map.getTile(Integer.parseInt(lineSplit[1]), Integer.parseInt(lineSplit[2]))
                            .setTerrainType(terrainTypes[Integer.parseInt(lineSplit[3])]);
                }
                else if (lineSplit[0].equals("Obstacle")) {
                    Tile tile = map.getTile(Integer.parseInt(lineSplit[2]), Integer.parseInt(lineSplit[3]));
                    ObstacleData model = Database.Obstacles.get(lineSplit[1]);
                    if (model == null) { System.err.println("Model " + lineSplit[1] + " was null.\n"); }
                    map.tryPlace(new Obstacle(model), tile);
                }
                else if (lineSplit[0].equals("Decoration")) {
                    map.tryPlace(new Decoration(Database.Decorations.get(lineSplit[1]),
                            Integer.parseInt(lineSplit[4]), Integer.parseInt(lineSplit[5])),
                            map.getTile(Integer.parseInt(lineSplit[2]), Integer.parseInt(lineSplit[3])));
                }

                line = reader.readLine();
            }
            reader.close();

            return map;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveMap(GameMap map, String fileName) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName));

            StringBuilder sb = new StringBuilder();

            sb.append("Swarogi map file\r\n");

            int w = map.getTilesX(), h = map.getTilesY();
            sb.append(String.format("Size;%d;%d\r\n", w, h));

            for (int i = 0; i < w; ++i) {
                for (int j = 0; j < h; ++j) {
                    sb.append(String.format("Terrain;%d;%d;%d\r\n", i, j, map.getTile(i, j).getTerrainType().getValue()));
                }
            }

            for (Placeable placeable : map.getPlaceables()) {
                if (placeable instanceof Obstacle) {
                    Tile tile = placeable.getTile();
                    sb.append(String.format("Obstacle;%s;%d;%d\r\n", placeable.getPlaceableData().getName(), tile.getIdX(), tile.getIdY()));
                }
                else if (placeable instanceof Decoration) {
                    Decoration decoration = (Decoration)placeable;
                    Tile tile = decoration.getTile();
                    sb.append(String.format("Decoration;%s;%d;%d;%d;%d\r\n", decoration.getPlaceableData().getName(), tile.getIdX(), tile.getIdY(),
                            decoration.getCustomTranslationX(), decoration.getCustomTranslationY()));
                }
            }

            writer.write(sb.toString());
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
