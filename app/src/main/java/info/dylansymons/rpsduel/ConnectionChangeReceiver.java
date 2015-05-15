package info.dylansymons.rpsduel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Class to receive notification of network state changes. On any state change, all of the pending
 * {@link InternetConnectionJob} instances will be executed through the
 * {@link InternetConnectionJobManager} singleton.
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        InternetConnectionJobManager.getManager().executeAll();
    }
}
