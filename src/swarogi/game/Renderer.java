package swarogi.game;

import swarogi.common.Configuration;
import swarogi.common.ContentManager;
import swarogi.common.TerrainExtensionInfo;
import swarogi.datamodels.UnitData;
import swarogi.enums.Characteristic;
import swarogi.enums.Direction;
import swarogi.enums.TerrainType;
import swarogi.enums.UnitDirection;
import swarogi.interfaces.Destructible;
import swarogi.interfaces.DestructibleData;
import swarogi.interfaces.PlaceableData;
import swarogi.models.Building;
import swarogi.models.Decoration;
import swarogi.models.Obstacle;
import swarogi.models.Unit;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Renderer {

    private Graphics graphics;
    private GameCamera camera;
    private int tileWidth;
    private int tileHeight;
    private int tileSlant;

    public void startRendering(Graphics graphics, GameCamera camera) {
        this.graphics = graphics;
        this.camera = camera;
        this.tileWidth = Configuration.TILE_WIDTH;
        this.tileHeight = Configuration.TILE_HEIGHT;
        this.tileSlant = Configuration.TILE_SLANT_WIDTH;
    }

    public void endRendering() {
        this.graphics = null;
        this.camera = null;
    }

    public void render(Obstacle obstacle) {
        PlaceableData model = obstacle.getPlaceableData();
        Tile tile = obstacle.getTile();
        Point tileCenter = tile.getCenter();
        tileCenter.x -= camera.x;
        tileCenter.y -= camera.y;
        BufferedImage texture = ContentManager.getModel(model.getTextureName());
        if (texture != null) {
            int textureWidth = (int)(texture.getWidth() * model.getXScale());
            int textureHeight = (int)(texture.getHeight() * model.getYScale());
            graphics.drawImage(texture,
                    tileCenter.x - textureWidth / 2 + model.getXTexturePosition(),
                    tileCenter.y - textureHeight / 2 + model.getYTexturePosition(),
                    textureWidth, textureHeight, null);
        }
    }

    public void render(Decoration decoration) {
        PlaceableData model = decoration.getPlaceableData();
        Tile tile = decoration.getTile();
        Point tileCenter = tile.getCenter();
        tileCenter.x -= camera.x;
        tileCenter.y -= camera.y;
        BufferedImage texture = ContentManager.getModel(model.getTextureName());
        if (texture != null) {
            int textureWidth = (int)(texture.getWidth() * model.getXScale());
            int textureHeight = (int)(texture.getHeight() * model.getYScale());
            graphics.drawImage(texture,
                    tileCenter.x - textureWidth / 2 + model.getXTexturePosition() + decoration.getCustomTranslationX(),
                    tileCenter.y - textureHeight / 2 + model.getYTexturePosition() + decoration.getCustomTranslationY(),
                    textureWidth, textureHeight, null);
        }
    }

    public void render(Unit unit) {
        UnitData model = unit.getUnitData();
        Tile tile = unit.getTile();
        Point tileCenter = tile.getCenter();
        tileCenter.x -= camera.x;
        tileCenter.y -= camera.y;
        String textureName = model.getTextureName();
        BufferedImage texture = ContentManager.getModel(textureName);
        BufferedImage textureBase = unit.getOwner().getTextureBase(textureName);

        if (texture != null && textureBase != null) {
            int textureWidth = (int)(texture.getWidth() * model.getXScale());
            int textureHeight = (int)(texture.getHeight() * model.getYScale());
            int x, y;

            if (unit.getFacingDirection() == UnitDirection.LEFT) {
                x = tileCenter.x - textureWidth / 2 + model.getXTexturePosition() + (int)unit.getCustomTranslationX();
                y = tileCenter.y - textureHeight / 2 + model.getYTexturePosition() + (int)unit.getCustomTranslationY();

                graphics.drawImage(textureBase, x, y, textureWidth, textureHeight, null);
                graphics.drawImage(texture, x, y, textureWidth, textureHeight, null);
            }
            else {
                x = tileCenter.x - textureWidth / 2 - model.getXTexturePosition() + (int)unit.getCustomTranslationX();
                y = tileCenter.y - textureHeight / 2 + model.getYTexturePosition() + (int)unit.getCustomTranslationY();

                graphics.drawImage(textureBase, x + textureWidth, y, -textureWidth, textureHeight, null);
                graphics.drawImage(texture, x + textureWidth, y, -textureWidth, textureHeight, null);
            }

            if (Configuration.areHpBarsVisible) {
                renderHpBar(unit, tileCenter.x + (int)unit.getCustomTranslationX(),
                        y + textureHeight / 2);
            }

            if (unit.hasCharacteristic(Characteristic.Leader)) {
                BufferedImage leaderMark = ContentManager.leaderMark;
                graphics.drawImage(leaderMark,
                        tileCenter.x + (int)unit.getCustomTranslationX() - leaderMark.getWidth() / 2,
                        y + textureHeight / 2 + model.getHpBarPositionY() + Configuration.LEADER_MARK_TRANSLATION_Y
                                - (Configuration.HP_BAR_HEIGHT + leaderMark.getHeight()) / 2,
                        null);
            }
        }
    }

    public void render(Building building) {
        PlaceableData model = building.getPlaceableData();
        Tile tile = building.getTile();
        Point tileCenter = tile.getCenter();
        tileCenter.x -= camera.x;
        tileCenter.y -= camera.y;
        String textureName = model.getTextureName();
        BufferedImage texture = ContentManager.getModel(textureName);
        BufferedImage textureBase = building.getOwner().getTextureBase(textureName);

        if (texture != null && textureBase != null) {
            int textureWidth = (int)(texture.getWidth() * model.getXScale());
            int textureHeight = (int)(texture.getHeight() * model.getYScale());
            int x = tileCenter.x - textureWidth / 2 + model.getXTexturePosition();
            int y = tileCenter.y - textureHeight / 2 + model.getYTexturePosition();

            graphics.drawImage(textureBase, x, y, textureWidth, textureHeight, null);
            graphics.drawImage(texture, x, y, textureWidth, textureHeight, null);

            if (Configuration.areHpBarsVisible) {
                renderHpBar(building, x + textureWidth / 2, y + textureHeight / 2);
            }
        }
    }

    public void render(Tile tile) {
        Point pos = tile.getTopLeft();
        int x = pos.x - camera.x;
        int y = pos.y - camera.y;
        TerrainType tileTerrainType = tile.getTerrainType();
        int tileTerrainPriority = tileTerrainType.getTilingPriority();

        graphics.drawImage(ContentManager.getTerrain(tileTerrainType),
                x, y, tileWidth, tileHeight, null);

        Tile neighbor = tile.getNeighbor(Direction.TOP_LEFT);
        if (neighbor != null) {
            TerrainType neighborTerrainType = neighbor.getTerrainType();
            int neighborTerrainPriority = neighborTerrainType.getTilingPriority();
            if (neighborTerrainPriority < tileTerrainPriority) {
                TerrainExtensionInfo terrainExtensionInfo = ContentManager.getTerrainExtension(tileTerrainType);
                graphics.drawImage(terrainExtensionInfo.texture,
                        x - terrainExtensionInfo.destinationX, y - terrainExtensionInfo.destinationY,
                        x + tileSlant, y + tileHeight / 2,
                        0, 0,
                        terrainExtensionInfo.x1, terrainExtensionInfo.halfY,
                        null);
            }
        }

        neighbor = tile.getNeighbor(Direction.TOP);
        if (neighbor != null) {
            TerrainType neighborTerrainType = neighbor.getTerrainType();
            int neighborTerrainPriority = neighborTerrainType.getTilingPriority();
            if (neighborTerrainPriority < tileTerrainPriority) {
                TerrainExtensionInfo terrainExtensionInfo = ContentManager.getTerrainExtension(tileTerrainType);
                graphics.drawImage(terrainExtensionInfo.texture,
                        x + tileSlant, y - terrainExtensionInfo.destinationY,
                        x + tileWidth - tileSlant, y + tileHeight / 2,
                        terrainExtensionInfo.x1, 0,
                        terrainExtensionInfo.x2, terrainExtensionInfo.halfY,
                        null);
            }
        }

        neighbor = tile.getNeighbor(Direction.TOP_RIGHT);
        if (neighbor != null) {
            TerrainType neighborTerrainType = neighbor.getTerrainType();
            int neighborTerrainPriority = neighborTerrainType.getTilingPriority();
            if (neighborTerrainPriority < tileTerrainPriority) {
                TerrainExtensionInfo terrainExtensionInfo = ContentManager.getTerrainExtension(tileTerrainType);
                graphics.drawImage(terrainExtensionInfo.texture,
                        x + tileWidth - tileSlant, y - terrainExtensionInfo.destinationY,
                        x + tileWidth + terrainExtensionInfo.destinationX, y + tileHeight / 2,
                        terrainExtensionInfo.x2, 0,
                        terrainExtensionInfo.texture.getWidth(), terrainExtensionInfo.halfY,
                        null);
            }
        }

        neighbor = tile.getNeighbor(Direction.BOTTOM_RIGHT);
        if (neighbor != null) {
            TerrainType neighborTerrainType = neighbor.getTerrainType();
            int neighborTerrainPriority = neighborTerrainType.getTilingPriority();
            if (neighborTerrainPriority < tileTerrainPriority) {
                TerrainExtensionInfo terrainExtensionInfo = ContentManager.getTerrainExtension(tileTerrainType);
                graphics.drawImage(terrainExtensionInfo.texture,
                        x + tileWidth - tileSlant, y + tileHeight / 2,
                        x + tileWidth + terrainExtensionInfo.destinationX, y + tileHeight + terrainExtensionInfo.destinationY,
                        terrainExtensionInfo.x2, terrainExtensionInfo.halfY,
                        terrainExtensionInfo.texture.getWidth(), terrainExtensionInfo.texture.getHeight(),
                        null);
            }
        }

        neighbor = tile.getNeighbor(Direction.BOTTOM);
        if (neighbor != null) {
            TerrainType neighborTerrainType = neighbor.getTerrainType();
            int neighborTerrainPriority = neighborTerrainType.getTilingPriority();
            if (neighborTerrainPriority < tileTerrainPriority) {
                TerrainExtensionInfo terrainExtensionInfo = ContentManager.getTerrainExtension(tileTerrainType);
                graphics.drawImage(terrainExtensionInfo.texture,
                        x + tileSlant, y + tileHeight / 2,
                        x + tileWidth - tileSlant, y + tileHeight + terrainExtensionInfo.destinationY,
                        terrainExtensionInfo.x1, terrainExtensionInfo.halfY,
                        terrainExtensionInfo.x2, terrainExtensionInfo.texture.getHeight(),
                        null);
            }
        }

        neighbor = tile.getNeighbor(Direction.BOTTOM_LEFT);
        if (neighbor != null) {
            TerrainType neighborTerrainType = neighbor.getTerrainType();
            int neighborTerrainPriority = neighborTerrainType.getTilingPriority();
            if (neighborTerrainPriority < tileTerrainPriority) {
                TerrainExtensionInfo terrainExtensionInfo = ContentManager.getTerrainExtension(tileTerrainType);
                graphics.drawImage(terrainExtensionInfo.texture,
                        x - terrainExtensionInfo.destinationX, y + tileHeight / 2,
                        x + tileSlant, y + tileHeight + terrainExtensionInfo.destinationY,
                        0, terrainExtensionInfo.halfY,
                        terrainExtensionInfo.x1, terrainExtensionInfo.texture.getHeight(),
                        null);
            }
        }
    }

    private void renderHpBar(Destructible destructible, int x, int y) {
        DestructibleData model = destructible.getDestructibleData();
        int w = model.getHpBarWidth();
        int h = Configuration.HP_BAR_HEIGHT;
        x -= w / 2;
        y += model.getHpBarPositionY() - h / 2;

        graphics.setColor(Configuration.HP_BAR_COLOR_BACKGROUND);
        graphics.fillRect(x - 1, y - 1, w + 2, h + 2);

        float hpPercentage = destructible.getHealth() / destructible.getMaxHealth();
        Color hpColor;
        int hpWidth;

        if (hpPercentage >= 1.0f) {
            hpWidth = w;
            hpColor = Configuration.HP_BAR_COLOR_FULL;
        }
        else if (hpPercentage > 0.5f) {
            hpWidth = (int)(hpPercentage * w);
            float t = 1.0f - (hpPercentage - 0.5f) * 2; // Normalizacja do (0; 1)
            hpColor = blendColors(Configuration.HP_BAR_COLOR_HALF, Configuration.HP_BAR_COLOR_FULL, t);
        }
        else if (hpPercentage > 0) {
            hpWidth = (int)(hpPercentage * w);
            float t = 1.0f - hpPercentage * 2; // Normalizacja do (0; 1)
            hpColor = blendColors(Configuration.HP_BAR_COLOR_EMPTY, Configuration.HP_BAR_COLOR_HALF, t);
        }
        else {
            hpWidth = 0;
            hpColor = Configuration.HP_BAR_COLOR_EMPTY;
        }

        graphics.setColor(hpColor);
        graphics.fillRect(x, y, hpWidth, h);
    }

    private static Color blendColors(Color c1, Color c2, float weight) {
        float w2 = 1.0f - weight;
        float r = c1.getRed() * weight + c2.getRed() * w2;
        float g = c1.getGreen() * weight + c2.getGreen() * w2;
        float b = c1.getBlue() * weight + c2.getBlue() * w2;
        return new Color((int)r, (int)g, (int)b);
    }
}
