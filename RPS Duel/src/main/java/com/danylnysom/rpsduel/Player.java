package com.danylnysom.rpsduel;

import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.HashSet;

/**
 * Represents the "logged-in" player.
 */
public class Player {
    public static final String GAMES = "games";
    public static final String GAMES_TODAY = "games_today";
    public static final String LEVEL = "level";
    public static final String LOSSES = "losses";
    public static final String NAME = "name";
    public static final String POINTS = "points";
    public static final String WINS = "wins";
    private static final String DAY = "day";

    private static Player singleton = null;

    private boolean initialized;
    private String name;
    private long day;
    private int gameTotal;
    private int gamesToday;
    private int points;
    private int wins;
    private HashSet<String> opponents;

    private Player() {
        initialized = false;
        name = null;
        gameTotal = 0;
        gamesToday = 0;
        points = 0;
        wins = 0;
        day = 0;
        opponents = null;
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
        wins = prefs.getInt(WINS, 0);
        points = prefs.getInt(POINTS, 0);
        day = prefs.getLong(DAY, System.currentTimeMillis());
        Calendar lastDay = Calendar.getInstance();
        lastDay.setTimeInMillis(day);
        Calendar today = Calendar.getInstance();
        if (!(lastDay.get(Calendar.DATE) == today.get(Calendar.DATE))) {
            day = System.currentTimeMillis();
            gamesToday = 0;
        } else {
            gamesToday = prefs.getInt(GAMES_TODAY, 0);
        }
        initialized = true;
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
            case POINTS:
                return points;
            case WINS:
                return wins;
            default:
                return 0;
        }
    }

    public int addGame(int receivedPoints) {
        if (points > 0) {
            wins++;
        }
        gameTotal++;
        gamesToday++;
        points += receivedPoints;
        return points;
    }

    public void saveChanges(SharedPreferences prefs) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(GAMES, gameTotal);
        editor.putInt(GAMES_TODAY, gamesToday);
        editor.putString(NAME, name);
        editor.putInt(POINTS, points);
        editor.putInt(WINS, wins);
        editor.putLong(DAY, day);
        editor.apply();
    }
}
