package com.danylnysom.rpsduel;

import android.content.Context;

import java.util.Random;

/**
 * An instance of a Game against a computer-controlled opponent.
 * <p/>
 * The opponent's weapon is randomly generated when getResult() is called.
 */
public class PracticeGame extends Game {
    Random rand;

    public PracticeGame(Context context, GameFragment fragment) {
        super(fragment);
        showWeaponSetPopup(context);
        rand = new Random(System.currentTimeMillis());
    }

    @Override
    public int getResult() {
        opponentChoice = rand.nextInt(weaponCount);

        int result = getWinStatus();

        switch (result) {
            case -1:
                Player.getPlayer().addPoints(-50);
                break;
            case 1:
                Player.getPlayer().addPoints(200);
        }

        return result;
    }
}
