package com.danylnysom.rpsduel.game;

/**
 * Simple callback for notifying a class that the user has selected a weapon.
 */
public interface GameCallBack {
    /**
     * @param position the index in the associated Game of the selected weapon
     */
    public void weaponSelected(int position);
}
