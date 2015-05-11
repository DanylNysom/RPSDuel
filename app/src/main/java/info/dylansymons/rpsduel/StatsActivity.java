package info.dylansymons.rpsduel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import info.dylansymons.rpsduel.api.playerApi.model.Player;


public class StatsActivity extends AppCompatActivity implements PlayerReceiver {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PlayerManager.getManager(this).getLocalPlayer(this, this);
        setContentView(new RelativeLayout(getApplicationContext()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updatePlayer(Player player) {
        if (player == null) {
            return;
        }
        RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_stats, null);
        ((TextView) layout.findViewById(R.id.name)).setText(player.getName());
        ((TextView) layout.findViewById(R.id.email)).setText(player.getEmail());
        ((TextView) layout.findViewById(R.id.level)).setText("" + player.getLevel());
        ((TextView) layout.findViewById(R.id.points)).setText("" + player.getPoints());
        ((TextView) layout.findViewById(R.id.wins)).setText("" + player.getWins());
        ((TextView) layout.findViewById(R.id.losses)).setText("" + player.getLosses());
        setContentView(layout);
    }
}
