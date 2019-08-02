package swarogi.interfaces;

public interface Action {
    boolean canBeExecuted();
    boolean hasStarted();
    boolean isCompleted();
    void start();
    void update();
    void finish();
    void abort();
}
