package com.danylnysom.rpsduel;

import android.content.SharedPreferences;

/**
 * Represents the "logged-in" player.
 */
public class Player {
    public static final String GAMES = "games";
    public static final String GAMES_TODAY = "games_today";
    public static final String LEVEL = "level";
    public static final String LOSSES = "losses";
    public static final String NAME = "name";
    public static final String OPPONENTS = "opponents";
    public static final String POINTS = "points";
    public static final String WINS = "wins";

    private static Player singleton = null;

    private String name;
    private int gameTotal;
    private int opponentTotal;
    private int points;
    private int wins;

    private Player() {
    }

    public static Player getPlayer() {
        if (singleton == null) {
            singleton = new Player();
        }
        return singleton;
    }

    public void initialize(SharedPreferences prefs) {
        name = prefs.getString(NAME, null);
        gameTotal = prefs.getInt(GAMES, 0);
        opponentTotal = prefs.getInt(OPPONENTS, 0);
        wins = prefs.getInt(WINS, 0);
        points = prefs.getInt(POINTS, 0);
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public int getStat(String key) {
        switch (key) {
            case GAMES:
                return gameTotal;
            case GAMES_TODAY:
                return 5;
            case LEVEL:
                return (points > 0) ? (int) (Math.log(points / 100)) : 0;
            case LOSSES:
                return gameTotal - wins;
            case OPPONENTS:
                return opponentTotal;
            case POINTS:
                return points;
            case WINS:
                return wins;
            default:
                return 0;
        }
    }
}
