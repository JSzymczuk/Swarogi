package swarogi.models;

import swarogi.datamodels.ObstacleData;
import swarogi.enums.Characteristic;
import swarogi.enums.ObjectState;
import swarogi.interfaces.PlaceableData;
import swarogi.game.Tile;
import swarogi.interfaces.Placeable;

public class Obstacle implements Placeable {

    private Tile tile;
    private ObstacleData model;

    public Obstacle(ObstacleData model) {
        this.model = model;
    }

    @Override
    public PlaceableData getPlaceableData() {
        return model;
    }

    @Override
    public ObjectState getObjectState() { return ObjectState.NORMAL; }

    @Override
    public Tile getTile() {
        return tile;
    }

    @Override
    public void onPositionChanged(Tile tile) {
        this.tile = tile;
    }

    @Override
    public boolean hasCharacteristic(Characteristic characteristic) {
        return model.hasCharacteristic(characteristic);
    }
}
