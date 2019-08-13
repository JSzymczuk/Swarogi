package swarogi.game;

import swarogi.common.ContentManager;
import swarogi.data.Database;
import swarogi.engine.MapLoader;

public class Game {

    GameWindow gameWindow;
    GameControls gameControls;

    public boolean initialize() {
        ContentManager.loadContent();
        System.out.println("Zakończono ładowanie modeli.");
        Database.initialize();
        System.out.println("Zakończono inicjalizację bazy danych.");

        gameWindow = new GameWindow();
        gameControls = new GameControls();

        GamePanel gamePanel = new GamePanel(MapLoader.loadMap("maps/map01.txt"), gameControls);
        gamePanel.addMouseListener(gameControls);
        gamePanel.addMouseMotionListener(gameControls);
        gameWindow.addKeyListener(gameControls);

        gameWindow.setPanel(gamePanel);
        gameWindow.setVisible(true);

        running = true;

        return true;
    }

    public boolean isRunning() { return running; }

    public void handleEvents() { gameControls.receiveEvents(); }

    public void update(long time) {
        gameWindow.currentPanel.update(time);
    }

    public void render() {
        gameWindow.currentPanel.repaint();
    }

    public void dispose() {
        // TODO: Dispose
    }

    private boolean running;
}
