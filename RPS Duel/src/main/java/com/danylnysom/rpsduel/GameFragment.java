package com.danylnysom.rpsduel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A Fragment implementation representing a game against an arbitrary opponent type/connection.
 */
public class GameFragment extends Fragment implements GameCallBack {
    private final static String GAMETYPE_KEY = "type";
    private final static String WEAPONCOUNT_KEY = "count";
    private static Toast currentToast = null;

    private GridView grid;

    private Game game;

    public static GameFragment newInstance(Game.CONNECTION_TYPE type) {
        GameFragment gf = new GameFragment();
        Bundle args = new Bundle();
        args.putString(GAMETYPE_KEY, type.toString());
        gf.setArguments(args);
        return gf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        Game.CONNECTION_TYPE gameType =
                Game.CONNECTION_TYPE.valueOf(args.getString(GAMETYPE_KEY));
        switch (gameType) {
            case PRACTICE:
                game = new PracticeGame(getActivity(), this);
                break;
            case NFC:
                break;
            case BLUETOOTH:
                game = new BluetoothGame(getActivity(), this);
            case WIFI:
                break;
            default:
                game = new PracticeGame(getActivity(), this);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        grid = (GridView) view.findViewById(R.id.weapon_grid);
        grid.setAdapter(new GameAdapter(game, this));
        return view;
    }

    @Override
    public void weaponSelected(int position) {
        View view = getView();
        TextView playerMessage = (TextView) view.findViewById(R.id.playerChoice);
        TextView opponentMessage = (TextView) view.findViewById(R.id.opponentChoice);
        TextView message = (TextView) view.findViewById(R.id.message);

        game.setPlayerChoice(position);
        game.getResult();

        game.displayResultMessage(playerMessage, message, opponentMessage);
        if (currentToast != null) {
            currentToast.cancel();
        }

        ((RPSActivity) getActivity()).updateLevelDisplay();
    }

    public void recreateView() {
        grid.setAdapter(new GameAdapter(game, this));
        grid.invalidate();
    }
}
