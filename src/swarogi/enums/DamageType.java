package swarogi.enums;

public enum DamageType {
    Normal("zwykły"),
    Missile("pocisk"),
    Ballistic("oblężniczy"),
    Magic("magia");

    DamageType(String name) { this.name = name; }
    public String getName() { return this.name; }
    private String name;
}
