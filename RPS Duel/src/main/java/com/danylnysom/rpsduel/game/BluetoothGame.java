package com.danylnysom.rpsduel.game;

import android.bluetooth.BluetoothSocket;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.danylnysom.rpsduel.fragment.BluetoothOpponentFinderFragment;
import com.danylnysom.rpsduel.fragment.GameFragment;
import com.danylnysom.rpsduel.player.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An instance of a Game with a human opponent connected via Bluetooth.
 * <p/>
 * Upon construction, a BluetoothOpponentFinderFragment will be created so that an opponent can be
 * found and a connection made.
 * The opponent's weapon is retrieved via a BluetoothSocket, through which the player's choice is
 * sent.
 */
public class BluetoothGame extends Game {
    private BluetoothSocket socket;
    private InputStream in = null;
    private OutputStream out = null;
    private String opponentName = null;
    private int opponentLevel;

    /**
     * Creates a new BluetoothGame and displays it in the location of the specified fragment.
     * Upon return, a BluetoothOpponentFinderFragment will be displayed.
     *
     * @param fragment the fragment being used to display this game
     */
    public BluetoothGame(GameFragment fragment) {
        super(fragment);
        FragmentManager fm = fragment.getFragmentManager();
        fm.popBackStack("bluetoothgame", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(fragment.getId(), BluetoothOpponentFinderFragment.newInstance(this));
        ft.addToBackStack("bluetoothgame");
        ft.commit();
    }

    /**
     * I highly recommend doing this before trying to read or write from the streams. For example,
     * call this before getResult() unless you do not want to play RPS today.
     * <p/>
     * The socket's InputStream and OutputStream are read and this BluetoothGame's associated member
     * variables are set, in case you were wondering.
     *
     * @param socket the socket from whence the streams shall come
     * @return true if there wasn't a problem. Good luck trying to figure out what went wrong if
     * false is returned.
     */
    public boolean setSocketStreams(BluetoothSocket socket) {
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            this.socket = socket;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Copies this game's opponent details.
     *
     * @param opponentTag the opponent's name and level in the format:
     *                    "[NAME] ([LEVEL])"
     *                    (no quotation marks, of course)
     */
    public void setOpponent(String opponentTag) {
        int parenIndex = opponentTag.lastIndexOf("(");
        opponentName = opponentTag.substring(0, parenIndex);
        opponentLevel =
                Integer.parseInt(opponentTag.substring(parenIndex + 1, opponentTag.length() - 1));
    }

    /**
     * Get's the opponents weapon choice from the InputStream, sends the players weapon via the
     * OutputStream, outputs the result, and updates the player's score accordingly. It's pretty
     * wonderful.
     * <p/>
     * You did make sure that you made a successful call to setSocketStreams(BluetoothSocket) first,
     * right???
     */
    @Override
    public void getResult() {
        try {
            out.write(playerChoice);
            opponentChoice = in.read();
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Player player = Player.getPlayer();
        int result = getWinStatus();
        int level = player.getStat(Player.LEVEL);
        switch (result) {
            case -1:
                player.addGame((int) (-1000 * level / (4.0 * opponentLevel) * (Math.pow(2, level - 3))),
                        fragment.getActivity());
                break;
            case 1:
                player.addGame((int) (1000 * (Math.pow(2, level - 5) + Math.pow(2, opponentLevel - 6))),
                        fragment.getActivity());
                break;
            case 0:
                Toast.makeText(fragment.getActivity(), "You tied - try again", Toast.LENGTH_SHORT).show();
                playerChoice = -1;
                opponentChoice = -1;
                break;
        }
        if (result != 0) {
            fragment.hideGrid();
        }
    }
}
