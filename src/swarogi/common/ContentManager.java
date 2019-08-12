package swarogi.common;

import swarogi.enums.TerrainType;
import swarogi.enums.TileSelectionTag;
import swarogi.enums.TribePath;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public final class ContentManager {

    public static BufferedImage getTileSelection(TileSelectionTag selectionTag) {
        return tileSelections.get(selectionTag);
    }

    public static BufferedImage getTerrain(TerrainType terrainType) {
        return terrain.getOrDefault(terrainType, null);
    }

    public static TerrainExtensionInfo getTerrainExtension(TerrainType terrainType) {
        return terrainExtensions.getOrDefault(terrainType, null);
    }

    public static BufferedImage getModel(String modelName) { return models.getOrDefault(modelName, null); }

    public static BufferedImage getIcon(String modelName) { return icons.getOrDefault(modelName, null); }

    public static BufferedImage getAnimal(TribePath animal, boolean hovered, boolean completed) {
        return hovered ? animalsHover.getOrDefault(animal, null)
                : completed ? animalsCompleted.getOrDefault(animal, null)
                : animalsDefault.getOrDefault(animal, null);
    }

    private final static HashMap<TileSelectionTag, BufferedImage> tileSelections;
    private final static HashMap<TerrainType, BufferedImage> terrain;
    private final static HashMap<TerrainType, TerrainExtensionInfo> terrainExtensions;
    private final static HashMap<String, BufferedImage> models;
    private final static HashMap<String, BufferedImage> modelTextureBases;
    private final static HashMap<String, BufferedImage> icons;

    private final static HashMap<TribePath, BufferedImage> animalsDefault;
    private final static HashMap<TribePath, BufferedImage> animalsHover;
    private final static HashMap<TribePath, BufferedImage> animalsCompleted;

    // TODO: Kolejna mapa?
    public final static BufferedImage borderTopLeft;
    public final static BufferedImage borderTop;
    public final static BufferedImage borderTopRight;
    public final static BufferedImage borderRight;
    public final static BufferedImage borderBottomRight;
    public final static BufferedImage borderBottom;
    public final static BufferedImage borderBottomLeft;
    public final static BufferedImage borderLeft;
    public final static BufferedImage bottomTextShadow;

    public final static BufferedImage bottomTextBorder;
    public final static BufferedImage summaryBoxBorder;
    public final static BufferedImage leaderMark;

    public final static BufferedImage grid;
    public final static BufferedImage tileHex;

    public static final BufferedImage iconLocked;
    public static final BufferedImage iconNoFunds;
    public static final BufferedImage iconFrameHover;
    public static final BufferedImage iconFrame;

    static {
        tileSelections = new HashMap<>();
        terrain = new HashMap<>();
        terrainExtensions = new HashMap<>();
        models = new HashMap<>();
        modelTextureBases = new HashMap<>();
        icons = new HashMap<>();
        animalsDefault = new HashMap<>();
        animalsHover = new HashMap<>();
        animalsCompleted = new HashMap<>();

        grid = loadImage("content/tiles/grid.png");
        tileHex = loadImage("content/tiles/tile-hex.png");

        borderTopLeft = loadImage("content/gui/border-top-left.png");
        borderTop = loadImage("content/gui/border-top.png");
        borderTopRight = loadImage("content/gui/border-top-right.png");
        borderRight = loadImage("content/gui/border-right.png");
        borderBottomRight = loadImage("content/gui/border-bottom-right.png");
        borderBottom = loadImage("content/gui/border-bottom.png");
        borderBottomLeft = loadImage("content/gui/border-bottom-left.png");
        borderLeft = loadImage("content/gui/border-left.png");
        bottomTextShadow = loadImage("content/gui/bottom-text-shadow.png");

        bottomTextBorder = loadImage("content/gui/bottom-text-border.png");
        summaryBoxBorder = loadImage("content/gui/summary-box-border.png");

        leaderMark = loadImage("content/gui/hero-crown.png");

        iconLocked = loadImage("content/icons/locked.png");
        iconNoFunds = loadImage("content/icons/no-funds.png");
        iconFrameHover = loadImage("content/icons/frame-hover.png");
        iconFrame = loadImage("content/icons/frame.png");
    }

    private static void loadIcons() {
        icons.put(Configuration.ATTACK_ACTION_ICON_NAME, loadImage("content/icons/attack.png"));
        icons.put(Configuration.BUILD_ACTION_ICON_NAME, loadImage("content/icons/build.png"));
        icons.put(Configuration.CANCEL_ICON_NAME, loadImage("content/icons/cancel.png"));
        icons.put(Configuration.NEXT_UNIT_ICON_NAME, loadImage("content/icons/next-unit.png"));
        icons.put(Configuration.TRIBE_PATHS_ICON_NAME, loadImage("content/icons/tribe-paths.png"));
        icons.put(Configuration.EXIT_BUILDING_ICON_NAME, loadImage("content/icons/exit-building.png"));

        icons.put("SkillArmorBonus", loadImage("content/icons/armor.png"));
        icons.put("SkillEnterBuilding", loadImage("content/icons/enter-building.png"));
        icons.put("SkillHeal", loadImage("content/icons/heal.png"));
        icons.put("SkillOrder", loadImage("content/icons/order.png"));
        icons.put("SkillRepair", loadImage("content/icons/repair.png"));
        icons.put("SkillThunderStrike", loadImage("content/icons/thunder-strike.png"));
        icons.put("SkillWarCry", loadImage("content/icons/war-cry.png"));
    }

    public static void loadContent() {
        // TODO: Przenieść do oddzielnego wątku? Ładować zasoby dopiero, gdy są potrzebne?
        // TODO: Pamiętać, że bazowe kolory modeli wczytywane są w klasie Player.

        loadSelections();
        loadTerrain();
        loadUnits();
        loadBuildings();
        loadAnimals();
        loadIcons();

        models.put("LimeTree", loadImage("content/obstacles/lime-tree.png"));
        models.put("OakTree", loadImage("content/obstacles/oak-tree.png"));
        models.put("PineTree", loadImage("content/obstacles/pine-tree.png"));
        models.put("WillowTree", loadImage("content/obstacles/willow-tree.png"));
        models.put("Rock", loadImage("content/obstacles/rock.png"));
        models.put("Bridge", loadImage("content/obstacles/bridge.png"));
        models.put("Daisy", loadImage("content/decorations/daisy.png"));
        models.put("Thickets", loadImage("content/decorations/thickets.png"));
        models.put("Grain", loadImage("content/decorations/grain.png"));
        models.put("Cabbage", loadImage("content/decorations/cabbage.png"));
    }

    private static void loadSelections() {
        BufferedImage tileSelectionBase = loadImage("content/tiles/selection.png");
        tileSelections.put(TileSelectionTag.HOVER_NEUTRAL, createColoredBaseFromImage(tileSelectionBase,
                240, 240, 240));
        tileSelections.put(TileSelectionTag.INACTIVE_NEGATIVE, createColoredBaseFromImage(tileSelectionBase,
                95, 0, 0));
        tileSelections.put(TileSelectionTag.INACTIVE_POSITIVE, createColoredBaseFromImage(tileSelectionBase,
                0, 95, 0));
        tileSelections.put(TileSelectionTag.INACTIVE_ALLIED, createColoredBaseFromImage(tileSelectionBase,
                0, 0, 95));
        tileSelections.put(TileSelectionTag.ACTIVE_NEGATIVE, createColoredBaseFromImage(tileSelectionBase,
                159, 0, 0));
        tileSelections.put(TileSelectionTag.ACTIVE_POSITIVE, createColoredBaseFromImage(tileSelectionBase,
                0, 191, 0));
        tileSelections.put(TileSelectionTag.ACTIVE_ALLIED, createColoredBaseFromImage(tileSelectionBase,
                0, 0, 191));
        tileSelections.put(TileSelectionTag.NOT_ACCESSIBLE, createColoredBaseFromImage(tileSelectionBase,
                32, 32, 32));
        tileSelections.put(TileSelectionTag.SELECTED, createColoredBaseFromImage(tileSelectionBase,
                239, 191, 0));
    }

    private static void loadTerrain() {
        terrain.put(TerrainType.None, loadImage("content/tiles/void.png"));
        terrain.put(TerrainType.Dirt, loadImage("content/tiles/dirt.png"));
        terrain.put(TerrainType.Grass, loadImage("content/tiles/grass.png"));
        terrain.put(TerrainType.Water, loadImage("content/tiles/water.png"));
        terrain.put(TerrainType.LushGrass, loadImage("content/tiles/lush-grass.png"));
        terrain.put(TerrainType.Sand, loadImage("content/tiles/sand.png"));
        terrain.put(TerrainType.Soil, loadImage("content/tiles/soil.png"));
        terrain.put(TerrainType.DryGrass, loadImage("content/tiles/dry-grass.png"));
        terrain.put(TerrainType.DarkGrass, loadImage("content/tiles/dark-grass.png"));

        terrainExtensions.put(TerrainType.Dirt, new TerrainExtensionInfo(loadImage("content/tiles/dirt-extension.png")));
        terrainExtensions.put(TerrainType.Grass, new TerrainExtensionInfo(loadImage("content/tiles/grass-extension.png")));
        terrainExtensions.put(TerrainType.LushGrass, new TerrainExtensionInfo(loadImage("content/tiles/lush-grass-extension.png")));
        terrainExtensions.put(TerrainType.Sand, new TerrainExtensionInfo(loadImage("content/tiles/sand-extension.png")));
        terrainExtensions.put(TerrainType.Soil, new TerrainExtensionInfo(loadImage("content/tiles/soil-extension.png")));
        terrainExtensions.put(TerrainType.DryGrass, new TerrainExtensionInfo(loadImage("content/tiles/dry-grass-extension.png")));
        terrainExtensions.put(TerrainType.DarkGrass, new TerrainExtensionInfo(loadImage("content/tiles/dark-grass-extension.png")));
    }

    private static void loadUnits() {
        models.put("Bowman", loadImage("content/units/models/bowman.png"));
        models.put("Hero", loadImage("content/units/models/hero.png"));
        models.put("Rider", loadImage("content/units/models/rider.png"));
        models.put("Volkhv", loadImage("content/units/models/volkhv.png"));
        models.put("Warrior", loadImage("content/units/models/warrior.png"));
        models.put("Worker", loadImage("content/units/models/worker.png"));

        modelTextureBases.put("Bowman", loadImage("content/units/bases/bowman.png"));
        modelTextureBases.put("Hero", loadImage("content/units/bases/hero.png"));
        modelTextureBases.put("Rider", loadImage("content/units/bases/rider.png"));
        modelTextureBases.put("Volkhv", loadImage("content/units/bases/volkhv.png"));
        modelTextureBases.put("Warrior", loadImage("content/units/bases/warrior.png"));
        modelTextureBases.put("Worker", loadImage("content/units/bases/worker.png"));

        icons.put("Bowman", loadImage("content/icons/bowman.png"));
        icons.put("Hero", loadImage("content/icons/hero.png"));
        icons.put("Rider", loadImage("content/icons/rider.png"));
        icons.put("Volkhv", loadImage("content/icons/volkhv.png"));
        icons.put("Warrior", loadImage("content/icons/warrior.png"));
        icons.put("Worker", loadImage("content/icons/worker.png"));
    }

    private static void loadBuildings() {
        models.put("Barracks", loadImage("content/buildings/models/barracks.png"));
        models.put("Chram", loadImage("content/buildings/models/chram.png"));
        models.put("Farm", loadImage("content/buildings/models/farm.png"));
        models.put("Gord", loadImage("content/buildings/models/gord.png"));
        models.put("Tower", loadImage("content/buildings/models/tower.png"));

        modelTextureBases.put("Barracks", loadImage("content/buildings/bases/barracks.png"));
        modelTextureBases.put("Chram", loadImage("content/buildings/bases/chram.png"));
        modelTextureBases.put("Farm", loadImage("content/buildings/bases/farm.png"));
        modelTextureBases.put("Gord", loadImage("content/buildings/bases/gord.png"));
        modelTextureBases.put("Tower", loadImage("content/buildings/bases/tower.png"));

        icons.put("Barracks", loadImage("content/icons/barracks.png"));
        icons.put("Chram", loadImage("content/icons/chram.png"));
        icons.put("Farm", loadImage("content/icons/farm.png"));
        icons.put("Gord", loadImage("content/icons/gord.png"));
        icons.put("Tower", loadImage("content/icons/tower.png"));
    }

    private static void loadAnimals() {
        animalsDefault.put(TribePath.Bear, loadImage("content/animals/bear-default.png"));
        animalsHover.put(TribePath.Bear, loadImage("content/animals/bear-active.png"));
        animalsCompleted.put(TribePath.Bear, loadImage("content/animals/bear-completed.png"));
        animalsDefault.put(TribePath.Fox, loadImage("content/animals/fox-default.png"));
        animalsHover.put(TribePath.Fox, loadImage("content/animals/fox-active.png"));
        animalsCompleted.put(TribePath.Fox, loadImage("content/animals/fox-completed.png"));
        animalsDefault.put(TribePath.Deer, loadImage("content/animals/deer-default.png"));
        animalsHover.put(TribePath.Deer, loadImage("content/animals/deer-active.png"));
        animalsCompleted.put(TribePath.Deer, loadImage("content/animals/deer-completed.png"));
        animalsDefault.put(TribePath.Owl, loadImage("content/animals/owl-default.png"));
        animalsHover.put(TribePath.Owl, loadImage("content/animals/owl-active.png"));
        animalsCompleted.put(TribePath.Owl, loadImage("content/animals/owl-completed.png"));
        animalsDefault.put(TribePath.Wolf, loadImage("content/animals/wolf-default.png"));
        animalsHover.put(TribePath.Wolf, loadImage("content/animals/wolf-active.png"));
        animalsCompleted.put(TribePath.Wolf, loadImage("content/animals/wolf-completed.png"));
    }

    private static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        }
        catch (IOException e) {
            System.err.println(path);
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage createColoredBaseFromImage(BufferedImage image, int red, int green, int blue) {
        image = copyImage(image);
        changeColor(image,0,0,0, red, green, blue);
        return image;
    }

    public static BufferedImage tintImage(BufferedImage image, int red, int green, int blue) {
        BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TRANSLUCENT);
        Graphics2D graphics = img.createGraphics();
        Color newColor = new Color(red, green, blue, 255);
        graphics.setXORMode(newColor);
        graphics.drawImage(image, null, 0, 0);
        graphics.dispose();
        return img;
    }

    public static void changeColor(BufferedImage imgBuf, int oldRed, int oldGreen, int oldBlue,
            int newRed, int newGreen, int newBlue) {

        int RGB_MASK = 0x00ffffff;
        int ALPHA_MASK = 0xff000000;

        int oldRGB = oldRed << 16 | oldGreen << 8 | oldBlue;
        int toggleRGB = oldRGB ^ (newRed << 16 | newGreen << 8 | newBlue);

        int w = imgBuf.getWidth();
        int h = imgBuf.getHeight();

        int[] rgb = imgBuf.getRGB(0, 0, w, h, null, 0, w);
        for (int i = 0; i < rgb.length; i++) {
            if ((rgb[i] & RGB_MASK) == oldRGB) {
                rgb[i] ^= toggleRGB;
            }
        }
        imgBuf.setRGB(0, 0, w, h, rgb, 0, w);
    }

    public static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics2D g = b.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    public static BufferedImage getModelTextureBase(String textureBaseName) {
        if (modelTextureBases.containsKey(textureBaseName)) {
            return modelTextureBases.get(textureBaseName);
        }
        return null;
    }
}