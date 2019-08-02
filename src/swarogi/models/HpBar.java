package swarogi.models;

import swarogi.interfaces.Destructible;

// TODO: Dodać nasłuch wydarzeń, jeśli wytrzymałość ma ubywać płynnie
// Póki nie ma nasłuchu, to nie jest wykorzystywane
public class HpBar {
    private Destructible destructible;

    public HpBar(Destructible destructible) {
        this.destructible = destructible;
    }

    public int getWidth() { return this.destructible.getDestructibleData().getHpBarWidth(); }
    public float getHealth() { return this.destructible.getHealth(); }
    public int getMaxHealth() { return this.destructible.getMaxHealth(); }
    public int getTranslationY() { return this.destructible.getDestructibleData().getHpBarPositionY(); }
}