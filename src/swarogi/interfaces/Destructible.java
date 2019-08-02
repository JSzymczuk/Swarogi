package swarogi.interfaces;

import swarogi.datamodels.EffectData;

public interface Destructible extends Placeable {
    float receiveDamage(float amount);
    float restoreHealth(float health);
    float getHealth();
    int getMaxHealth();
    void onAttacked();
    void onDestroyed();
    DestructibleData getDestructibleData();
    String getName();
    int getDefense();
    void addEffect(EffectData effectData);
}
