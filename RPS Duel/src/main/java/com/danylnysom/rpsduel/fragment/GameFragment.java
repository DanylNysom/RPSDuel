package com.danylnysom.rpsduel.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.danylnysom.rpsduel.R;
import com.danylnysom.rpsduel.activity.ActionbarCallback;
import com.danylnysom.rpsduel.adapter.GameAdapter;
import com.danylnysom.rpsduel.game.BluetoothGame;
import com.danylnysom.rpsduel.game.Game;
import com.danylnysom.rpsduel.game.GameCallBack;
import com.danylnysom.rpsduel.game.PracticeGame;

/**
 * A Fragment implementation representing a game against an arbitrary opponent type/connection.
 */
public class GameFragment extends Fragment implements GameCallBack {
    private final static String GAMETYPE_KEY = "type";

    private GridView grid;

    private Game game;

    /**
     * Creates a new GameFragment with the provided game type specified as the GAMETYPE_KEY argument.
     *
     * @param type the type of game to be played
     * @return a new GameFragment of the requested type
     */
    public static GameFragment newInstance(Game.CONNECTION_TYPE type) {
        GameFragment gf = new GameFragment();
        Bundle args = new Bundle();
        args.putString(GAMETYPE_KEY, type.toString());
        gf.setArguments(args);
        gf.setRetainInstance(true);
        return gf;
    }

    /**
     * Creates a game based on the GAMETYPE_KEY argument.
     *
     * @param savedInstanceState not used
     */
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
                game = new BluetoothGame(this);
                break;
            case WIFI:
                break;
            default:
                game = new PracticeGame(getActivity(), this);
                break;
        }
    }

    /**
     * Creates the grid to display the weapons in, as well as the spots for the player weapon choice,
     * opponent weapon choice, and game result message.
     *
     * @param inflater           as usual
     * @param container          not used
     * @param savedInstanceState not used
     * @return the created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        if (view != null) {
            grid = (GridView) view.findViewById(R.id.weapon_grid);
            grid.setAdapter(new GameAdapter(game, this, getActivity()));
        }
        return view;
    }

    /**
     * Called after the player has chosen a weapon. This calls game.getResult() to find and display
     * the game's result, updates the player's stats, and updates the point and level displays in
     * the action bar.
     *
     * @param position the index in the associated Game of the selected weapon
     */
    @Override
    public void weaponSelected(int position) {
        View view = getView();
        TextView playerMessage = (TextView) view.findViewById(R.id.playerChoice);
        TextView opponentMessage = (TextView) view.findViewById(R.id.opponentChoice);
        TextView message = (TextView) view.findViewById(R.id.message);

        game.setPlayerChoice(position);
        game.getResult();

        game.displayResultMessage(playerMessage, message, opponentMessage);

        ((ActionbarCallback) getActivity()).updateLevelDisplay();
    }

    /**
     * Makes the weapon grid disappear before your very eyes - poof!
     */
    public void hideGrid() {
        grid.setVisibility(View.INVISIBLE);
    }

    /**
     * Recreates the grid of weapons.
     */
    public void recreateView() {
        grid.setAdapter(new GameAdapter(game, this, getActivity()));
        grid.setVisibility(View.VISIBLE);
        grid.invalidate();
    }
}
