package swarogi.common;

import swarogi.interfaces.WindowSizeProvider;

import java.awt.*;

public final class WindowSize {
    public static Dimension getSize() {
        return sizeProvider.getSize();
    }

    public static void setWindowSizeProvider(WindowSizeProvider provider) {
        sizeProvider = provider;
    }

    private static WindowSizeProvider sizeProvider;
}
