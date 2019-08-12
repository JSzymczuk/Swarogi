package swarogi.enums;

public enum ActionButton {

    CONFIRM(0),
    CANCEL(1),
    MOVE_UP(2),
    MOVE_RIGHT(3),
    MOVE_DOWN(4),
    MOVE_LEFT(5),

    OPTION_0(6),
    OPTION_1(7),
    OPTION_2(8),
    OPTION_3(9),
    OPTION_4(10),
    OPTION_5(11),
    OPTION_6(12),
    OPTION_7(13),
    OPTION_8(14),
    OPTION_9(15),

    MENU_1(16),
    MENU_2(17),
    MENU_3(18),
    MENU_4(19),
    MENU_5(20),
    MENU_6(21),
    MENU_7(22),
    MENU_8(23),
    MENU_9(24),
    MENU_10(25),
    MENU_11(26),
    MENU_12(27),

    BUILDING_MENU(28),
    ATTACK(29),
    END_TURN(30),
    TRIBE_PATHS_MENU(31),
    NEXT_UNIT(32),

    ALTERNATE_1(33);

    ActionButton(int value) { this.value = value; }
    public int getValue() { return value; }

    private final int value;

    public static int getOption(ActionButton button) { return button.value - ActionButton.OPTION_0.value; }

    public static ActionButton toOption(int option) {
        int firstVal = ActionButton.OPTION_0.getValue();
        option += firstVal;
        if (firstVal <= option && option <= ActionButton.OPTION_9.getValue()) {
            return ActionButton.values()[option];
        }
        return null;
    }
}
