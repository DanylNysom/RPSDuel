package com.danylnysom.rpsduel.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.danylnysom.rpsduel.R;
import com.danylnysom.rpsduel.game.BluetoothGame;
import com.danylnysom.rpsduel.player.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * A fragment used for finding an opponent to connect with via bluetooth. Shows a list of nearby
 * discoverable bluetooth devices, allowing the user to select one to initiate a game.
 */
public class BluetoothOpponentFinderFragment extends Fragment {
    private static final String SERVER_NAME = "rpsduel";
    private static final String SERVER_UUID = "03579bd0-80c4-418f-a2c5-424f7733bf6e";
    private static final int DISCOVER_REQUEST_CODE = 16234;
    private static final int POPUP_DURATION = 60;
    private static final int CONFIRM = 111;

    private String originalName = null;
    private ArrayAdapter<String> opponentsAdapter;
    private BluetoothAdapter btadapter;
    private BluetoothServerSocket server;
    private BluetoothSocket socket;
    private BroadcastReceiver receiver = null;
    private AcceptThread serverThread = null;
    private Context context = null;
    private BluetoothGame game = null;
    private ProgressDialog progress = null;
    private int confirmation = -1;

    /**
     * Overridden to force usage of the newInstance() method.
     */
    private BluetoothOpponentFinderFragment() {
        super();
    }

    /**
     * Creates a new BluetoothOpponentFinderFragment instance.
     *
     * @return a new BluetoothOpponentFinderFragment
     */
    public static BluetoothOpponentFinderFragment newInstance(BluetoothGame game) {
        BluetoothOpponentFinderFragment boff = new BluetoothOpponentFinderFragment();
        boff.setRetainInstance(true);
        boff.game = game;
        return boff;
    }

    /**
     * Sets up the view to be ready for when other devices are discovered. Uses the
     * fragment_opponentfinder.xml layout.
     *
     * @param inflater           the usual, used to inflate the view
     * @param container          not used
     * @param savedInstanceState not used
     * @return the inflated and filled-in view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        context = inflater.getContext();
        ListView list = (ListView) inflater.inflate(R.layout.fragment_opponentfinder, null, false);
        opponentsAdapter = new ArrayAdapter<>(inflater.getContext(), android.R.layout.simple_list_item_1, new ArrayList<String>());
        if (list != null) {
            list.setAdapter(opponentsAdapter);
            list.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    btadapter.cancelDiscovery();

                    if (serverThread != null) {
                        serverThread.cancel();
                    }
                    String deviceString = opponentsAdapter.getItem(position);
                    final String[] opponent = deviceString.split("\n");
                    new RequestConnectionThread(opponent).start();

                    progress = new ProgressDialog(parent.getContext(), ProgressDialog.STYLE_SPINNER);
                    progress.setTitle("Waiting for confirmation");
                    progress.setOnDismissListener(new Dialog.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (confirmation == CONFIRM) {
                                connectionMade(opponent[0]);
                            } else {
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    progress.show();
                }
            });
        }
        return list;
    }

    /**
     * Starts looking for opponents and preparing for incoming connections. Requests that the user
     * allow the device to be discoverable, registers a reciever for when devices are found, and
     * starts searching for other devices.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            btadapter = ((BluetoothManager) getActivity().getSystemService(Activity.BLUETOOTH_SERVICE)).getAdapter();
        } else {
            btadapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (originalName == null) {
            originalName = btadapter.getName();
        }

        btadapter.cancelDiscovery();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {
                        opponentsAdapter.add(device.getName() + "\n" + device.getAddress());
                        opponentsAdapter.notifyDataSetChanged();
                    }
                }
            }
        };

        /* Register BroadcastReceiver for finding new devices */
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(receiver, filter);

