package com.danylnysom.rpsduel;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RPSActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Player player = Player.getPlayer();
        setContentView(R.layout.activity_rps);
        player.initialize(getPreferences(MODE_PRIVATE));

        String name = player.getName();
        if (name == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.action_practice) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.actionbar, null);
        ((TextView) view.findViewById(R.id.name)).setText(name);
        ((TextView) view.findViewById(R.id.level)).setText(String.valueOf(Player.getPlayer().getStat(Player.LEVEL)));
        actionBar.setCustomView(view);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.show();
    }
}
