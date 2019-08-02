package swarogi.interfaces;

import swarogi.enums.ActionButton;

import java.awt.*;

public interface ControlsProvider {
    Point getPointerPosition();
    boolean hasMousePositionChanged();
    boolean isButtonPressed(ActionButton button);
    boolean isButtonDown(ActionButton button);
    boolean isButtonUp(ActionButton button);
    ActionButton getFirstSelectedOption();
}
