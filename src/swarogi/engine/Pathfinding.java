package swarogi.engine;

import swarogi.enums.TerrainType;
import swarogi.game.Tile;
import swarogi.interfaces.Destructible;

import java.util.*;

public class Pathfinding {

    private Tile origin;
    private float maxDistance;
    private HashMap<Tile, PathfindingNodeInfo> pathfindingInfo;
    private HashMap<Destructible, Tile> targetPathTiles;

    public Pathfinding(Tile origin, float maxDistance) {
        this.origin = origin;
        this.maxDistance = maxDistance;
        this.pathfindingInfo = new HashMap<>();
        this.targetPathTiles = new HashMap<>();
        findMeeleTargetsPaths(origin, maxDistance);
    }

    public boolean canAccess(Tile tile) {
        return pathfindingInfo.containsKey(tile);
    }
    public boolean canAccess(Destructible destructible) { return targetPathTiles.containsKey(destructible); }

    public int getDistanceTo(Tile tile) {
        return pathfindingInfo.containsKey(tile) ? pathfindingInfo.get(tile).distance : -1;
    }

    public int getDistanceTo(Destructible destructible) {
        return this.getDistanceTo(targetPathTiles.getOrDefault(destructible, null));
    }

    public Set<Tile> getAccessibleTiles() {
        return new HashSet<>(pathfindingInfo.keySet());
    }

    public Set<Destructible> getAccessibleTargets() {
        return new HashSet<>(targetPathTiles.keySet());
    }

    public Stack<Tile> getPathTo(Tile tile) {
        if (!pathfindingInfo.containsKey(tile)) { return null; }

        Stack<Tile> result = new Stack<>();
        result.push(tile);
        PathfindingNodeInfo current = pathfindingInfo.get(tile);

        while (current.previous != null) {
            result.push(current.previous);
            current = pathfindingInfo.get(current.previous);
        }

        return  result;
    }

    public Stack<Tile> getPathTo(Destructible destructible) {
        return getPathTo(targetPathTiles.getOrDefault(destructible, null));
    }

    // Wyszukiwanie dróg do poszczególnych pól w zasięgu. Zamiast tego
    // stosowane jest poniższe wyszukiwanie dróg i celów jednocześnie.
//    private HashMap<Tile, PathfindingNodeInfo> findPaths(Tile from, float maxDistance) {
//
//        HashMap<Tile, PathfindingNodeInfo> nodeInfoMap = new HashMap<>();
//        ArrayDeque<Tile> open = new ArrayDeque<>();
//
//        open.push(from);
//        nodeInfoMap.put(from, new PathfindingNodeInfo(null, 0));
//
//        while (!open.isEmpty()) {
//            Tile current = open.poll();
//            int currentDistance = nodeInfoMap.get(current).distance + 1;
//
//            if (currentDistance <= maxDistance) {
//                for (Tile tile : current.getNeighbors()) {
//                    if (tile.isMovementAllowed()) {
//                        if (nodeInfoMap.containsKey(tile)) {
//                            PathfindingNodeInfo info = nodeInfoMap.get(tile);
//                            if (info.distance > currentDistance) {
//                                open.add(tile);
//                                info.distance = currentDistance;
//                            }
//                        }
//                        else {
//                            open.add(tile);
//                            nodeInfoMap.put(tile, new PathfindingNodeInfo(current, currentDistance));
//                        }
//                    }
//                }
//            }
//        }
//
//        return nodeInfoMap;
//    }

    private void findMeeleTargetsPaths(Tile from, float maxDistance) {

        this.pathfindingInfo.clear();                                         // Informacje o polach (min odległość itp)
        this.targetPathTiles.clear();                                         // Optymalne pole dla celu
        HashMap<Tile, List<Destructible>> destructiblesAt = new HashMap<>();  // Listy celów na polach
        ArrayDeque<Tile> open = new ArrayDeque<>();

        open.push(from);
        pathfindingInfo.put(from, new PathfindingNodeInfo(null, 0));

        while (!open.isEmpty()) {
            Tile current = open.poll();
            int currentDistance = pathfindingInfo.get(current).distance;

            if (currentDistance <= maxDistance) { // Jednostki nie muszą należeć do obszaru ruchu - wystarczy, że do niego przylegają
                for (Tile tile : current.getNeighbors()) {

                    // Znajdź potencjalne cele na sąsiedzie rozważanego pola
                    List<Destructible> destructibles;
                    if (destructiblesAt.containsKey(tile)) {
                        destructibles = destructiblesAt.get(tile);
                    }
                    else {
                        destructibles = tile.getDestructibles();
                        destructiblesAt.put(tile, destructibles);
                    }

                    // Zapisz nowe cele, a jeśli nowa droga do celu jest krótsza,
                    // zapisz rozważane pole jako obecne optymalne dla celu
                    for (Destructible d : destructibles) {
                        if (targetPathTiles.containsKey(d)) {
                            if (pathfindingInfo.get(targetPathTiles.get(d)).distance > currentDistance) {
                                targetPathTiles.replace(d, current);
                            }
                        }
                        else {
                            targetPathTiles.put(d, current);
                        }
                    }
                }
            }
            if (currentDistance < maxDistance) {
                for (Tile tile : current.getNeighbors()) {
                    // Dodaj informacje o połączeniu z rozważanym polem
                    if (currentDistance <= maxDistance && tile.isMovementAllowed()) {
                        if (pathfindingInfo.containsKey(tile)) {
                            PathfindingNodeInfo info = pathfindingInfo.get(tile);
                            if (info.distance > currentDistance + 1) {
                                open.add(tile);
                                info.distance = currentDistance + 1;
                            }
                        } else {
                            open.add(tile);
                            pathfindingInfo.put(tile, new PathfindingNodeInfo(current, currentDistance + 1));
                        }
                    }
                }
            }
        }
    }

    class PathfindingNodeInfo {
        Tile previous;
        int distance;

        PathfindingNodeInfo(Tile previous, int distance) {
            this.previous = previous;
            this.distance = distance;
        }
    }
}
