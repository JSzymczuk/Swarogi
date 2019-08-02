package swarogi.enums;

public enum TerrainType {
    None(0, false, false, false, 0),
    Dirt(1, true, true, true, 1),
    DryGrass(2, true, true, true, 1),
    Grass(3, true, true, true, 2),
    DarkGrass(4, true, true, true, 2),
    LushGrass(5, true, true, true, 2),
    Soil(6, true, false, true, 5),
    Sand(7, true, false, true, 0),
    Water(8, false, true, true, 2),
    Bridge(20, true, false, true, 0); // wirtualny typ - ustawiany przez mosty

    private TerrainType(int value, boolean isPassable, boolean isBuildingAllowed, boolean isPlacingAllowed, int fertility) {
        this.value = value;
        this.passableFlag = isPassable;
        this.buildingAllowed = isBuildingAllowed;
        this.placingFlag = isPlacingAllowed;
        this.fertility = fertility;
    }

    public int getValue() { return value; }
    public int getFertility() { return fertility; }
    public boolean isPassable() { return passableFlag; }
    public boolean isBuildingAllowed() { return buildingAllowed; }
    public boolean isPlacingAllowed() { return placingFlag; }
    public boolean isWater() { return this == TerrainType.Water; }

    private final int value;
    private final boolean passableFlag;
    private final boolean buildingAllowed;
    private final boolean placingFlag;
    private final int fertility;
}
