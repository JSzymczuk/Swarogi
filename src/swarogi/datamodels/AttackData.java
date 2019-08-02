package swarogi.datamodels;

import swarogi.enums.AttackType;
import swarogi.enums.DamageType;
import swarogi.enums.TargetType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttackData {
    private AttackType attackType;
    private DamageType damageType;
    private int minDistance;
    private int maxDistance;
    private int minDamage;
    private int maxDamage;
    private List<TargetType> allowedTargets;

    public AttackData(AttackType attackType, DamageType damageType, int minDamage, int maxDamage, int minDistance, int maxDistance, TargetType... targetTypes) {
        this.attackType = attackType;
        this.damageType = damageType;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.allowedTargets = new ArrayList<>();
        this.allowedTargets.addAll(Arrays.asList(targetTypes));
    }

    public AttackType getAttackType() { return this.attackType; }
    public DamageType getDamageType() { return this.damageType; }
    public int getMinDistance() { return this.minDistance; }
    public int getMaxDistance() { return this.maxDistance; }
    public int getMinDamage() { return this.minDamage; }
    public int getMaxDamage() { return this.maxDamage; }
    public List<TargetType> getAllowedTargets() { return allowedTargets; }
}
