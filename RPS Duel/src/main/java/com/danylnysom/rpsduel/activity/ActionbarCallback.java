package com.danylnysom.rpsduel.activity;

/**
 * Callbacks relating to the ActionBar. Should probably only be implemented by Activities.
 */
public interface ActionbarCallback {

    /**
     * Set the level and points displays.
     */
    public abstract void updateLevelDisplay();

    /**
     * Set any part of the action bar that only needs to be done on the first run.
     */
    public abstract void initializeActionBar();

}