        /* Allow this device to be discovered by others */
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, POPUP_DURATION);
        startActivityForResult(discoverableIntent, DISCOVER_REQUEST_CODE);

        /* Start searching for other devices */
        btadapter.startDiscovery();
    }

    /**
     * If the activity was one for requesting discoverability, start a server thread.
     * <p/>
     * There's no reason to start one if discoverability wasn't enabled, since no opponents will
     * be able to find this device let alone connect to it.
     *
     * @param requestCode the only code handled here is DISCOVER_REQUEST_CODE
     * @param resultCode  not used
     * @param data        not used
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DISCOVER_REQUEST_CODE:
                if (resultCode != Activity.RESULT_CANCELED) {
                    /* Start serversocket to accept connections */
                    serverThread = new AcceptThread(getActivity());
                    serverThread.start();
                }
        }
    }

    /**
     * Sets the bluetooth adapter's name to the player's name and level.
     * <p/>
     * The original name of the adapter will be saved, to be restored in onPause.
     */
    @Override
    public void onResume() {
        super.onResume();
        Player player = Player.getPlayer();
        btadapter.setName(player.getName() + "(" + player.getStat(Player.LEVEL) + ")");
        if (opponentsAdapter != null) {
            opponentsAdapter.clear();
        }
    }

    /**
     * Sets the name of the bluetooth adapter to what it originally was.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (originalName != null) {
            btadapter.setName(originalName);
        }
    }

    /**
     * Cancels everything that was started by this BluetoothOpponentFinderFragment instance.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (receiver != null) {
            try {
                getActivity().unregisterReceiver(receiver);
            } catch (IllegalArgumentException iae) {
                // don't worry about it - receiver just wasn't registered
            }
        }
        if (btadapter != null) {
            btadapter.cancelDiscovery();
        }
        if (opponentsAdapter != null) {
            opponentsAdapter.clear();
        }
    }

    /**
     * A connection has been made, so start the game!
     */
    void connectionMade(String opponent) {
        game.setSocketStreams(socket);
        game.setMultiplayerWeaponSet();
        getFragmentManager().popBackStackImmediate();
    }

    /**
     * A thread containing a BluetoothServerSocket that is used to wait for incoming connections.
     */
    private class AcceptThread extends Thread {
        /**
         * Needed for UI operations
         */
        private Activity activity;

        /**
         * Open a BluetoothServerSocket and reference it by the server field.
         */
        public AcceptThread(Activity activity) {
            this.activity = activity;
            BluetoothServerSocket tmp = null;
            try {
                tmp = btadapter.listenUsingInsecureRfcommWithServiceRecord(SERVER_NAME, UUID.fromString(SERVER_UUID));
            } catch (IOException e) {
                System.err.println("server didn't open???");
            }
            server = tmp;
        }

        /**
         * Listen for a connection on the server socket.
         * <p/>
         * When one is made, close the server and play a game using the socket returned by the
         * server.
         */
        public void run() {
            while (socket == null) {
                try {
                    socket = server.accept();
                    btadapter.cancelDiscovery();
                    byte[] buffer = new byte[1024];
                    int bytesRead = socket.getInputStream().read(buffer);
                    String name = new String(buffer, 0, bytesRead);
                    processIncomingRequest(name);
                } catch (IOException e) {
                    socket = null;
                    break;
                }
            }
        }

        /**
         * Tells the user that an opponent wants to duel, asks if they want to duel said opponent,
         * and lets them choose.
         * <p/>
         * If they want to duel a game is started, otherwise the BluetoothSocket is closed so a new
         * opponent can request a duel.
         *
         * @param name the name of the opponent
         */
        private void processIncomingRequest(final String name) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setMessage("Opponent " + name + " wants to duel")
                            .setOnCancelListener(new AlertDialog.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    try {
                                        socket.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    socket = null;
                                }
                            })
                            .setPositiveButton("Duel!", new AlertDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == AlertDialog.BUTTON_POSITIVE) {
                                        try {
                                            socket.getOutputStream().write(CONFIRM);
                                            server.close();
                                            connectionMade(name);
                                        } catch (IOException e) {

                                        }
                                    }
                                }
                            })
                            .setNegativeButton("Reject", new AlertDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which != AlertDialog.BUTTON_POSITIVE) {
                                        try {
                                            socket.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        socket = null;
                                    }
                                }
                            })
                            .create();
                    dialog.show();
                }
            });
        }

        /**
         * Close the server socket
         */
        public void cancel() {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A thread for requesting a duel.
     * <p/>
     * Basically just waits for a response from the target opponent. If this weren't its own thread
     * the GUI would lock up, which is functionality that was voted against at our last Round Table.
     */
    private class RequestConnectionThread extends Thread {
        private String name;
        private String address;

        public RequestConnectionThread(String[] opponent) {
            name = opponent[0];//deviceString.replaceFirst("^.*\n", "");
            address = opponent[1];
        }

        public void run() {
            BluetoothSocket tmp;
            BluetoothDevice target = btadapter.getRemoteDevice(address);

            try {
                tmp = target.createInsecureRfcommSocketToServiceRecord(UUID.fromString(SERVER_UUID));
                tmp.connect();
                socket = tmp;
                Player player = Player.getPlayer();
                byte[] connectMessage = (player.getName() + " (" + player.getStat(Player.LEVEL) + ")").getBytes();
                socket.getOutputStream().write(connectMessage);
                confirmation = socket.getInputStream().read();
                if (progress != null) {
                    progress.dismiss();
                    progress = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
