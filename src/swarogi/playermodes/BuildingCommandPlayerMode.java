package swarogi.playermodes;

import swarogi.actions.CreateUnitAction;
import swarogi.common.Configuration;
import swarogi.common.ContentManager;
import swarogi.data.Database;
import swarogi.datamodels.BuildingData;
import swarogi.datamodels.SkillData;
import swarogi.datamodels.UnitData;
import swarogi.enums.ActionButton;
import swarogi.enums.HorizontalAlign;
import swarogi.enums.TileSelectionTag;
import swarogi.enums.VerticalAlign;
import swarogi.game.GameCamera;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.gui.DiamondIcon;
import swarogi.gui.Icon;
import swarogi.gui.RenderingHelper;
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
    private Icon removeUnitIcon;

    public BuildingCommandPlayerMode(Player player, PlayerModeChangeListener listener, GameMap map, Building building) {
        super(player, listener, map);
        this.building = building;
        this.buildingTiles = building.getAllTiles();
        setIcons();
    }

    @Override
    public void update() {
        ControlsProvider controlsProvider = getControls();

        if (getControls().isButtonDown(ActionButton.OPTION_9) && building.getUnitsInside().size() > 0) {
            building.removeUnit(building.getUnitsInside().get(0));
        }

        super.update();
    }

    @Override
    public void renderSelection(Graphics g, GameCamera camera) {

        for (Tile tile : buildingTiles) {
            renderTile(g, tile, camera, TileSelectionTag.SELECTED);
        }

        if (!isGuiInteraction()) {
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
    }

    @Override
    public void renderGui(Graphics g, Dimension size, Font font) {

        RenderingHelper.drawSummaryBox(g, size);
        RenderingHelper.drawBuildingSummary(g, size, building);
        RenderingHelper.drawTextArea(g, size, getText());
        RenderingHelper.drawIcons(g, getIcons(), size);
    }

    private void setIcons() {
        int margin = Configuration.ICON_MARGIN;
        int dx = margin + Configuration.ICON_SIZE;

        Point pos = RenderingHelper.getCancelIconPosition();
        // Wstecz
        {
            Icon icon = new DiamondIcon();
            icon.hAlign = HorizontalAlign.RIGHT;
            icon.vAlign = VerticalAlign.TOP;
            icon.x = pos.x;
            icon.y = pos.y;
            icon.actionButton = ActionButton.CANCEL;
            icon.textureKey = Configuration.CANCEL_ICON_NAME;
            icon.hoverText = "Wstecz [PPM]";
            icon.clickAction = this::exitMode;
            icon.hoverAction = () -> setText(icon.hoverText);
            icon.unhoverAction = () -> setText(null);
            addIcon(icon);
        }

        if (!building.isReady()) { return; }

        pos = RenderingHelper.getRegularIconPosition(true);
        int x = pos.x;
        int y = pos.y;
        int i = 0;

        if (building.getModel().getUnitsCapacity() > 0) {
            // Wyjdź z budynku
            Icon icon = new DiamondIcon();
            icon.hAlign = HorizontalAlign.LEFT;
            icon.vAlign = VerticalAlign.BOTTOM;
            icon.x = pos.x;
            icon.y = pos.y;
            icon.actionButton = ActionButton.OPTION_9;
            icon.textureKey = Configuration.EXIT_BUILDING_ICON_NAME;
            icon.hoverText = "Usuń jednostkę z budynku [0]";
            icon.clickAction = this::removeUnit;
            icon.lockedFlag = building.getUnitsInside().size() == 0;
            icon.hoverAction = () -> setText(icon.hoverText);
            icon.unhoverAction = () -> setText(null);
            addIcon(icon);
            removeUnitIcon = icon;
            i++;
        }

        // Tworzone jednostki
        for (UnitData unit : building.getModel().getCreatedUnits()) {
            Icon icon = new DiamondIcon();
            icon.hAlign = HorizontalAlign.LEFT;
            icon.vAlign = VerticalAlign.BOTTOM;
            icon.x = x + i * dx;
            icon.y = y;
            icon.actionButton = ActionButton.toOption(i);
            icon.textureKey = unit.getTextureName();
            icon.clickAction = () -> createUnit(unit);
            icon.hoverAction = () -> setText(icon.hoverText);
            icon.unhoverAction = () -> setText(null);
            icon.value = unit;
            addIcon(icon);
            ++i;
        }

        refreshUnitIcons(null);
    }

    private void refreshUnitIcons(UnitData createdUnit) {
        List<Icon> icons = getIcons();

        int food = player.getFood();
        int wood = player.getWood();
        int armySize = player.getArmySize();
        int commandPoints = player.getCommandPoints();
        int armyCapacity = player.getArmyCapacity();

        if (createdUnit != null) {
            food -= createdUnit.getFoodCost();
            wood -= createdUnit.getWoodCost();
            armySize += createdUnit.getRequiredArmySize();
            commandPoints -= Configuration.UNIT_CREATION_COMMAND_POINTS_COST;
        }

        int i = 0;
        for (Icon icon : icons) {
            if (!(icon.value instanceof UnitData)) { continue; }

            ++i;
            UnitData unit = (UnitData) icon.value;
            if (unit.getRequiredTribeLevel() > player.getTribeLevel()) {
                icon.lockedFlag = true;
                icon.hoverText = unit.getName() + " (wymagany poziom " + Integer.toString(unit.getRequiredTribeLevel()) + ") " + unit.getDescription();
            } else if (commandPoints < Configuration.UNIT_CREATION_COMMAND_POINTS_COST) {
                icon.lockedFlag = true;
                icon.hoverText = unit.getName() + " (Żywność: " + Integer.toString(unit.getFoodCost())
                        + " Drewno: " + Integer.toString(unit.getWoodCost())
                        + " Armia: " + Integer.toString(unit.getRequiredArmySize())
                        + ") " + unit.getDescription();
            } else if (unit.getFoodCost() <= food && unit.getWoodCost() <= wood && armySize + unit.getRequiredArmySize() <= armyCapacity) {
                icon.hoverText = unit.getName() + " [" + i
                        + "] (Żywność: " + Integer.toString(unit.getFoodCost())
                        + " Drewno: " + Integer.toString(unit.getWoodCost())
                        + " Armia: " + Integer.toString(unit.getRequiredArmySize())
                        + ") " + unit.getDescription();
            } else {
                icon.noFundsFlag = true;
                icon.hoverText = unit.getName() + " (Żywność: " + Integer.toString(unit.getFoodCost())
                        + " Drewno: " + Integer.toString(unit.getWoodCost())
                        + " Armia: " + Integer.toString(unit.getRequiredArmySize())
                        + ") " + unit.getDescription();
            }
        }
    }

    private void createUnit(UnitData unitData) {
        if (player.areRequirementsMet(unitData)) {
            getListener().addAction(new CreateUnitAction(unitData, building, getMap(), player));
            refreshUnitIcons(unitData);
        }
    }

    private void exitMode() {
        changePlayerMode(new SelectionPlayerMode(getPlayer(), getListener(), getMap()));
    }

    private void removeUnit() {
        // TODO: To raczej powinno być robione akcją.
        List<Unit> units = building.getUnitsInside();
        if (units.size() > 0) {
            if (building.removeUnit(units.get(0)) && removeUnitIcon != null && units.size() == 0) {
                removeUnitIcon.lockedFlag = building.getUnitsInside().size() == 0;
            }
        }
    }
}
