package com.danylnysom.rpsduel;

import android.support.v4.app.FragmentTransaction;

/**
 * An instance of a Game against a computer-controlled opponent.
 * <p/>
 * The opponent's weapon is retrieved via a BluetoothSocket.
 */
public class BluetoothGame extends Game {
    public BluetoothGame(GameFragment fragment) {
        super(fragment);
        FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
        ft.add(fragment.getId(), new BluetoothOpponentFinderFragment());
        ft.commit();
    }

    @Override
    public int getResult() {
        return getWinStatus();
    }
}
