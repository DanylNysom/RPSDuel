package info.dylansymons.rpsduel;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;


/**
 * A base class to wrap communication with the Google Play Services PlusClient.
 */
public abstract class PlusBaseActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = PlusBaseActivity.class.getSimpleName();

    // A magic number we will use to know that our sign-in error resolution activity has completed
    private static final int OUR_REQUEST_CODE = 49404;
    // A flag to track when a connection is already in progress
    public boolean mPlusClientIsConnecting = false;
    // A flag to stop multiple dialogues appearing for the user
    private boolean mAutoResolveOnFail;
    // This is the helper object that connects to Google Play Services.
    private GoogleApiClient apiClient;

    // The saved result from {@link #onConnectionFailed(ConnectionResult)}.  If a connection
    // attempt has been made, this is non-null.
    // If this IS null, then the connect method is still running.
    private ConnectionResult mConnectionResult;

    /**
     * Called when the GoogleApiClient is successfully connected.
     */
    protected abstract void onPlusClientSignIn();

    /**
     * Called when the {@link GoogleApiClient} is disconnected.
     */
    protected abstract void onPlusClientSignOut();

    /**
     * Called when the {@link GoogleApiClient} is blocking the UI.  If you have a progress bar widget,
     * this tells you when to show or hide it.
     */
    protected abstract void onPlusClientBlockingUI(boolean show);

    /**
     * Called when there is a change in connection state.  If you have "Sign in"/ "Connect",
     * "Sign out"/ "Disconnect", or "Revoke access" buttons, this lets you know when their states
     * need to be updated.
     */
    protected abstract void updateConnectButtonState();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the PlusClient connection.
        // Scopes indicate the information about the user your application will be able to access.
        apiClient =
                new GoogleApiClient.Builder(this, this, this).addScope(Plus.SCOPE_PLUS_LOGIN)
                        .addScope(Plus.SCOPE_PLUS_PROFILE)
                        .addApi(Plus.API)
                        .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Try to sign in the user.
     */
    public void signIn() {
        if (!apiClient.isConnected()) {
            // Show the dialog as we are now signing in.
            setProgressBarVisible(true);
            // Make sure that we will start the resolution (e.g. fire the intent and pop up a
            // dialog for the user) for any errors that come in.
            mAutoResolveOnFail = true;
            // We should always have a connection result ready to resolve,
            // so we can start that process.
            if (mConnectionResult != null) {
                startResolution();
            } else {
                // If we don't have one though, we can start connect in
                // order to retrieve one.
                initiatePlusClientConnect();
            }
        }

        updateConnectButtonState();
    }

    /**
     * Connect the {@link GoogleApiClient} only if a connection isn't already in progress.  This will
     * call back to {@link #onConnected(android.os.Bundle)} or
     * {@link #onConnectionFailed(com.google.android.gms.common.ConnectionResult)}.
     */
    private void initiatePlusClientConnect() {
        if (!apiClient.isConnected() && !apiClient.isConnecting()) {
            apiClient.connect();
        }
    }

    /**
     * Disconnect the {@link GoogleApiClient} only if it is connected (otherwise, it can throw an error.)
     * This will call back to {@link # onDisconnected()}.
     */
    private void initiatePlusClientDisconnect() {
        if (apiClient.isConnected()) {
            apiClient.disconnect();
        }
    }

    /**
     * Sign out the user (so they can switch to another account).
     */
    public void signOut() {

        // We only want to sign out if we're connected.
        if (apiClient.isConnected()) {
            // Clear the default account in order to allow the user to potentially choose a
            // different account from the account chooser.
            Plus.AccountApi.clearDefaultAccount(apiClient);

            // Disconnect from Google Play Services, then reconnect in order to restart the
            // process from scratch.
            initiatePlusClientDisconnect();

            Log.v(TAG, "Sign out successful!");
        }

        updateConnectButtonState();
    }

    /**
     * Revoke Google+ authorization completely.
     */
    public void revokeAccess() {
        if (apiClient.isConnected()) {
            // Clear the default account as in the Sign Out.
            Plus.AccountApi.clearDefaultAccount(apiClient);

            // Revoke access to this entire application. This will call back to
            // onAccessRevoked when it is complete, as it needs to reach the Google
            // authentication servers to revoke all tokens.
            Plus.AccountApi.revokeAccessAndDisconnect(apiClient);
            signOut();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        initiatePlusClientConnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        initiatePlusClientDisconnect();
    }

    public boolean isPlusClientConnecting() {
        return mPlusClientIsConnecting;
    }

    private void setProgressBarVisible(boolean flag) {
        mPlusClientIsConnecting = flag;
        onPlusClientBlockingUI(flag);
    }

    /**
     * A helper method to flip the mResolveOnFail flag and start the resolution
     * of the ConnectionResult from the failed connect() call.
     */
    private void startResolution() {
        try {
            // Don't start another resolution now until we have a result from the activity we're
            // about to start.
            mAutoResolveOnFail = false;
            // If we can resolve the error, then call start resolution and pass it an integer tag
            // we can use to track.
            // This means that when we get the onActivityResult callback we'll know it's from
            // being started here.
            mConnectionResult.startResolutionForResult(this, OUR_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            // Any problems, just try to connect() again so we get a new ConnectionResult.
            mConnectionResult = null;
            initiatePlusClientConnect();
        }
    }

    /**
     * An earlier connection failed, and we're now receiving the result of the resolution attempt
     * by PlusClient.
     *
     * @see #onConnectionFailed(ConnectionResult)
     */
    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        updateConnectButtonState();
        if (requestCode == OUR_REQUEST_CODE && responseCode == RESULT_OK) {
            // If we have a successful result, we will want to be able to resolve any further
            // errors, so turn on resolution with our flag.
            mAutoResolveOnFail = true;
            // If we have a successful result, let's call connect() again. If there are any more
            // errors to resolve we'll get our onConnectionFailed, but if not,
            // we'll get onConnected.
            initiatePlusClientConnect();
        } else if (requestCode == OUR_REQUEST_CODE) {
            // If we've got an error we can't resolve, we're no longer in the midst of signing
            // in, so we can stop the progress spinner.
            setProgressBarVisible(false);
        }
    }

    /**
     * Successfully connected (called by PlusClient)
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        updateConnectButtonState();
        setProgressBarVisible(false);
        onPlusClientSignIn();
    }

    /**
     * Successfully disconnected (called by PlusClient)
     //   */
//    @Override
//    public void onDisconnected() {
//        updateConnectButtonState();
//        onPlusClientSignout();
//    }

    /**
     * Connection failed for some reason (called by PlusClient)
     * Try and resolve the result.  Failure here is usually not an indication of a serious error,
     * just that the user's input is needed.
     *
     * @see #onActivityResult(int, int, Intent)
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        updateConnectButtonState();

        // Most of the time, the connection will fail with a user resolvable result. We can store
        // that in our mConnectionResult property ready to be used when the user clicks the
        // sign-in button.
        if (result.hasResolution()) {
            mConnectionResult = result;
            if (mAutoResolveOnFail) {
                // This is a local helper function that starts the resolution of the problem,
                // which may be showing the user an account chooser or similar.
                startResolution();
            }
        }
    }

    public GoogleApiClient getPlusClient() {
        return apiClient;
    }

    @Override
    public void onResume() {
        super.onResume();
        apiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        apiClient.disconnect();
    }
}
