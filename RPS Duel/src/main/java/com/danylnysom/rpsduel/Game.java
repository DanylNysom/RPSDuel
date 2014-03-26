package com.danylnysom.rpsduel;

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

/**
 * Represents a single RPS match against an arbitrary opponent or opponents.
 */
public abstract class Game {
    public static final String RPS_3 = "Traditional";
    public static final String RPS_5 = "Lizard Spock";
    public static final String RPS_7 = "RPS 7";
    public static final String RPS_9 = "RPS 9";
    public static final String JABBERWOCKY = "The Jabberwocky";
    public static final String[] sets = {RPS_3, RPS_5, RPS_7, RPS_9, JABBERWOCKY};
    private static final String[] WEAPONS_3 = {"Rock", "Paper", "Scissors"};
    private static final String[] WEAPONS_5 = {"Rock", "Spock", "Paper", "Lizard", "Scissors"};
    private static final String[] WEAPONS_7 = {"Rock", "Water", "Air", "Paper",
            "Sponge", "Scissors", "Fire"};
    private static final String[] WEAPONS_9 = {"Rock", "Gun", "Water", "Air", "Paper",
            "Sponge", "Human", "Scissors", "Fire"};
    private static final String[] WEAPONS_JABBERWOCKY = {"Jabberwock", "Vorpal Sword", "Boy"};
    public String[] weapons;
    public String[][] messages;
    protected int playerChoice;
    protected int opponentChoice;
    protected int weaponCount;
    private String[][] MESSAGES_3 = {
            {"ties", "is covered by", "crushes"},
            {"covers", "ties", "is cut by"},
            {"are crushed by", "cut", "tie"}
    };
    private String[][] MESSAGES_5 = {
            {"ties", "is vaporized by", "is covered by", "crushes", "crushes"},
            {"vaporizes", "ties", "is disproved by", "is poisoned by", "smashes"},
            {"covers", "disproves", "ties", "is eaten by", "is cut by"},
            {"is crushed by", "poisons", "eats", "is decapitated by"},
            {"are crushed by", "are smashed by", "cut", "decapitate", "tie"}
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
    private String[][] MESSAGES_JABBERWOCKY = {
            {"ties", "is decapitated by", "eats"},
            {"decapitates", "ties", "is handled by"},
            {"is eaten by", "handles", "ties"}
    };
    private GameFragment fragment;

    public Game(GameFragment fragment) {
        playerChoice = -1;
        opponentChoice = -1;
        this.fragment = fragment;
    }

    protected void showWeaponSetPopup(final Context context) {
        WeaponListAdapter adapter = new WeaponListAdapter();
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

    protected int getWinStatus() {
        if (playerChoice == opponentChoice) {
            return 0;
        } else if (((playerChoice - opponentChoice + weaponCount) % weaponCount) <= weaponCount / 2) {
            return 1;
        } else {
            return -1;
        }
    }

    public void setPlayerChoice(int choice) {
        playerChoice = choice;
    }

    public boolean displayResultMessage(TextView playerView, TextView messageView, TextView opponentView) {
        if (playerChoice == -1 || opponentChoice == -1) {
            return false;
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

    public static enum CONNECTION_TYPE {
        PRACTICE,
        NFC,
        BLUETOOTH,
        WIFI
    }

    protected class WeaponListAdapter implements ListAdapter {
        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return Player.getPlayer().getStat(Player.LEVEL) >= position;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return sets.length;
        }

        @Override
        public Object getItem(int position) {
            return sets[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) convertView;
            if (view == null) {
                view = new TextView(parent.getContext());
            }
            view.setText(sets[position]);
            view.setHeight(180);
            view.setTextAppearance(view.getContext(), R.style.TextAppearance_AppCompat_Widget_PopupMenu_Large);
            if (!isEnabled(position)) {
                view.setTextColor(Color.DKGRAY);
            }
            return view;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return sets.length == 0;
        }
    }
}
