package info.dylansymons.rpsduel.player;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.net.UnknownHostException;

import info.dylansymons.rpsduel.R;
import info.dylansymons.rpsduel.api.playerApi.PlayerApi;
import info.dylansymons.rpsduel.api.playerApi.model.Player;

public class PlayerManager {
    public static final String PLAYER_PREFS = "player";
    public static final String NAME = "name";
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

    public void getLocalPlayer(Context context, PlayerReceiver receiver) {
        if (createPlayerFlag) {
            String name = getName(context);
            new PlayerLogInTask(localPlayerEmail, name).execute(receiver);
        } else {
            new PlayerGetter(localPlayerEmail).execute(receiver);
        }
    }

    public void setLocalPlayer(String email) {
        this.localPlayerEmail = email;
        this.createPlayerFlag = true;
    }

    public void deleteAccount(Activity activity) {
        new PlayerDeleteTask(activity).execute(localPlayerEmail);
    }

    public String getName(Context context) {
        if (context != null) {
            return context
                    .getSharedPreferences(PLAYER_PREFS, Activity.MODE_PRIVATE)
                    .getString(NAME, "");
        } else {
            return "";
        }
    }

    public void updateName(Activity activity, String name, PlayerReceiver receiver) {
        activity
                .getSharedPreferences(PLAYER_PREFS, Activity.MODE_PRIVATE)
                .edit()
                .putString(NAME, name)
                .apply();
        String data[][] = {{"name", name}};
        new PlayerUpdateTask(data).execute(receiver);
    }

    private class PlayerLogInTask extends AsyncTask<PlayerReceiver, Void, Player> {
        private PlayerApi playerApiService = null;
        private PlayerReceiver receivers[];
        private String email;
        private String name;
        private boolean connectionFailed;

        public PlayerLogInTask(String email, String name) {
            this.email = email;
            this.name = name;
            connectionFailed = false;
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
                returnPlayer = playerApiService.insertOrGet(email, name).execute();
            } catch (UnknownHostException uhe) {
                connectionFailed = true;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return returnPlayer;
        }

        @Override
        protected void onPostExecute(Player retreivedPlayer) {
            for (PlayerReceiver receiver : receivers) {
                if (!connectionFailed) {
                    receiver.updatePlayer(retreivedPlayer);
                } else {
                    receiver.connectionFailed();
                }
            }
        }
    }

    private class PlayerUpdateTask extends AsyncTask<PlayerReceiver, Void, Player> {
        private PlayerApi playerApiService;
        private String[][] data;
        private PlayerReceiver[] receivers;
        private boolean connectionFailed;


        public PlayerUpdateTask(String[][] data) {
            this.data = data;
            connectionFailed = false;
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
            Player player = null;
            try {
                player = playerApiService.get(localPlayerEmail).execute();
                for (String[] pair : data) {
                    if (pair[0].equals(NAME)) {
                        player.setName(pair[1]);
                    }
                }
                playerApiService.update(localPlayerEmail, player);
            } catch (UnknownHostException uhe) {
                connectionFailed = true;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return player;
        }

        @Override
        protected void onPostExecute(Player retreivedPlayer) {
            for (PlayerReceiver receiver : receivers) {
                if (!connectionFailed) {
                    receiver.updatePlayer(retreivedPlayer);
                } else {
                    receiver.connectionFailed();
                }
            }
        }

    }

    private class PlayerDeleteTask extends AsyncTask<String, Void, Void> {
        private PlayerApi playerApiService = null;
        private Activity activity;

        public PlayerDeleteTask(Activity activity) {
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
        protected Void doInBackground(String... params) {
            try {
                playerApiService.remove(params[0]).execute();
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
            return null;
        }
    }

    private class PlayerGetter extends AsyncTask<PlayerReceiver, Void, Player> {
        private PlayerApi playerApiService = null;
        private PlayerReceiver receivers[];
        private String targetEmail;
        private boolean connectionFailed;

        public PlayerGetter(String email) {
            this.targetEmail = email;
            connectionFailed = false;
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
                connectionFailed = true;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return returnPlayer;
        }

        @Override
        protected void onPostExecute(Player retreivedPlayer) {
            for (PlayerReceiver receiver : receivers) {
                if (!connectionFailed) {
                    receiver.updatePlayer(retreivedPlayer);
                } else {
                    receiver.connectionFailed();
                }
            }
        }
    }
}
