package com.danylnysom.rpsduel.player;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.widget.Toast;

import java.util.Calendar;

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

    private String name = null;
    private long day;
    private int gameTotal;
    private int gamesToday;
    private int points;
    private int wins;

    /**
     * So that we can cancel before starting a new one, to avoid having awkwardly long stacks of
     * them.
     */
    private Toast currentToast = null;

    private Player() {
        gameTotal = 0;
        gamesToday = 0;
        points = 0;
        wins = 0;
        day = 0;
    }

    /**
     * Gets the singleton instance of this class.
     * <p/>
     * One will be created if it doesn't already exist.
     *
     * @return the singleton instance of this class. Didn't I say that already?
     */
    public static Player getPlayer() {
        if (singleton == null) {
            singleton = new Player();
        }
        return singleton;
    }

    /**
     * Set the values of the singleton instance to the ones stored in the provided SharedPreferences.
     * <p/>
     * Anything not stored in the preferences will be set to 0 or null, whichever is applicable.
     *
     * @param prefs the preferences object containing data about the player
     */
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
    }

    /**
     * Return's the players name.
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Set's the players name
     *
     * @param value the new name of the player
     */
    public void setName(String value) {
        name = value;
    }

    /**
     * Gets a stat for the player.
     *
     * @param key the key (GAMES, LEVEL, WINS...) for the requested stat
     * @return the current value of the requested stat
     */
    public int getStat(String key) {
        switch (key) {
            case GAMES:
                return gameTotal;
            case GAMES_TODAY:
                return 5;
            case LEVEL:
                return (points >= 1000) ? (int) (Math.log(points / 1000) / Math.log(2)) + 1 : 0;
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

    /**
     * Adds a game to this player's stats.
     * <p/>
     * The total number of games and total games today will be incremented. If receivedPoints is
     * positive, a win will also be added.
     *
     * @param receivedPoints the number of points received from the game - can be negative
     */
    public void addGame(int receivedPoints, Context context) {
        StringBuilder builder = new StringBuilder();
        if (receivedPoints > 0) {
            wins++;
            builder.append('+');
        }
        builder.append(receivedPoints);
        gameTotal++;
        gamesToday++;
        addPoints(receivedPoints);

        if (receivedPoints != 0) {
            if (currentToast != null) {
                currentToast.cancel();
                currentToast.setText(builder.toString());
            } else {
                currentToast = Toast.makeText(context, builder.toString(), Toast.LENGTH_SHORT);
            }
            currentToast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 0);
            currentToast.show();
        }
    }

    /**
     * Stores the current stats into the SharedPreferences.
     *
     * @param prefs the SharedPreferences instance to store the current stats into.
     */
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

    /**
     * Adds points to the player's total.
     * <p/>
     * If the total becomes negative, it will be set to 0.
     *
     * @param newPoints the number of points to add - can be negative
     */
    public void addPoints(int newPoints) {
        points += newPoints;
        if (points < 0) {
            points = 0;
        }
    }
}
