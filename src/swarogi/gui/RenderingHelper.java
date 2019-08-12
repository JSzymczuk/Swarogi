package swarogi.gui;

import swarogi.common.Configuration;
import swarogi.common.ContentManager;
import swarogi.datamodels.AttackData;
import swarogi.datamodels.EffectData;
import swarogi.models.Building;
import swarogi.models.Player;
import swarogi.models.Unit;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.List;

public final class RenderingHelper {

    public static int getSummaryBoxEndX() {
        return ContentManager.borderLeft.getWidth() / 2 + ContentManager.summaryBoxBorder.getWidth();
    }

    public static int getTextAreaHeight() {
        return Configuration.TEXT_AREA_HEIGHT
                + ContentManager.borderBottom.getHeight()
                + ContentManager.bottomTextBorder.getHeight();
    }

    public static int getTextAreaStartY(Dimension size) {
        return size.height - getTextAreaHeight();
    }

    public static Point getSummaryBoxPosition(Dimension size) {
        int x = ContentManager.borderLeft.getWidth() / 2;
        int y = size.height - Configuration.TEXT_AREA_HEIGHT - ContentManager.bottomTextBorder.getHeight() / 2
                - ContentManager.borderBottom.getHeight() - ContentManager.summaryBoxBorder.getHeight();
        return new Point(x, y);
    }

    public static Point getTribePathIconPosition() {
        int temp = Configuration.ICON_MARGIN + Configuration.ICON_SIZE / 2;
        return new Point(temp + ContentManager.borderRight.getWidth(),
                temp + RenderingHelper.TOP_PANEL_HEIGHT);
    }

    public static Point getCancelIconPosition() {
        int temp = Configuration.ICON_MARGIN + Configuration.ICON_SIZE / 2;
        return new Point(temp + ContentManager.borderLeft.getWidth(), temp + RenderingHelper.TOP_PANEL_HEIGHT);
    }

    public static Point getRegularIconPosition(boolean includeSummaryBox) {
        int temp = Configuration.ICON_MARGIN + Configuration.ICON_SIZE / 2;
        if (includeSummaryBox) {
            return new Point(RenderingHelper.getSummaryBoxEndX() + temp, RenderingHelper.getTextAreaHeight() + temp);
        }
        else {
            return new Point(ContentManager.borderRight.getWidth() + temp, RenderingHelper.getTextAreaHeight() + temp);
        }
    }


    public static void drawSummaryBox(Graphics g, Dimension size) {
        BufferedImage borderImage = ContentManager.summaryBoxBorder;
        int halfBorderWidth = ContentManager.bottomTextBorder.getHeight() / 2;

        Point pos = getSummaryBoxPosition(size);

        g.setColor(new Color(0, 0, 0, 192));
        g.fillRect(pos.x, pos.y + halfBorderWidth,
                borderImage.getWidth() - halfBorderWidth, borderImage.getHeight() - halfBorderWidth);

        g.drawImage(borderImage, pos.x, pos.y, null);
    }

    public static void drawUnitSummary(Graphics g, Dimension size, Unit unit) {

        Point pos = getSummaryBoxPosition(size);

        int margin = 25;
        int x = pos.x + margin;
        int y = pos.y + margin + ContentManager.bottomTextBorder.getHeight();
        int lineHeight = 20;

        g.setColor(Color.white);

        g.drawString(unit.hasActionPoints(1) ? unit.isConstructingBuilding() ? unit.getName() + " (buduje)" : unit.getName() : unit.getName() + " (wykonano akcję)", x, y);
        g.drawString("HP: " + Integer.toString((int)unit.getHealth()) + "/" + Integer.toString(unit.getMaxHealth()), x, y + lineHeight);
        g.drawString("Pancerz (" + unit.getArmorType().getName() + "): " + Integer.toString(unit.getDefense()), x, y + 2 * lineHeight);
        g.drawString("Kroki: " + Integer.toString(unit.getSteps()), x, y + 3 * lineHeight);

        int i = 4;
        for (AttackData attack : unit.getAttacks()) {
            Point damageRange = unit.getDamageForAttack(attack);
            g.drawString("Atak (" + attack.getAttackType().getName() + ", " + attack.getDamageType().getName() + "): "
                    + Integer.toString(damageRange.x) + " - " + Integer.toString(damageRange.y), x, y + i * lineHeight);
            ++i;
        }

        if (unit.getEffects().size() > 0) {
            g.drawString("Efekty: " +
                            String.join(", ", unit.getEffects().stream().map(EffectData::getName).collect(Collectors.toList())),
                    x, y + i * lineHeight);
        }
    }

    public static void drawBuildingSummary(Graphics g, Dimension size, Building building) {

        Point pos = getSummaryBoxPosition(size);

        int margin = 25;
        int x = pos.x + margin;
        int y = pos.y + margin + ContentManager.bottomTextBorder.getHeight();
        int lineHeight = 20;

        g.setColor(Color.white);

        g.drawString(building.isReady() ? building.getName() : building.getName() + " (w budowie)", x, y);
        g.drawString("HP: " + Integer.toString((int)building.getHealth()) + "/" + Integer.toString(building.getMaxHealth()), x, y + lineHeight);
        g.drawString("Pancerz (" + building.getModel().getBaseArmorType().getName() + "): " + Integer.toString(building.getDefense()), x, y + 2 * lineHeight);

        int unitCapacity = building.getModel().getUnitsCapacity();
        if (unitCapacity > 0) {
            List<Unit> unitsInside = building.getUnitsInside();
            List<String> unitNames = new ArrayList<>();
            int i;
            for (i = 0; i < unitsInside.size(); ++i) {
                unitNames.add(unitsInside.get(i).getName());
            }
            for (; i < unitCapacity; ++i) {
                unitNames.add("brak");
            }
            g.drawString("W środku: [" + String.join(", ", unitNames) + "]", x, y + 3 * lineHeight);
        }
    }

