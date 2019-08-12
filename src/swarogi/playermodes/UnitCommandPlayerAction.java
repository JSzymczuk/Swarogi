package swarogi.playermodes;

import swarogi.actions.MovementAction;
import swarogi.actions.SkillAction;
import swarogi.common.Configuration;
import swarogi.common.ContentManager;
import swarogi.common.WindowSize;
import swarogi.data.Database;
import swarogi.datamodels.SkillData;
import swarogi.engine.Pathfinding;
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
import swarogi.interfaces.*;
import swarogi.models.Player;
import swarogi.models.Unit;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UnitCommandPlayerAction extends SelectionPlayerMode implements TargetManagableMode {

    private Unit unit;
    private Pathfinding pathfinding;
    private Set<Tile> pathfindingTiles;
    private TargetManager targetManager;
    private List<SkillData> skills;

    public UnitCommandPlayerAction(Player player, PlayerModeChangeListener listener, GameMap map, Unit unit) {
        super(player, listener, map);
        this.unit = unit;
        this.pathfinding = new Pathfinding(unit.getTile(), unit.hasActionPoints(Configuration.MOVEMENT_ACTION_POINTS_COST) ? unit.getSteps() : 0);
        this.pathfindingTiles = pathfinding.getAccessibleTiles();
        this.targetManager = new TargetManager(this, unit, pathfinding);
        this.skills = unit.getUnitData().getBaseSkills();
        setIcons();
    }

    @Override
    public void update() {

        if (checkGuiInteraction()) { return; }

        updateHoverable();

        // Potwierdź (wykonaj ruch)
        ControlsProvider controls = getControls();
        if (controls.isButtonDown(ActionButton.CONFIRM)) {
            Placeable hovered = getHoveredPlaceable();
            if (unit.hasActionPoints(Configuration.MOVEMENT_ACTION_POINTS_COST)) {

                if (hovered == null) { // Czyli nie wskazujemy na jednostkę
                    List<Tile> hoveredTiles = getHoveredTiles();

                    // Jeśli wskazujemy na puste pole
                    if (hoveredTiles != null && hoveredTiles.size() == 1) {
                        Tile tile = hoveredTiles.get(0);
                        if (pathfinding.canAccess(tile)) {
                            unit.setPath(pathfinding.getPathTo(tile));
                            getListener().addAction(new MovementAction(unit, true)); // Zużyj punkt akcji
                            exitMode();
                        }
                    }
                } else {
                    if (hovered instanceof PlayerUnit) {
                        Player unitOwner = ((PlayerUnit) hovered).getOwner();
                        if (player == unitOwner) {
                            onSelect();
                        } else if (player.getTeam() != unitOwner.getTeam()) {
                            targetManager.tryAttack((Destructible) hovered);
                        }
                    } else if (hovered instanceof Destructible) {
                        targetManager.tryAttack((Destructible) hovered);
                    }
                }
            }
            else {
                if (hovered == null) { // Czyli nie wskazujemy na jednostkę
                    exitMode();
                }
                else if (hovered instanceof PlayerUnit) {
                    onSelect();
                }
            }
        }
    }

    @Override
    public void renderSelection(Graphics g, GameCamera camera) {

        HashMap<Tile, TileSelectionTag> tilesToDraw = new HashMap<>();

        // Dodaj wszystkie pola, na które można się przemieścić
        for (Tile tile : pathfindingTiles) {
            tilesToDraw.put(tile, TileSelectionTag.HOVER_NEUTRAL);
        }

        if (!isGuiInteraction()) {
            List<Tile> hoveredTiles = getHoveredTiles();

            // Wskazywana jest jednostka:
            Placeable hoveredPlaceable = getHoveredPlaceable();
            if (hoveredPlaceable != null) {
                TileSelectionTag tileSelectionTag = getPlaceableSelectionTag(player, hoveredPlaceable, TileSelectionTag.INACTIVE_POSITIVE,
                        TileSelectionTag.INACTIVE_ALLIED, TileSelectionTag.INACTIVE_NEGATIVE, TileSelectionTag.HOVER_NEUTRAL);
                for (Tile tile : hoveredTiles) {
                    tilesToDraw.put(tile, tileSelectionTag);
                }
            }

            // Wskazywane jest pole:
            else {
                for (Tile tile : hoveredTiles) {
                    if (pathfindingTiles.contains(tile)) {
                        tilesToDraw.put(tile, TileSelectionTag.ACTIVE_POSITIVE);
                    } else {
                        tilesToDraw.put(tile, TileSelectionTag.INACTIVE_POSITIVE);
                    }
                }
            }

            // Dodaj wrogie cele ataków
            int playerTeam = player.getTeam();
            for (Destructible target : targetManager.getTargets()) {
                if (target instanceof PlayerUnit && ((PlayerUnit)target).getOwner().getTeam() != playerTeam) {
                    if (target == hoveredPlaceable) {
                        for (Tile tile : targetManager.getTargetTiles(target)) {
                            tilesToDraw.put(tile, TileSelectionTag.ACTIVE_NEGATIVE);
                        }
                    }
                    else {
                        for (Tile tile : targetManager.getTargetTiles(target)) {
                            tilesToDraw.put(tile, TileSelectionTag.INACTIVE_NEGATIVE);
                        }
                    }
                }
            }
        }
        else {
            // Dodaj wrogie cele ataków
            int playerTeam = player.getTeam();
            for (Destructible target : targetManager.getTargets()) {
                if (target instanceof PlayerUnit && ((PlayerUnit)target).getOwner().getTeam() != playerTeam) {
                    for (Tile tile : targetManager.getTargetTiles(target)) {
                        tilesToDraw.put(tile, TileSelectionTag.INACTIVE_NEGATIVE);
                    }
                }
            }
        }


        // Dodaj obecną jednostkę
        tilesToDraw.put(unit.getTile(), TileSelectionTag.SELECTED);

        // Narysuj wszystko
        for (Tile tile : tilesToDraw.keySet()) {
            renderTile(g, tile, camera, tilesToDraw.get(tile));
        }
    }

    @Override
    public void renderGui(Graphics g, Dimension size, Font font) {
        RenderingHelper.drawSummaryBox(g, size);
        RenderingHelper.drawUnitSummary(g, size, unit);
        RenderingHelper.drawTextArea(g, size, getText());
        RenderingHelper.drawIcons(g, getIcons(), size);
    }

    private void setIcons() {

        int dx = Configuration.ICON_MARGIN + Configuration.ICON_SIZE;

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

        // Następna jednostka
        {
            Icon icon = new DiamondIcon();
            icon.hAlign = HorizontalAlign.RIGHT;
            icon.vAlign = VerticalAlign.TOP;
            icon.x = pos.x + dx;
            icon.y = pos.y;
            icon.actionButton = ActionButton.NEXT_UNIT;
            icon.textureKey = Configuration.NEXT_UNIT_ICON_NAME;
            icon.hoverText = "Następna jednostka [N]";
            icon.clickAction = this::changeUnit;
            icon.hoverAction = () -> setText(icon.hoverText);
            icon.unhoverAction = () -> setText(null);
            addIcon(icon);
        }

        pos = RenderingHelper.getRegularIconPosition(true);
        int x = pos.x;
        int y = pos.y;

        // Atak
        if (hasAttackMode()) {
            Icon icon = new DiamondIcon();
            icon.hAlign = HorizontalAlign.LEFT;
            icon.vAlign = VerticalAlign.BOTTOM;
            icon.x = x;
            icon.y = y;
            icon.actionButton = ActionButton.ATTACK;
            icon.textureKey = Configuration.ATTACK_ACTION_ICON_NAME;
            icon.lockedFlag = !canEnterAttackMode();
            icon.hoverText = "Atak [A]";
            icon.clickAction = this::enterAttackMode;
            icon.hoverAction = () -> setText(icon.hoverText);
            icon.unhoverAction = () -> setText(null);
            addIcon(icon);
            x += dx;
        }

        // Budowa
        if (hasBuildMode()) {
            Icon icon = new DiamondIcon();
            icon.hAlign = HorizontalAlign.LEFT;
            icon.vAlign = VerticalAlign.BOTTOM;
            icon.x = x;
            icon.y = y;
            icon.actionButton = ActionButton.BUILDING_MENU;
            icon.textureKey = Configuration.BUILD_ACTION_ICON_NAME;
            icon.lockedFlag = !canEnterBuildMode();
            icon.hoverText = "Budowa [B]";
            icon.clickAction = this::enterBuildMode;
            icon.hoverAction = () -> setText(icon.hoverText);
            icon.unhoverAction = () -> setText(null);
            addIcon(icon);
            x += dx;
        }

        // Zdolności
        for (int i = 0; i < skills.size(); ++i) {
            SkillData skill = skills.get(i);

            Icon icon = new DiamondIcon();
            icon.hAlign = HorizontalAlign.LEFT;
            icon.vAlign = VerticalAlign.BOTTOM;
            icon.x = x + i * dx;
            icon.y = y;
            icon.actionButton = ActionButton.toOption(i);
            icon.textureKey = skill.getIconName();

            // Czy gracz ma niezbędne rozwinięcia
            if (player.canUseSkill(skill)) {
                // Czy jednostka może wykonać akcję
                if (unit.hasActionPoints(Configuration.SKILL_ACTION_POINTS_COST)) {
                    // Czy minął czas pomiędzy użyciami
                    if (unit.isSkillReady(skill)) {
                        // Czy można pokryć koszt
                        if (player.hasFood(skill.getFoodCost()) && player.hasCommandPoints(skill.getCommandPoints())) {
                            icon.hoverText = skill.getName() + " [" + Integer.toString(i + 1) + "] (koszt żywności: "
                                    + skill.getFoodCost() + ", punkty akcji: "
                                    + skill.getCommandPoints() + ") - " + skill.getDescription();

                            icon.clickAction = () -> enterSkillMode(skill);
                        } else {
                            icon.noFundsFlag = true;
                            icon.hoverText = skill.getName() + " (koszt żywności: " + skill.getFoodCost() + ", punkty akcji: "
                                    + skill.getCommandPoints() + ") - " + skill.getDescription();
                        }
                    } else {
                        icon.lockedFlag = true;
                        icon.hoverText = skill.getName() + " (do ponownego użycia: " + Integer.toString(unit.getSkillCooldown(skill)) + ") - " + skill.getDescription();
                    }
                } else {
                    icon.lockedFlag = true;
                    icon.hoverText = skill.getName() + " (koszt żywności: " + skill.getFoodCost() + ", punkty akcji: "
                            + skill.getCommandPoints() + ") - " + skill.getDescription();
                }

            } else {
                icon.lockedFlag = true;
                icon.hoverText = skill.getName() + " (wymagane rozwinięcie: " + Database.TribePaths.get(skill.getRequiredPath()).getName()
                        + " " + skill.getRequiredPathLevel() + ") - " + skill.getDescription();
            }

            icon.hoverAction = () -> setText(icon.hoverText);
            icon.unhoverAction = () -> setText(null);

            addIcon(icon);
        }
    }


    // TODO: Ale że jak to java nie ma pakietowych interfejsów?!
    @Override
    public PlayerModeChangeListener getListener() { return super.getListener(); }
    @Override
    public GameMap getMap() { return super.getMap(); }
    @Override
    public Player getPlayer() { return super.getPlayer(); }
    @Override
    public void changePlayerMode(PlayerMode playerMode) { super.changePlayerMode(playerMode); }


    private boolean hasAttackMode() {
        return unit.getAttacks().size() > 0;
    }

    private boolean hasBuildMode() {
        return unit.getUnitData().getCreatedBuildings().size() > 0;
    }

    private boolean canEnterAttackMode() {
        return unit.hasActionPoints(Configuration.ATTACK_ACTION_POINTS_COST)
                && player.hasCommandPoints(Configuration.ATTACK_COMMAND_POINTS_COST);
    }

    private boolean canEnterBuildMode() {
        return unit.hasActionPoints(Configuration.BUILD_ACTION_POINTS_COST)
                && player.hasCommandPoints(Configuration.BUILDING_COMMAND_POINTS_COST);
    }

    private void enterBuildMode() {
        if (hasBuildMode() && canEnterBuildMode()) {
            changePlayerMode(new BuildingPlayerMode(getPlayer(), getListener(), getMap(), unit));
        }
    }

    private void enterAttackMode() {
        if (hasAttackMode() && canEnterAttackMode()) {
            changePlayerMode(new AttackPlayerMode(getPlayer(), getListener(), unit, getMap()));
        }
    }

    private void enterSkillMode(SkillData skill) {
        if (player.hasFood(skill.getFoodCost()) && player.hasCommandPoints(skill.getCommandPoints())
                && player.canUseSkill(skill) && unit.isSkillReady(skill)
                && unit.hasActionPoints(Configuration.SKILL_ACTION_POINTS_COST)) {

            // Jeśli akcja nie wymaga wyboru celu, wykonaj ją natychmiast
            if (skill.isAutoUse()) {
                getListener().addAction(new SkillAction(unit, null, skill, getMap()));
                changePlayerMode(new SelectionPlayerMode(player, getListener(), getMap()));
            }
            // W przeciwnym przypadku przejdź do trybu wyboru celu
            else {
                changePlayerMode(new SkillUsePlayerMode(player, getListener(), getMap(), unit, skill));
            }
        }
    }

    private void changeUnit() {
        List<Unit> units = player.getUnits().stream()
                .filter(u -> u.hasActionPoints(1) && !u.isInsideBuilding())
                .collect(Collectors.toList());
        if (units.size() > 0) {
            int idx = 0;
            if (units.contains(unit)) {
                idx = units.indexOf(unit);
                if (idx == units.size() - 1) { idx = 0; }
                else { ++idx; }
            }
            changePlayerMode(new UnitCommandPlayerAction(player, getListener(), getMap(), units.get(idx)));
        }
    }

    private void exitMode() {
        changePlayerMode(new SelectionPlayerMode(getPlayer(), getListener(), getMap()));
    }
}
