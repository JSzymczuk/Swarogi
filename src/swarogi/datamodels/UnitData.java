package swarogi.datamodels;

import swarogi.enums.ArmorType;
import swarogi.interfaces.DestructibleData;

import java.util.ArrayList;
import java.util.List;

public class UnitData extends AbstractPlaceableData implements DestructibleData {

    private String description;

    private final List<BuildingData> createdBuildings;
    private final List<AttackData> attacks;
    private final List<SkillData> skills;
    private final List<UpgradeData> upgrades;
    private List<Integer> defaultAttacks;

    private int maxHealth;
    private int defense;
    private ArmorType armorType;
    private int maxActions;
    private int steps;
    private float movementSpeed;

    private int foodCost;
    private int woodCost;
    private int requiredArmySize;
    private int requiredTribeLevel;

    private int hpBarWidth;
    private int hpBarY;

    public UnitData() {
        super();
        this.attacks = new ArrayList<>();
        this.skills = new ArrayList<>();
        this.createdBuildings = new ArrayList<>();
        this.upgrades = new ArrayList<>();
        this.hpBarWidth = 100;
        this.defaultAttacks = new ArrayList<>();
    }

    public int getBaseMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }

    public int getBaseDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }

    public ArmorType getBaseArmorType() { return armorType; }
    public void setArmorType(ArmorType armorType) { this.armorType = armorType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String getPlacingTileGroup() { return "SINGLE_TILE"; }
    @Override
    public String getMovementTileGroup() { return "SINGLE_TILE"; }
    @Override
    public String getBuildingTileGroup() { return "NONE"; }

    @Override
    public boolean isMovable() { return true; }
    @Override
    public boolean isSelectable() { return true; }
    @Override
    public boolean isIgnoringPlacingRules() { return false; }
    @Override
    public boolean isIgnoringBuildingRules() { return true; }

    public int getBaseSteps() { return steps; }
    public void setSteps(int steps) { this.steps = steps; }

    public float getBaseMovementSpeed() { return movementSpeed; }
    public void setMovementSpeed(float speed) { this.movementSpeed = speed; }

    public List<BuildingData> getCreatedBuildings() { return this.createdBuildings; }
    public void addCreatedUnit(BuildingData buildingData) { this.createdBuildings.add(buildingData); }

    @Override
    public int getHpBarWidth() { return hpBarWidth; }
    public void setHpBarWidth(int value) { this.hpBarWidth = value; }

    @Override
    public int getHpBarPositionY() { return this.hpBarY; }
    public void setHpBarPositionY(int value) { this.hpBarY = value; }

    public List<AttackData> getBaseAttacks() { return this.attacks; }
    public void addAttack(AttackData attackData) { this.attacks.add(attackData); }

    public List<SkillData> getBaseSkills() { return this.skills; }
    public void addSkill(SkillData skillData) { this.skills.add(skillData); }

    public int getMaxActions() { return this.maxActions; }
    public void setMaxActions(int value) { this.maxActions = value; }

    public int getFoodCost() { return this.foodCost; }
    public void setFoodCost(int cost) { this.foodCost = cost; }

    public int getWoodCost() { return this.woodCost; }
    public void setWoodCost(int cost) { this.woodCost = cost; }

    public int getRequiredArmySize() { return this.requiredArmySize; }
    public void setRequiredArmySize(int limit) { this.requiredArmySize = limit; }

    public int getRequiredTribeLevel() { return this.requiredTribeLevel; }
    public void setRequiredTribeLevel(int level) { this.requiredTribeLevel = level; }

    public List<UpgradeData> getUpgrades() { return this.upgrades; }
    public void addUpgrade(UpgradeData upgradeData) { this.upgrades.add(upgradeData); }
    public boolean hasUpgrade(UpgradeData upgradeData) { return this.upgrades.contains(upgradeData); }

    public List<Integer> getDefaultAttacks() { return this.defaultAttacks; }
    public void setDefaultAttacks(List<Integer> defaultAttacks) { this.defaultAttacks = defaultAttacks; }

//    private boolean selectableFlag;
//    private boolean ignoringPlacingRulesFlag;
//    private boolean ignoringBuildingRulesFlag;

//    @Override
//    public boolean isSelectable() { return selectableFlag; }
//    public void setSelectableFlag(boolean flagValue) { selectableFlag = flagValue; }
//
//    @Override
//    public boolean isIgnoringPlacingRules() { return ignoringPlacingRulesFlag; }
//    public void setIgnoringPlacingRulesFlag(boolean flagValue) { ignoringPlacingRulesFlag = flagValue; }
//
//    @Override
//    public boolean isIgnoringBuildingRules() { return ignoringBuildingRulesFlag; }
//    public void setIgnoringBuildingRulesFlag(boolean flagValue) { ignoringBuildingRulesFlag = flagValue; }
}

