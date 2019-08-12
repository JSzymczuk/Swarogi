package swarogi.playermodes;

import swarogi.actions.AttackAction;
import swarogi.actions.MovementAction;
import swarogi.common.Configuration;
import swarogi.datamodels.AttackData;
import swarogi.engine.Pathfinding;
import swarogi.enums.*;
import swarogi.game.GameCamera;
import swarogi.game.GameMap;
import swarogi.game.Tile;
import swarogi.game.TilesSelection;
import swarogi.gui.DiamondIcon;
import swarogi.gui.Icon;
import swarogi.gui.RenderingHelper;
import swarogi.interfaces.*;
import swarogi.models.Player;
import swarogi.models.Unit;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AttackPlayerMode extends SelectionPlayerMode implements TargetManagableMode {

    private Unit unit;
    private Destructible hoveredDestructible;
    private TargetManager targetManager;

    public AttackPlayerMode(Player player, PlayerModeChangeListener listener, Unit unit, GameMap map) {
        super(player, listener, map);
        this.unit = unit;
        this.targetManager = new TargetManager(this, unit, null);
        setIcons();
    }

    @Override
    public boolean isDebugOnly() { return false; }
    @Override
    public boolean isPausingGameplay() { return false; }
    @Override
    public boolean isLockingCamera() { return false; }

    @Override
    public void update() {

        if (checkGuiInteraction()) { return; }

        updateHoverable();

        // Sprawdź czy wskazujemy na jakiś cel
        Placeable hoveredPlaceable = getHoveredPlaceable();
        if (hoveredPlaceable instanceof Destructible && targetManager.hasTarget((Destructible)hoveredPlaceable)) {
            hoveredDestructible = (Destructible) hoveredPlaceable;
        }
        else {
            hoveredDestructible = null;
        }

        // Zaatakuj cel
        if (getControls().isButtonDown(ActionButton.CONFIRM) && hoveredDestructible != null) {
            targetManager.tryAttack(hoveredDestructible);
        }
    }

    @Override
    public void renderSelection(Graphics g, GameCamera camera) {
        // Narysuj pola dostępnych celów
        for (Destructible destructible : targetManager.getTargets()) {

            TileSelectionTag tileSelectionTag;
            // TODO: Copy-paste z tym wyborem koloru (3 razy w tej funkcji) - napisać metodę pomocniczą.
            if (destructible == hoveredDestructible) {
                if (destructible instanceof PlayerUnit) {
                    tileSelectionTag = ((PlayerUnit) destructible).getOwner() == player ?
                            TileSelectionTag.ACTIVE_POSITIVE
                            : TileSelectionTag.ACTIVE_NEGATIVE;
                }
                else {
                    tileSelectionTag = TileSelectionTag.ACTIVE_POSITIVE;
                }
            }
            else {
                if (destructible instanceof PlayerUnit) {
                    tileSelectionTag = ((PlayerUnit) destructible).getOwner() == player ?
                            TileSelectionTag.INACTIVE_POSITIVE
                            : TileSelectionTag.INACTIVE_NEGATIVE;
                }
                else {
                    tileSelectionTag = TileSelectionTag.HOVER_NEUTRAL;
                }
            }

            for (Tile t : targetManager.getTargetTiles(destructible)) {
                renderTile(g, t, camera, tileSelectionTag);
            }
        }

        // Pole wybranej jednostki
        renderTile(g, unit.getTile(), camera, TileSelectionTag.SELECTED);

        if (!isGuiInteraction()) {
            // Wskazywany obiekt (jeśli nie jest celem)
            Placeable hoveredPlaceable = getHoveredPlaceable();
            if (hoveredPlaceable != unit && !targetManager.hasTarget((Destructible) hoveredPlaceable)) {
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
    }

    @Override
    public void renderGui(Graphics g, Dimension size, Font font) {
        RenderingHelper.drawSummaryBox(g, size);
        RenderingHelper.drawUnitSummary(g, size, unit);
        RenderingHelper.drawTextArea(g, size, getText());
        RenderingHelper.drawIcons(g, getIcons(), size);
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

    private void setIcons() {
        // Wstecz
        {
            Point pos = RenderingHelper.getCancelIconPosition();
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
    }

    private void exitMode() {
        changePlayerMode(new UnitCommandPlayerAction(getPlayer(), getListener(), getMap(), unit));
    }
}

interface TargetManagableMode {
    PlayerModeChangeListener getListener();
    GameMap getMap();
    Player getPlayer();
    void changePlayerMode(PlayerMode playerMode);
}

class TargetManager {

    private class TargetInfo {
        List<Tile> tiles;
        AttackData preferredAttack;

        TargetInfo(List<Tile> tiles, AttackData attack) {
            this.tiles = tiles;
            this.preferredAttack = attack;
        }
    }

    private TargetManagableMode owner;
    private Unit unit;
    private Pathfinding pathfinding;
    private HashMap<Destructible, TargetInfo> destructiblesInRange; // Lista pól dostępnych celów

    TargetManager(TargetManagableMode owner, Unit unit, Pathfinding pathfinding) {
        this.owner = owner;
        this.unit = unit;
        this.pathfinding = pathfinding;
        this.destructiblesInRange = new HashMap<>();

        List<AttackData> attacks = unit.getAttacks();
        GameMap map = owner.getMap();

        // Znajdź cele w zwarciu
        if (attacks.stream().anyMatch(a -> a.getAttackType() == AttackType.Melee)) {
            if (pathfinding == null) {
                this.pathfinding = new Pathfinding(unit.getTile(), unit.getSteps());
            }
            for (Destructible destructible : this.pathfinding.getAccessibleTargets()) {
                destructiblesInRange.put(destructible, new TargetInfo(getTilesForTarget(destructible), null));
            }
        }

        for (AttackData currentAttackData : attacks) {
            AttackType currentAttackType = currentAttackData.getAttackType();

            // Rozważamy atak w zwarciu
            if (currentAttackType == AttackType.Melee) {

                // Cele dla ataków w zwarciu są zawsze wspólne.
                // Ignorowane będą wszystkie cele, które są celami ataków zasigowych.
                for (Destructible destructible : destructiblesInRange.keySet()) {
                    TargetInfo targetInfo = destructiblesInRange.get(destructible);

                    // Jeżeli dla celu nie znaleziono jeszcze ataku, ustaw obecny.
                    if (targetInfo.preferredAttack == null) {
                        targetInfo.preferredAttack = currentAttackData;
                    }
                    else {
                        // Jeśli preferowany atak dla celu nie jest zasięgowy oraz obrażenia są nizsze,
                        // ustaw nowy preferowany atak.
                        if (targetInfo.preferredAttack.getAttackType() == AttackType.Melee
                                && targetInfo.preferredAttack.getMaxDamage() < currentAttackData.getMaxDamage()) {
                            targetInfo.preferredAttack = currentAttackData;
                        }
                    }
                }
            }
            // Rozważamy atak zasięgowy
            else if (currentAttackType == AttackType.Ranged) {

                // Cele dla ataków zasięgowych nie są wspólne. Znajdź dopuszczalne cele:
                HashMap<Destructible, Integer> rangedAttackTargets = map.getMinDistancesToDestructiblesInRange(unit.getTile(),
                        currentAttackData.getMaxDistance());
                int minDistance = currentAttackData.getMinDistance();

                for (Destructible destructible : rangedAttackTargets.keySet()) {
                    // Cel musi być w minimalnym zasięgu
                    if (rangedAttackTargets.get(destructible) >= minDistance) {

                        if (destructiblesInRange.containsKey(destructible)) {
                            // Cel może być osiągnięty innym atakiem
                            TargetInfo targetInfo = destructiblesInRange.get(destructible);

                            // Jeżeli dla celu nie znaleziono jeszcze ataku, ustaw obecny.
                            if (targetInfo.preferredAttack == null) {
                                targetInfo.preferredAttack = currentAttackData;
                            }
                            else {
                                // Jeśli preferowany atak dla celu nie jest zasięgowy lub jego obrażenia są niższe,
                                // ustaw nowy preferowany atak.
                                if (targetInfo.preferredAttack.getAttackType() == AttackType.Melee
                                        || targetInfo.preferredAttack.getMaxDamage() < currentAttackData.getMaxDamage()) {
                                    targetInfo.preferredAttack = currentAttackData;
                                }
                            }
                        }
                        else {
                            // Znaleziono nowy cel
                            destructiblesInRange.put(destructible, new TargetInfo(getTilesForTarget(destructible), currentAttackData));
                        }
                    }
                }
            }
        }
    }

    boolean tryAttack(Destructible target) {
        if (target != null && destructiblesInRange.containsKey(target)) {
            PlayerModeChangeListener listener = owner.getListener();
            GameMap map = owner.getMap();

            AttackData preferredAttack = destructiblesInRange.get(target).preferredAttack;

            if (preferredAttack != null) {
                if (preferredAttack.getAttackType() == AttackType.Melee) {
                    unit.setPath(pathfinding.getPathTo(target));
                    listener.addAction(new MovementAction(unit, false)); // Nie zużywaj punktu akcji na ten ruch
                    listener.addAction(new AttackAction(unit, target, preferredAttack, map));
                    owner.changePlayerMode(new SelectionPlayerMode(owner.getPlayer(), listener, map));
                    return true;
                }
                else if (preferredAttack.getAttackType() == AttackType.Ranged) {
                    listener.addAction(new AttackAction(unit, target, preferredAttack, map));
                    owner.changePlayerMode(new SelectionPlayerMode(owner.getPlayer(), listener, map));
                    return true;
                }
            }
        }
        return false;
    }

    boolean hasTarget(Destructible destructible) {
        return destructiblesInRange.containsKey(destructible);
    }

    Set<Destructible> getTargets() { return destructiblesInRange.keySet(); }

    List<Tile> getTargetTiles(Destructible destructible) { return destructiblesInRange.get(destructible).tiles; }

    private List<Tile> getTilesForTarget(Destructible destructible) {
        return TilesSelection.get(destructible.getDestructibleData().getPlacingTileGroup(), destructible.getTile());
    }
}