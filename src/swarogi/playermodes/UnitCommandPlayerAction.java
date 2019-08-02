package swarogi.playermodes;

import swarogi.actions.MovementAction;
import swarogi.actions.SkillAction;
import swarogi.common.Configuration;
import swarogi.common.ContentManager;
import swarogi.data.Database;
import swarogi.datamodels.AttackData;
import swarogi.datamodels.EffectData;
import swarogi.datamodels.SkillData;
import swarogi.engine.Pathfinding;
import swarogi.enums.ActionButton;
import swarogi.enums.TileSelectionTag;
import swarogi.game.GameCamera;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.interfaces.*;
import swarogi.models.Player;
import swarogi.models.Unit;

import javax.xml.crypto.Data;
import java.awt.*;
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
    }

    @Override
    public void update() {

        updateHoverable();

        ControlsProvider controlsProvider = getControls();

        if (getControls().isButtonDown(ActionButton.TRIBE_PATHS_MENU)) {
            changePlayerMode(new TribePathsPlayerMode(player, getListener(), this));
            return;
        }

        // Zmień na następną jednostkę
        if (getControls().isButtonDown(ActionButton.NEXT_UNIT)) {
            List<Unit> units = player.getUnits().stream().filter(u -> u.hasActionPoints(1)).collect(Collectors.toList());
            if (units.size() > 0) {
                int idx = 0;
                if (units.contains(unit)) {
                    idx = units.indexOf(unit);
                    if (idx == units.size() - 1) { idx = 0; }
                    else { ++idx; }
                }
                changePlayerMode(new UnitCommandPlayerAction(player, getListener(), getMap(), units.get(idx)));
                return;
            }
        }

        // Wybrano akcję
        ActionButton optionButton = controlsProvider.getFirstSelectedOption();
        if (optionButton != null) {
            int option = ActionButton.getOption(optionButton);
            if (option < skills.size()) {
                SkillData skill = skills.get(option);
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
                        changePlayerMode(new SkillUsePlayerMode(player, getListener(), getMap(), unit, skills.get(option)));
                    }
                    return;
                }
            }
        }

        // Anuluj (odznacz jednostkę)
        if (controlsProvider.isButtonDown(ActionButton.CANCEL)) {
            changePlayerMode(new SelectionPlayerMode(getPlayer(), getListener(), getMap()));
            return;
        }

        // Potwierdź (wykonaj ruch)
        if (controlsProvider.isButtonDown(ActionButton.CONFIRM)) {
            if (unit.hasActionPoints(Configuration.MOVEMENT_ACTION_POINTS_COST)) {
                Placeable hovered = getHoveredPlaceable();

                if (hovered == null) { // Czyli nie wskazujemy na jednostkę
                    List<Tile> hoveredTiles = getHoveredTiles();

                    // Jeśli wskazujemy na puste pole
                    if (hoveredTiles != null && hoveredTiles.size() == 1) {
                        Tile tile = hoveredTiles.get(0);
                        if (pathfinding.canAccess(tile)) {
                            unit.setPath(pathfinding.getPathTo(tile));
                            getListener().addAction(new MovementAction(unit, true)); // Zużyj punkt akcji
                            changePlayerMode(new SelectionPlayerMode(getPlayer(), getListener(), getMap()));
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
        }

        // Włącz tryb ataku
        else if (!unit.isMoving() && controlsProvider.isButtonDown(ActionButton.ATTACK)) {
                // && unit.getUnitData().getAttacksData()) {
            if (unit.hasActionPoints(Configuration.ATTACK_ACTION_POINTS_COST)) {
                changePlayerMode(new AttackPlayerMode(getPlayer(), getListener(), unit, getMap()));
            }
        }

        // Włącz tryb budowy
        else if (!unit.isMoving() && controlsProvider.isButtonDown(ActionButton.BUILDING_MENU)
                && unit.getUnitData().getCreatedBuildings().size() > 0) {
            if (unit.hasActionPoints(Configuration.BUILD_ACTION_POINTS_COST)) {
                changePlayerMode(new BuildingPlayerMode(getPlayer(), getListener(), getMap(), unit));
            }
        }
    }

    @Override
    public void renderSelection(Graphics g, GameCamera camera) {

        List<Tile> hoveredTiles = getHoveredTiles();

        HashMap<Tile, TileSelectionTag> tilesToDraw = new HashMap<>();

        // Dodaj wszystkie pola, na które można się przemieścić
        for (Tile tile : pathfindingTiles) {
            tilesToDraw.put(tile, TileSelectionTag.HOVER_NEUTRAL);
        }

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
                }
                else {
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

        // Dodaj obecną jednostkę
        tilesToDraw.put(unit.getTile(), TileSelectionTag.SELECTED);

        // Narysuj wszystko
        for (Tile tile : tilesToDraw.keySet()) {
            renderTile(g, tile, camera, tilesToDraw.get(tile));
        }
    }

    @Override
    public void renderGui(Graphics g, Dimension size, Font font) {
        renderTextBack(g, size);

        int marginLeft = 40;
        int y = size.height - ContentManager.bottomTextShadow.getHeight() + 40;
        int lineHeight = 20;

        g.setColor(Color.white);

        g.drawString(unit.hasActionPoints(1) ? unit.isConstructingBuilding() ? unit.getName() + " (buduje)" : unit.getName() : unit.getName() + " (wykonano akcję)", marginLeft, y);
        g.drawString("HP: " + Integer.toString((int)unit.getHealth()) + "/" + Integer.toString(unit.getMaxHealth()), marginLeft, y + lineHeight);
        g.drawString("Pancerz (" + unit.getArmorType().getName() + "): " + Integer.toString(unit.getDefense()), marginLeft, y + 2 * lineHeight);
        g.drawString("Kroki: " + Integer.toString(unit.getSteps()), marginLeft, y + 3 * lineHeight);

        int i = 4;
        for (AttackData attack : unit.getAttacks()) {
            Point damageRange = unit.getDamageForAttack(attack);
            g.drawString("Atak (" + attack.getAttackType().getName() + ", " + attack.getDamageType().getName() + "): "
                    + Integer.toString(damageRange.x) + " - " + Integer.toString(damageRange.y), marginLeft, y + i * lineHeight);
            ++i;
        }

        if (unit.getEffects().size() > 0) {
            g.drawString("Efekty: " +
                    String.join(", ", unit.getEffects().stream().map(EffectData::getName).collect(Collectors.toList())),
                    marginLeft, y + i * lineHeight);
        }

        if (skills.size() > 0) {
            marginLeft = 240;
            int dx = (size.width - marginLeft) / skills.size();
            for (i = 0; i < skills.size(); ++i) {
                SkillData skill = skills.get(i);

                // Czy gracz ma niezbędne rozwinięcia
                if (player.canUseSkill(skill)) {
                    // Czy jednostka może wykonać akcję
                    if (unit.hasActionPoints(Configuration.SKILL_ACTION_POINTS_COST)) {
                        // Czy minął czas pomiędzy użyciami
                        if (unit.isSkillReady(skill)) {
                            // Czy można pokryć koszt
                            if (player.hasFood(skill.getFoodCost()) && player.hasCommandPoints(skill.getCommandPoints())) {
                                g.setColor(Color.white);
                                g.drawString("[" + Integer.toString(i + 1) + "] " + skill.getName() + "(Ż: " + skill.getFoodCost() + ", A: " + skill.getCommandPoints() + ")", i * dx + marginLeft, y);
                            } else {
                                g.setColor(Color.red);
                                g.drawString(skill.getName() + " (Ż: " + skill.getFoodCost() + ", A: " + skill.getCommandPoints() + ")", i * dx + marginLeft, y);
                            }
                        }
                        else {
                            g.setColor(Color.black);
                            g.drawString(skill.getName(), i * dx + marginLeft, y);
                            g.drawString("(do ponownego użycia: " + Integer.toString(unit.getSkillCooldown(skill)) + ")", i * dx + marginLeft, y + lineHeight);
                        }
                    }
                    else {
                        g.setColor(Color.black);
                        g.drawString(skill.getName() + " (Ż: " + skill.getFoodCost() + ", " + skill.getCommandPoints() + ")", i * dx + marginLeft, y);
                    }

                } else {
                    g.setColor(Color.black);
                    g.drawString(skill.getName(), i * dx + marginLeft, y);
                    g.drawString(" (wymagane: " + Database.TribePaths.get(skill.getRequiredPath()).getName()
                            + " " + skill.getRequiredPathLevel() + ")", i * dx + marginLeft, y + lineHeight);
                }
            }
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
}