    public static void drawTextArea(Graphics g, Dimension size, String text) {
        int incr = ContentManager.bottomTextBorder.getWidth();
        int halfBorderTopHeight = ContentManager.bottomTextBorder.getHeight() / 2;

        int height = Configuration.TEXT_AREA_HEIGHT;
        int x = 0;
        int y = getTextAreaStartY(size);
        int margin = 25;

        g.setColor(new Color(0, 0, 0, 192));
        g.fillRect(x, y + halfBorderTopHeight,
                size.width, height + halfBorderTopHeight + ContentManager.borderBottom.getHeight() / 2);

        g.setColor(Color.white);
        if (text != null) {
            g.drawString(text, x + margin, y + halfBorderTopHeight + margin);
        }

        for (; x < size.width; x += incr) {
            g.drawImage(ContentManager.bottomTextBorder, x, y, null);
        }
    }

    public static void drawIcons(Graphics g, List<Icon> icons, Dimension size) {
        for (Icon icon : icons) {
            drawIcon(g, icon, size);
        }
    }

    public static void drawIcon(Graphics g, Icon icon, Dimension size) {
        Point pos = icon.getPosition(size);
        int x = pos.x, y = pos.y;

        drawCenteredImage(g, ContentManager.getIcon(icon.textureKey), x, y);
        if (icon.lockedFlag) {
            drawCenteredImage(g, ContentManager.iconLocked, x, y);
            drawCenteredImage(g, ContentManager.iconFrame, x, y);
        }
        else if (icon.noFundsFlag) {
            drawCenteredImage(g, ContentManager.iconNoFunds, x, y);
            drawCenteredImage(g, ContentManager.iconFrame, x, y);
        }
        else if (icon.hoveredFlag) {
            drawCenteredImage(g, ContentManager.iconFrameHover, x, y);
        }
        else {
            drawCenteredImage(g, ContentManager.iconFrame, x, y);
        }
    }

    public static void drawBorder(Graphics g, Dimension size, Font font, Player currentPlayer) {

        g.setColor(Color.black);
        g.fillRect(0, 0, size.width, TOP_PANEL_HEIGHT);

        int topFromX = ContentManager.borderTopLeft.getWidth();
        int topToX = size.width - ContentManager.borderTopRight.getWidth();

        int bottomFromX = ContentManager.borderBottomLeft.getWidth();
        int bottomToX = size.width - ContentManager.borderBottomRight.getWidth();

        int leftFromY = ContentManager.borderTopLeft.getHeight();
        int leftToY = size.height - ContentManager.borderBottomLeft.getHeight();

        int rightFromY = ContentManager.borderTopLeft.getHeight();
        int rightToY = size.height - ContentManager.borderBottomRight.getHeight();

        int incr = ContentManager.borderTop.getWidth();
        for (int x = topFromX; x < topToX; x += incr) {
            g.drawImage(ContentManager.borderTop, x, 0, null);
        }

        incr = ContentManager.borderBottom.getWidth();
        int temp = size.height - ContentManager.borderBottom.getHeight();
        for (int x = bottomFromX; x < bottomToX; x += incr) {
            g.drawImage(ContentManager.borderBottom, x, temp, null);
        }

        incr = ContentManager.borderLeft.getHeight();
        for (int y = leftFromY; y < leftToY; y += incr) {
            g.drawImage(ContentManager.borderLeft, 0, y, null);
        }

        incr = ContentManager.borderRight.getHeight();
        temp = size.width - ContentManager.borderRight.getWidth();
        for (int y = rightFromY; y < rightToY; y += incr) {
            g.drawImage(ContentManager.borderRight, temp, y, null);
        }

        g.drawImage(ContentManager.borderTopLeft, 0, 0, null);
        g.drawImage(ContentManager.borderBottomLeft, 0, leftToY, null);
        g.drawImage(ContentManager.borderTopRight, topToX, 0, null);
        g.drawImage(ContentManager.borderBottomRight, bottomToX, rightToY, null);

        int x6 = size.width / 6;
        int leftPadding = 20;
        int topPadding = 30;

        g.setFont(font);
        g.setColor(currentPlayer.getColor());
        g.drawString(currentPlayer.getName(), leftPadding, topPadding);

        g.setColor(Color.white);
        g.drawString("Poziom: " + Integer.toString(currentPlayer.getTribeLevel()), leftPadding + x6, topPadding);
        g.drawString("Żywność: " + Integer.toString(currentPlayer.getFood()), leftPadding + 2 * x6, topPadding);
        g.drawString("Drewno: " + Integer.toString(currentPlayer.getWood()), leftPadding + 3 * x6, topPadding);
        g.drawString("Rozmiar armii: " + Integer.toString(currentPlayer.getArmySize()) + "/"
                + Integer.toString(currentPlayer.getArmyCapacity()), leftPadding + 4 * x6, topPadding);
        g.drawString("Punkty akcji: " + Integer.toString(currentPlayer.getCommandPoints()) + "/" +
                "" + Integer.toString(currentPlayer.getMaxCommandPoints()), leftPadding + 5 * x6, topPadding);
    }


    public static void drawCenteredImage(Graphics g, BufferedImage image, int x, int y) {
        g.drawImage(image, x - image.getWidth() / 2, y - image.getHeight() / 2, null);
    }

    public static final int TOP_PANEL_HEIGHT = 51;
}

