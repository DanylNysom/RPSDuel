package info.dylansymons.rpsduel;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.net.UnknownHostException;

import info.dylansymons.rpsduel.api.playerApi.PlayerApi;
import info.dylansymons.rpsduel.api.playerApi.model.Player;

public class PlayerManager {
    public static final String PLAYER_PREFS = "player";
    public static final String NAME_PREF = "name";
    private static PlayerManager singleton;
    private String localPlayerEmail;
    private String appName;
    private boolean createPlayerFlag;

    private PlayerManager(String appName) {
        createPlayerFlag = false;
        this.appName = appName;
    }

    public static PlayerManager getManager(Context context) {
        if (singleton == null) {
            singleton = new PlayerManager(context.getResources().getString(R.string.app_name));
        }
        return singleton;
    }

    public void getLocalPlayer(Activity activity, PlayerReceiver receiver) {
        if (createPlayerFlag) {
            String name = activity.getSharedPreferences(PLAYER_PREFS, Activity.MODE_PRIVATE)
                    .getString(NAME_PREF, "???");
            new PlayerLogInTask(activity).execute(localPlayerEmail, name);
        }
        new PlayerGetter(activity, localPlayerEmail).execute(receiver);
    }

    public void setLocalPlayer(String email) {
        this.localPlayerEmail = email;
        this.createPlayerFlag = true;
    }

    private class PlayerLogInTask extends AsyncTask<String, Void, Player> {
        private PlayerApi playerApiService = null;
        private Activity activity;
        private String error;

        public PlayerLogInTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            PlayerApi.Builder builder = new PlayerApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setApplicationName(appName)
                    .setRootUrl("https://rpsduel.appspot.com/_ah/api");
            playerApiService = builder.build();
        }

        @Override
        protected Player doInBackground(String... params) {
            Player returnPlayer = null;

            try {
                returnPlayer = playerApiService.insertOrGet(params[0], params[1]).execute();
            } catch (UnknownHostException uhe) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return returnPlayer;
        }
    }

    private class PlayerGetter extends AsyncTask<PlayerReceiver, Void, Player> {
        private PlayerApi playerApiService = null;
        private PlayerReceiver receivers[];
        private Activity activity;
        private String targetEmail;

        public PlayerGetter(Activity activity, String email) {
            this.activity = activity;
            this.targetEmail = email;
        }

        @Override
        protected void onPreExecute() {
            PlayerApi.Builder builder = new PlayerApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setApplicationName(appName)
                    .setRootUrl("https://rpsduel.appspot.com/_ah/api");
            playerApiService = builder.build();
        }

        @Override
        protected Player doInBackground(PlayerReceiver... params) {
            receivers = params;
            Player returnPlayer = null;

            try {
                returnPlayer = playerApiService.get(targetEmail).execute();
            } catch (UnknownHostException uhe) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return returnPlayer;
        }

        @Override
        protected void onPostExecute(Player retreivedPlayer) {
            for (PlayerReceiver receiver : receivers) {
                receiver.updatePlayer(retreivedPlayer);
            }
        }
    }
}