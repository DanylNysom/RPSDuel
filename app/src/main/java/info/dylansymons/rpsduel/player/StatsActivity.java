package info.dylansymons.rpsduel.player;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.Plus;

import info.dylansymons.rpsduel.R;
import info.dylansymons.rpsduel.api.playerApi.model.Player;
import info.dylansymons.rpsduel.base.PlusBaseActivity;
import info.dylansymons.rpsduel.connection.InternetConnectionJob;
import info.dylansymons.rpsduel.connection.InternetConnectionJobManager;

/**
 * An Activity for displaying the local player's statistics to the user.
 */
public class StatsActivity extends PlusBaseActivity implements PlayerReceiver {
    private static final String TAG = StatsActivity.class.getSimpleName();
    private boolean error;
    private View mStatsView;
    private View mLogInView;
    private View mErrorView;
    private View mProgressView;

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
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mStatsView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLogInView.setVisibility(show ? View.GONE : View.VISIBLE);
        mErrorView.setVisibility(show ? View.GONE : View.VISIBLE);
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
        mProgressView = findViewById(R.id.progressView);
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
