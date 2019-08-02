package swarogi.models;

import swarogi.common.Configuration;
import swarogi.data.Database;
import swarogi.datamodels.*;
import swarogi.engine.Movement;
import swarogi.enums.*;
import swarogi.interfaces.*;
import swarogi.game.Tile;

import javax.xml.crypto.Data;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Unit implements Placeable, Destructible, PlayerUnit {

    private UnitData model;
    private float health;
    private Player owner;
    private Tile tile;

    private Stack<Tile> path;
    private float customTranslationX;
    private float customTranslationY;
    private UnitDirection facingDirection;

    private Building constructedBuilding;
    Building containgBuilding;

    private HashMap<SkillData, Integer> skillsCooldown;
    private HashMap<EffectData, Integer> effects;

    public Unit(UnitData model, Player owner) {
        this.owner = owner;
        this.model = model;
        this.health = getMaxHealth();
        this.facingDirection = UnitDirection.LEFT;
        this.effects = new HashMap<>();
        this.skillsCooldown = new HashMap<>();
        for (SkillData skillData : model.getBaseSkills()) {
            skillsCooldown.put(skillData, skillData.getInitialCooldown());
        }
    }

    public UnitData getUnitData() { return model; }
    @Override
    public PlaceableData getPlaceableData() { return model; }
    @Override
    public DestructibleData getDestructibleData() {
        return model;
    }

    @Override
    public String getName() { return model.getName(); }

    @Override
    public Tile getTile() { return tile; }

    @Override
    public Player getOwner() { return this.owner; }
    public void setOwner(Player owner) { this.owner = owner; }

    public float getCustomTranslationX() { return customTranslationX; }
    //public void setCustomTranslationX(float value) { this.customTranslationX = value; }
    public float getCustomTranslationY() { return customTranslationY; }
    //public void setCustomTranslationY(float value) { this.customTranslationY = value; }

    public UnitDirection getFacingDirection() { return facingDirection; }
    //public void setFacingDirection(UnitDirection direction) { this.facingDirection = direction; }

    public void setPath(Stack<Tile> path) { this.path = path; }
    public Stack<Tile> getPath() { return (Stack<Tile>)this.path.clone(); }
    public void clearPath() { if (this.path != null) { path.clear(); } }

    public float getMovementSpeed() { return model.getBaseMovementSpeed(); }
    public int getSteps() {
        return applyValueUpgradeBonus(Database.IncreasedMovement, model.getBaseSteps());
    }

    public void updateSkillsCooldown() {
        for (SkillData skillData : skillsCooldown.keySet()) {
            int value = skillsCooldown.get(skillData);
            if (value > 0) {
                skillsCooldown.replace(skillData, value - 1);
            }
        }
    }

    public boolean isMoving() {
        return this.path != null && !this.path.isEmpty();
    }
    public boolean canMove() { return true; }
    public boolean isAlive() { return health > 0; }

    // TODO: Do przerobienia. Na razie naiwna implementacja.
    public void updateMovement() {
        if (isMoving()) {
            Tile nextGoal = this.path.peek();
            Point p1 = tile.getCenter();
            Point p2 = nextGoal.getCenter();

            float dx = p2.x - p1.x;
            float dy = p2.y - p1.y;

            float length = (float) Math.sqrt(dx * dx + dy * dy);

            if (length < 0.0001f) {
                path.pop();
                return;
            }

            if (dx < 0) {
                if (facingDirection == UnitDirection.RIGHT) { facingDirection = UnitDirection.LEFT; }
            }
            else if (dx > 0) {
                if (facingDirection == UnitDirection.LEFT) { facingDirection = UnitDirection.RIGHT; }
            }

            float speed = getMovementSpeed();
            float velx = dx / length * speed;
            float vely = dy / length * speed;

            // Współrzędne następnego kroku
            float nextX = customTranslationX + velx;
            float nextY = customTranslationY + vely;

            // Jednostka dotarła do następnego pola
            if (nextX * nextX + nextY * nextY >= length * length) {
                Movement.place(this, nextGoal);
                path.pop();
            }
            else {
                customTranslationX = nextX;
                customTranslationY = nextY;
            }
        }
    }

    @Override
    public float receiveDamage(float amount) {
        if (amount > health) {
            float result = health;
            health = 0;
            return result;
        }
        health -= amount;
        return amount;
    }

    @Override
    public float restoreHealth(float amount) {
        float maxHealth = getMaxHealth();
        if (health + amount > maxHealth) {
            float result = maxHealth - health;
            health = maxHealth;
            return result;
        }
        health += amount;
        return amount;
    }

    @Override
    public float getHealth() { return health; }
    @Override
    public int getMaxHealth() {
        return applyPercentalUpgradeBonus(Database.IncreasedUnitHealth,  model.getBaseMaxHealth());
    }
    @Override
    public int getDefense() {
        int def = applyValueUpgradeBonus(Database.IncreasedUnitArmor, model.getBaseDefense());
        if (effects.containsKey(Database.VolkhvSkillArmorBonus)) {
            def += Database.VolkhvSkillArmorBonus.getBaseValue();
        }
        return def;
    }

    public ArmorType getArmorType() { return model.getBaseArmorType(); }

    public Point getDamageForAttack(AttackData attack) {
        if (attack.getAttackType() == AttackType.Melee) {
            int minValue = applyPercentalUpgradeBonus(Database.IncreasedMeeleDamage, attack.getMinDamage());
            int maxValue = applyPercentalUpgradeBonus(Database.IncreasedMeeleDamage, attack.getMaxDamage());
            if (effects.containsKey(Database.HeroSkillDamageBonus)) {
                int bonus = Database.HeroSkillDamageBonus.getBaseValue();
                minValue += bonus;
                maxValue += bonus;
            }
            return new Point(minValue, maxValue);
        }
        else { // Ranged
            int minValue = applyPercentalUpgradeBonus(Database.IncreasedRangedDamage, attack.getMinDamage());
            int maxValue = applyPercentalUpgradeBonus(Database.IncreasedRangedDamage, attack.getMaxDamage());
            return new Point(minValue, maxValue);
        }
    }

    @Override
    public void onDestroyed() {
        owner.decreaseArmySize(this.model.getRequiredArmySize());
        owner.removeUnit(this);
        if (this.constructedBuilding != null) {
            this.constructedBuilding.removeBuilder(this);
        }
    }

    public void onTargetDestroyed(Destructible destructible) { }

    @Override
    public void onPositionChanged(Tile tile) {
        this.tile = tile;
        this.customTranslationX = 0;
        this.customTranslationY = 0;
    }

    // TODO: Na pewno można to jakoś poprawić
    public List<AttackData> getAttacks() {
        List<Integer> enabledAttacks = model.getDefaultAttacks();
        for (Integer i : owner.getCustomEnabledAttacksFor(model)) {
            enabledAttacks.add(i);
        }
        for (Integer i : owner.getCustomDisabledAttacksFor(model)) {
            enabledAttacks.remove(i);
        }
        List<AttackData> attacksData = model.getBaseAttacks();
        List<AttackData> result = new ArrayList<>();
        for (int i : enabledAttacks) {
            result.add(attacksData.get(i));
        }
        return result;
    }

    public boolean isInsideBuilding() { return containgBuilding != null; }

    @Override
    public boolean hasCharacteristic(Characteristic characteristic) {
        return model.hasCharacteristic(characteristic);
    }

    private int remainingActionPoints;

    public boolean hasActionPoints(int points) { return remainingActionPoints >= points; }
    public void replenishActionPoints() {
        this.remainingActionPoints = model.getMaxActions();
    }
    public boolean useActionPoints(int points) {
        if (this.remainingActionPoints >= points) {
            this.remainingActionPoints -= points;
            return true;
        }
        return false;
    }

    public void setConstructedBuilding(Building building) {
        if (this.constructedBuilding != null) {
            this.constructedBuilding.removeBuilder(this);
        }
        this.constructedBuilding = building;
        if (building != null) {
            building.addBuilder(this);
        }
    }

    public boolean isConstructingBuilding() { return this.constructedBuilding != null; }

    private int applyPercentalUpgradeBonus(UpgradeData upgradeData, int value) {
        if (getUnitData().hasUpgrade(upgradeData) && owner.hasUpgrade(upgradeData)) {
            int bonus = (int)owner.getUpgradeValue(upgradeData);
            value += bonus * value / 100;
        }
        return value;
    }

    private int applyValueUpgradeBonus(UpgradeData upgradeData, int value) {
        if (getUnitData().hasUpgrade(upgradeData) && owner.hasUpgrade(upgradeData)) {
            int bonus = (int)owner.getUpgradeValue(upgradeData);
            value += bonus;
        }
        return value;
    }

    public int getSkillCooldown(SkillData skillData) {
        if (skillsCooldown.containsKey(skillData)) {
            return skillsCooldown.get(skillData);
        }
        return -1;
    }
    public void setSkillCooldown(SkillData skillData, int cooldown) {
        if (this.skillsCooldown.containsKey(skillData)) {
            skillsCooldown.replace(skillData, cooldown);
        }
    }
    public boolean isSkillReady(SkillData skillData) {
        return getSkillCooldown(skillData) == 0;
    }

    public void addEffect(EffectData effectData) {
        this.effects.put(effectData, effectData.getDuration());
    }

    public boolean hasEffect(EffectData effectData) { return this.effects.containsKey(effectData); }

    public Set<EffectData> getEffects() { return this.effects.keySet(); }

    private boolean wasAttacked;

    public void onAttacked() { this.wasAttacked = true; }

    public void updateEffects() {

        //Regeneracja
        // Jednoskta nie wykonała żadnej akcji ani nie została zaatakowana, a gracz ma rozwinięcie z regeneracją
        if (!wasAttacked && remainingActionPoints == model.getMaxActions()
                && !isInsideBuilding() && owner.hasUpgrade(Database.IncreasedRegeneration)) {
            if (health < getMaxHealth()) {
                int amount = (int)owner.getUpgradeValue(Database.IncreasedRegeneration) * Configuration.BASE_REGENERATION / 100;
                if (effects.containsKey(Database.Regeneration)) {
                    amount += Database.Regeneration.getBaseValue();
                }
                restoreHealth(amount);
            }
        }
        else {
            if (health < getMaxHealth() && effects.containsKey(Database.Regeneration)) {
                restoreHealth(Database.Regeneration.getBaseValue());
            }
        }

        // Krwawienie
        if (effects.containsKey(Database.Bleeding)) {
            // TODO: Znowu compy-paste
            this.receiveDamage(Database.Bleeding.getBaseValue());
            if (getHealth() == 0) {
                Movement.destroy(this);
                tile.getMap().removeDestructible(this); // TODO: Nasłuch wydarzeń?
                this.onDestroyed();
            }
        }

        Set<EffectData> currentEffects = new HashSet<>(effects.keySet());
        for (EffectData effect : currentEffects) {
            int turnsLeft = this.effects.get(effect) - 1;
            if (turnsLeft == 0) {
                effects.remove(effect);
            }
            else {
                effects.replace(effect, turnsLeft);
            }
        }

        wasAttacked = false;
    }
}
