package swarogi.datamodels;

public class EffectData {

    private String name;
    private String description;
    private boolean instant;
    private int duration;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public boolean isInstant() { return instant; }
    public void setInstant(boolean instant) { this.instant = instant; }
}
