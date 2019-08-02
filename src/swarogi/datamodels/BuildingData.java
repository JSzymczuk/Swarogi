package swarogi.datamodels;

import swarogi.enums.ArmorType;
import swarogi.enums.Characteristic;
import swarogi.interfaces.DestructibleData;

import java.util.ArrayList;
import java.util.List;

public class BuildingData extends AbstractPlaceableData implements DestructibleData {

    private String description;

    // Punkty, na których mogą zostać umieszczone jednostki opuszczające budynek.
    private String assemblyPoints;
    private final List<UnitData> createdUnits;

    // Placeable
    private String movementTileGroup;
    private String buildingTileGroup;
    private String placingTileGroup;

    // Destructible
    private int maxHealth;
    private int defense;

    public BuildingData() {
        super();
        createdUnits = new ArrayList<>();
        hpBarWidth = 100;
        allowedUnits = new ArrayList<>();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getBaseMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }

    public int getBaseDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }

    public ArmorType getBaseArmorType() { return ArmorType.Fortified; }

    @Override
    public String getMovementTileGroup() { return movementTileGroup; }
    public void setMovementTileGroup(String name) { this.movementTileGroup = name; }

    @Override
    public String getBuildingTileGroup() { return buildingTileGroup; }
    public void setBuildingTileGroup(String name) { this.buildingTileGroup = name; }

    @Override
    public String getPlacingTileGroup() { return placingTileGroup; }
    public void setPlacingTileGroup(String name) { this.placingTileGroup = name; }

    @Override
    public boolean isSelectable() { return true; }
    @Override
    public boolean isMovable() { return false; }
    @Override
    public boolean isIgnoringPlacingRules() { return false; }
    @Override
    public boolean isIgnoringBuildingRules() { return false; }

    public String getAssemblyPoints() { return assemblyPoints; }
    public void setAssemblyPoints(String name) { this.assemblyPoints = name; }

    public List<UnitData> getCreatedUnits() { return this.createdUnits; }
    public void addCreatedUnit(UnitData unitData) { this.createdUnits.add(unitData); }

    private int hpBarWidth;
    private int hpBarY;

    @Override
    public int getHpBarWidth() { return hpBarWidth; }
    public void setHpBarWidth(int value) { this.hpBarWidth = value; }

    @Override
    public int getHpBarPositionY() { return this.hpBarY; }
    public void setHpBarPositionY(int value) { this.hpBarY = value; }

    private int foodCost;
    private int woodCost;
    private int requiredTribeLevel;
    private int constructionTime;

    public int getFoodCost() { return this.foodCost; }
    public void setFoodCost(int cost) { this.foodCost = cost; }

    public int getWoodCost() { return this.woodCost; }
    public void setWoodCost(int cost) { this.woodCost = cost; }

    public int getRequiredTribeLevel() { return this.requiredTribeLevel; }
    public void setRequiredTribeLevel(int level) { this.requiredTribeLevel = level; }

    public int getConstructionTime() { return this.constructionTime; }
    public void setConstructionTime(int value) { this.constructionTime = value; }

    private boolean foodProvidingFlag;
    private boolean woodProvidingFlag;

    public boolean isProvidingFood() { return this.foodProvidingFlag; }
    public void setFoodProvidingFlag(boolean value) { this.foodProvidingFlag = value; }
    public boolean isProvidingWood() { return this.woodProvidingFlag; }
    public void setWoodProvidingFlag(boolean value) { this.woodProvidingFlag = value; }

    private int capacity;
    private boolean acceptingAllUnitsFlag;
    private final List<UnitData> allowedUnits;

    public int getUnitsCapacity() { return this.capacity; }
    public void setUnitsCapacity(int value) { this.capacity = value; }

    public boolean isAcceptingAllUnits() { return this.acceptingAllUnitsFlag; }
    public void setAcceptingAllUnitsFlag(boolean value) { this.acceptingAllUnitsFlag = value; }

    public List<UnitData> getAllowedUnits() { return this.allowedUnits; }
    public void addAllowedUnits(UnitData unitData) { this.allowedUnits.add(unitData); }
}