package swarogi.models;

import swarogi.datamodels.DecorationData;
import swarogi.enums.Characteristic;
import swarogi.game.Tile;
import swarogi.interfaces.Placeable;
import swarogi.interfaces.PlaceableData;

public class Decoration implements Placeable {

    private Tile tile;
    private DecorationData model;
    private int customTranslationX;
    private int customTranslationY;

    public Decoration(DecorationData model, int x, int y) {
        this.model = model;
        this.customTranslationX = x;
        this.customTranslationY = y;
    }

    @Override
    public PlaceableData getPlaceableData() {
        return model;
    }

    @Override
    public Tile getTile() {
        return tile;
    }

    @Override
    public void onPositionChanged(Tile tile) {
        this.tile = tile;
    }

    public int getCustomTranslationX() { return customTranslationX; }
    public int getCustomTranslationY() { return customTranslationY; }

    @Override
    public boolean hasCharacteristic(Characteristic characteristic) {
        return model.hasCharacteristic(characteristic);
    }
}