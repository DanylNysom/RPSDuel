package com.danylnysom.rpsduel;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RPSActivity extends ActionBarActivity {

    private ViewGroup contentView;

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

    @Override
    protected void onPause() {
        super.onPause();
        Player.getPlayer().saveChanges(getPreferences(MODE_PRIVATE));
    }

    public void initializeActionBar() {
        String name = Player.getPlayer().getName();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.actionbar, null);
        ((TextView) view.findViewById(R.id.name)).setText(name);
        actionBar.setCustomView(view);
        actionBar.setDisplayShowCustomEnabled(true);
        updateLevelDisplay();
        actionBar.show();
    }

    public void updateLevelDisplay() {
        RelativeLayout view = (RelativeLayout) getSupportActionBar().getCustomView();
        ((TextView) view.findViewById(R.id.points)).setText(String.valueOf(Player.getPlayer().getStat(Player.POINTS)));
        ((TextView) view.findViewById(R.id.level)).setText(String.valueOf(Player.getPlayer().getStat(Player.LEVEL)));
    }
}
