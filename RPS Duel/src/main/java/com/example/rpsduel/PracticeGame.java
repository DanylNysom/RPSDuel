package com.example.rpsduel;

import java.util.Random;

/**
 * Created by dylan on 3/12/14.
 */
public class PracticeGame extends Game {
    Random rand;

    public PracticeGame(int weaponSetSize) {
        super(weaponSetSize);
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
