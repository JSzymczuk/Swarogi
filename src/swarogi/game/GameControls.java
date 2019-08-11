package swarogi.game;

import swarogi.enums.ActionButton;
import swarogi.interfaces.ControlsProvider;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayDeque;
import java.util.HashMap;

public class GameControls implements ControlsProvider, MouseListener, MouseMotionListener, KeyListener {

    private static final int totalButtons;
    private static final HashMap<Integer, Integer> mouseMapping;
    private static final HashMap<Integer, Integer> keyboardMapping;

    private final ArrayDeque<MouseEvent> mouseEvents;
    private final ArrayDeque<KeyEvent> keyEvents;

    private final boolean[] oldButtonStates;
    private final boolean[] newButtonStates;
    private Point oldPointerPosition;
    private Point newPointerPosition;

    static {
        // TODO: Zrobić metodę initialize?
        totalButtons = ActionButton.values().length;

        mouseMapping = new HashMap<>();
        mouseMapping.put(MouseEvent.BUTTON1, ActionButton.CONFIRM.getValue());
        mouseMapping.put(MouseEvent.BUTTON3, ActionButton.CANCEL.getValue());

        keyboardMapping = new HashMap<>();
        keyboardMapping.put(KeyEvent.VK_UP, ActionButton.MOVE_UP.getValue());
        keyboardMapping.put(KeyEvent.VK_RIGHT, ActionButton.MOVE_RIGHT.getValue());
        keyboardMapping.put(KeyEvent.VK_DOWN, ActionButton.MOVE_DOWN.getValue());
        keyboardMapping.put(KeyEvent.VK_LEFT, ActionButton.MOVE_LEFT.getValue());

        keyboardMapping.put(KeyEvent.VK_1, ActionButton.OPTION_0.getValue());
        keyboardMapping.put(KeyEvent.VK_2, ActionButton.OPTION_1.getValue());
        keyboardMapping.put(KeyEvent.VK_3, ActionButton.OPTION_2.getValue());
        keyboardMapping.put(KeyEvent.VK_4, ActionButton.OPTION_3.getValue());
        keyboardMapping.put(KeyEvent.VK_5, ActionButton.OPTION_4.getValue());
        keyboardMapping.put(KeyEvent.VK_6, ActionButton.OPTION_5.getValue());
        keyboardMapping.put(KeyEvent.VK_7, ActionButton.OPTION_6.getValue());
        keyboardMapping.put(KeyEvent.VK_8, ActionButton.OPTION_7.getValue());
        keyboardMapping.put(KeyEvent.VK_9, ActionButton.OPTION_8.getValue());
        keyboardMapping.put(KeyEvent.VK_0, ActionButton.OPTION_9.getValue());

        keyboardMapping.put(KeyEvent.VK_F1, ActionButton.MENU_1.getValue());
        keyboardMapping.put(KeyEvent.VK_F2, ActionButton.MENU_2.getValue());
        keyboardMapping.put(KeyEvent.VK_F3, ActionButton.MENU_3.getValue());
        keyboardMapping.put(KeyEvent.VK_F4, ActionButton.MENU_4.getValue());
        keyboardMapping.put(KeyEvent.VK_F5, ActionButton.MENU_5.getValue());
        keyboardMapping.put(KeyEvent.VK_F6, ActionButton.MENU_6.getValue());
        keyboardMapping.put(KeyEvent.VK_F7, ActionButton.MENU_7.getValue());
        keyboardMapping.put(KeyEvent.VK_F8, ActionButton.MENU_8.getValue());
        keyboardMapping.put(KeyEvent.VK_F9, ActionButton.MENU_9.getValue());
        keyboardMapping.put(KeyEvent.VK_F10, ActionButton.MENU_10.getValue());
        keyboardMapping.put(KeyEvent.VK_F11, ActionButton.MENU_11.getValue());
        keyboardMapping.put(KeyEvent.VK_F12, ActionButton.MENU_12.getValue());

        keyboardMapping.put(KeyEvent.VK_B, ActionButton.BUILDING_MENU.getValue());
        keyboardMapping.put(KeyEvent.VK_A, ActionButton.ATTACK.getValue());
        keyboardMapping.put(KeyEvent.VK_ALT, ActionButton.ALTERNATE_1.getValue());
        keyboardMapping.put(KeyEvent.VK_ENTER, ActionButton.END_TURN.getValue());
        keyboardMapping.put(KeyEvent.VK_R, ActionButton.TRIBE_PATHS_MENU.getValue());
        keyboardMapping.put(KeyEvent.VK_N, ActionButton.NEXT_UNIT.getValue());

        // TODO: Wyrzucić to w ostatecznej wersji. Dodane, bo czuję, że nie raz się na to nadzieję.
        if (totalButtons != mouseMapping.size() + keyboardMapping.size()) {
            System.out.println("Programista zapomniał dodać mapowania wszystkich przycisków w GameControls. :)");
        }
    }

