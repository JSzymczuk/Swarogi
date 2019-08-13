package swarogi.models;

import swarogi.common.Configuration;
import swarogi.data.Database;
import swarogi.datamodels.BuildingData;
import swarogi.datamodels.EffectData;
import swarogi.engine.Movement;
import swarogi.enums.Characteristic;
import swarogi.enums.ObjectState;
import swarogi.enums.PlacingType;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.game.TilesSelection;
import swarogi.interfaces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Building implements Placeable, Destructible, PlayerUnit {

    private BuildingData model;
    private float health;
    private Player owner;
    private Tile tile;
    private List<Tile> adjacentTiles;
    private int adjacentTrees;
    private List<Unit> builders;
    private int constructionTime;

    public Building(BuildingData model, Player owner) {
        this(model, owner, false);
    }

    public Building(BuildingData model, Player owner, boolean fullHp) {
        this.model = model;
        this.health = fullHp ? getMaxHealth() : (float)getMaxHealth() / (model.getConstructionTime() + 1);
        this.owner = owner;
        this.builders = new ArrayList<>();
        this.unitsInside = new ArrayList<>();
    }

    @Override
    public PlaceableData getPlaceableData() { return model; }
    public BuildingData getModel() { return model; }

    @Override
    public String getName() { return model.getName(); }

    @Override
    public Tile getTile() { return tile; }

    @Override
    public ObjectState getObjectState() {
        if (constructionTime == 0) { return ObjectState.NORMAL; }
        if (constructionTime == model.getConstructionTime()) { return ObjectState.CONSTRUCTION_EARLY; }
        return ObjectState.CONSTRUCTION_LATE;
    }

    @Override
    public void onPositionChanged(Tile tile) {
        this.tile = tile;
        adjacentTiles = Tile.getAdjacentTiles(getAllTiles());
        adjacentTrees = 0;
        for (Tile t : adjacentTiles) {
            if (t.getPlaceables().stream().anyMatch(p -> p.hasCharacteristic(Characteristic.Tree))) {
                ++adjacentTrees;
            }
        }
    }

    @Override
    public Player getOwner() { return this.owner; }
    public void setOwner(Player owner) { this.owner = owner; }

    public List<Tile> getAllTiles() { return TilesSelection.get(model.getPlacingTileGroup(), tile); }

    int getFoodProvided() {
        int sum = 0;
        if (model.isProvidingFood()) {
            for (Tile t : adjacentTiles) {
                // Zasoby tylko za puste pola (a przynajmniej nie trwale zajęte).
                if (t.isPlacingAllowed(PlacingType.LAND_OR_WATER)) {
                    sum += t.getTerrainType().getFertility();
                }
            }
        }
        return sum * getInnerWorkers();
    }

    int getWoodProvided() {
        // TODO: Jeśli założymy, że drzewa mogą być niszczone (albo się pojawiać), to trzeba zmienić
        return (model.isProvidingWood() ? adjacentTrees * Configuration.WOOD_PER_ADJACENT_TREE : 0) * getInnerWorkers();
    }

    private int getInnerWorkers() {
        return unitsInside.stream().filter(u -> u.hasCharacteristic(Characteristic.Worker)).collect(Collectors.toList()).size();
    }

    // TODO: Redundancja: receiveDamage i restoreHealth za każdym razem mają taką samą implementację.

    @Override
    public float receiveDamage(float amount) {
        if (amount > health) {
            float result = health;
            health = 0;
            return result;
        }
        health -= amount;
        return amount;
    }

    @Override
    public float restoreHealth(float amount) {
        float maxHealth = getMaxHealth();
        if (health + amount > maxHealth) {
            float result = maxHealth - health;
            health = maxHealth;
            return result;
        }
        health += amount;
        return amount;
    }

    @Override
    public float getHealth() { return health; }
    @Override
    public int getMaxHealth() {
        return model.getBaseMaxHealth();
    }
    @Override
    public int getDefense() { return model.getBaseDefense(); }


    @Override
    public DestructibleData getDestructibleData() {
        return this.model;
    }

    @Override
    public boolean hasCharacteristic(Characteristic characteristic) {
        return this.model.hasCharacteristic(characteristic);
    }

    public int getRemainingConstructionTime() { return this.constructionTime; }
    public void setRemainingConstructionTime(int time) { this.constructionTime = time; }
    public boolean isReady() { return this.constructionTime == 0; }
    public void updateConstructionTime() {
        if (this.constructionTime > 0 && this.builders.size() > 0) {
            --this.constructionTime;
            this.health += (float)getMaxHealth() / (model.getConstructionTime() + 1);
            if (this.constructionTime == 0) {
                List<Unit> buildersCopy = new ArrayList<>(builders);
                for (Unit builder : buildersCopy) {
                    builder.setConstructedBuilding(null);
                }
            }
        }
    }

    public void addBuilder(Unit unit) { this.builders.add(unit); }
    public void removeBuilder(Unit unit) { this.builders.add(unit); }

    public void addEffect(EffectData effectData) {
        // TODO: Do zaimplementowania
    }


    // TODO: Bycie magazynem powinno być niezależne od tego czy budynek, czy nie? (ewentualne statki)

    private List<Unit> unitsInside;

    public boolean canAddUnit(Unit unit) {
        return unit != null && this.isReady() && unit.containgBuilding == null && unitsInside.size() < model.getUnitsCapacity()
                && owner == unit.getOwner() && (model.isAcceptingAllUnits() || model.getAllowedUnits().contains(unit.getUnitData()));
    }

    public List<Unit> getUnitsInside() { return unitsInside; }

    public void addUnit(Unit unit) {
        if (canAddUnit(unit)) {
            unitsInside.add(unit);
            unit.getTile().removeBuildingObstacle(unit);
            unit.getTile().removeMovementObstacle(unit);
            unit.getTile().removeSelectable(unit);
            GameMap map = this.tile.getMap();
            map.removeDestructible(unit);
            map.removePlaceable(unit);
            unit.containgBuilding = this;
        }
    }

    public boolean removeUnit(Unit unit) {
        if (unitsInside.contains(unit) && Movement.tryPlaceUnitOnAssemblyPoint(this, unit)) {
            unitsInside.remove(unit);
            unit.containgBuilding = null;
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyed() {
        owner.removeBuilding(this);
        List<Tile> tiles = getAllTiles();
        GameMap map = getTile().getMap();
        int i, n = tiles.size();
        for (i = 0; i < n; ++i) {
            Unit unit = unitsInside.get(i);
            unit.addEffect(Database.Stunned);
            map.tryPlace(unit, tiles.get(i));
        }
        // TODO: Zabij pozostałe jednostki, dla których nie wystarczyło miejsca? Rozłóż je po sąsiadujących?

    }

    public void onAttacked() {}
}
