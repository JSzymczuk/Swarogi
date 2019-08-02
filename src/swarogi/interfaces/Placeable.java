package swarogi.interfaces;

import swarogi.enums.Characteristic;
import swarogi.game.Tile;

public interface Placeable {
    PlaceableData getPlaceableData();
    Tile getTile();
    boolean hasCharacteristic(Characteristic characteristic);
    void onPositionChanged(Tile tile);
}
