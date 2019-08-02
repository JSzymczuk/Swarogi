package swarogi.playermodes;

import swarogi.common.Configuration;
import swarogi.common.ContentManager;
import swarogi.common.WindowSize;
import swarogi.data.Database;
import swarogi.datamodels.ReseachData;
import swarogi.datamodels.UpgradeData;
import swarogi.enums.ActionButton;
import swarogi.enums.TribePath;
import swarogi.game.GameCamera;
import swarogi.interfaces.ControlsProvider;
import swarogi.interfaces.PlayerModeChangeListener;
import swarogi.models.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class TribePathsPlayerMode extends PlayerMode {

    private HashMap<TribePath, Point> animalsRelativePositions;
    private TribePath hovered;
    private PlayerMode previousMode;
    private final static int hoverRadius = 90;

    TribePathsPlayerMode(Player player, PlayerModeChangeListener listener, PlayerMode previousMode) {
        super(player, listener);

        this.previousMode = previousMode;

        animalsRelativePositions = new HashMap<>();

        double r = 220;
        double angle = 5 * Math.PI / 12;
        double angleStep = 2 * Math.PI / 5;

        // TODO: Dodać potrzebne metody do enuma i zrobić pętlę
        animalsRelativePositions.put(TribePath.Bear, new Point((int)(r * Math.cos(angle)), (int)(r * Math.sin(angle))));
        animalsRelativePositions.put(TribePath.Fox, new Point((int)(r * Math.cos(angle + angleStep)), (int)(r * Math.sin(angle + angleStep))));
        animalsRelativePositions.put(TribePath.Deer, new Point((int)(r * Math.cos(angle + 2 * angleStep)), (int)(r * Math.sin(angle +  + 2 * angleStep))));
        animalsRelativePositions.put(TribePath.Owl, new Point((int)(r * Math.cos(angle +  + 3 * angleStep)), (int)(r * Math.sin(angle +  + 3 * angleStep))));
        animalsRelativePositions.put(TribePath.Wolf, new Point((int)(r * Math.cos(angle +  + 4 * angleStep)), (int)(r * Math.sin(angle +  + 4 * angleStep))));
    }

    @Override
    public boolean isDebugOnly() { return false; }
    @Override
    public boolean isPausingGameplay() { return false; }
    @Override
    public boolean isLockingCamera() { return false; }

    @Override
    public void update() {

        ControlsProvider controls = getControls();

        if (controls.isButtonDown(ActionButton.TRIBE_PATHS_MENU) || controls.isButtonDown(ActionButton.CANCEL)) {
            changePlayerMode(previousMode);
            return;
        }

        if (controls.hasMousePositionChanged()) {
            Point pos = controls.getPointerPosition();
            int x = pos.x, y = pos.y;
            Dimension size = WindowSize.getSize();
            Point center = getCenterPoint(size);

            for (TribePath animal : animalsRelativePositions.keySet()) {
                Point animalRelativePosition = animalsRelativePositions.get(animal);
                int dx = x - center.x + animalRelativePosition.x;
                int dy = y - center.y + animalRelativePosition.y;
                if (dx * dx + dy * dy <= hoverRadius * hoverRadius) {
                    hovered = animal;
                    return;
                }
            }

            hovered = null;
        }

        if (hovered != null && controls.isButtonDown(ActionButton.CONFIRM)
                && player.getTribePathLevel(hovered) < Configuration.MAX_TRIBE_PATH_LEVEL) {
            player.startResearch(hovered);
        }
    }

    @Override
    public void renderSelection(Graphics g, GameCamera camera) {

    }

    @Override
    public void renderGui(Graphics g, Dimension size, Font font) {
        g.setColor(new Color(0,0,0, 128));
        g.fillRect(0, 0, size.width, size.height);
        renderTextBack(g, size);

        Point center = getCenterPoint(size);

        for (TribePath animal : animalsRelativePositions.keySet()) {
            drawAnimal(g, animal, center);
        }

        int marginLeft = 40;
        int y = size.height - ContentManager.bottomTextShadow.getHeight() + 40;
        int lineHeight = 20;

        g.setColor(Color.white);

        if (player.isReseachInProgress()) {
            g.drawString("Trwają badania", marginLeft, y);
        }
        else if (hovered != null && player.getTribePathLevel(hovered) < Configuration.MAX_TRIBE_PATH_LEVEL) {
            int pathLevel = player.getTribePathLevel(hovered);
            ReseachData hoveredPath = Database.TribePaths.get(hovered);
            g.drawString(hoveredPath.getName() + " (poziom " + Integer.toString(pathLevel + 1) + ")", marginLeft, y);

            Map<UpgradeData, Integer> upgrades = hoveredPath.getUpgrades(pathLevel);
            for (UpgradeData upgrade : upgrades.keySet()) {
                y += lineHeight;
                int upgradeLevel = upgrades.get(upgrade);
                g.drawString(upgrade.getDescription().replace("{value}", upgrade.getValue(upgradeLevel).toString()),
                        marginLeft, y);
            }
        }
    }

    private void drawAnimal(Graphics g, TribePath animal, Point center) {
        Point translation = animalsRelativePositions.get(animal);
        drawCenteredImage(g, ContentManager.getAnimal(animal, hovered == animal,
                player.getTribePathLevel(animal) == Configuration.MAX_TRIBE_PATH_LEVEL),
                center.x - translation.x, center.y - translation.y);
    }

    private void drawCenteredImage(Graphics g, BufferedImage image, int x, int y) {
        g.drawImage(image, x - image.getWidth() / 2, y - image.getHeight() / 2, null);
    }

    private Point getCenterPoint(Dimension size) {
        int marginTop = size.height / 7;

        int x = size.width / 2;
        int y = marginTop + (size.height - ContentManager.bottomTextShadow.getHeight() - marginTop) / 2;

        return new Point(x, y);
    }
}
