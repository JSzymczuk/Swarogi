package swarogi.game;

import swarogi.common.Configuration;

import javax.swing.*;

public class GameWindow extends JFrame {

    GamePanel currentPanel = null;

    public void setPanel(GamePanel panel) {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        currentPanel = panel;
        add(panel);
    }

    public GameWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(Configuration.WINDOW_WIDTH, Configuration.WINDOW_HEIGHT);
        this.setTitle(Configuration.WINDOW_TITLE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // this.setUndecorated(true); // Ukrywa pasek
        this.setLocationRelativeTo(null);
    }
}
