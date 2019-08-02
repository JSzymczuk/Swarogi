package swarogi.enums;

public enum AttackType {
    Melee("zwarcie"),
    Ranged("dystans");

    AttackType(String name) { this.name = name; }
    public String getName() { return this.name; }
    private String name;
}
