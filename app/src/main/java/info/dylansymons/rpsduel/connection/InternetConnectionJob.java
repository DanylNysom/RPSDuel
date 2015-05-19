package info.dylansymons.rpsduel.connection;

import android.content.Context;

import info.dylansymons.rpsduel.player.PlayerManager;
import info.dylansymons.rpsduel.player.PlayerReceiver;

/**
 * A job to be executed when the network connection state changes. After creating an instance, pass
 * the instance to {@link InternetConnectionJobManager#addJob(InternetConnectionJob)} to have the
 * {@link #execute()} method be called when the connection state changes.
 */
public abstract class InternetConnectionJob {
    /**
     * Called by the {@link InternetConnectionJobManager} when the network connection state changes.
     */
    public abstract void execute();

    /**
     * An {@link InternetConnectionJob} to retrieve the local player.
     */
    public static class PlayerGetter extends InternetConnectionJob {
        private Context context;
        private PlayerReceiver receiver;

        /**
         * @param context  the Context of the activity requesting the player's information
         * @param receiver the {@link PlayerReceiver} instance to send the player to
         */
        public PlayerGetter(Context context, PlayerReceiver receiver) {
            this.context = context;
            this.receiver = receiver;
        }

        /**
         * Retrieves the local player and sends it to the {@link PlayerReceiver} passed to the
         * constructor. For efficiency of the system as a whole, this method should do very little
         * or call another method that runs asynchronously.
         * @see PlayerReceiver
         */
        @Override
        public void execute() {
            PlayerManager.getManager(context).getLocalPlayer(context, receiver);
        }
    }
}
