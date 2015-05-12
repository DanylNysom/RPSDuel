package info.dylansymons.rpsduel;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.Plus;

import info.dylansymons.rpsduel.api.playerApi.model.Player;


public class StatsActivity extends PlusBaseActivity implements PlayerReceiver {
    private static final String TAG = StatsActivity.class.getSimpleName();
    private View mStatsView;
    private View mLogInView;

    @Override
    protected void onPlusClientSignIn() {
        String email = Plus.AccountApi.getAccountName(getPlusClient());
        PlayerManager.getManager(this).setLocalPlayer(email);
        PlayerManager.getManager(this).getLocalPlayer(this, this);
    }

    @Override
    protected void onPlusClientSignOut() {

    }

    @Override
    protected void onPlusClientBlockingUI(boolean show) {

    }

    @Override
    protected void updateConnectButtonState() {
        boolean connected = getPlusClient().isConnected();

        mStatsView.setVisibility(connected ? View.VISIBLE : View.GONE);
        mLogInView.setVisibility(connected ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        mLogInView = findViewById(R.id.logInView);
        mStatsView = findViewById(R.id.statsView);

        SignInButton signIn = (SignInButton) findViewById(R.id.plus_sign_in_button);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    @Override
    public void updatePlayer(Player player) {
        if (player == null) {
            return;
        }
        mLogInView.setVisibility(View.GONE);
        ((TextView) mStatsView.findViewById(R.id.name)).setText(player.getName());
        ((TextView) mStatsView.findViewById(R.id.email)).setText(player.getEmail());
        ((TextView) mStatsView.findViewById(R.id.level)).setText("" + player.getLevel());
        ((TextView) mStatsView.findViewById(R.id.points)).setText("" + player.getPoints());
        ((TextView) mStatsView.findViewById(R.id.wins)).setText("" + player.getWins());
        ((TextView) mStatsView.findViewById(R.id.losses)).setText("" + player.getLosses());
    }

    @Override
    public void onConnectionSuspended(int i) {
        updateConnectButtonState();
        onPlusClientSignOut();
    }
}
