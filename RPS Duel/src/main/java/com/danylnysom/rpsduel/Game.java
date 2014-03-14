package com.danylnysom.rpsduel;

import android.widget.TextView;

/**
 * Represents a single RPS match against an arbitrary opponent or opponents.
 */
public abstract class Game {
    private static final String[] WEAPONS_3 = {"Rock", "Paper", "Scissors"};
    private static final String[] WEAPONS_5 = {"Rock", "Scissors", "Lizard", "Paper", "Spock"};
    private static final String[] WEAPONS_7 = {"Rock", "Water", "Air", "Paper",
            "Sponge", "Scissors", "Fire"};
    private static final String[] WEAPONS_9 = {"Rock", "Gun", "Water", "Air", "Paper",
            "Sponge", "Human", "Scissors", "Fire"};
    public static String[] weapons;
    public static String[][] messages;
    protected int playerChoice;
    protected int opponentChoice;
    protected int weaponCount;
    private String[][] MESSAGES_3 = {
            {"ties", "is covered by", "crushes"},
            {"covers", "ties", "is cut by"},
            {"are crushed by", "cut", "tie"}
    };
    private String[][] MESSAGES_5 = {
            {"ties", "crushes", "crushes", "is covered by", "is vaporized by"},
            {"are crushed by", "tie", "decapitate", "cut", "are smashed by"},
            {"is crushed by", "is decapitated by", "ties", "eats", "poisons"},
            {"covers", "is cut by", "is eaten by", "ties", "disproves"},
            {"vaporizes", "smashes", "is poisoned by", "is disproved by", "ties"}
    };
    private String[][] MESSAGES_7 = {
            {"ties", "is eroded by", "is eroded by", "is covered by", "crushes", "crushes", "pounds out"},
            {"erodes", "ties", "is evaporated by", "is floated on by", "is absorbed by", "rusts", "puts out"},
            {"erodes", "evaporates", "ties", "is fanned by", "has pockets used by", "is swished through by", "blows out"},
            {"covers", "floats on", "fans", "ties", "is soaked by", "is cut by", "is burned by"},
            {"is crushed by", "absorbs", "uses pockets of", "soaks", "ties", "is cut by", "is burned by"},
            {"are crushed by", "are rusted by", "swish through", "cut", "cut", "tie", "are melted by"},
            {"is pounded out by", "is put out by", "is blown out by", "burns", "burns", "melts", "ties"}
    };
    private String[][] MESSAGES_9 = {
            {"ties", "is targeted by", "is eroded by", "is eroded by", "is covered by", "crushes", "crushes", "crushes", "pounds out"},
            {"targets", "ties", "is rusted by", "is tarnished by", "is outlawed by", "is cleaned by", "shoots", "outclasses", "\'fires\'"},
            {"erodes", "rusts", "ties", "is evaporated by", "is floated on by", "is absorbed by", "is drunk by", "rusts", "puts out"},
            {"erodes", "tarnishes", "evaporates", "ties", "is fanned by", "has pockets used by", "is breathed by", "is swished through by", "blows out"},
            {"covers", "outlaws", "floats on", "fans", "ties", "is soaked by", "is written by", "is cut by", "is burned by"},
            {"is crushed by", "cleans", "absorbs", "uses pockets of", "soaks", "ties", "is used to clean by", "is cut by", "is burned by"},
            {"is crushed by", "is shot by", "drinks", "breathes", "writes", "cleans with", "ties", "is cut by", "is burned by"},
            {"are crushed by", "are outclassed by", "are rusted by", "swish through", "cut", "cut", "cut", "tie", "are melted by"},
            {"is pounded out by", "is \'fire\'d by", "is put out by", "is blown out by", "burns", "burns", "burns", "melts", "ties"}
    };

    public Game(int weaponSetSize) {
        playerChoice = -1;
        opponentChoice = -1;
        weaponCount = weaponSetSize;
        switch (weaponCount) {
            case 3:
                weapons = WEAPONS_3;
                messages = MESSAGES_3;
                break;
            case 5:
                weapons = WEAPONS_5;
                messages = MESSAGES_5;
                break;
            case 7:
                weapons = WEAPONS_7;
                messages = MESSAGES_7;
                break;
            case 9:
                weapons = WEAPONS_9;
                messages = MESSAGES_9;
                break;
            default:
                weapons = WEAPONS_3;
                messages = MESSAGES_3;
                break;
        }
    }

    public void setPlayerChoice(int choice) {
        playerChoice = choice;
    }

    public boolean displayResultMessage(TextView playerView, TextView messageView, TextView opponentView) {
        if (playerChoice == -1 || opponentChoice == -1) {
            return false;
        }
        playerView.setText(weapons[playerChoice]);
        opponentView.setText(weapons[opponentChoice]);
        messageView.setText(getMessage());
        return true;
    }

    public String getMessage() {
        return messages[playerChoice][opponentChoice];
    }

    public int getWeaponCount() {
        return weaponCount;
    }

    public String getWeapon(int index) {
        return weapons[index];
    }

    public abstract int getResult();
}
