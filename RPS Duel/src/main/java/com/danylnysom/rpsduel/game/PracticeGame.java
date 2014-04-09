package com.danylnysom.rpsduel.game;

import android.content.Context;

import com.danylnysom.rpsduel.fragment.GameFragment;
import com.danylnysom.rpsduel.player.Player;

import java.util.Random;

/**
 * An instance of a Game against a computer-controlled opponent.
 * <p/>
 * The opponent's weapon is randomly generated when getResult() is called.
 */
public class PracticeGame extends Game {
    private static final int WIN_POINTS = 200;
    private static final int LOSS_POINTS = -50;
    private final Random rand;

    /**
     * Creates a new PracticeGame and shows a popup for the user to select a weapon set.
     *
     * @param context  used to create the weapon set popup
     * @param fragment passed to the super constructor
     */
    public PracticeGame(Context context, GameFragment fragment) {
        super(fragment);
        showWeaponSetPopup(context);
        rand = new Random(System.currentTimeMillis());
    }

    /**
     * Randomly selects a weapon for the opponent.
     * <p/>
     * Adds WIN_POINTS points if the player's weapon beats the opponent's, subtracts LOSS_POINTS
     * points if it loses to the opponent's, and does nothing if they tie.
     */
    @Override
    public void getResult() {
        opponentChoice = rand.nextInt(weaponCount);

        Player player = Player.getPlayer();
        int result = getWinStatus();

        switch (result) {
            case -1:
                player.addGame(LOSS_POINTS, fragment.getActivity());
                break;
            case 1:
                player.addGame(WIN_POINTS, fragment.getActivity());
        }
    }
}
