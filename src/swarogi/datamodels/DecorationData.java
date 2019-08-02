package swarogi.datamodels;

public class DecorationData extends AbstractPlaceableData {
    @Override
    public boolean isMovable() { return false; }
    @Override
    public boolean isSelectable() { return false; }
    @Override
    public boolean isIgnoringPlacingRules() { return true; }
    @Override
    public boolean isIgnoringBuildingRules() { return true; }

    @Override
    public String getPlacingTileGroup() { return "SINGLE_TILE"; }
    @Override
    public String getMovementTileGroup() { return "NONE"; }
    @Override
    public String getBuildingTileGroup() { return "NONE"; }
}

