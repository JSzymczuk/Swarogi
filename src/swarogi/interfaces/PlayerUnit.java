package swarogi.interfaces;

import swarogi.enums.Characteristic;
import swarogi.models.Player;

public interface PlayerUnit {
    Player getOwner();
    boolean hasCharacteristic(Characteristic characteristic);
}
