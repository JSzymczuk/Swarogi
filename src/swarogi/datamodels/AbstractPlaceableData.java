package swarogi.datamodels;

import swarogi.enums.Characteristic;
import swarogi.enums.PlacingType;
import swarogi.enums.TerrainType;
import swarogi.interfaces.PlaceableData;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPlaceableData implements PlaceableData {

    private String name;
    private String textureName;
    private int xTexturePosition;
    private int yTexturePosition;
    private float xScale;
    private float yScale;
    private PlacingType placingType;
    private TerrainType newTerrainType;
    private List<Characteristic> characteristics;

    public AbstractPlaceableData() {
        this.xScale = 1.0f;
        this.yScale = 1.0f;
        this.placingType = PlacingType.ONLY_LAND;
        this.characteristics = new ArrayList<>();
    }

    @Override
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String getTextureName() { return textureName; }
    public void setTextureName(String textureName) { this.textureName = textureName; }

    @Override
    public int getXTexturePosition() { return xTexturePosition; }
    public void setXTexturePosition(int x) { this.xTexturePosition = x; }

    @Override
    public int getYTexturePosition() { return yTexturePosition; }
    public void setYTexturePosition(int y) { this.yTexturePosition = y; }

    @Override
    public float getXScale() { return xScale; }
    public void setXScale(float xScale) { this.xScale = xScale; }

    @Override
    public float getYScale() { return yScale; }
    public void setYScale(float yScale) { this.yScale = yScale; }

    @Override
    public PlacingType getPlacingType() { return this.placingType; }
    public void setPlacingType(PlacingType value) { this.placingType = value; }

    @Override
    public TerrainType getInducedTerrainType() { return this.newTerrainType; }
    public void setInducedTerrainType(TerrainType value) { this.newTerrainType = value; }

    @Override
    public List<Characteristic> getCharacteristics() { return this.characteristics; }
    public boolean hasCharacteristic(Characteristic characteristic) { return this.characteristics.contains(characteristic); }
    public void addCharacteristic(Characteristic characteristic) { this.characteristics.add(characteristic); }
}
