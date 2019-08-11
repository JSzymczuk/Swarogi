package swarogi.data;

import swarogi.datamodels.*;
import swarogi.enums.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Database {

     public static final ObstacleData OakTree;
     public static final ObstacleData WillowTree;
     public static final ObstacleData PineTree;
     public static final ObstacleData LimeTree;
     public static final ObstacleData Rock;
     public static final ObstacleData Bridge;

     public static final DecorationData Daisy;
     public static final DecorationData Cabbage;
     public static final DecorationData Thickets;
     public static final DecorationData Grain;

     public static final UnitData Worker;
     public static final UnitData Warrior;
     public static final UnitData Volkhv;
     public static final UnitData Bowman;
     public static final UnitData Rider;
     public static final UnitData Hero;

     public static final BuildingData Gord;
     public static final BuildingData Tower;
     public static final BuildingData Barracks;
     public static final BuildingData Farm;
     public static final BuildingData Chram;

    public static final UpgradeData IncreasedMeeleDamage;
    public static final UpgradeData IncreasedUnitArmor;
    public static final UpgradeData IncreasedMeleeBleeding;
    public static final UpgradeData IncreasedBleedingDamage; // nie jest uwzględnione
    public static final UpgradeData IncreasedMovement;
    public static final UpgradeData IncreasedRangedDamage;
    public static final UpgradeData IncreasedActionPoints;
    public static final UpgradeData IncreasedResources;
    public static final UpgradeData IncreasedUnitHealth;
    public static final UpgradeData IncreasedRegeneration;

    public static final Map<TribePath, ReseachData> TribePaths;

    public static final ValueEffectData Bleeding;
    public static final EffectData Stunned;
    public static final ValueEffectData Regeneration;
    public static final ValueEffectData HeroSkillDamageBonus;
    public static final ValueEffectData VolkhvSkillArmorBonus;
    public static final EffectData RestoreActionPoints;
    public static final EffectData EnterBuildingEffect;

    public static final Map<String, ObstacleData> Obstacles;
    public static final Map<String, DecorationData> Decorations;

    static {

        ValueEffectData bleeding = new ValueEffectData();
        bleeding.setName("Krwawienie");
        bleeding.setDescription("TODO");
        bleeding.setDuration(4);
        bleeding.setBaseValue(4);
        Bleeding = bleeding;

        EffectData stunned = new EffectData();
        stunned.setName("Ogłuszenie");
        stunned.setDescription("TODO");
        stunned.setDuration(1);
        stunned.setInstant(false);
        Stunned = stunned;

        EffectData restoreActionPoints = new EffectData();
        restoreActionPoints.setName("Przywróć punkty akcji");
        restoreActionPoints.setDescription("TODO");
        restoreActionPoints.setDuration(0);
        restoreActionPoints.setInstant(true);
        RestoreActionPoints = restoreActionPoints;

        ValueEffectData regeneration = new ValueEffectData();
        regeneration.setName("Regeneracja");
        regeneration.setDescription("TODO");
        regeneration.setDuration(3);
        regeneration.setBaseValue(5);
        Regeneration = regeneration;

        ValueEffectData damageBonus = new ValueEffectData();
        damageBonus.setName("Zwiększone obrażenia");
        damageBonus.setDescription("TODO");
        damageBonus.setDuration(2);
        damageBonus.setBaseValue(2);
        HeroSkillDamageBonus = damageBonus;

        ValueEffectData armorBonus = new ValueEffectData();
        armorBonus.setName("Zwiększony pancerz");
        armorBonus.setDescription("TODO");
        armorBonus.setDuration(3);
        armorBonus.setBaseValue(3);
        VolkhvSkillArmorBonus = armorBonus;

        EffectData enterBuildingEffect = new EffectData();
        enterBuildingEffect.setName("Wejdź do budynku (efekt)");
        enterBuildingEffect.setDescription("TODO");
        enterBuildingEffect.setDuration(0);
        enterBuildingEffect.setInstant(true);
        EnterBuildingEffect = enterBuildingEffect;



        SkillData enterBuilding = new SkillData();
        enterBuilding.setName("Wejdź do budynku");
        enterBuilding.setDescription("TODO");
        enterBuilding.setCommandPoints(1);
        enterBuilding.setFoodCost(0);
        enterBuilding.setCooldown(0);
        enterBuilding.setTargetEffectInduced(EnterBuildingEffect);
        enterBuilding.setEffectProbability(100);
        enterBuilding.addTarget(TargetType.Ally);
        enterBuilding.addTarget(TargetType.Building);
        enterBuilding.addTarget(TargetType.Ground);
        enterBuilding.setTargetApproachingFlag(true);
        enterBuilding.setPositiveFlag(true);

        SkillData heroDamageBonus = new SkillData();
        heroDamageBonus.setName("Krzyk bojowy");
        heroDamageBonus.setDescription("TODO");
        heroDamageBonus.setCommandPoints(1);
        heroDamageBonus.setFoodCost(100);
        heroDamageBonus.setCooldown(4);
        heroDamageBonus.setEffectProbability(100);
        heroDamageBonus.setTargetEffectInduced(HeroSkillDamageBonus);
        heroDamageBonus.setAutoUse(true);
        heroDamageBonus.setMinDistance(1);
        heroDamageBonus.setMaxDistance(5);
        heroDamageBonus.addTarget(TargetType.Ally);
        heroDamageBonus.addTarget(TargetType.Living);
        heroDamageBonus.addTarget(TargetType.Ground);
        heroDamageBonus.setRequirement(TribePath.Bear, 3);

        SkillData heroReplenishActionPoints = new SkillData();
        heroReplenishActionPoints.setName("Rozkaz dowódcy");
        heroReplenishActionPoints.setDescription("TODO");
        heroReplenishActionPoints.setCommandPoints(1);
        heroReplenishActionPoints.setFoodCost(50);
        heroReplenishActionPoints.setCooldown(2);
        heroReplenishActionPoints.setEffectProbability(100);
        heroReplenishActionPoints.setTargetEffectInduced(RestoreActionPoints);
        heroReplenishActionPoints.setAutoUse(false);
        heroReplenishActionPoints.setMinDistance(1);
        heroReplenishActionPoints.setMaxDistance(5);
        heroReplenishActionPoints.addTarget(TargetType.PlayerUnit);
        heroReplenishActionPoints.addTarget(TargetType.Living);
        heroReplenishActionPoints.addTarget(TargetType.Machine);
        heroReplenishActionPoints.setRequirement(TribePath.Owl, 3);

        SkillData volkhvArmorBonus = new SkillData();
        volkhvArmorBonus.setName("Pancerz Swaroga");
        volkhvArmorBonus.setDescription("TODO");
        volkhvArmorBonus.setCommandPoints(1);
        volkhvArmorBonus.setFoodCost(40);
        volkhvArmorBonus.setEffectProbability(100);
        volkhvArmorBonus.setTargetEffectInduced(VolkhvSkillArmorBonus);
        volkhvArmorBonus.setCooldown(1);
        volkhvArmorBonus.setMinDistance(1);
        volkhvArmorBonus.setMaxDistance(6);
        volkhvArmorBonus.setPositiveFlag(true);
        volkhvArmorBonus.addTarget(TargetType.Ally);
        volkhvArmorBonus.addTarget(TargetType.Living);
        volkhvArmorBonus.setRequirement(TribePath.Deer, 3);

        SkillData thunderStrike = new SkillData();
        thunderStrike.setName("Gniew Peruna");
        thunderStrike.setDescription("TODO");
        thunderStrike.setCommandPoints(1);
        thunderStrike.setFoodCost(40);
        thunderStrike.setEffectProbability(50);
        thunderStrike.setTargetEffectInduced(Stunned);
        thunderStrike.setMinDamage(25);
        thunderStrike.setMaxDamage(32);
        thunderStrike.setCooldown(1);
        thunderStrike.setMinDistance(2);
        thunderStrike.setMaxDistance(10);
        thunderStrike.setPositiveFlag(false);
        thunderStrike.addTarget(TargetType.Enemy);
        thunderStrike.addTarget(TargetType.Destructible);
        thunderStrike.addTarget(TargetType.Living);
        thunderStrike.setRequirement(TribePath.Wolf, 3);

        SkillData volkhvRegeneration = new SkillData();
        volkhvRegeneration.setName("Łaska Mokoszy");
        volkhvRegeneration.setDescription("TODO");
        volkhvRegeneration.setCommandPoints(1);
        volkhvRegeneration.setFoodCost(30);
        volkhvRegeneration.setEffectProbability(100);
        volkhvRegeneration.setTargetEffectInduced(Regeneration);
        volkhvRegeneration.setCooldown(1);
        volkhvRegeneration.setMinDistance(1);
        volkhvRegeneration.setMaxDistance(6);
        volkhvRegeneration.setPositiveFlag(true);
        volkhvRegeneration.addTarget(TargetType.Ally);
        volkhvRegeneration.addTarget(TargetType.Destructible);
        volkhvRegeneration.addTarget(TargetType.Living);



        IncreasedMeeleDamage = new UpgradeData(); // Premia procentowa
        IncreasedMeeleDamage.setDescription("Zwiększa obrażenia zadawane przez Wojowników i Jeźdźców o {value}%.");
        IncreasedMeeleDamage.addValue(10);
        IncreasedMeeleDamage.addValue(20);
        IncreasedMeeleDamage.addValue(30);
        IncreasedMeeleDamage.addValue(40);
        IncreasedMeeleDamage.addValue(50);

        IncreasedMeleeBleeding = new UpgradeData(); // Dodatkowe punkty procentowe
        IncreasedMeleeBleeding.setDescription("Zapewnia szansę {value}%, że ataki zadawane przez Wojowników i Jeźdźców wywołają efekt Krwawienia.");
        IncreasedMeleeBleeding.addValue(10);
        IncreasedMeleeBleeding.addValue(20);
        IncreasedMeleeBleeding.addValue(30);
        IncreasedMeleeBleeding.addValue(40);
        IncreasedMeleeBleeding.addValue(50);

        IncreasedUnitHealth = new UpgradeData(); // Premia procentowa
        IncreasedUnitHealth.setDescription("Zwiększa maksymalne punkty wytrzymałości jednostek o {value}%.");
        IncreasedUnitHealth.addValue(10);
        IncreasedUnitHealth.addValue(20);
        IncreasedUnitHealth.addValue(30);
        IncreasedUnitHealth.addValue(40);
        IncreasedUnitHealth.addValue(50);

        IncreasedActionPoints = new UpgradeData(); // Wartość
        IncreasedActionPoints.setDescription("Zwiększa maksymalną liczbę punktów akcji o {value}.");
        IncreasedActionPoints.addValue(1);
        IncreasedActionPoints.addValue(2);
        IncreasedActionPoints.addValue(3);
        IncreasedActionPoints.addValue(4);
        IncreasedActionPoints.addValue(5);

        IncreasedMovement = new UpgradeData(); // Wartość
        IncreasedMovement.setDescription("Zwiększa maksymalny zasięg ruchu o: {value} pól.");
        IncreasedMovement.addValue(1);
        IncreasedMovement.addValue(2);
        IncreasedMovement.addValue(3);
        IncreasedMovement.addValue(4);
        IncreasedMovement.addValue(5);

        IncreasedUnitArmor = new UpgradeData(); // Wartość
        IncreasedUnitArmor.setDescription("Zwiększa pancerz Wojowników, Strzelców oraz Jeźdźców o {value}.");
        IncreasedUnitArmor.addValue(1);
        IncreasedUnitArmor.addValue(2);
        IncreasedUnitArmor.addValue(3);

        IncreasedBleedingDamage = new UpgradeData();  // Premia procentowa
        IncreasedBleedingDamage.setDescription("Efekt krwawienia będzie powodował utratę większej liczby punktów wytrzymałości.");
        IncreasedBleedingDamage.addValue(150);
        IncreasedBleedingDamage.addValue(200);
        IncreasedBleedingDamage.addValue(300);

        IncreasedRangedDamage = new UpgradeData(); // Premia procentowa
        IncreasedRangedDamage.setDescription("Zwiększa obrażenia zadawane przez pociski Strzelców o {value}%.");
        IncreasedRangedDamage.addValue(10);
        IncreasedRangedDamage.addValue(20);
        IncreasedRangedDamage.addValue(30);

        IncreasedResources = new UpgradeData(); // Premia procentowa
        IncreasedResources.setDescription("Zwiększa ilość uzyskiwanych surowców o{value}%.");
        IncreasedResources.addValue(10);
        IncreasedResources.addValue(20);
        IncreasedResources.addValue(30);

        IncreasedRegeneration = new UpgradeData();  // Premia procentowa
        IncreasedRegeneration.setDescription("Jednostki, które nie wykonały ruchu w poprzedniej turze będą odzyskiwać część wytrzymałości.");
        IncreasedRegeneration.addValue(100);
        IncreasedRegeneration.addValue(150);
        IncreasedRegeneration.addValue(200);



        ReseachData bearTribePath = new ReseachData();
        bearTribePath.setName("Droga Niedźwiedzia");
        bearTribePath.addUpgrade(0, IncreasedMeeleDamage, 0);
        bearTribePath.addUpgrade(1, IncreasedMeeleDamage, 1);
        bearTribePath.addUpgrade(1, IncreasedUnitArmor, 0);
        bearTribePath.addUpgrade(2, IncreasedMeeleDamage, 2);
        bearTribePath.addUpgrade(3, IncreasedMeeleDamage, 3);
        bearTribePath.addUpgrade(3, IncreasedUnitArmor, 1);
        bearTribePath.addUpgrade(4, IncreasedMeeleDamage, 4);
        bearTribePath.addUpgrade(4, IncreasedUnitArmor, 2);
        bearTribePath.setResearchTime(0, 1);
        bearTribePath.setResearchTime(1, 1);
        bearTribePath.setResearchTime(2, 2);
        bearTribePath.setResearchTime(3, 2);
        bearTribePath.setResearchTime(4, 3);
        bearTribePath.setTribeLevelBonus(0, 1);
        bearTribePath.setTribeLevelBonus(1, 1);
        bearTribePath.setTribeLevelBonus(2, 2);
        bearTribePath.setTribeLevelBonus(3, 2);
        bearTribePath.setTribeLevelBonus(4, 3);

        ReseachData wolfTribePath = new ReseachData();
        wolfTribePath.setName("Droga Wilka");
        wolfTribePath.addUpgrade(0, IncreasedMeleeBleeding, 0);
        wolfTribePath.addUpgrade(1, IncreasedMeleeBleeding, 1);
        wolfTribePath.addUpgrade(1, IncreasedBleedingDamage, 0);
        wolfTribePath.addUpgrade(2, IncreasedMeleeBleeding, 2);
        wolfTribePath.addUpgrade(3, IncreasedMeleeBleeding, 3);
        wolfTribePath.addUpgrade(3, IncreasedBleedingDamage, 1);
        wolfTribePath.addUpgrade(4, IncreasedMeleeBleeding, 4);
        wolfTribePath.addUpgrade(4, IncreasedBleedingDamage, 2);
        wolfTribePath.setResearchTime(0, 1);
        wolfTribePath.setResearchTime(1, 1);
        wolfTribePath.setResearchTime(2, 2);
        wolfTribePath.setResearchTime(3, 2);
        wolfTribePath.setResearchTime(4, 3);
        wolfTribePath.setTribeLevelBonus(0, 1);
        wolfTribePath.setTribeLevelBonus(1, 1);
        wolfTribePath.setTribeLevelBonus(2, 2);
        wolfTribePath.setTribeLevelBonus(3, 2);
        wolfTribePath.setTribeLevelBonus(4, 3);

        ReseachData deerTribePath = new ReseachData();
        deerTribePath.setName("Droga Jelenia");
        deerTribePath.addUpgrade(0, IncreasedUnitHealth, 0);
        deerTribePath.addUpgrade(1, IncreasedUnitHealth, 1);
        deerTribePath.addUpgrade(1, IncreasedRegeneration, 0);
        deerTribePath.addUpgrade(2, IncreasedUnitHealth, 2);
        deerTribePath.addUpgrade(3, IncreasedUnitHealth, 3);
        deerTribePath.addUpgrade(3, IncreasedRegeneration, 1);
        deerTribePath.addUpgrade(4, IncreasedUnitHealth, 4);
        deerTribePath.addUpgrade(4, IncreasedRegeneration, 2);
        deerTribePath.setResearchTime(0, 1);
        deerTribePath.setResearchTime(1, 1);
        deerTribePath.setResearchTime(2, 2);
        deerTribePath.setResearchTime(3, 2);
        deerTribePath.setResearchTime(4, 3);
        deerTribePath.setTribeLevelBonus(0, 1);
        deerTribePath.setTribeLevelBonus(1, 1);
        deerTribePath.setTribeLevelBonus(2, 2);
        deerTribePath.setTribeLevelBonus(3, 2);
        deerTribePath.setTribeLevelBonus(4, 3);

        ReseachData owlTribePath = new ReseachData();
        owlTribePath.setName("Droga Sowy");
        owlTribePath.addUpgrade(0, IncreasedActionPoints, 0);
        owlTribePath.addUpgrade(1, IncreasedActionPoints, 1);
        owlTribePath.addUpgrade(1, IncreasedResources, 0);
        owlTribePath.addUpgrade(2, IncreasedActionPoints, 2);
        owlTribePath.addUpgrade(3, IncreasedActionPoints, 3);
        owlTribePath.addUpgrade(3, IncreasedResources, 1);
        owlTribePath.addUpgrade(4, IncreasedActionPoints, 4);
        owlTribePath.addUpgrade(4, IncreasedResources, 2);
        owlTribePath.setResearchTime(0, 1);
        owlTribePath.setResearchTime(1, 1);
        owlTribePath.setResearchTime(2, 2);
        owlTribePath.setResearchTime(3, 2);
        owlTribePath.setResearchTime(4, 3);
        owlTribePath.setTribeLevelBonus(0, 1);
        owlTribePath.setTribeLevelBonus(1, 1);
        owlTribePath.setTribeLevelBonus(2, 2);
        owlTribePath.setTribeLevelBonus(3, 2);
        owlTribePath.setTribeLevelBonus(4, 3);

        ReseachData foxTribePath = new ReseachData();
        foxTribePath.setName("Droga Lisa");
        foxTribePath.addUpgrade( 0, IncreasedMovement, 0);
        foxTribePath.addUpgrade( 1, IncreasedMovement, 1);
        foxTribePath.addUpgrade( 1, IncreasedRangedDamage, 0);
        foxTribePath.addUpgrade( 2, IncreasedMovement, 2);
        foxTribePath.addUpgrade( 3, IncreasedMovement, 3);
        foxTribePath.addUpgrade( 3, IncreasedRangedDamage, 1);
        foxTribePath.addUpgrade( 4, IncreasedMovement, 4);
        foxTribePath.addUpgrade( 4, IncreasedRangedDamage, 2);
        foxTribePath.setResearchTime(0, 1);
        foxTribePath.setResearchTime(1, 1);
        foxTribePath.setResearchTime(2, 2);
        foxTribePath.setResearchTime(3, 2);
        foxTribePath.setResearchTime(4, 3);
        foxTribePath.setTribeLevelBonus(0, 1);
        foxTribePath.setTribeLevelBonus(1, 1);
        foxTribePath.setTribeLevelBonus(2, 2);
        foxTribePath.setTribeLevelBonus(3, 2);
        foxTribePath.setTribeLevelBonus(4, 3);

        TribePaths = new HashMap<>();
        TribePaths.put(TribePath.Bear, bearTribePath);
        TribePaths.put(TribePath.Wolf, wolfTribePath);
        TribePaths.put(TribePath.Deer, deerTribePath);
        TribePaths.put(TribePath.Owl, owlTribePath);
        TribePaths.put(TribePath.Fox, foxTribePath);



        ObstacleData oakTree = new ObstacleData();
        oakTree.setName("Dąb");
        oakTree.setTextureName("OakTree");
        oakTree.setXTexturePosition(0);
        oakTree.setYTexturePosition(-70);
        oakTree.setXScale(0.5f);
        oakTree.setYScale(0.5f);
        oakTree.setPlacingTileGroup("SINGLE_TILE");
        oakTree.setMovementTileGroup("SINGLE_TILE");
        oakTree.setBuildingTileGroup("SINGLE_TILE");
        oakTree.addCharacteristic(Characteristic.Tree);
        OakTree = oakTree;

        ObstacleData willowTree = new ObstacleData();
        willowTree.setName("Wierzba");
        willowTree.setTextureName("WillowTree");
        willowTree.setXTexturePosition(-5);
        willowTree.setYTexturePosition(-65);
        willowTree.setXScale(0.5f);
        willowTree.setYScale(0.5f);
        willowTree.setPlacingTileGroup("SINGLE_TILE");
        willowTree.setMovementTileGroup("SINGLE_TILE");
        willowTree.setBuildingTileGroup("SINGLE_TILE");
        willowTree.addCharacteristic(Characteristic.Tree);
        WillowTree = willowTree;

        ObstacleData pineTree = new ObstacleData();
        pineTree.setName("Młoda sosna");
        pineTree.setTextureName("PineTree");
        pineTree.setXTexturePosition(-2);
        pineTree.setYTexturePosition(-45);
        pineTree.setXScale(0.5f);
        pineTree.setYScale(0.5f);
        pineTree.setPlacingTileGroup("SINGLE_TILE");
        pineTree.setMovementTileGroup("SINGLE_TILE");
        pineTree.setBuildingTileGroup("SINGLE_TILE");
        pineTree.addCharacteristic(Characteristic.Tree);
        PineTree = pineTree;

        ObstacleData limeTree = new ObstacleData();
        limeTree.setName("Lipa");
        limeTree.setTextureName("LimeTree");
        limeTree.setXTexturePosition(-5);
        limeTree.setYTexturePosition(-75);
        limeTree.setXScale(0.5f);
        limeTree.setYScale(0.5f);
        limeTree.setPlacingTileGroup("SINGLE_TILE");
        limeTree.setMovementTileGroup("SINGLE_TILE");
        limeTree.setBuildingTileGroup("SINGLE_TILE");
        limeTree.addCharacteristic(Characteristic.Tree);
        LimeTree = limeTree;

        ObstacleData rock = new ObstacleData();
        rock.setName("Głaz");
        rock.setTextureName("Rock");
        rock.setXTexturePosition(0);
        rock.setYTexturePosition(-8);
        rock.setXScale(1.0f);
        rock.setYScale(1.0f);
        rock.setPlacingTileGroup("SINGLE_TILE");
        rock.setMovementTileGroup("SINGLE_TILE");
        rock.setBuildingTileGroup("SINGLE_TILE");
        Rock = rock;

        ObstacleData bridge = new ObstacleData();
        bridge.setName("Most");
        bridge.setTextureName("Bridge");
        bridge.setXTexturePosition(30);
        bridge.setYTexturePosition(0);
        bridge.setXScale(1.0f);
        bridge.setYScale(1.0f);
        bridge.setPlacingTileGroup("LINE_2_DIAG_1");
        bridge.setMovementTileGroup("NONE");
        bridge.setBuildingTileGroup("LINE_2_DIAG_1");
        bridge.setInducedTerrainType(TerrainType.Bridge);
        bridge.setIgnoringPlacingRulesFlag(true);
        bridge.setPlacingType(PlacingType.ONLY_WATER);
        Bridge = bridge;



        DecorationData daisy = new DecorationData();
        daisy.setName("Stokrotka");
        daisy.setTextureName("Daisy");
        daisy.setXTexturePosition(0);
        daisy.setYTexturePosition(0);
        daisy.setXScale(1.0f);
        daisy.setYScale(1.0f);
        Daisy = daisy;

        DecorationData cabbage = new DecorationData();
        cabbage.setName("Kapusta");
        cabbage.setTextureName("Cabbage");
        cabbage.setXTexturePosition(0);
        cabbage.setYTexturePosition(0);
        cabbage.setXScale(1.0f);
        cabbage.setYScale(1.0f);
        Cabbage = cabbage;

        DecorationData thickets = new DecorationData();
        thickets.setName("Zarośla");
        thickets.setTextureName("Thickets");
        thickets.setXTexturePosition(0);
        thickets.setYTexturePosition(0);
        thickets.setXScale(1.0f);
        thickets.setYScale(1.0f);
        Thickets = thickets;

        DecorationData grain = new DecorationData();
        grain.setName("Zboże");
        grain.setTextureName("Grain");
        grain.setXTexturePosition(0);
        grain.setYTexturePosition(0);
        grain.setXScale(1.0f);
        grain.setYScale(1.0f);
        Grain = grain;



        UnitData worker = new UnitData();
        Worker = worker;
        UnitData warrior = new UnitData();
        Warrior = warrior;
        UnitData bowman = new UnitData();
        Bowman = bowman;
        UnitData rider = new UnitData();
        Rider = rider;
        UnitData volkhv = new UnitData();
        Volkhv = volkhv;
        UnitData leader = new UnitData();
        Hero = leader;

        BuildingData gord = new BuildingData();
        Gord = gord;
        BuildingData barracks = new BuildingData();
        Barracks = barracks;
        BuildingData farm = new BuildingData();
        Farm = farm;
        BuildingData tower = new BuildingData();
        Tower = tower;
        BuildingData chram = new BuildingData();
        Chram = chram;

        // Worker
        worker.setName("Robotnik");
        worker.setDescription("TODO");

        worker.setFoodCost(20);
        worker.setWoodCost(0);
        worker.setRequiredArmySize(1);
        worker.setRequiredTribeLevel(0);

        worker.setMaxHealth(20);
        worker.setDefense(0);
        worker.setArmorType(ArmorType.Light);
        worker.setHpBarWidth(60);
        worker.setHpBarPositionY(-61);

        worker.addAttack(new AttackData(AttackType.Melee, DamageType.Normal, 4, 6, 1, 1,
                TargetType.Destructible, TargetType.Ground, TargetType.Building, TargetType.Living, TargetType.Machine));
        worker.setDefaultAttacks(List.of(0));
        worker.setMaxActions(1);

        worker.setSteps(5);
        worker.setMovementSpeed(6.5f);

        worker.setTextureName("Worker");
        worker.setXTexturePosition(9);
        worker.setYTexturePosition(-30);
        //worker.setXTexturePosition(2);
        //worker.setYTexturePosition(-34);
        //worker.setXScale(0.4f);
        //worker.setYScale(0.4f);

        worker.addCreatedUnit(barracks);
        worker.addCreatedUnit(farm);
        worker.addCreatedUnit(tower);
        worker.addCreatedUnit(chram);

        worker.addCharacteristic(Characteristic.Living);
        worker.addCharacteristic(Characteristic.Destructible);
        worker.addCharacteristic(Characteristic.Ground);
        worker.addCharacteristic(Characteristic.Worker);

        worker.addUpgrade(IncreasedMovement);
        worker.addUpgrade(IncreasedUnitHealth);
        worker.addUpgrade(IncreasedRegeneration);

        worker.addSkill(enterBuilding);
        // Koniec Worker

        // Warrior
        warrior.setName("Wojownik");
        warrior.setDescription("TODO");

        warrior.setFoodCost(50);
        warrior.setWoodCost(0);
        warrior.setRequiredArmySize(2);
        warrior.setRequiredTribeLevel(0);

        warrior.setMaxHealth(35);
        warrior.setDefense(0);
        warrior.setArmorType(ArmorType.Heavy);
        warrior.setHpBarWidth(60);
        warrior.setHpBarPositionY(-61);

        warrior.addAttack(new AttackData(AttackType.Melee, DamageType.Normal, 14, 17, 1, 1,
                TargetType.Destructible, TargetType.Ground, TargetType.Building, TargetType.Living, TargetType.Machine));
        warrior.setDefaultAttacks(List.of(0));
        warrior.setMaxActions(1);
        warrior.setSteps(5);
        warrior.setMovementSpeed(6.5f);

        warrior.setTextureName("Warrior");
        warrior.setXTexturePosition(0);
        warrior.setYTexturePosition(-30);
//        warrior.setXTexturePosition(2);
//        warrior.setYTexturePosition(-34);
//        warrior.setXScale(0.4f);
//        warrior.setYScale(0.4f);

        warrior.addCharacteristic(Characteristic.Living);
        warrior.addCharacteristic(Characteristic.Destructible);
        warrior.addCharacteristic(Characteristic.Ground);

        warrior.addUpgrade(IncreasedMeeleDamage);
        warrior.addUpgrade(IncreasedUnitArmor);
        warrior.addUpgrade(IncreasedMeleeBleeding);
        warrior.addUpgrade(IncreasedBleedingDamage);
        warrior.addUpgrade(IncreasedMovement);
        warrior.addUpgrade(IncreasedUnitHealth);
        warrior.addUpgrade(IncreasedRegeneration);
        // Koniec Warrior

        // Bowman
        bowman.setName("Strzelec");
        bowman.setDescription("TODO");

        bowman.setFoodCost(40);
        bowman.setWoodCost(10);
        bowman.setRequiredArmySize(2);
        bowman.setRequiredTribeLevel(2);

        bowman.setMaxHealth(25);
        bowman.setDefense(0);
        bowman.setArmorType(ArmorType.Light);
        bowman.setHpBarWidth(60);
        bowman.setHpBarPositionY(-60);

        bowman.addAttack(new AttackData(AttackType.Ranged, DamageType.Missile, 11, 15, 2, 9,
                TargetType.Destructible, TargetType.Ground, TargetType.Flying, TargetType.Living, TargetType.Machine, TargetType.Building));
        bowman.addAttack(new AttackData(AttackType.Melee, DamageType.Normal, 5, 8, 1, 1,
                TargetType.Destructible, TargetType.Ground, TargetType.Building, TargetType.Living, TargetType.Machine));
        bowman.addAttack(new AttackData(AttackType.Ranged, DamageType.Ballistic, 11, 15, 2, 7,
                TargetType.Destructible, TargetType.Ground, TargetType.Building));
        bowman.setDefaultAttacks(List.of(0, 1));
        bowman.setMaxActions(1);
        bowman.setSteps(5);
        bowman.setMovementSpeed(6.5f);

        bowman.setTextureName("Bowman");
        bowman.setXTexturePosition(0);
        bowman.setYTexturePosition(-30);
//        bowman.setXTexturePosition(2);
//        bowman.setYTexturePosition(-34);
//        bowman.setXScale(0.4f);
//        bowman.setYScale(0.4f);

        bowman.addCharacteristic(Characteristic.Living);
        bowman.addCharacteristic(Characteristic.Destructible);
        bowman.addCharacteristic(Characteristic.Ground);

        bowman.addUpgrade(IncreasedRangedDamage);
        bowman.addUpgrade(IncreasedUnitArmor);
        bowman.addUpgrade(IncreasedMovement);
        bowman.addUpgrade(IncreasedUnitHealth);
        bowman.addUpgrade(IncreasedRegeneration);
        // Koniec Bowman

        // Rider
        rider.setName("Konny");
        rider.setDescription("TODO");

        rider.setFoodCost(110);
        rider.setWoodCost(20);
        rider.setRequiredArmySize(4);
        rider.setRequiredTribeLevel(5);

        rider.setMaxHealth(40);
        rider.setDefense(0);
        rider.setArmorType(ArmorType.Heavy);
        rider.setHpBarWidth(60);
        rider.setHpBarPositionY(-69);

        rider.addAttack(new AttackData(AttackType.Melee, DamageType.Normal, 14, 24, 1, 1,
                TargetType.Destructible, TargetType.Ground, TargetType.Building, TargetType.Living, TargetType.Machine));
        rider.setDefaultAttacks(List.of(0));
        rider.setMaxActions(1);
        rider.setSteps(6);
        rider.setMovementSpeed(6.5f);

        rider.setTextureName("Rider");
        rider.setXTexturePosition(-1);
        rider.setYTexturePosition(-39);
//        rider.setXTexturePosition(-4);
//        rider.setYTexturePosition(-40);
//        rider.setXScale(0.4f);
//        rider.setYScale(0.4f);

        rider.addCharacteristic(Characteristic.Living);
        rider.addCharacteristic(Characteristic.Destructible);
        rider.addCharacteristic(Characteristic.Ground);

        rider.addUpgrade(IncreasedMeeleDamage);
        rider.addUpgrade(IncreasedUnitArmor);
        rider.addUpgrade(IncreasedMeleeBleeding);
        rider.addUpgrade(IncreasedBleedingDamage);
        rider.addUpgrade(IncreasedMovement);
        rider.addUpgrade(IncreasedUnitHealth);
        rider.addUpgrade(IncreasedRegeneration);
        // Koniec rider

        // Volkhv
        volkhv.setName("Guślarz");
        volkhv.setDescription("TODO");

        volkhv.setFoodCost(50);
        volkhv.setWoodCost(10);
        volkhv.setRequiredArmySize(2);
        volkhv.setRequiredTribeLevel(0);

        volkhv.setMaxActions(1);
        volkhv.setSteps(6);
        volkhv.setMovementSpeed(6.5f);

        volkhv.setMaxHealth(25);
        volkhv.setDefense(0);
        volkhv.setArmorType(ArmorType.Light);
        volkhv.setHpBarWidth(60);
        volkhv.setHpBarPositionY(-59);

        volkhv.setSteps(4);
        volkhv.setMovementSpeed(6.5f);

        volkhv.setTextureName("Volkhv");
        volkhv.setXTexturePosition(-1);
        volkhv.setYTexturePosition(-31);
//        volkhv.setXTexturePosition(2);
//        volkhv.setYTexturePosition(-34);
//        volkhv.setXScale(0.4f);
//        volkhv.setYScale(0.4f);

        volkhv.addCharacteristic(Characteristic.Living);
        volkhv.addCharacteristic(Characteristic.Destructible);
        volkhv.addCharacteristic(Characteristic.Ground);

        volkhv.addUpgrade(IncreasedMovement);
        volkhv.addUpgrade(IncreasedUnitHealth);
        volkhv.addUpgrade(IncreasedRegeneration);

        volkhv.addAttack(new AttackData(AttackType.Melee, DamageType.Normal, 6, 11, 1, 1,
                TargetType.Destructible, TargetType.Ground, TargetType.Building, TargetType.Living, TargetType.Machine));
        volkhv.setDefaultAttacks(List.of(0));

        volkhv.addSkill(volkhvRegeneration);
        volkhv.addSkill(volkhvArmorBonus);
        volkhv.addSkill(thunderStrike);
        //volkhv.setDefaultSkills(List.of(0, 1, 2));
        // Koniec volkhv

        // Hero
        leader.setName("Dowódca");
        leader.setDescription("TODO");

        leader.setFoodCost(0);
        leader.setWoodCost(0);
        leader.setRequiredArmySize(1);
        leader.setRequiredTribeLevel(0);

        leader.setMaxHealth(100);
        leader.setDefense(0);
        leader.setArmorType(ArmorType.Heavy);
        leader.setHpBarWidth(80);
        leader.setHpBarPositionY(-68);

        leader.addAttack(new AttackData(AttackType.Melee, DamageType.Normal, 15, 22, 1, 1,
                TargetType.Destructible, TargetType.Ground, TargetType.Building, TargetType.Living, TargetType.Machine));
        leader.setDefaultAttacks(List.of(0));
        leader.setMaxActions(1);
        leader.setSteps(6);
        leader.setMovementSpeed(6.5f);

        leader.setTextureName("Hero");
        leader.setXTexturePosition(-1);
        leader.setYTexturePosition(-39);
//        leader.setXTexturePosition(-4);
//        leader.setYTexturePosition(-40);
//        leader.setXScale(0.4f);
//        leader.setYScale(0.4f);

        leader.addCharacteristic(Characteristic.Leader);
        leader.addCharacteristic(Characteristic.Living);
        leader.addCharacteristic(Characteristic.Destructible);
        leader.addCharacteristic(Characteristic.Ground);

        leader.addUpgrade(IncreasedMovement);
        leader.addUpgrade(IncreasedRegeneration);

        leader.addSkill(heroDamageBonus);
        leader.addSkill(heroReplenishActionPoints);
        // Koniec Hero




        // Gord
        gord.setName("Gród");
        gord.setDescription("TODO");

        gord.setFoodCost(1000);
        gord.setWoodCost(800);
        gord.setRequiredTribeLevel(0);
        gord.setConstructionTime(10);

        gord.setMaxHealth(250);
        gord.setDefense(0);
        gord.setHpBarWidth(280);
        gord.setHpBarPositionY(-202);

        gord.setTextureName("Gord");
        gord.setXTexturePosition(50);
        gord.setYTexturePosition(-70);
        gord.setXScale(0.4f);
        gord.setYScale(0.4f);

        gord.setPlacingTileGroup("TRIANGLE_2_PADDED_1");
        gord.setMovementTileGroup("TRIANGLE_2_PADDED_1");
        gord.setBuildingTileGroup("TRIANGLE_2_PADDED_2");
        gord.setAssemblyPoints("GORD_ASSEMBLY_POINTS");

        gord.addCharacteristic(Characteristic.Building);
        gord.addCharacteristic(Characteristic.Destructible);
        gord.addCharacteristic(Characteristic.Ground);

        gord.addCreatedUnit(worker);
        // Koniec Gord

        // Tower
        tower.setName("Wieża strażnicza");
        tower.setDescription("TODO");

        tower.setFoodCost(60);
        tower.setWoodCost(50);
        tower.setRequiredTribeLevel(0);
        tower.setConstructionTime(2);

        tower.setMaxHealth(80);
        tower.setDefense(0);
        tower.setHpBarWidth(120);
        tower.setHpBarPositionY(-134);

        tower.setTextureName("Tower");
        tower.setXTexturePosition(38);
        tower.setYTexturePosition(-85);
        tower.setXScale(0.45f);
        tower.setYScale(0.45f);

        tower.setPlacingTileGroup("TRIANGLE_2");
        tower.setMovementTileGroup("TRIANGLE_2");
        tower.setBuildingTileGroup("TRIANGLE_2");
        tower.setAssemblyPoints("TOWER_ASSEMBLY_POINTS");

        tower.addCharacteristic(Characteristic.Building);
        tower.addCharacteristic(Characteristic.Destructible);
        tower.addCharacteristic(Characteristic.Ground);
        // Koniec Tower

         // Barracks
        barracks.setName("Dom wojów");
        barracks.setDescription("TODO");

        barracks.setFoodCost(100);
        barracks.setWoodCost(80);
        barracks.setRequiredTribeLevel(0);
        barracks.setConstructionTime(2);

        barracks.setMaxHealth(85);
        barracks.setDefense(0);
        barracks.setHpBarWidth(160);
        barracks.setHpBarPositionY(-135);

        barracks.setTextureName("Barracks");
        barracks.setXTexturePosition(0);
        barracks.setYTexturePosition(-100);
        barracks.setXScale(0.5f);
        barracks.setYScale(0.5f);

        barracks.setPlacingTileGroup("DIAMOND_2");
        barracks.setMovementTileGroup("DIAMOND_2");
        barracks.setBuildingTileGroup("DIAMOND_2_PADDED_1");
        barracks.setAssemblyPoints("BARRACKS_ASSEMBLY_POINTS");

        barracks.addCharacteristic(Characteristic.Building);
        barracks.addCharacteristic(Characteristic.Destructible);
        barracks.addCharacteristic(Characteristic.Ground);

        barracks.addCreatedUnit(warrior);
        barracks.addCreatedUnit(bowman);
        barracks.addCreatedUnit(rider);
        // Koniec Barracks

        // Farm
        farm.setName("Chata");
        farm.setDescription("TODO");

        farm.setFoodCost(20);
        farm.setWoodCost(30);
        farm.setRequiredTribeLevel(0);
        farm.setConstructionTime(1);

        farm.setMaxHealth(60);
        farm.setDefense(0);
        farm.setHpBarWidth(120);
        farm.setHpBarPositionY(-88);

        farm.setFoodProvidingFlag(true);
        farm.setWoodProvidingFlag(true);

        farm.setTextureName("Farm");
        farm.setXTexturePosition(45);
        farm.setYTexturePosition(-20);
        farm.setXScale(0.45f);
        farm.setYScale(0.45f);

        farm.setPlacingTileGroup("TRIANGLE_2");
        farm.setMovementTileGroup("TRIANGLE_2");
        farm.setBuildingTileGroup("TRIANGLE_2");
        farm.setAssemblyPoints("TOWER_ASSEMBLY_POINTS");

        farm.setUnitsCapacity(3);
        farm.addAllowedUnits(Worker);
        farm.setAcceptingAllUnitsFlag(false);

        farm.addCharacteristic(Characteristic.Building);
        farm.addCharacteristic(Characteristic.Destructible);
        farm.addCharacteristic(Characteristic.Ground);
        // Koniec Farm

        // Chram
        chram.setName("Chram");
        chram.setDescription("TODO");

        chram.setFoodCost(100);
        chram.setWoodCost(150);
        chram.setRequiredTribeLevel(8);
        chram.setConstructionTime(3);

        chram.setMaxHealth(110);
        chram.setDefense(0);
        chram.setHpBarWidth(160);
        chram.setHpBarPositionY(-128);

        chram.setTextureName("Chram");
        chram.setXTexturePosition(0);
        chram.setYTexturePosition(-61);
        chram.setXScale(0.5f);
        chram.setYScale(0.5f);

        chram.setPlacingTileGroup("DIAMOND_2");
        chram.setMovementTileGroup("DIAMOND_2");
        chram.setBuildingTileGroup("DIAMOND_2_PADDED_1");
        chram.setAssemblyPoints("BARRACKS_ASSEMBLY_POINTS");

        chram.addCharacteristic(Characteristic.Building);
        chram.addCharacteristic(Characteristic.Destructible);
        chram.addCharacteristic(Characteristic.Ground);

        chram.addCreatedUnit(volkhv);
        // Koniec chram

        Obstacles = new HashMap<>();
        Obstacles.put(OakTree.getName(), OakTree);
        Obstacles.put(WillowTree.getName(), WillowTree);
        Obstacles.put(PineTree.getName(), PineTree);
        Obstacles.put(LimeTree.getName(), LimeTree);
        Obstacles.put(Rock.getName(), Rock);
        Obstacles.put(Bridge.getName(), Bridge);

        Decorations = new HashMap<>();
        Decorations.put(Daisy.getName(), Daisy);
        Decorations.put(Cabbage.getName(), Cabbage);
        Decorations.put(Thickets.getName(), Thickets);
        Decorations.put(Grain.getName(), Grain);
    }
}
