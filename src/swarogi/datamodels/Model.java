package swarogi.datamodels;

import swarogi.enums.ObjectState;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Model {
    private Map<ObjectState, BufferedImage> textures;

    public Model() {
        textures = new HashMap<>();
    }

    public Model(BufferedImage defaultTexture) {
        textures = new HashMap<>();
        textures.put(ObjectState.NORMAL, defaultTexture);
    }

    public void addState(ObjectState state, BufferedImage texture) {
        textures.put(state, texture);
    }

    public BufferedImage getTexture(ObjectState state) {
        if (textures.containsKey(state)) {
            return textures.get(state);
        }
        return textures.getOrDefault(ObjectState.NORMAL, null);
    }
}
