package swarogi.interfaces;

import swarogi.enums.Characteristic;
import swarogi.enums.PlacingType;
import swarogi.enums.TerrainType;

import java.util.List;

public interface PlaceableData {
    String getName();

    String getTextureName();
    int getXTexturePosition();
    int getYTexturePosition();
    float getXScale();
    float getYScale();

    // Pola, do których obiekt będzie przypisany.
    String getPlacingTileGroup();
    // Pola, na których obiekt uniemożliwi ruch (np. przez kępę roślin można przejść, ale znajduje się na polu).
    String getMovementTileGroup();
    // Pola, na których obiekt uniemożliwi budowanie.
    String getBuildingTileGroup();

    // Czy obiekt może zostać zaznaczony przez użytkownika.
    boolean isSelectable();
    // Czy użytkownik może zmienić położenie obiektu.
    boolean isMovable();
    // Obiekt może być umieszczany na polach, na których znajdują się inne obiekty.
    boolean isIgnoringPlacingRules();
    // Podczas umieszczania obiektu nie trzeba sprawdzać reguł budowy.
    boolean isIgnoringBuildingRules();

    PlacingType getPlacingType();
    TerrainType getInducedTerrainType();

    List<Characteristic> getCharacteristics();
}

