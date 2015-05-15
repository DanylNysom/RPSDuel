package info.dylansymons.rpsduel;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.Plus;

import info.dylansymons.rpsduel.api.playerApi.model.Player;

/**
 * An Activity for displaying the local player's statistics to the user.
 */
public class StatsActivity extends PlusBaseActivity implements PlayerReceiver {
    private static final String TAG = StatsActivity.class.getSimpleName();
    private boolean error;
    private View mStatsView;
    private View mLogInView;
    private View mErrorView;

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

        mStatsView.setVisibility(connected && !error ? View.VISIBLE : View.GONE);
        mLogInView.setVisibility(!connected && !error ? View.VISIBLE : View.GONE);
        mErrorView.setVisibility(error ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        mLogInView = findViewById(R.id.logInView);
        mStatsView = findViewById(R.id.statsView);
        mErrorView = findViewById(R.id.errorView);
        error = false;

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
        error = false;
        ((TextView) mStatsView.findViewById(R.id.name)).setText(player.getName());
        ((TextView) mStatsView.findViewById(R.id.email)).setText(player.getEmail());
        ((TextView) mStatsView.findViewById(R.id.level)).setText("" + player.getLevel());
        ((TextView) mStatsView.findViewById(R.id.points)).setText("" + player.getPoints());
        ((TextView) mStatsView.findViewById(R.id.wins)).setText("" + player.getWins());
        ((TextView) mStatsView.findViewById(R.id.losses)).setText("" + player.getLosses());
        updateConnectButtonState();
    }

    @Override
    public void connectionFailed() {
        error = true;
        ((TextView) mErrorView).setText(getString(R.string.no_internet));
        updateConnectButtonState();
        InternetConnectionJobManager.getManager().addJob(
                new InternetConnectionJob.PlayerGetter(this, this));
    }

    @Override
    public void onConnectionSuspended(int i) {
        updateConnectButtonState();
        onPlusClientSignOut();
    }
}
