package swarogi.common;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class Configuration {

    public static final int WINDOW_WIDTH = 960;
    public static final int WINDOW_HEIGHT = 720;
    public static final String WINDOW_TITLE = "Swarogi 0.1";
    public static final int FPS = 30;

    public static final int TILE_WIDTH;
    public static final int TILE_HEIGHT;
    public static final int TILE_SLANT_WIDTH;

    public static final int CAMERA_SPEED = 20;

    public static final int BASE_COMMAND_POINTS = 10;
    public static final int MAX_TRIBE_PATH_LEVEL = 5;
    public static final int WOOD_PER_ADJACENT_TREE = 1;
    public static final int MIN_ATTACK_DAMAGE = 1;
    public static final int BASE_REGENERATION = 6;
    public static final int INITIAL_TRIBE_LEVEL = 1;
    public static final int INITIAL_FOOD = 200;
    public static final int INITIAL_WOOD = 100;
    public static final int INITIAL_ARMY_LIMIT = 30;
    public static final int INITIAL_COMMAND_POINTS = 10;

    // Liczba punktów akcji gracza potrzebna do wykonania akcji
    public static final int UNIT_CREATION_COMMAND_POINTS_COST = 0;
    public static final int MOVEMENT_COMMAND_POINTS_COST = 1;
    public static final int ATTACK_COMMAND_POINTS_COST = 1;
    public static final int BUILDING_COMMAND_POINTS_COST = 1;

    // Liczba zużytych akcji przez jednostkę
    public static final int MOVEMENT_ACTION_POINTS_COST = 1;
    public static final int ATTACK_ACTION_POINTS_COST = 1;
    public static final int BUILD_ACTION_POINTS_COST = 1;
    public static final int SKILL_ACTION_POINTS_COST = 1;

    public static final int HP_BAR_HEIGHT = 5;
    public static final Color HP_BAR_COLOR_BACKGROUND = new Color(32,48,48);
    public static final Color HP_BAR_COLOR_FULL = new Color(32,240,32);
    public static final Color HP_BAR_COLOR_HALF = new Color(240,192,32);
    public static final Color HP_BAR_COLOR_EMPTY = new Color(128,32,32);

    public static final int MAX_PLAYERS = 6;
    public static final List<Color> PLAYER_COLORS;

    public static boolean areHpBarsVisible = true;
    public static boolean mapBuildingXSymmetry = false;
    public static boolean mapBuildingYSymmetry = false;
    public static boolean mapBuildingDiagSymmetry = false;

    // TODO: Usunąć z ostatecznej wersji, gdy zostaną dobrane rozmiary pól
    private static final float TILE_SCALE = 0.5f;
    private static final int BASE_TILE_WIDTH = 200;
    private static final int BASE_TILE_HEIGHT = 149;
    private static final int BASE_TILE_SLANT_WIDTH = 51;

    static {
        TILE_WIDTH = (int)(TILE_SCALE * BASE_TILE_WIDTH);
        TILE_HEIGHT = (int)(TILE_SCALE * BASE_TILE_HEIGHT);
        TILE_SLANT_WIDTH = (int)(TILE_SCALE * BASE_TILE_SLANT_WIDTH);

        PLAYER_COLORS = new ArrayList<>();
        PLAYER_COLORS.add(new Color(48, 192, 96)); // zielony
        PLAYER_COLORS.add(new Color(240, 224, 48)); // żółty
        PLAYER_COLORS.add(new Color(48, 96, 192)); // niebieski
        PLAYER_COLORS.add(new Color(192, 48, 32)); // czerwony
        PLAYER_COLORS.add(new Color(32, 160, 192)); // cyjan
        PLAYER_COLORS.add(new Color(224, 48, 192)); // różowy
    }
}
