import swarogi.common.Configuration;
import swarogi.game.Game;

public class Main {

    public static void main(String[] args) {

        int preferredFrameDuration = 1000 / Configuration.FPS;
        long initialFrame;
        long frameDuration;

        Game game = new Game();
        if (game.initialize()) {
            while (game.isRunning()) {
                initialFrame = System.currentTimeMillis();

                game.handleEvents();
                game.update(initialFrame);
                game.render();

                frameDuration = System.currentTimeMillis() - initialFrame;

                if (frameDuration < preferredFrameDuration) {
                    try {
                        Thread.sleep(preferredFrameDuration - frameDuration);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            game.dispose();
        }
    }
}
