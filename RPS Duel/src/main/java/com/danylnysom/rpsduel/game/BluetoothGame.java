package com.danylnysom.rpsduel.game;

import android.support.v4.app.FragmentTransaction;

import com.danylnysom.rpsduel.fragment.GameFragment;

/**
 * An instance of a Game with a human opponent connected via Bluetooth.
 * <p/>
 * Upon construction, a BluetoothOpponentFinderFragment will be created so that an opponent can be
 * found and a connection made.
 * The opponent's weapon is retrieved via a BluetoothSocket, through which the player's choice is
 * sent.
 */
public class BluetoothGame extends Game {
    /**
     * Creates a new BluetoothGame and displays it in the location of the specified fragment.
     * Upon return, a BluetoothOpponentFinderFragment will be displayed.
     *
     * @param fragment the fragment being used to display this game
     */
    public BluetoothGame(GameFragment fragment) {
        super(fragment);
        FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
        ft.add(fragment.getId(), new BluetoothOpponentFinderFragment());
        ft.commit();
    }

    @Override
    /**
     * Gets the opponents weapon from the bluetooth connection, then computes the result of
     * the game.
     *
     * @return the number of points won, or lost (if negative)
     */
    public void getResult() {
        getWinStatus();
    }
}
