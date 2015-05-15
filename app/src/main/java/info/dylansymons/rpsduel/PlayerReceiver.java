package info.dylansymons.rpsduel;

import info.dylansymons.rpsduel.api.playerApi.model.Player;

/**
 * Used to provide callback methods for {@link PlayerManager} asynchronous tasks.
 */
public interface PlayerReceiver {
    /**
     * Called when a request to retrieve a player was successful
     *
     * @param player the {@link Player} that was requested and retrieved
     */
    void updatePlayer(Player player);

    /**
     * Called when the request failed due to a failed network connection
     */
    void connectionFailed();
}
