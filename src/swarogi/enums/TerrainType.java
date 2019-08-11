package swarogi.enums;

public enum TerrainType {
    None(0, false, false, false, 0, 20),
    Dirt(1, true, true, true, 1, 5),
    DryGrass(2, true, true, true, 1, 10),
    Grass(3, true, true, true, 2, 11),
    DarkGrass(4, true, true, true, 2, 12),
    LushGrass(5, true, true, true, 2, 13),
    Soil(6, true, false, true, 5, 6),
    Sand(7, true, false, true, 0, 7),
    Water(8, false, true, true, 2, 0),
    Bridge(20, true, false, true, 0, 0); // wirtualny typ - ustawiany przez mosty

    TerrainType(int value, boolean isPassable, boolean isBuildingAllowed, boolean isPlacingAllowed, int fertility, int tilingPriority) {
        this.value = value;
        this.passableFlag = isPassable;
        this.buildingAllowed = isBuildingAllowed;
        this.placingFlag = isPlacingAllowed;
        this.fertility = fertility;
        this.tilingPriority = tilingPriority;
    }

    public int getValue() { return value; }
    public int getFertility() { return fertility; }
    public int getTilingPriority() { return tilingPriority; }
    public boolean isPassable() { return passableFlag; }
    public boolean isBuildingAllowed() { return buildingAllowed; }
    public boolean isPlacingAllowed() { return placingFlag; }
    public boolean isWater() { return this == TerrainType.Water; }

    private final int value;
    private final boolean passableFlag;
    private final boolean buildingAllowed;
    private final boolean placingFlag;
    private final int fertility;
    private final int tilingPriority;
}
