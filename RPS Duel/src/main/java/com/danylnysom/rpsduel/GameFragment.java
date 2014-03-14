package com.danylnysom.rpsduel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A Fragment implementation representing a game against an arbitrary opponent type/connection.
 */
public class GameFragment extends Fragment implements GameCallBack {
    public static final int PRACTICE = 0;
    public static final int NFC = 1;
    public static final int BLUETOOTH = 2;
    public static final int WIFI = 3;
    private final static String GAMETYPE_KEY = "type";
    private final static String WEAPONCOUNT_KEY = "count";
    private static Toast currentToast = null;

    private Game game;

    public static GameFragment newInstance(int weaponCount, int type) {
        GameFragment gf = new GameFragment();
        Bundle args = new Bundle();
        args.putInt(WEAPONCOUNT_KEY, weaponCount);
        args.putInt(GAMETYPE_KEY, type);
        gf.setArguments(args);
        return gf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        int weaponCount = args.getInt(WEAPONCOUNT_KEY);
        int gameType = args.getInt(GAMETYPE_KEY, PRACTICE);
        switch (gameType) {
            case PRACTICE:
                game = new PracticeGame(weaponCount);
                break;
            default:
                game = new PracticeGame(weaponCount);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_game, null, false);
        GridView grid = (GridView) view.findViewById(R.id.weapon_grid);
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
        int result = game.getResult();
        String text;
        switch (result) {
            case -1:
                text = "You lose";
                break;
            case 1:
                text = "You win";
                break;
            default:
                text = "You tie";
        }
        game.displayResultMessage(playerMessage, message, opponentMessage);
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        currentToast.setGravity(Gravity.CENTER, 0, 0);
        currentToast.show();
    }
}
