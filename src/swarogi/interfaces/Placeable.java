package swarogi.interfaces;

import swarogi.enums.Characteristic;
import swarogi.enums.ObjectState;
import swarogi.game.Tile;

public interface Placeable {
    PlaceableData getPlaceableData();
    Tile getTile();
    ObjectState getObjectState();
    boolean hasCharacteristic(Characteristic characteristic);
    void onPositionChanged(Tile tile);
}
