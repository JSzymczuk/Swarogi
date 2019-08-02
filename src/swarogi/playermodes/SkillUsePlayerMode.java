package swarogi.playermodes;

import swarogi.actions.MovementAction;
import swarogi.actions.SkillAction;
import swarogi.datamodels.SkillData;
import swarogi.engine.Movement;
import swarogi.engine.Pathfinding;
import swarogi.engine.Targeting;
import swarogi.enums.ActionButton;
import swarogi.enums.TargetType;
import swarogi.enums.TileSelectionTag;
import swarogi.game.GameCamera;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.game.TilesSelection;
import swarogi.interfaces.*;
import swarogi.models.Player;
import swarogi.models.Unit;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class SkillUsePlayerMode extends SelectionPlayerMode {

    private Unit unit;
    private Destructible hoveredDestructible;
    private SkillData skill;
    private HashMap<Destructible, List<Tile>> allowedTargets;
    private Pathfinding pathfinding;

    public SkillUsePlayerMode(Player player, PlayerModeChangeListener listener, GameMap map, Unit unit, SkillData skill) {
        super(player, listener, map);
        this.unit = unit;
        this.skill = skill;
        this.allowedTargets = new HashMap<>();

        List<TargetType> allowedTargetTypes = skill.getAllowedTargets();
        int minDistance = skill.getMinDistance();

        if (skill.isApproachingTarget()) {
            pathfinding = new Pathfinding(unit.getTile(), unit.getSteps());
            for (Destructible destructible : pathfinding.getAccessibleTargets()) {
                if (Targeting.canTarget(unit, destructible, allowedTargetTypes)) {
                    this.allowedTargets.put(destructible, getTilesForTarget(destructible));
                }
            }
        }
        else {
            HashMap<Destructible, Integer> destructiblesInRange = map.getMinDistancesToDestructiblesInRange(unit.getTile(), skill.getMaxDistance());
            for (Destructible destructible : destructiblesInRange.keySet()) {
                if (minDistance <= destructiblesInRange.get(destructible) && Targeting.canTarget(unit, destructible, allowedTargetTypes)) {
                    this.allowedTargets.put(destructible, getTilesForTarget(destructible));
                }
            }
        }
    }

    @Override
    public boolean isDebugOnly() { return false; }
    @Override
    public boolean isPausingGameplay() { return false; }
    @Override
    public boolean isLockingCamera() { return false; }

    @Override
    public void update() {
        ControlsProvider controlsProvider = getControls();

        // Wyjdź z trybu zdolności
        if (controlsProvider.isButtonDown(ActionButton.CANCEL)) {
            changePlayerMode(new UnitCommandPlayerAction(getPlayer(), getListener(), getMap(), unit));
            return;
        }

        updateHoverable();

        // Sprawdź czy wskazujemy na jakiś cel
        Placeable hoveredPlaceable = getHoveredPlaceable();
        if (hoveredPlaceable instanceof Destructible && allowedTargets.containsKey(hoveredPlaceable)) {
            hoveredDestructible = (Destructible) hoveredPlaceable;
        }
        else {
            hoveredDestructible = null;
        }

        // Użyj zdolności
        if (controlsProvider.isButtonDown(ActionButton.CONFIRM) && hoveredDestructible != null) {
            PlayerModeChangeListener listener = getListener();
            GameMap map = getMap();
            if (skill.isApproachingTarget()) {
                List<Tile> closestTiles = Movement.getClosestAdjacentTiles(pathfinding, allowedTargets.get(hoveredDestructible));
                Tile closestTile = closestTiles.get(0);
                unit.setPath(pathfinding.getPathTo(closestTile));
                listener.addAction(new MovementAction(unit, false));
            }
            listener.addAction(new SkillAction(unit, hoveredDestructible, skill, map));
            changePlayerMode(new SelectionPlayerMode(getPlayer(), listener, map));
        }
    }

    @Override
    public void renderSelection(Graphics g, GameCamera camera) {
        // Narysuj pola dostępnych celów
        boolean positive = skill.isPositive();
        for (Destructible destructible : allowedTargets.keySet()) {
            TileSelectionTag tileSelectionTag = hoveredDestructible == destructible ? positive ?
                    TileSelectionTag.ACTIVE_POSITIVE : TileSelectionTag.ACTIVE_NEGATIVE
                    : positive ? TileSelectionTag.INACTIVE_POSITIVE : TileSelectionTag.INACTIVE_NEGATIVE;

            for (Tile t : allowedTargets.get(destructible)) {
                renderTile(g, t, camera, tileSelectionTag);
            }
        }

        // Pole wybranej jednostki
        renderTile(g, unit.getTile(), camera, TileSelectionTag.SELECTED);

        // Wskazywany obiekt (jeśli nie jest celem)
        Placeable hoveredPlaceable = getHoveredPlaceable();
        if (hoveredPlaceable != unit && !allowedTargets.containsKey((Destructible)hoveredPlaceable)) {
            List<Tile> hoveredTiles = getHoveredTiles();

            if (hoveredTiles.size() > 0) {

                TileSelectionTag tileSelectionTag = getPlaceableSelectionTag(player, hoveredPlaceable,
                        TileSelectionTag.INACTIVE_POSITIVE, TileSelectionTag.INACTIVE_ALLIED,
                        TileSelectionTag.INACTIVE_NEGATIVE, TileSelectionTag.HOVER_NEUTRAL);

                for (Tile tile : hoveredTiles) {
                    renderTile(g, tile, camera, tileSelectionTag);
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

    // TODO: Copy-paste z AttackPlayerMode -> TargetManager
    private List<Tile> getTilesForTarget(Destructible destructible) {
        return TilesSelection.get(destructible.getDestructibleData().getPlacingTileGroup(), destructible.getTile());
    }
}