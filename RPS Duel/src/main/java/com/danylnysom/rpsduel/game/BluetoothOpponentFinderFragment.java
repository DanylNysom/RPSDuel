package com.danylnysom.rpsduel.game;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import com.danylnysom.rpsduel.player.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * A fragment used for finding an opponent to connect with via bluetooth. Shows a list of nearby
 * discoverable bluetooth devices, allowing the user to select one to initiate a game.
 */
class BluetoothOpponentFinderFragment extends Fragment {
    private static final String SERVER_NAME = "rpsduel";
    private static final String SERVER_UUID = "03579bd0-80c4-418f-a2c5-424f7733bf6e";
    private static final int DISCOVER_REQUEST_CODE = 16234;
    private static final int POPUP_DURATION = 60;

    private String originalName = null;
    private boolean initiated = false;
    private ArrayAdapter<String> opponentsAdapter;
    private BluetoothAdapter btadapter;
    private BluetoothServerSocket server;
    private BluetoothSocket socket;
    private BroadcastReceiver receiver = null;
    private AcceptThread serverThread = null;

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
                    String address = deviceString.replaceFirst("^.*\n", "");

                    BluetoothSocket tmp = null;
                    BluetoothDevice target = btadapter.getRemoteDevice(address);
                    try {
                        tmp = target.createRfcommSocketToServiceRecord(UUID.fromString(SERVER_UUID));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    socket = tmp;
                    connectionMade();
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
    public void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            btadapter = ((BluetoothManager) getActivity().getSystemService(Activity.BLUETOOTH_SERVICE)).getAdapter();
        } else {
            btadapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (originalName == null) {
            originalName = btadapter.getName();
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {
                        opponentsAdapter.add(device.getName() + "\n" + device.getAddress());
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
                    serverThread = new AcceptThread();
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
        btadapter.setName(originalName);
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
    void connectionMade() {
    }

    /**
     * A thread containing a BluetoothServerSocket that is used to wait for incoming connections.
     */
    private class AcceptThread extends Thread {
        /**
         * Open a BluetoothServerSocket and reference it by the server field.
         */
        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = btadapter.listenUsingRfcommWithServiceRecord(SERVER_NAME, UUID.fromString(SERVER_UUID));
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
            while (socket != null) {
                try {
                    socket = server.accept();
                } catch (IOException e) {
                    break;
                }
                if (socket != null) {
                    try {
                        initiated = true;
                        server.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    connectionMade();
                    break;
                }
            }
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
}
