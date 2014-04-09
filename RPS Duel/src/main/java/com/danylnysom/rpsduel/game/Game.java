package com.danylnysom.rpsduel.game;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.danylnysom.rpsduel.R;
import com.danylnysom.rpsduel.fragment.GameFragment;
import com.danylnysom.rpsduel.player.Player;

/**
 * Represents a single RPS match against an arbitrary opponent or opponents.
 */
public abstract class Game {
    private static final String RPS_3 = "Traditional";
    private static final String RPS_5 = "Lizard Spock";
    private static final String RPS_7 = "RPS 7";
    private static final String RPS_9 = "RPS 9";
    private static final String JABBERWOCKY = "The Jabberwocky";
    private static final String[] sets = {RPS_3, RPS_5, RPS_7, RPS_9, JABBERWOCKY};
    private static final String[] WEAPONS_3 = {"Rock", "Paper", "Scissors"};
    private static final String[] WEAPONS_5 = {"Rock", "Spock", "Paper", "Lizard", "Scissors"};
    private static final String[] WEAPONS_7 = {"Rock", "Water", "Air", "Paper",
            "Sponge", "Scissors", "Fire"};
    private static final String[] WEAPONS_9 = {"Rock", "Gun", "Water", "Air", "Paper",
            "Sponge", "Human", "Scissors", "Fire"};
    private static final String[] WEAPONS_JABBERWOCKY = {"Jabberwock", "Vorpal Sword", "Boy"};
    private final String[][] MESSAGES_3 = {
            {"ties", "is covered by", "crushes"},
            {"covers", "ties", "is cut by"},
            {"are crushed by", "cut", "tie"}
    };
    private final String[][] MESSAGES_5 = {
            {"ties", "is vaporized by", "is covered by", "crushes", "crushes"},
            {"vaporizes", "ties", "is disproved by", "is poisoned by", "smashes"},
            {"covers", "disproves", "ties", "is eaten by", "is cut by"},
            {"is crushed by", "poisons", "eats", "ties", "is decapitated by"},
            {"are crushed by", "are smashed by", "cut", "decapitate", "tie"}
    };
    private final String[][] MESSAGES_7 = {
            {"ties", "is eroded by", "is eroded by", "is covered by", "crushes", "crushes", "pounds out"},
            {"erodes", "ties", "is evaporated by", "is floated on by", "is absorbed by", "rusts", "puts out"},
            {"erodes", "evaporates", "ties", "is fanned by", "has pockets used by", "is swished through by", "blows out"},
            {"covers", "floats on", "fans", "ties", "is soaked by", "is cut by", "is burned by"},
            {"is crushed by", "absorbs", "uses pockets of", "soaks", "ties", "is cut by", "is burned by"},
            {"are crushed by", "are rusted by", "swish through", "cut", "cut", "tie", "are melted by"},
            {"is pounded out by", "is put out by", "is blown out by", "burns", "burns", "melts", "ties"}
    };
    private final String[][] MESSAGES_9 = {
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
    private final String[][] MESSAGES_JABBERWOCKY = {
            {"ties", "is decapitated by", "eats"},
            {"decapitates", "ties", "is handled by"},
            {"is eaten by", "handles", "ties"}
    };
    private final GameFragment fragment;
    int opponentChoice;
    int weaponCount;
    private String[] weapons;
    private String[][] messages;
    private int playerChoice;

    Game(GameFragment fragment) {
        playerChoice = -1;
        opponentChoice = -1;
        this.fragment = fragment;
    }

    /**
     * Shows a dialog with a list of the available weapon sets, allowing the user to select one to
     * play with.
     *
     * @param context used to create the dialog
     */
    void showWeaponSetPopup(final Context context) {
        ListAdapter adapter = new WeaponListAdapter();
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Choose a weapon set");
        alert.setAdapter(adapter, new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        weapons = WEAPONS_3;
                        messages = MESSAGES_3;
                        break;
                    case 1:
                        weapons = WEAPONS_5;
                        messages = MESSAGES_5;
                        break;
                    case 2:
                        weapons = WEAPONS_7;
                        messages = MESSAGES_7;
                        break;
                    case 3:
                        weapons = WEAPONS_9;
                        messages = MESSAGES_9;
                        break;
                    case 4:
                        weapons = WEAPONS_JABBERWOCKY;
                        messages = MESSAGES_JABBERWOCKY;
                        break;
                    default:
                        weapons = WEAPONS_3;
                        messages = MESSAGES_3;
                        break;
                }
                weaponCount = weapons.length;
                fragment.recreateView();
            }
        });
        alert.setOnCancelListener(new AlertDialog.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                weapons = WEAPONS_3;
                messages = MESSAGES_3;
                weaponCount = weapons.length;
                fragment.recreateView();
            }
        });
        alert.show();
    }

    /**
     * Returns the status of the win, in the classic -1 / 0 / 1 style.
     *
     * @return -1 if the player has lost to the opponent, 0 if they tie, or 1 if the player has won
     */
    int getWinStatus() {
        if (playerChoice == opponentChoice) {
            return 0;
        } else if (((playerChoice - opponentChoice + weaponCount) % weaponCount) <= weaponCount / 2) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * Set's the player's choice of weapon.
     *
     * @param choice the index of the weapon that the player has chosen
     */
    public void setPlayerChoice(int choice) {
        playerChoice = choice;
    }

    /**
     * Displays the weapons chosen and the associated message ("crushes", "is covered by", ...).
     *
     * @param playerView   the view to display the player's weapon choice in
     * @param messageView  the view to display the message in
     * @param opponentView the view to display the opponent's weapon choice in
     */
    public void displayResultMessage(TextView playerView, TextView messageView, TextView opponentView) {
        if (playerChoice == -1 || opponentChoice == -1) {
            return;
        }
        int color = Color.GRAY;
        switch (getWinStatus()) {
            case -1:
                color = Color.RED;
                break;
            case 1:
                color = Color.GREEN;
                break;
        }
        playerView.setText(weapons[playerChoice]);
        opponentView.setText(weapons[opponentChoice]);
        messageView.setText(getMessage());
        messageView.setTextColor(color);
    }

    /**
     * Get's the message associated with the combination of player and opponent weapon, for example
     * "crushes" if the player chose rock and the opponent chose scissors.
     *
     * @return a message describing how the fight went, essentially
     */
    CharSequence getMessage() {
        return messages[playerChoice][opponentChoice];
    }

    /**
     * Returns the number of weapons in the weapon set being used
     *
     * @return the number of weapons available to the player in the game being played
     */
    public int getWeaponCount() {
        return weaponCount;
    }

    /**
     * Returns the weapon at the specified index.
     *
     * @param index the index in the weapon list of the weapon being requested.
     * @return the weapon at the requested index
     */
    public CharSequence getWeapon(int index) {
        return weapons[index];
    }

    public abstract void getResult();

    public static enum CONNECTION_TYPE {
        PRACTICE,
        NFC,
        BLUETOOTH,
        WIFI
    }

    /**
     * A ListAdapter implementation to be used to display all of the available weapon sets.
     */
    class WeaponListAdapter implements ListAdapter {
        private static final int WEAPONLIST_ROW_HEIGHT = 180;

        /**
         * Returns false, since only the available weapon sets are enabled.
         *
         * @return false
         */
        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        /**
         * Returns true if the player has a high enough level to use the weapon set being requested.
         *
         * @param position the index of the weapon set in question
         * @return true if the weapon set has been unlocked, false otherwise
         */
        @Override
        public boolean isEnabled(int position) {
            return Player.getPlayer().getStat(Player.LEVEL) >= position;
        }

        /**
         * Does nothing.
         *
         * @param observer not used
         */
        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        /**
         * Does nothing.
         *
         * @param observer not used
         */
        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        /**
         * Gets the number of available weapon sets.
         *
         * @return the number of available weapon sets
         */
        @Override
        public int getCount() {
            return sets.length;
        }

        /**
         * Returns a weapon set.
         *
         * @param position the index of the weapon set
         * @return the weapon set at the specified index
         */
        @Override
        public Object getItem(int position) {
            return sets[position];
        }

        /**
         * Just returns the position
         *
         * @param position returned
         * @return position
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Always returns true.
         *
         * @return true
         */
        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) convertView;
            if (view == null) {
                view = new TextView(fragment.getActivity());
            }
            view.setText(sets[position]);
            view.setHeight(WEAPONLIST_ROW_HEIGHT);
            view.setTextAppearance(fragment.getActivity(), R.style.TextAppearance_AppCompat_Widget_PopupMenu_Large);
            if (!isEnabled(position)) {
                view.setTextColor(Color.DKGRAY);
            }
            return view;
        }

        /**
         * Always returns 0, since there is only one view type.
         *
         * @param position the position of the item - doesn't actually matter
         * @return true
         */
        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        /**
         * Returns 1, since there is only one view type.
         *
         * @return 1
         */
        @Override
        public int getViewTypeCount() {
            return 1;
        }

        /**
         * Should always return false.
         *
         * @return true if their are no availble weapon sets, which should never be the case
         */
        @Override
        public boolean isEmpty() {
            return sets.length == 0;
        }
    }
}
