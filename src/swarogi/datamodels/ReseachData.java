package swarogi.datamodels;

import swarogi.common.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReseachData {

    private List<Map<UpgradeData, Integer>> levels;
    private String name;
    private int[] tribeLevelBonuses;
    private int[] researchTime;
    private String[] decriptions;

    public ReseachData() {
        int n = Configuration.MAX_TRIBE_PATH_LEVEL;
        this.levels = new ArrayList<>();
        for (int i = 0; i < n; ++i) {
            levels.add(new HashMap<>());
        }
        this.tribeLevelBonuses = new int[n];
        this.researchTime = new int[n];
        this.decriptions = new String[n];
    }

    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }

    public Map<UpgradeData, Integer> getUpgrades(int level) { return levels.get(level); }

    public void addUpgrade(int level, UpgradeData upgrade, int upgradeLevel) {
        this.levels.get(level).put(upgrade, upgradeLevel);
    }

    public int getTribeLevelBonus(int level) { return this.tribeLevelBonuses[level]; }
    public void setTribeLevelBonus(int level, int value) { this.tribeLevelBonuses[level] = value; }

    public int getResearchTime(int level) { return this.researchTime[level]; }
    public void setResearchTime(int level, int value) { this.researchTime[level] = value; }

    public String getDescription(int level) { return this.decriptions[level]; }
    public void setDescription(int level, String value) { this.decriptions[level] = value; }
}
