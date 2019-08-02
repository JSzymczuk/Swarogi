package swarogi.enums;

public enum ArmorType {
    Light("lekki"),
    Heavy("ciężki"),
    Fortified("fortyfikacja");

    ArmorType(String name) { this.name = name; }
    public String getName() { return this.name; }
    private String name;
}
