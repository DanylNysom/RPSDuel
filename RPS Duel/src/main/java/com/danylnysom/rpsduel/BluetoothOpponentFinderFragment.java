package com.danylnysom.rpsduel;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class BluetoothOpponentFinderFragment extends Fragment {
    private static final String SERVER_NAME = "rpsduel";
    private static final String SERVER_UUID = "03579bd0-80c4-418f-a2c5-424f7733bf6e";
    private static final int DISCOVER_REQUEST_CODE = 16234;

    private boolean initiated = false;
    private ArrayList<String> opponents;
    private ArrayAdapter<String> opponentsAdapter;
    private BluetoothAdapter btadapter;
    private BluetoothServerSocket server;
    private BluetoothSocket socket;
    private BroadcastReceiver receiver = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ListView list = (ListView) inflater.inflate(R.layout.fragment_opponentfinder, null, false);
        opponents = new ArrayList<>();
        opponentsAdapter = new ArrayAdapter<>(container.getContext(), android.R.layout.simple_list_item_1, opponents);
        list.setAdapter(opponentsAdapter);
        list.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                btadapter.cancelDiscovery();
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
        return list;
    }

    @Override
    public void onStart() {
        super.onResume();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            btadapter = ((BluetoothManager) getActivity().getSystemService(Activity.BLUETOOTH_SERVICE)).getAdapter();
        } else {
            btadapter = BluetoothAdapter.getDefaultAdapter();
        }

        Player player = Player.getPlayer();
        btadapter.setName(player.getName() + "(" + player.getStat(Player.LEVEL) + ")");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    opponentsAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            }
        };

        /* Register BroadcastReceiver for finding new devices */
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(receiver, filter);

        /* Allow this device to be discovered by others */
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60);
        startActivityForResult(discoverableIntent, DISCOVER_REQUEST_CODE);

        /* Start searching for other devices */
        btadapter.startDiscovery();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DISCOVER_REQUEST_CODE:
                if (resultCode != Activity.RESULT_CANCELED) {
                    /* Start serversocket to accept connections */
                    new AcceptThread().start();
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (opponentsAdapter != null) {
            opponentsAdapter.clear();
        }
    }

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

    protected void connectionMade() {
    }

    private class AcceptThread extends Thread {
        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = btadapter.listenUsingRfcommWithServiceRecord(SERVER_NAME, UUID.fromString(SERVER_UUID));
            } catch (IOException e) {
                System.err.println("server didn't open???");
            }
            server = tmp;
        }

        public void run() {
            // Keep listening until exception occurs or a socket is returned
            while (socket != null) {
                try {
                    socket = server.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
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
         * Will cancel the listening socket, and cause the thread to finish
         */
        public void cancel() {
            try {
                server.close();
            } catch (IOException e) {
            }
        }
    }
}
