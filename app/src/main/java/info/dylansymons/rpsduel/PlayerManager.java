package info.dylansymons.rpsduel;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import info.dylansymons.rpsduel.api.playerApi.PlayerApi;
import info.dylansymons.rpsduel.api.playerApi.model.Player;

public class PlayerManager {
    private static PlayerManager singleton;
    private PlayerGetter getter;
    private Player localPlayer;

    private PlayerManager() {
        getter = new PlayerGetter();
    }

    public static PlayerManager getManager() {
        if (singleton == null) {
            singleton = new PlayerManager();
        }
        return singleton;
    }

    public Player getLocalPlayer() {
        getter.start();
        try {
            getter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return localPlayer;
    }

    private class PlayerGetter extends Thread {
        private PlayerApi playerApiService = null;

        public PlayerGetter() {
            PlayerApi.Builder builder = new PlayerApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setRootUrl("https://rpsduel.appspot.com/_ah/api")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            playerApiService = builder.build();
        }

        public void run() {
            Player player = new Player();
            player.setEmail("email@place.com");
            player.setName("PlayerName");
            player.setPoints(24601);
            player.setWins(10);
            player.setLosses(7);
            try {
                playerApiService.insert(player).execute();
                localPlayer = playerApiService.get(player.getEmail()).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
