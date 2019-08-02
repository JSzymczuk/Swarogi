package swarogi.datamodels;

public class ObstacleData extends AbstractPlaceableData {

    private String movementTileGroup;
    private String buildingTileGroup;
    private String placingTileGroup;

    private boolean ignorePlacingRules;

    @Override
    public boolean isSelectable() { return false; }
    @Override
    public boolean isMovable() { return false; }
    @Override
    public boolean isIgnoringBuildingRules() { return false; }

    @Override
    public boolean isIgnoringPlacingRules() { return this.ignorePlacingRules; }
    public void setIgnoringPlacingRulesFlag(boolean value) { this.ignorePlacingRules = value; }

    @Override
    public String getMovementTileGroup() { return movementTileGroup; }
    public void setMovementTileGroup(String name) { this.movementTileGroup = name; }

    @Override
    public String getBuildingTileGroup() { return buildingTileGroup; }
    public void setBuildingTileGroup(String name) { this.buildingTileGroup = name; }

    @Override
    public String getPlacingTileGroup() { return placingTileGroup; }
    public void setPlacingTileGroup(String name) { this.placingTileGroup = name; }
}
