package com.danylnysom.rpsduel.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.danylnysom.rpsduel.R;
import com.danylnysom.rpsduel.fragment.GameFragment;
import com.danylnysom.rpsduel.fragment.NewUserFragment;
import com.danylnysom.rpsduel.game.Game;
import com.danylnysom.rpsduel.player.Player;

/**
 * The entry point to all things RPS Duel.
 */
public class RPSActivity extends ActionBarActivity implements ActionbarCallback {
    private ViewGroup contentView;

    /**
     * Sets everything up. If this is the first time the user has run this app, or if they have
     * cleared their data, a NewUserFragment will be shown so that the user can enter a name.
     * Otherwise a StatsFragment will be displayed.
     *
     * @param savedInstanceState is not used here - yet, at least
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Player player = Player.getPlayer();
        contentView = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_rps, null);
        setContentView(contentView);
        player.initialize(getPreferences(MODE_PRIVATE));

        String name = player.getName();
        if (name == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.frame, new NewUserFragment());
            ft.addToBackStack("newuser");
            ft.commit();
        } else {
            initializeActionBar();
        }
    }

    /**
     * Initializes the options menu, of course.
     *
     * @param menu the menu to inflate the menu into
     * @return true, unless the sun has exploded
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Deals with a selection of a menu item. What happens depends on the item that was selected:
     * <p/>
     * Practice:    starts a new PracticeGame
     * Duel:        starts a new DuelGame
     * (other):     returns false
     *
     * @param item the item that was selected
     * @return true if the item was added to the menu by this activity, and was handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        switch (item.getItemId()) {
            case R.id.action_practice:
                contentView.removeAllViews();
                ft.replace(R.id.frame, GameFragment.newInstance(Game.CONNECTION_TYPE.PRACTICE));
                ft.commit();
                return true;
            case R.id.action_duel:
                contentView.removeAllViews();
                ft.replace(R.id.frame, GameFragment.newInstance(Game.CONNECTION_TYPE.BLUETOOTH));
                ft.commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Saves the changes to the player by calling Player.saveChanges(SharedPreferences).
     */
    @Override
    protected void onPause() {
        super.onPause();
        Player.getPlayer().saveChanges(getPreferences(MODE_PRIVATE));
    }

    /**
     * Sets up the action bar.
     * <p/>
     * Removes the home button and title. Sets the view to the actionbar.xml layout, and sets the
     * player's name, level, and points in the view.
     */
    public void initializeActionBar() {
        String name = Player.getPlayer().getName();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.actionbar, null);
        if (view != null) {
            ((TextView) view.findViewById(R.id.name)).setText(name);
            actionBar.setCustomView(view);
            actionBar.setDisplayShowCustomEnabled(true);
        }
        updateLevelDisplay();
        actionBar.show();
    }

    /**
     * Updates the points and level displays in the actionbar to their current values.
     */
    @Override
    public void updateLevelDisplay() {
        RelativeLayout view = (RelativeLayout) getSupportActionBar().getCustomView();
        Player player = Player.getPlayer();
        ((TextView) view.findViewById(R.id.points)).setText(String.valueOf(player.getStat(Player.POINTS)));
        ((TextView) view.findViewById(R.id.level)).setText(String.valueOf(player.getStat(Player.LEVEL)));
    }

    /**
     * Supposedly displays a fun spinning progress indicate for when background operations are
     * being performed. It doesn't seem to work. Actually I think it works, but I'm not calling it
     * where I should be.
     *
     * @param doShow true if the indicator should be displayed; false if it should be hidden
     */
    public void showProgressIndicator(boolean doShow) {
        RelativeLayout view = (RelativeLayout) getSupportActionBar().getCustomView();
        ProgressBar indicator = (ProgressBar) view.findViewById(R.id.progressIndicator);
        if (doShow) {
            indicator.setVisibility(View.VISIBLE);
        } else {
            indicator.setVisibility(View.INVISIBLE);
        }
    }
}
