package swarogi.actions;

import swarogi.common.Configuration;
import swarogi.common.Rng;
import swarogi.data.Database;
import swarogi.datamodels.EffectData;
import swarogi.datamodels.SkillData;
import swarogi.engine.Movement;
import swarogi.engine.Targeting;
import swarogi.enums.TargetType;
import swarogi.game.GameMap;
import swarogi.interfaces.Action;
import swarogi.interfaces.Destructible;
import swarogi.models.Building;
import swarogi.models.Player;
import swarogi.models.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SkillAction implements Action {

    private Unit user;
    private Destructible target;
    private SkillData skillData;
    private GameMap gameMap;
    private boolean started;

    public SkillAction(Unit user, Destructible target, SkillData skillData, GameMap gameMap) {
        this.user = user;
        this.target = target;
        this.skillData = skillData;
        this.gameMap = gameMap;
    }

    @Override
    public boolean canBeExecuted() {
        // TODO: Wypadałoby sprawdzić zasięg rzucenia
        Player player = user.getOwner();
        if (player.hasCommandPoints(skillData.getCommandPoints())
                && player.canUseSkill(skillData)
                && player.hasFood(skillData.getFoodCost())
                && user.hasActionPoints(Configuration.SKILL_ACTION_POINTS_COST)
                && user.isSkillReady(skillData)) {

            if (!skillData.isAutoUse()) {
                return target != null && Targeting.canTarget(user, target, skillData.getAllowedTargets());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean hasStarted() { return started; }

    @Override
    public boolean isCompleted() { return started; }

    @Override
    public void start() {
        started = true;
        Player player = user.getOwner();

        player.decreaseCommandPoints(skillData.getCommandPoints());
        player.decreaseFood(skillData.getFoodCost());
        user.useActionPoints(Configuration.SKILL_ACTION_POINTS_COST);
        user.setConstructedBuilding(null);
        user.setSkillCooldown(skillData, skillData.getCooldown());

        List<Destructible> targets = new ArrayList<>();
        if (skillData.isAutoUse()) {
            int minDistance = skillData.getMinDistance();
            List<TargetType> targetingInfo = skillData.getAllowedTargets();
            HashMap<Destructible, Integer> destructiblesInRange = gameMap.getMinDistancesToDestructiblesInRange(user.getTile(), skillData.getMaxDistance());
            for (Destructible destructible : destructiblesInRange.keySet()) {
                if (destructiblesInRange.get(destructible) >= minDistance && Targeting.canTarget(user, destructible, targetingInfo)) {
                    targets.add(destructible);
                }
            }
        }
        else {
            targets.add(target);
        }

        // TODO: Dla każdego celu generowane są nowe obrażenia. Tak powinno być?

        for (Destructible destructible : targets) {
            // Sprawdź czy zdolność zadaje obrażenia lub leczy
            int damage = Rng.getInt(skillData.getMinDamage(), skillData.getMaxDamage());
            if (damage > 0) {
                float damageDealt = destructible.receiveDamage(Math.max(Configuration.MIN_ATTACK_DAMAGE, damage - destructible.getDefense()));
                destructible.onAttacked();
                System.out.printf("%s otrzymuje %f obrażeń.\n", destructible.getName(), damageDealt);
                if (destructible.getHealth() == 0) {
                    Movement.destroy(destructible);
                    gameMap.removeDestructible(destructible);
                    destructible.onDestroyed();
                    user.onTargetDestroyed(destructible);
                    return;
                }
            }
            else if (damage < 0) {
                float healthRestored = destructible.restoreHealth(-damage);
                System.out.printf("%s odzyskuje %f punktów wytrzymałości.\n", destructible.getName(), healthRestored);
            }
        }

        EffectData effectData = skillData.getTargetEffectInduced();
        if (effectData != null) {
            if (Rng.getInt(100) <= skillData.getEffectProbability()) {

                if (effectData.isInstant()) {
                    for (Destructible destructible : targets) {
                        if (effectData == Database.RestoreActionPoints && destructible instanceof Unit) {
                            Unit targetUnit = (Unit) destructible;
                            if (!targetUnit.hasEffect(Database.Stunned)) {
                                targetUnit.replenishActionPoints();
                            }
                        }
                        else if (effectData == Database.EnterBuildingEffect && !user.isInsideBuilding()
                                && destructible instanceof Building) {
                            Building targetBuilding = (Building) destructible;
                            targetBuilding.addUnit(user);
                        }
                    }
                }
                else {
                    for (Destructible destructible : targets) {
                        destructible.addEffect(effectData);
                    }
                }
            }
        }
    }

    @Override
    public void update() { }

    @Override
    public void finish() { }

    @Override
    public void abort() { }
}
