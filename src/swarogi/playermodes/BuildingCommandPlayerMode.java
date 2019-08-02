package swarogi.playermodes;

import swarogi.actions.CreateUnitAction;
import swarogi.common.ContentManager;
import swarogi.datamodels.BuildingData;
import swarogi.datamodels.UnitData;
import swarogi.enums.ActionButton;
import swarogi.enums.TileSelectionTag;
import swarogi.game.GameCamera;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.interfaces.ControlsProvider;
import swarogi.interfaces.Placeable;
import swarogi.interfaces.PlayerModeChangeListener;
import swarogi.interfaces.PlayerUnit;
import swarogi.models.Building;
import swarogi.models.Player;
import swarogi.models.Unit;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BuildingCommandPlayerMode extends SelectionPlayerMode {

    private Building building;
    private List<Tile> buildingTiles;

    public BuildingCommandPlayerMode(Player player, PlayerModeChangeListener listener, GameMap map, Building building) {
        super(player, listener, map);
        this.building = building;
        this.buildingTiles = building.getAllTiles();
    }

    @Override
    public void update() {

        ControlsProvider controlsProvider = getControls();

        if (getControls().isButtonDown(ActionButton.TRIBE_PATHS_MENU)) {
            changePlayerMode(new TribePathsPlayerMode(player, getListener(), this));
            return;
        }

        if (getControls().isButtonDown(ActionButton.OPTION_9) && building.getUnitsInside().size() > 0) {
            building.removeUnit(building.getUnitsInside().get(0));
        }

        if (building != null && building.isReady()) {
            ActionButton selectedButton = controlsProvider.getFirstSelectedOption();
            if (selectedButton != null) {
                List<UnitData> createdUnits = building.getModel().getCreatedUnits();
                int option = ActionButton.getOption(selectedButton);
                if (option < createdUnits.size()) {
                    UnitData selectedUnit = createdUnits.get(option);
                    if (player.areRequirementsMet(selectedUnit)) {
                        getListener().addAction(new CreateUnitAction(selectedUnit, building, getMap(), player));
                    }
                }
            }
        }

        if (controlsProvider.isButtonDown(ActionButton.CANCEL)) {
            changePlayerMode(new SelectionPlayerMode(getPlayer(), getListener(), getMap()));
        }

        super.update();
    }

    @Override
    public void renderSelection(Graphics g, GameCamera camera) {

        for (Tile tile : buildingTiles) {
            renderTile(g, tile, camera, TileSelectionTag.SELECTED);
        }

        List<Tile> hoveredTiles = getHoveredTiles();

        if (hoveredTiles.size() > 0) {
            Placeable hoveredPlaceable = getHoveredPlaceable();
            TileSelectionTag tileSelectionTag = TileSelectionTag.HOVER_NEUTRAL;

            // Ustal kolor wskazywanego obiektu / pola
            if (hoveredPlaceable != null) {
                tileSelectionTag = getPlaceableSelectionTag(player, hoveredPlaceable, TileSelectionTag.INACTIVE_POSITIVE,
                        TileSelectionTag.INACTIVE_ALLIED, TileSelectionTag.INACTIVE_NEGATIVE, TileSelectionTag.HOVER_NEUTRAL);
            }

            // Narysuj tylko te pola, które nie należą do budynku
            for (Tile tile : hoveredTiles) {
                if (!buildingTiles.contains(tile)) {
                    renderTile(g, tile, camera, tileSelectionTag);
                }
            }
        }
    }

    @Override
    public void renderGui(Graphics g, Dimension dimension, Font font) {
        renderTextBack(g, dimension);

        int marginLeft = 40;
        int y = dimension.height - ContentManager.bottomTextShadow.getHeight() + 40;
        int lineHeight = 20;

        g.setColor(Color.white);

        g.drawString( building.isReady() ? building.getName() : building.getName() + " (w budowie)", marginLeft, y);
        g.drawString("HP: " + Integer.toString((int)building.getHealth()) + "/" + Integer.toString(building.getMaxHealth()), marginLeft, y + lineHeight);
        g.drawString("Pancerz (" + building.getModel().getBaseArmorType().getName() + "): " + Integer.toString(building.getDefense()), marginLeft, y + 2 * lineHeight);

        int unitCapacity = building.getModel().getUnitsCapacity();
        if (unitCapacity > 0) {
            List<Unit> unitsInside = building.getUnitsInside();
            List<String> unitNames = new ArrayList<>();
            int i;
            for (i = 0; i < unitsInside.size(); ++i) {
                unitNames.add(unitsInside.get(i).getName());
            }
            for (; i < unitCapacity; ++i) {
                unitNames.add("brak");
            }
            g.drawString("W środku: [" + String.join(", ", unitNames) + "]", marginLeft, y + 3 * lineHeight);

            if (unitsInside.size() > 0) {
                marginLeft += 200;
                g.drawString("[0] Usuń jednostkę", marginLeft, y);
            }
        }

        List<UnitData> createdUnits = building.getModel().getCreatedUnits();
        int n = createdUnits.size();

        if (building.isReady() && n > 0) {
            marginLeft += 200;
            int dx = (dimension.width - marginLeft) / n;

            for (int i = 0; i < n; ++i) {
                UnitData unit = createdUnits.get(i);

                if (unit.getRequiredTribeLevel() > player.getTribeLevel()) {
                    g.setColor(Color.black);
                    g.drawString(displayString(unit)
                            + "\n(wym. poz. " + Integer.toString(unit.getRequiredTribeLevel()) + ")", i * dx + marginLeft, y);
                } else if (player.getCommandPoints() == 0) {
                    g.setColor(Color.black);
                    g.drawString(displayString(unit), i * dx + marginLeft, y);
                } else if (player.areRequirementsMet(unit)) {
                    g.setColor(Color.white);
                    g.drawString("[" + Integer.toString(i + 1) + "] " + displayString(unit), i * dx + marginLeft, y);
                } else {
                    g.setColor(Color.red);
                    g.drawString(displayString(unit), i * dx + marginLeft, y);
                }
            }
        }
    }

    private String displayString(UnitData unit) {
        return unit.getName()
                + " - Ż: " + Integer.toString(unit.getFoodCost())
                + " | D: " + Integer.toString(unit.getWoodCost())
                + " | A: " + Integer.toString(unit.getRequiredArmySize());
    }
}