    public GameControls() {
        mouseEvents = new ArrayDeque<>();
        keyEvents = new ArrayDeque<>();
        newPointerPosition = new Point();
        oldButtonStates = new boolean[totalButtons];
        newButtonStates = new boolean[totalButtons];
    }

    public Point getPointerPosition() { return newPointerPosition; }

    public boolean hasMousePositionChanged() {
        return newPointerPosition.x != oldPointerPosition.x || newPointerPosition.y != oldPointerPosition.y;
    }

    public boolean isButtonPressed(ActionButton button) {
        return newButtonStates[button.getValue()];
    }

    public boolean isButtonDown(ActionButton button) {
        int buttonValue = button.getValue();
        return newButtonStates[buttonValue] && !oldButtonStates[buttonValue];
    }

    public boolean isButtonUp(ActionButton button) {
        int buttonValue = button.getValue();
        return !newButtonStates[buttonValue] && oldButtonStates[buttonValue];
    }

    public void receiveEvents() {
        // Przepisz stan z poprzedniej klatki
        oldPointerPosition = newPointerPosition;
        for (int i = 0; i < totalButtons; ++i) {
            oldButtonStates[i] = newButtonStates[i];
        }

        // Znajdź nowe wydarzenie myszy
        // TODO: Rejestrowane jest tylko ostatnie wydarzenie myszy na klatkę. Dobrze? Źle?
        MouseEvent mouseEvent = null;
        while (!mouseEvents.isEmpty()) {
            mouseEvent = mouseEvents.pop();
        }

        if (mouseEvent != null) {
            int mouseEventId = mouseEvent.getID();
            int buttonCode = mouseEvent.getButton();

            switch (mouseEventId) {
                case MouseEvent.MOUSE_MOVED:
                    newPointerPosition = new Point(mouseEvent.getX(), mouseEvent.getY());
                    break;
                case MouseEvent.MOUSE_RELEASED:
                    if (mouseMapping.containsKey(buttonCode)) {
                        newButtonStates[mouseMapping.get(buttonCode)] = false;
                    }
                    break;
                case MouseEvent.MOUSE_PRESSED:
                    if (mouseMapping.containsKey(buttonCode)) {
                        newButtonStates[mouseMapping.get(buttonCode)] = true;
                    }
                    break;
            }
        }

        // Wczytaj wszystkie wydarzenia klawiatury
        KeyEvent keyEvent = null;
        while (!keyEvents.isEmpty()) {
            keyEvent = keyEvents.pop();
            int keyCode = keyEvent.getKeyCode();
            if (keyboardMapping.containsKey(keyCode)) {
                newButtonStates[keyboardMapping.get(keyCode)] = keyEvent.getID() == KeyEvent.KEY_PRESSED;
            }
        }
    }

    public ActionButton getFirstSelectedOption() {
        for (int i = ActionButton.OPTION_0.getValue(); i <= ActionButton.OPTION_9.getValue(); ++i) {
            ActionButton actionButton = ActionButton.values()[i];
            if (isButtonDown(actionButton)) {
                return actionButton;
            }
        }
        return null;
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) { mouseEvents.add(e); }

    @Override
    public void mouseReleased(MouseEvent e) { mouseEvents.add(e); }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void mouseDragged(MouseEvent e) { }

    @Override
    public void mouseMoved(MouseEvent e) { mouseEvents.add(e); }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) { keyEvents.add(e); }

    @Override
    public void keyReleased(KeyEvent e) { keyEvents.add(e); }
}
