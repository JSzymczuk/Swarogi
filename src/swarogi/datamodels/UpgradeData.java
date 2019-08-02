package swarogi.datamodels;

import java.util.ArrayList;
import java.util.List;

public class UpgradeData {
    private List<Object> values;
    private String description;

    public UpgradeData() {
        this.values = new ArrayList<>();
    }

    public void addValue(Object value) { values.add(value); }
    public Object getValue(int level) { return values.get(level); }

    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return this.description; }
}
