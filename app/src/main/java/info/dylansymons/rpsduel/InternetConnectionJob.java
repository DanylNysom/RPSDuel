package info.dylansymons.rpsduel;

import android.content.Context;

public abstract class InternetConnectionJob {
    public abstract void execute();

    public static class PlayerGetter extends InternetConnectionJob {
        private Context context;
        private PlayerReceiver receiver;

        public PlayerGetter(Context context, PlayerReceiver receiver) {
            this.context = context;
            this.receiver = receiver;
        }

        @Override
        public void execute() {
            PlayerManager.getManager(context).getLocalPlayer(context, receiver);
        }
    }
}
