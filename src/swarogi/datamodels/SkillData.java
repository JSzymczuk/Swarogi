package swarogi.datamodels;

import swarogi.enums.TargetType;
import swarogi.enums.TribePath;

import java.util.ArrayList;
import java.util.List;

public class SkillData {

    private String name;
    private String description;
    private int foodCost;
    private EffectData effectInduced;
    private EffectData userEffectInduced;
    private int effectProbability;
    private int minDamage;
    private int maxDamage;
    private List<TargetType> allowedTargets;
    private int minDistance;
    private int maxDistance;
    private boolean approachTargetFlag;
    private boolean positive;
    private boolean autoUse;
    private int cooldown;
    private int initialCooldown;
    private int commandPoints;
    private TribePath requiredPath;
    private int requiredPathLevel;
    //private String targetAreaTileGroup; // TODO: Jeszcze nie jest wspierane

    public SkillData() {
        this.allowedTargets = new ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getFoodCost() { return foodCost; }
    public void setFoodCost(int foodCost) { this.foodCost = foodCost; }

    public int getEffectProbability() { return effectProbability; }
    public void setEffectProbability(int effectProbability) { this.effectProbability = effectProbability; }

    public EffectData getTargetEffectInduced() { return effectInduced; }
    public void setTargetEffectInduced(EffectData effectInduced) { this.effectInduced = effectInduced; }

    public EffectData getUserEffectInduced() { return userEffectInduced; }
    public void setUserEffectInduced(EffectData userEffectInduced) { this.userEffectInduced = userEffectInduced; }

    public int getMinDamage() { return minDamage; }
    public void setMinDamage(int minDamage) { this.minDamage = minDamage; }

    public int getMaxDamage() { return maxDamage; }
    public void setMaxDamage(int maxDamage) { this.maxDamage = maxDamage; }

    public List<TargetType> getAllowedTargets() { return allowedTargets; }
    public void addTarget(TargetType targetType) { this.allowedTargets.add(targetType); }

    public int getMinDistance() { return minDistance; }
    public void setMinDistance(int minDistance) { this.minDistance = minDistance; }

    public int getMaxDistance() { return maxDistance; }
    public void setMaxDistance(int maxDistance) { this.maxDistance = maxDistance; }

    //public String getTargetAreaTileGroup() { return targetAreaTileGroup; }
    //public void setTargetAreaTileGroup(String targetAreaTileGroup) { this.targetAreaTileGroup = targetAreaTileGroup; }

    public int getCommandPoints() { return this.commandPoints; }
    public void setCommandPoints(int value) { this.commandPoints = value; }

    public int getCooldown() { return cooldown; }
    public void setCooldown(int cooldown) { this.cooldown = cooldown; }

    public boolean isAutoUse() { return autoUse; }
    public void setAutoUse(boolean autoUse) { this.autoUse = autoUse; }

    public boolean isPositive() { return positive; }
    public void setPositiveFlag(boolean positive) { this.positive = positive; }

    public void setRequirement(TribePath path, int level) {
        this.requiredPath = path;
        this.requiredPathLevel = level;
    }
    public TribePath getRequiredPath() { return this.requiredPath; }
    public int getRequiredPathLevel() { return this.requiredPathLevel; }

    public int getInitialCooldown() { return this.initialCooldown; }
    public void setInitialCooldown(int value) { this.initialCooldown = value; }

    public boolean isApproachingTarget() { return this.approachTargetFlag; }
    public void setTargetApproachingFlag(boolean value) { this.approachTargetFlag = value; }
}
