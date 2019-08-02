package swarogi.engine;

import swarogi.enums.Characteristic;
import swarogi.enums.TargetType;
import swarogi.interfaces.Destructible;
import swarogi.interfaces.PlayerUnit;
import swarogi.models.Building;
import swarogi.models.Unit;

import java.util.List;

public final class Targeting {

    public static boolean canTarget(Unit origin, Destructible target, List<TargetType> targetTypes) {

        PlayerUnit targetUnit = null;
        if (target instanceof Unit) { targetUnit = (Unit)target; }
        else if(target instanceof Building) { targetUnit = (Building)target; }

        if (targetUnit != null) {
            // Sprawdź kryterium samonamierzalności
            if (targetTypes.contains(TargetType.Self) && origin != targetUnit) {
                return false;
            }
            if (targetTypes.contains(TargetType.NonSelf) && origin == targetUnit) {
                return false;
            }

            // Sprawdź kryterium przynależności
            if (targetTypes.contains(TargetType.Enemy) && origin.getOwner().getTeam() == targetUnit.getOwner().getTeam()) {
                return false;
            }
            if (targetTypes.contains(TargetType.Ally) && origin.getOwner().getTeam() != targetUnit.getOwner().getTeam()) {
                return false;
            }
            if (targetTypes.contains(TargetType.PlayerUnit) && origin.getOwner() != targetUnit.getOwner()) {
                return false;
            }
            boolean hasCh = targetUnit.hasCharacteristic(Characteristic.Destructible);
            // Sprawdź kryterium zniszczalności
            if (targetTypes.contains(TargetType.Destructible) && !hasCh) {
                return false;
            }
            if (targetTypes.contains(TargetType.Indestructible) && !targetUnit.hasCharacteristic(Characteristic.Indestructible)) {
                return false;
            }

            // Sprawdź kryterium ruchu
            if (targetTypes.contains(TargetType.Ground) && !targetUnit.hasCharacteristic(Characteristic.Ground)) {
                return false;
            }
            if (targetTypes.contains(TargetType.Flying) && !targetUnit.hasCharacteristic(Characteristic.Flying)) {
                return false;
            }
            if (targetTypes.contains(TargetType.Water) && !targetUnit.hasCharacteristic(Characteristic.Water)) {
                return false;
            }

            // Sprawdź kryterium grupy jednostek
            if (targetTypes.contains(TargetType.NonLeader) && targetUnit.hasCharacteristic(Characteristic.Leader)) {
                return false;
            }
            if (targetTypes.contains(TargetType.Leader) && targetUnit.hasCharacteristic(Characteristic.Leader)) {
                return true;
            }
            if (targetTypes.contains(TargetType.Worker) && !targetUnit.hasCharacteristic(Characteristic.Worker)) {
                return false;
            }

            // Sprawdź kryterium dopuszczenia rodzaju celu
            if (targetTypes.contains(TargetType.Living) && targetUnit.hasCharacteristic(Characteristic.Living)) {
                return true;
            }
            if (targetTypes.contains(TargetType.Building) && targetUnit.hasCharacteristic(Characteristic.Building)) {
                return true;
            }
            if (targetTypes.contains(TargetType.Machine) && targetUnit.hasCharacteristic(Characteristic.Machine)) {
                return true;
            }
        }

        return false;
    }
}
