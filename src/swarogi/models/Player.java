package swarogi.models;

import swarogi.common.Configuration;
import swarogi.common.ContentManager;
import swarogi.data.Database;
import swarogi.datamodels.BuildingData;
import swarogi.datamodels.SkillData;
import swarogi.datamodels.UnitData;
import swarogi.datamodels.UpgradeData;
import swarogi.enums.ActionButton;
import swarogi.enums.ObjectState;
import swarogi.enums.TribePath;
import swarogi.game.GameCamera;
import swarogi.interfaces.ControlsProvider;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Player {

    private String name;
    private int team; // TODO: Zrobić z tego klasę
    private Color color;
    private HashMap<String, BufferedImage> textureBases;

    private int tribeLevel;
    private int commandPoints;
    private int food;
    private int wood;
    private int armySize;
    private int armyCapacity;

    private final List<Unit> units;
    private final List<Building> buildings;
    private Unit leader;
    private Building gord;

    private GameCamera camera;
    private ControlsProvider controlsProvider;

    private Map<UnitData, List<Integer>> customEnabledAttacks;
    private Map<UnitData, List<Integer>> customDisabledAttacks;
    private Map<UnitData, List<Integer>> customEnabledSkills;

    public Player() {
        this.textureBases = new HashMap<>();
        this.units = new ArrayList<>();
        this.buildings = new ArrayList<>();
        this.upgrades = new HashMap<>();
        this.tribePathsLevels = new HashMap<>();
        this.customEnabledAttacks = new HashMap<>();
        this.customDisabledAttacks = new HashMap<>();
        this.customEnabledSkills = new HashMap<>();
    }

    public Building getGord() { return this.gord; }
    public void setGord(Building value) { this.gord = value; }
    public Unit getLeader() { return this.leader; }
    public void setLeader(Unit value) { this.leader = value; }

    public int getTribeLevel() { return this.tribeLevel; }
    public void setTribeLevel(int value) { this.tribeLevel = value; }
    public int increaseTribeLevel(int value) { this.tribeLevel += value; return this.tribeLevel; }

    public int getFood() { return this.food; }
    public void setFood(int value) { this.food = value; }
    public boolean hasFood(int value) { return this.food >= value; }
    public int increaseFood(int value) { this.food += value; return this.food; }
    public int decreaseFood(int value) { this.food -= value; if (this.food < 0) { this.food = 0; } return this.food; }

    public int getWood() { return this.wood; }
    public void setWood(int value) { this.wood = value; }
    public boolean hasWood(int value) { return this.wood >= value; }
    public int increaseWood(int value) { this.wood += value; return this.wood; }
    public int decreaseWood(int value) { this.wood -= value; if (this.wood < 0) { this.wood = 0; } return this.wood; }

    public int getMaxCommandPoints() {
        int value = Configuration.BASE_COMMAND_POINTS;
        if (hasUpgrade(Database.IncreasedActionPoints)) {
            value += (int)getUpgradeValue(Database.IncreasedActionPoints);
        }
        return value;
    }
    public int getCommandPoints() { return this.commandPoints; }
    public void restoreCommandPoints() { this.commandPoints = getMaxCommandPoints(); }
    public boolean hasCommandPoints(int amount) { return this.commandPoints >= amount; }
    public void decreaseCommandPoints(int value) { this.commandPoints -= value; }

    public int getArmyCapacity() { return this.armyCapacity; }
    public void setArmyCapacity(int value) { this.armyCapacity = value; }
    public int increaseArmyCapacity(int value) { this.armyCapacity += value; return this.armyCapacity; }
    public int decreaseArmyCapacity(int value) { this.armyCapacity -= value; if (this.armyCapacity < 0) { this.armyCapacity = 0; } return this.armyCapacity; }

    public int getArmySize() { return this.armySize; }
    public boolean hasArmySize(int value) { return this.armyCapacity - this.armySize >= value; }
    public void increaseArmySize(int value) { if (this.armySize + value <= this.armyCapacity) { this.armySize += value; } }
    public void decreaseArmySize(int value) { if (this.armySize - value >= 0) { this.armySize -= value; } }
    public void recalculateArmySize() {
        int sum = 0;
        for (Unit unit : units) {
            sum += unit.getUnitData().getRequiredArmySize();
        }
        this.armySize = sum;
    }

    public boolean areRequirementsMet(BuildingData building) {
        return hasFood(building.getFoodCost())
                && hasWood(building.getWoodCost())
                && getTribeLevel() >= building.getRequiredTribeLevel();
    }

    public boolean areRequirementsMet(UnitData unit) {
        return hasFood(unit.getFoodCost())
                && hasWood(unit.getWoodCost())
                && hasArmySize(unit.getRequiredArmySize())
                && getTribeLevel() >= unit.getRequiredTribeLevel();
    }

    public void payFor(BuildingData model) {
        decreaseFood(model.getFoodCost());
        decreaseWood(model.getWoodCost());
    }

    public void payFor(UnitData model) {
        decreaseFood(model.getFoodCost());
        decreaseWood(model.getWoodCost());
        increaseArmySize(model.getRequiredArmySize());
    }

    // TODO: Mapa budynków zapewniajacych surowce?
    public void getResources() {
        if (hasUpgrade(Database.IncreasedResources)) {
            int value = (int)getUpgradeValue(Database.IncreasedResources);
            for (Building building : buildings) {
                if (building.isReady()) {
                    BuildingData model = building.getModel();
                    if (model.isProvidingFood()) {
                        increaseFood(building.getFoodProvided() * (100 + value) / 100);
                    }
                    if (model.isProvidingWood()) {
                        increaseWood(building.getWoodProvided() * (100 + value) / 100);
                    }
                }
            }
        }
        else {
            for (Building building : buildings) {
                if (building.isReady()) {
                    BuildingData model = building.getModel();
                    if (model.isProvidingFood()) {
                        increaseFood(building.getFoodProvided());
                    }
                    if (model.isProvidingWood()) {
                        increaseWood(building.getWoodProvided());
                    }
                }
            }
        }
    }

    public BufferedImage getTextureBase(String textureBaseName, ObjectState state) {
        if (textureBases.containsKey(textureBaseName)) {
            return textureBases.get(textureBaseName);
        }
        else {
            BufferedImage textureBase = ContentManager.getModelTextureBase(textureBaseName, state);
            if (textureBase != null) {
                textureBase = ContentManager.createColoredBaseFromImage(textureBase,
                        color.getRed(), color.getGreen(), color.getBlue());
                this.textureBases.put(textureBaseName, textureBase);
                return textureBase;
            }
        }
        return null;
    }

    public void updateCamera() {
        boolean moveLeft = controlsProvider.isButtonPressed(ActionButton.MOVE_LEFT);
        boolean moveRight = controlsProvider.isButtonPressed(ActionButton.MOVE_RIGHT);
        boolean moveDown = controlsProvider.isButtonPressed(ActionButton.MOVE_DOWN);
        boolean moveUp = controlsProvider.isButtonPressed(ActionButton.MOVE_UP);

        if (moveLeft && !moveRight) { camera.x -= Configuration.CAMERA_SPEED; }
        else if (moveRight && !moveLeft) { camera.x += Configuration.CAMERA_SPEED; }

        if (moveUp && !moveDown) { camera.y -= Configuration.CAMERA_SPEED; }
        else if (moveDown && !moveUp) { camera.y += Configuration.CAMERA_SPEED; }
    }

    public void setColor(int red, int green, int blue) {
        this.color = new Color(red, green, blue);
    }
    public Color getColor() { return color; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getTeam() { return team; }
    public void setTeam(int team) { this.team = team; }

    public void setCamera(GameCamera camera) { this.camera = camera; }
    public GameCamera getCamera() { return this.camera; }

    public void setControls(ControlsProvider controlsProvider) { this.controlsProvider = controlsProvider; }
    public ControlsProvider getControls() { return this.controlsProvider; }

    public List<Building> getBuildings() { return this.buildings; }
    public void addBuilding(Building building) { this.buildings.add(building); }
    public void removeBuilding(Building building) { this.buildings.remove(building); }

    public List<Unit> getUnits() { return this.units; }
    public void addUnit(Unit unit) { this.units.add(unit); }
    public void removeUnit(Unit unit) {
        this.units.remove(unit);
    }

    public boolean isActive() { return true; }

    public void updateUnits() {
        for (Unit unit : units) {
            unit.updateEffects();
            unit.updateSkillsCooldown();

            if (!unit.hasEffect(Database.Stunned)) {
                unit.replenishActionPoints();
            }
        }
    }

    public void updateBuildings() {
        for (Building building : buildings) {
            if (!building.isReady()) {
                building.updateConstructionTime();
            }
        }
    }

    private Map<UpgradeData, Integer> upgrades;
    private Map<TribePath, Integer> tribePathsLevels;

    private TribePath currentReseach;
    private int currentResearchTime;


    public boolean hasUpgrade(UpgradeData upgrade) {
        return upgrades.containsKey(upgrade);
    }

    public int getUpgradeLevel(UpgradeData upgrade) {
        return upgrades.get(upgrade);
    }

    public Object getUpgradeValue(UpgradeData upgrade) {
        return upgrade.getValue(upgrades.get(upgrade));
    }

    public int getTribePathLevel(TribePath path) {
        if (tribePathsLevels.containsKey(path)) {
            return tribePathsLevels.get(path) + 1;
        }
        return 0;
    }

    public void startResearch(TribePath path) {
        if (!isReseachInProgress()) {
            int current = getTribePathLevel(path);
            if (current < Configuration.MAX_TRIBE_PATH_LEVEL) {
                currentReseach = path;
                currentResearchTime = Database.TribePaths.get(path).getResearchTime(current);
            }
        }
    }

    public boolean isReseachInProgress() {
        return currentReseach != null;
    }

    public void updateResearch() {
        if (currentResearchTime > 0) {
            --currentResearchTime;
            if (currentResearchTime == 0) {
                increaseTribePathLevel(currentReseach);
                currentReseach = null;
            }
        }
    }

    private void increaseTribePathLevel(TribePath path) {
        if (tribePathsLevels.containsKey(path)) {
            int level = tribePathsLevels.get(path) + 1;
            if (level < Configuration.MAX_TRIBE_PATH_LEVEL) {
                tribePathsLevels.replace(path, level);
                updateUpgrades(path, level);
                increaseTribeLevel(Database.TribePaths.get(path).getResearchTime(level));
            }
        }
        else {
            tribePathsLevels.put(path, 0);
            updateUpgrades(path, 0);
            increaseTribeLevel(Database.TribePaths.get(path).getResearchTime(0));
        }
    }

    private void updateUpgrades(TribePath path, int level) {
        Map<UpgradeData, Integer> upgrades = Database.TribePaths.get(path).getUpgrades(level);
        for (UpgradeData upgrade : upgrades.keySet()) {
            if (this.upgrades.containsKey(upgrade)) {
                this.upgrades.replace(upgrade, upgrades.get(upgrade));
            }
            else {
                this.upgrades.put(upgrade, upgrades.get(upgrade));
            }
        }
    }

    public List<Integer> getCustomEnabledAttacksFor(UnitData unitData) {
        return customEnabledAttacks.getOrDefault(unitData, new ArrayList<>());
    }
    public void addCustomEnabledAttack(UnitData unitData, int attack) {
        if (customEnabledAttacks.containsKey(unitData)) {
            customEnabledAttacks.get(unitData).add(attack);
        }
        else {
            List<Integer> value = new ArrayList<>();
            value.add(attack);
            customEnabledAttacks.put(unitData, value);
        }
    }

    public List<Integer> getCustomDisabledAttacksFor(UnitData unitData) {
        return customDisabledAttacks.getOrDefault(unitData, new ArrayList<>());
    }
    public void addCustomDisabledAttack(UnitData unitData, int attack) {
        if (customDisabledAttacks.containsKey(unitData)) {
            customDisabledAttacks.get(unitData).add(attack);
        }
        else {
            List<Integer> value = new ArrayList<>();
            value.add(attack);
            customDisabledAttacks.put(unitData, value);
        }
    }

    public List<Integer> getCustomEnabledSkillsFor(UnitData unitData) {
        return customEnabledSkills.getOrDefault(unitData, new ArrayList<>());
    }
    public void addCustomEnabledSkill(UnitData unitData, int skill) {
        if (customEnabledSkills.containsKey(unitData)) {
            customEnabledSkills.get(unitData).add(skill);
        }
        else {
            List<Integer> value = new ArrayList<>();
            value.add(skill);
            customEnabledSkills.put(unitData, value);
        }
    }

    public boolean canUseSkill(SkillData skillData) {
        TribePath path = skillData.getRequiredPath();
        if (path != null) {
            return skillData.getRequiredPathLevel() <= getTribePathLevel(path);
        }
        return true;
    }
}
