package swarogi.actions;

import swarogi.common.Configuration;
import swarogi.common.Rng;
import swarogi.data.Database;
import swarogi.datamodels.AttackData;
import swarogi.engine.Movement;
import swarogi.engine.Targeting;
import swarogi.enums.AttackType;
import swarogi.game.GameMap;
import swarogi.interfaces.Action;
import swarogi.interfaces.Destructible;
import swarogi.models.Unit;

import java.awt.*;

public class AttackAction implements Action {

    private Unit attacker;
    private Destructible target;
    private AttackData attackData;
    private GameMap gameMap;
    private boolean started;

    public AttackAction(Unit attacker, Destructible target, AttackData attackData, GameMap gameMap) {
        this.attacker = attacker;
        this.target = target;
        this.attackData = attackData;
        this.gameMap = gameMap;
    }

    @Override
    public boolean canBeExecuted() {
        // TODO: Wypadałoby sprawdzić zasięg ataku
        return attacker.isAlive() && target.getHealth() > 0 && Targeting.canTarget(attacker, target, attackData.getAllowedTargets())
            && attacker.hasActionPoints(Configuration.ATTACK_ACTION_POINTS_COST)
            && attacker.getOwner().hasCommandPoints(Configuration.ATTACK_COMMAND_POINTS_COST);
    }

    @Override
    public boolean hasStarted() { return started; }

    @Override
    public boolean isCompleted() { return started; }

    @Override
    public void start() {
        started = true;

        // TODO: Jakoś ładniej to obsługiwać?
        attacker.getOwner().decreaseCommandPoints(Configuration.ATTACK_COMMAND_POINTS_COST);
        attacker.useActionPoints(Configuration.ATTACK_ACTION_POINTS_COST);
        attacker.setConstructedBuilding(null);

        Point damageRange = attacker.getDamageForAttack(attackData);
        float damageDealt = target.receiveDamage(Math.max(Configuration.MIN_ATTACK_DAMAGE,
                Rng.getInt(damageRange.x, damageRange.y) - target.getDefense()));
        System.out.printf("%s zadaje %f obrażeń %s.\n",
                attacker.getUnitData().getName(), damageDealt, target.getName());
        target.onAttacked();
        if (target.getHealth() == 0) {
            Movement.destroy(target);
            gameMap.removeDestructible(target);
            target.onDestroyed();
            attacker.onTargetDestroyed(target);
        }
        else {
            if (attackData.getAttackType() == AttackType.Melee
                    && attacker.getOwner().hasUpgrade(Database.IncreasedMeleeBleeding)) {
                int prob = (int)attacker.getOwner().getUpgradeValue(Database.IncreasedMeleeBleeding);
                if (Rng.getInt(100) <= prob) {
                    target.addEffect(Database.Bleeding);
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
