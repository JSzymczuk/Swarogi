package swarogi.gui;

import swarogi.enums.ActionButton;
import swarogi.enums.HorizontalAlign;
import swarogi.enums.VerticalAlign;

import java.awt.*;

public abstract class Icon {
    public HorizontalAlign hAlign;
    public VerticalAlign vAlign;
    public int x;
    public int y;
    public String textureKey;
    public ActionButton actionButton;
    public boolean lockedFlag;
    public boolean noFundsFlag;
    public boolean hoveredFlag;
    public String hoverText;
    public Runnable clickAction;
    public Runnable hoverAction;
    public Runnable unhoverAction;
    public Object value;

    public Point getPosition(Dimension size) {
        int xPos = 0, yPos = 0;

        if (hAlign == HorizontalAlign.LEFT) {
            xPos = x;
        } else if (hAlign == HorizontalAlign.RIGHT) {
            xPos = size.width - x;
        } else if (hAlign == HorizontalAlign.CENTER) {
            xPos = size.width / 2 + x;
        }

        if (vAlign == VerticalAlign.TOP) {
            yPos = y;
        } else if (vAlign == VerticalAlign.BOTTOM) {
            yPos = size.height - y;
        } else if (vAlign == VerticalAlign.MIDDLE) {
            yPos = size.height / 2 + y;
        }

        return new Point(xPos, yPos);
    }

    public abstract boolean checkHover(Dimension size, Point point);

    public void onClick() {
        if (clickAction != null && !lockedFlag && !noFundsFlag) {
            clickAction.run();
        }
    }

    public void onHover() {
        if (hoverAction != null) {
            hoverAction.run();
        }
    }

    public void onUnhover() {
        if (unhoverAction != null) {
            unhoverAction.run();
        }
    }
}
