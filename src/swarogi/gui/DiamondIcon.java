package swarogi.gui;

import swarogi.common.Configuration;

import java.awt.*;

public class DiamondIcon extends Icon {
    @Override
    public boolean checkHover(Dimension size, Point point) {
        Point pos = getPosition(size);
        return Math.abs(pos.x - point.x) + Math.abs(pos.y - point.y) <= Configuration.ICON_SIZE / 2;
    }
}
