package swarogi.interfaces;

import swarogi.playermodes.PlayerMode;

public interface PlayerModeChangeListener {
    void onPlayerModeChanged(PlayerMode playerMode);
    void addAction(Action action);
}
