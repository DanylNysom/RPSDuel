package com.example.rpsduel;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RPSActivity extends Activity {
    public static final String LOSSES_PREF = "losses";
    public static final String NAME_PREF = "name";
    public static final String POINTS_PREF = "points";
    public static final String WINS_PREF = "wins";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rps);
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String name = prefs.getString(NAME_PREF, null);
        if (name == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.frame, new NewUserFragment());
            ft.addToBackStack("newuser");
            ft.commit();
        } else {
            initializeActionBar(name);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.r, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.action_practice) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frame, GameFragment.newInstance(9, GameFragment.PRACTICE));
            ft.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets the title on the Action Bar to the provided player name.
     *
     * @param name The name of the player. Must be non-null.
     */
    public void initializeActionBar(String name) {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.actionbar, null);
        ((TextView) view.findViewById(R.id.name)).setText(name);
        ((TextView) view.findViewById(R.id.level)).setText(String.valueOf(getLevel()));
        actionBar.setCustomView(view);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.show();
    }

    public int getLevel() {
        int points = getPreferences(MODE_PRIVATE).getInt(POINTS_PREF, 0);
        return (points > 0) ? (int) (Math.log(points / 100)) : 0;
    }
}
