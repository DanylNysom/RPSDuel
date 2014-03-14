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
        super(context, fragment);
        showWeaponSetPopup(context);
        rand = new Random(System.currentTimeMillis());
    }

    @Override
    public int getResult() {
        opponentChoice = rand.nextInt(weaponCount);
        if (playerChoice == opponentChoice) {
            return 0;
        } else if (((playerChoice - opponentChoice + weaponCount) % weaponCount) <= weaponCount / 2) {
            return 1;
        } else {
            return -1;
        }
    }
}
