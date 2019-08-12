package swarogi.game;

import swarogi.common.Configuration;
import swarogi.common.ContentManager;
import swarogi.common.TerrainExtensionInfo;
import swarogi.common.WindowSize;
import swarogi.engine.MapLoader;
import swarogi.enums.Direction;
import swarogi.enums.TerrainType;
import swarogi.gui.RenderingHelper;
import swarogi.interfaces.WindowSizeProvider;
import swarogi.data.Database;
import swarogi.interfaces.*;
import swarogi.enums.ActionButton;
import swarogi.models.*;
import swarogi.playermodes.*;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class GamePanel extends JPanel implements PlayerModeChangeListener, WindowSizeProvider {

    private GameMap map;
    private Renderer renderer;
    private ArrayList<Player> players;
    private int currentPlayerId;
    private PlayerMode currentPlayerMode;
    private ArrayDeque<Action> actions;
    private ControlsProvider controlsProvider;
    private Font font;
    private List<Tile> visibleTiles;

    public GamePanel(GameMap map, ControlsProvider controls) {
        this.map = map;
        this.controlsProvider = controls;

        this.actions = new ArrayDeque<>();
        this.renderer = new Renderer();

        this.font = new Font("TimesRoman", Font.PLAIN, 14);

        this.players = new ArrayList<>();

        this.visibleTiles = new ArrayList<>();

        List<Tile> playersPosition = map.getPlayerPositions();
        int n = Math.min(playersPosition.size(), Configuration.MAX_PLAYERS);

        for (int i = 0; i < n; ++i) {
            Player player = new Player();
            initializePlayer(player, "Gracz " + Integer.toString(i + 1), Configuration.PLAYER_COLORS.get(i), i, playersPosition.get(i));
            this.players.add(player);
        }

        currentPlayerId = players.size();

        WindowSize.setWindowSizeProvider(this);

        nextPlayer();
    }

    public void update(long time) {

        //Point mousePosition = controlsProvider.getPointerPosition();
        // TODO: Zbadać zdarzenia z interfejsem?

        Player currentPlayer = players.get(currentPlayerId);

        if (!currentPlayerMode.isLockingCamera()) {
            currentPlayer.updateCamera();
        }

        updateModeSelection();

        currentPlayerMode.update();

        ControlsProvider controls = currentPlayer.getControls();

        if (currentPlayer.getCamera().hasPositionChanged()) {
            updateVisibleTiles(); // TODO: Może to w ogóle przenieść do kamery?
        }

        // TODO: Tylko dla trybu edytora map
        if (controls.isButtonDown(ActionButton.MENU_9)) {
            Configuration.mapBuildingXSymmetry = !Configuration.mapBuildingXSymmetry;
        }
        if (controls.isButtonDown(ActionButton.MENU_10)) {
            Configuration.mapBuildingYSymmetry = !Configuration.mapBuildingYSymmetry;
        }
        if (controls.isButtonDown(ActionButton.MENU_11)) {
            Configuration.mapBuildingDiagSymmetry = !Configuration.mapBuildingDiagSymmetry;
        }
        if (controls.isButtonDown(ActionButton.MENU_12)) {
            MapLoader.saveMap(map, "exported.txt");
        }

        if (controls.isButtonPressed(ActionButton.ALTERNATE_1)) {
            if (controls.isButtonDown(ActionButton.OPTION_0)) {
                Configuration.areHpBarsVisible = !Configuration.areHpBarsVisible;
            }
            if (controls.isButtonDown(ActionButton.OPTION_1)) {
                Configuration.isHexagonalMeshVisible = !Configuration.isHexagonalMeshVisible;
            }
        }

        if (!currentPlayerMode.isPausingGameplay()) {
            // Zaktualizuj akcje
            while (!actions.isEmpty()) {
                Action currentAction = actions.peek();
                if (!currentAction.hasStarted()) {
                    if (currentAction.canBeExecuted()) {
                        currentAction.start();
                    }
                    else {
                        currentAction.abort();
                        actions.poll();
                        break;
                    }
                }
                if (currentAction.isCompleted()) {
                    currentAction.finish();
                    actions.poll();
                }
                else {
                    currentAction.update();
                    break;
                }
            }
        }

        if (controlsProvider.isButtonDown(ActionButton.END_TURN)) {
            nextPlayer();
        }
    }

    private void updateVisibleTiles() {
        visibleTiles.clear();

        GameCamera camera = players.get(currentPlayerId).getCamera();
        Tile firstTile = map.getTileByCoordinates(Math.max(Configuration.TILE_SLANT_WIDTH, camera.x), Math.max(Configuration.TILE_HEIGHT / 2, camera.y));
        if (firstTile == null) { return; }

        int firstTileX = Math.max(0, firstTile.xIdx - 1);
        int firstTileY = Math.max(0, firstTile.yIdx - 1);

        Dimension size = getSize();
        int lastTileX = Math.min(map.getTilesX() - 1, firstTileX + size.width / (Configuration.TILE_WIDTH - Configuration.TILE_SLANT_WIDTH) + 2); // + 2 bo po 1 dodatkowym z każdej strony
        int lastTileY = Math.min(map.getTilesY() - 1, firstTileY + size.height / Configuration.TILE_HEIGHT + 2);

        for (int i = firstTileX; i <= lastTileX; ++i) {
            for (int j = firstTileY; j <= lastTileY; ++j) {
                Tile tile = map.getTile(i, j);
                if (tile != null) { // TODO: Chyba zbędne sprawdzenie
                    visibleTiles.add(tile);
                }
            }
        }

        visibleTiles.sort(Comparator.comparingInt((Tile t) -> t.getTerrainType().getTilingPriority()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(32,32,32));

        if (map == null) { return; }

        GameCamera camera = players.get(currentPlayerId).getCamera();
        renderer.startRendering(g, camera);

        int cameraX = camera.x;
        int cameraY = camera.y;

        // Narysuj kafelki
        List<Tile> visibleTiles = new ArrayList<Tile>(this.visibleTiles);
        for (Tile tile : visibleTiles) {
            renderer.render(tile);
        }

        // Narysuj obramowanie kafelków
        if (Configuration.isHexagonalMeshVisible) {
            BufferedImage tileHex = ContentManager.tileHex;
            int tileWidth = Configuration.TILE_WIDTH;
            int tileHeight = Configuration.TILE_HEIGHT;

            for (Tile tile : visibleTiles) {
                Point pos = tile.getTopLeft();
                int x = pos.x - cameraX;
                int y = pos.y - cameraY;
                g.drawImage(tileHex, x, y, tileWidth, tileHeight, null);
            }
        }

        // Narysuj zaznaczenie
        currentPlayerMode.renderSelection(g, camera);

        // TODO: Mało estetyczne rozwiązanie, ale pozwala rozdzielić logikę od interfejsu.
        // TODO: Pobierać tylko widoczne obiekty (drzewo AABB będzie jak znalazł).
        for (Placeable placeable : map.getPlaceables()) {
            if (placeable instanceof Obstacle) {
                renderer.render((Obstacle)placeable);
            }
            else if (placeable instanceof Unit) {
                renderer.render((Unit)placeable);
            }
            else if (placeable instanceof Building) {
                renderer.render((Building) placeable);
            }
            else if (placeable instanceof Decoration) {
                renderer.render((Decoration)placeable);
            }
        }

        currentPlayerMode.renderGui(g, getSize(), font);

        RenderingHelper.drawBorder(g, getSize(), font, players.get(currentPlayerId));

        renderer.endRendering();
    }

    // "Brutalne" przełączenie tryby gry pomiędzy opcjami debugowymi.
    private void updateModeSelection() {
        Player player = players.get(currentPlayerId);
        ControlsProvider controlsProvider = player.getControls();

        if (controlsProvider.isButtonDown(ActionButton.MENU_1)) {
            System.out.println("Wybrano tryb rozgrywki");
            currentPlayerMode = new SelectionPlayerMode(player, this, map);
        }
        else if (controlsProvider.isButtonDown(ActionButton.MENU_2)) {
            System.out.println("Wybrano tryb umieszczania jednostek");
            currentPlayerMode = new DebugUnitsPlacingPlayerMode(player, this, map);
        }
        else if (controlsProvider.isButtonDown(ActionButton.MENU_3)) {
            System.out.println("Wybrano tryb umieszczania budynków");
            currentPlayerMode = new DebugBuildingsPlacingPlayerMode(player, this, map);
        }
        else if (controlsProvider.isButtonDown(ActionButton.MENU_4)) {
            System.out.println("Wybrano tryb umieszczania innych obiektów");
            currentPlayerMode = new DebugOthersPlacingPlayerMode(player, this, map);
        }
        else if (controlsProvider.isButtonDown(ActionButton.MENU_5)) {
            System.out.println("Wybrano tryb edycji terenu");
            currentPlayerMode = new DebugChangeTerrainPlayerMode(player, this, map);
        }
    }

    @Override
    public void onPlayerModeChanged(PlayerMode playerMode) {
        this.currentPlayerMode = playerMode;
    }

    @Override
    public void addAction(Action action) {
        this.actions.add(action);
    }

    private void initializePlayer(Player player, String name, Color color, int team, Tile gordPosition) {
        player.setName(name);
        player.setColor(color.getRed(), color.getGreen(), color.getBlue());
        player.setTeam(team);

        player.setTribeLevel(Configuration.INITIAL_TRIBE_LEVEL);
        player.setFood(Configuration.INITIAL_FOOD);
        player.setWood(Configuration.INITIAL_WOOD);
        player.setArmyCapacity(Configuration.INITIAL_ARMY_LIMIT);

        Building building = new Building(Database.Gord, player);

        GameCamera camera = new GameCamera();
        Point center = map.getTile(0, 0).getCenter();
        Dimension windowSize = this.getSize();
        camera.x = - (windowSize.width - center.x) / 2;
        camera.y = - (windowSize.height - center.y) / 2;

        player.setCamera(camera);

        PlaceableData model = building.getPlaceableData();
        Point tileCenter = gordPosition.getCenter();
        String textureName = model.getTextureName();
        BufferedImage texture = ContentManager.getModel(textureName);

        if (texture != null) {
            int textureWidth = (int) (texture.getWidth() * model.getXScale());
            int textureHeight = (int) (texture.getHeight() * model.getYScale());
            int x = tileCenter.x + model.getXTexturePosition();
            int y = tileCenter.y + model.getYTexturePosition();
            camera.x = x - Configuration.WINDOW_WIDTH / 2;
            camera.y = y - Configuration.WINDOW_HEIGHT / 2;
        }

        // TODO: Analogiczne umieszczenie jest w CreateBuildingAction. Wyciągnąć to gdzieś.
        if (map.tryPlace(building, gordPosition)) {
            map.addDestructible(building);
            player.setGord(building);
            player.addBuilding(building);
            for (Tile tile : building.getAllTiles()) {
                for (Placeable decoration : tile.removeDecorations()) {
                    this.map.removePlaceable(decoration);
                }
            }
            building.setRemainingConstructionTime(0);
            building.restoreHealth(building.getMaxHealth());
        }

        Unit leader = new Unit(Database.Hero, player);
        Tile leaderPosition = map.getTileNeighbor(gordPosition, Direction.BOTTOM);
        if (leaderPosition != null) { leaderPosition = map.getTileNeighbor(leaderPosition, Direction.BOTTOM); }
        if (map.tryPlace(leader, leaderPosition)) {
            map.addDestructible(leader);
            player.setLeader(leader);
            player.addUnit(leader);
            player.increaseArmySize(leader.getUnitData().getRequiredArmySize());
        }
    }

    private void nextPlayer() {

        int n = players.size();
        if (currentPlayerId < n) {
            Player previousPlayer = players.get(currentPlayerId);
            previousPlayer.setControls(null);
            ++currentPlayerId;
        }
        if (currentPlayerId == n) {
            // TODO: Rozegrać turę dla neutralnych, jeśli będą
            currentPlayerId = 0;
        }

        Player currentPlayer = players.get(currentPlayerId);
        currentPlayer.updateResearch();
        currentPlayer.restoreCommandPoints();
        currentPlayer.updateUnits();     // Jednostka może umrzeć zanim wybuduje
        currentPlayer.getResources();    // Nowo wybudowany budynek nie dostarcza jeszcze surowców
        currentPlayer.updateBuildings();
        currentPlayer.setControls(controlsProvider);
        currentPlayerMode = new SelectionPlayerMode(currentPlayer, this, map);

        updateVisibleTiles();
    }
}
