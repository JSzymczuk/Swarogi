package swarogi.playermodes;

import swarogi.common.Configuration;
import swarogi.common.ContentManager;
import swarogi.common.WindowSize;
import swarogi.data.Database;
import swarogi.datamodels.ReseachData;
import swarogi.datamodels.UpgradeData;
import swarogi.enums.ActionButton;
import swarogi.enums.HorizontalAlign;
import swarogi.enums.TribePath;
import swarogi.enums.VerticalAlign;
import swarogi.game.GameCamera;
import swarogi.gui.DiamondIcon;
import swarogi.gui.Icon;
import swarogi.gui.RenderingHelper;
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
    private String defaultText;
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

        defaultText = player.isReseachInProgress() ? "Trwają badania" : null;

        // Ścieżki rozwoju
        {
            Point pos = RenderingHelper.getTribePathIconPosition();
            Icon icon = new DiamondIcon();
            icon.hAlign = HorizontalAlign.LEFT;
            icon.vAlign = VerticalAlign.TOP;
            icon.x = pos.x;
            icon.y = pos.y;
            icon.actionButton = ActionButton.TRIBE_PATHS_MENU;
            icon.textureKey = Configuration.TRIBE_PATHS_ICON_NAME;
            icon.hoverText = "Ścieżki rozwoju [R]";
            icon.clickAction = this::exitMode;
            icon.hoverAction = () -> setText(icon.hoverText);
            icon.unhoverAction = () -> setText(defaultText);
            addIcon(icon);
        }

        {
            Point pos = RenderingHelper.getCancelIconPosition();
            Icon icon = new DiamondIcon();
            icon.hAlign = HorizontalAlign.RIGHT;
            icon.vAlign = VerticalAlign.TOP;
            icon.x = pos.x;
            icon.y = pos.y;
            icon.actionButton = ActionButton.CANCEL;
            icon.textureKey = Configuration.CANCEL_ICON_NAME;
            icon.hoverText = "Wstecz [PPM]";
            icon.clickAction = this::exitMode;
            icon.hoverAction = () -> setText(icon.hoverText);
            icon.unhoverAction = () -> setText(defaultText);
            addIcon(icon);
        }

        setText(defaultText);
    }

    @Override
    public boolean isDebugOnly() { return false; }
    @Override
    public boolean isPausingGameplay() { return false; }
    @Override
    public boolean isLockingCamera() { return false; }

    @Override
    public void update() {

        if (checkGuiInteraction()) { return; }

        ControlsProvider controls = getControls();

        if (controls.hasMousePositionChanged()) {
            Point pos = controls.getPointerPosition();
            int x = pos.x, y = pos.y;
            Dimension size = WindowSize.getSize();
            Point center = getCenterPoint(size);

            hovered = null;

            for (TribePath animal : animalsRelativePositions.keySet()) {
                Point animalRelativePosition = animalsRelativePositions.get(animal);
                int dx = x - center.x + animalRelativePosition.x;
                int dy = y - center.y + animalRelativePosition.y;
                if (dx * dx + dy * dy <= hoverRadius * hoverRadius) {
                    hovered = animal;
                    break;
                }
            }

            if (!player.isReseachInProgress()) {
                if (hovered != null) {
                    ReseachData hoveredPath = Database.TribePaths.get(hovered);
                    StringBuilder text = new StringBuilder();
                    int pathLevel;

                    if (player.getTribePathLevel(hovered) < Configuration.MAX_TRIBE_PATH_LEVEL) {
                        pathLevel = player.getTribePathLevel(hovered);
                        text.append(hoveredPath.getName())
                                .append(" (poziom ")
                                .append(Integer.toString(pathLevel + 1))
                                .append("): ");
                    } else {
                        pathLevel = Configuration.MAX_TRIBE_PATH_LEVEL - 1;
                        text.append(hoveredPath.getName())
                                .append(": ");
                    }

                    Map<UpgradeData, Integer> upgrades = hoveredPath.getUpgrades(pathLevel);
                    for (UpgradeData upgrade : upgrades.keySet()) {
                        int upgradeLevel = upgrades.get(upgrade);
                        text.append(upgrade.getDescription().replace("{value}", upgrade.getValue(upgradeLevel).toString()));
                    }
                    setText(text.toString());
                } else {
                    setText(defaultText);
                }
            }
        }

        if (hovered != null && controls.isButtonDown(ActionButton.CONFIRM) && !player.isReseachInProgress()
                && player.getTribePathLevel(hovered) < Configuration.MAX_TRIBE_PATH_LEVEL) {
            player.startResearch(hovered);
            defaultText = "Trwają badania";
            setText(defaultText);
        }
    }

    @Override
    public void renderSelection(Graphics g, GameCamera camera) {

    }

    @Override
    public void renderGui(Graphics g, Dimension size, Font font) {

        g.setColor(new Color(0,0,0, 128));
        g.fillRect(0, 0, size.width, size.height);

        Point center = getCenterPoint(size);

        for (TribePath animal : animalsRelativePositions.keySet()) {
            drawAnimal(g, animal, center);
        }

        RenderingHelper.drawTextArea(g, size, getText());
        RenderingHelper.drawIcons(g, getIcons(), size);
    }

    private void drawAnimal(Graphics g, TribePath animal, Point center) {
        Point translation = animalsRelativePositions.get(animal);
        RenderingHelper.drawCenteredImage(g, ContentManager.getAnimal(animal, hovered == animal,
                player.getTribePathLevel(animal) == Configuration.MAX_TRIBE_PATH_LEVEL),
                center.x - translation.x, center.y - translation.y);
    }

    private Point getCenterPoint(Dimension size) {
        int marginTop = size.height / 7;

        int x = size.width / 2;
        int y = marginTop + (size.height - ContentManager.bottomTextShadow.getHeight() - marginTop) / 2;

        return new Point(x, y);
    }

    private void exitMode() {
        changePlayerMode(previousMode);
    }
}
