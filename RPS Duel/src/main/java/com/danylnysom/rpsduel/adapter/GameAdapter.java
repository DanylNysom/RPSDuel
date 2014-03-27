package com.danylnysom.rpsduel.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;

import com.danylnysom.rpsduel.game.Game;
import com.danylnysom.rpsduel.game.GameCallBack;

/**
 * Adapter for generating clickable items that the user can use to select a weapon.
 * <p/>
 * Right now they're buttons with text, but eventually they should be images.
 */
public class GameAdapter implements ListAdapter {
    private static final int BUTTON_HEIGHT = 200;
    private final Game game;
    private final GameCallBack myCallBack;
    private final Context context;

    public GameAdapter(Game game, GameCallBack callBack, Context context) {
        this.game = game;
        this.myCallBack = callBack;
        this.context = context;
    }

    /**
     * All items are always enabled.
     *
     * @return true
     */
    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    /**
     * Always returns true.
     *
     * @param position the index of the item in question
     * @return true
     */
    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    /**
     * Does nothing.
     *
     * @param observer not used
     */
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    /**
     * Does nothing.
     *
     * @param observer not used
     */
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    /**
     * Returns the number of available weapons in the current game.
     *
     * @return the number of available weapons in the current game
     */
    @Override
    public int getCount() {
        return game.getWeaponCount();
    }

    /**
     * Returns a weapon.
     *
     * @param position the index of the weapon
     * @return the weapon at the specified index
     */
    @Override
    public Object getItem(int position) {
        return game.getWeapon(position);
    }

    /**
     * Just returns the position
     *
     * @param position returned
     * @return position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Always returns true.
     *
     * @return true
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * Returns a button with text matching the name of the weapon at the specified position.
     *
     * @param position    the usual
     * @param convertView the usual
     * @param parent      the usual
     * @return the created button
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Button button;
        if (convertView == null) {
            button = new Button(context);
        } else {
            button = (Button) convertView;
        }
        button.setText(game.getWeapon(position));
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCallBack.weaponSelected(position);
            }
        });
        button.setHeight(BUTTON_HEIGHT);
        button.setTextScaleX(0.9f);
        return button;
    }

    /**
     * Always returns 0, since there is only one view type.
     *
     * @param position  the position of the item - doesn't actually matter
     * @return true
     */
    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    /**
     * Returns 1, since there is only one view type.
     *
     * @return 1
     */
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    /**
     * Queries if this weapon set is empty.
     *
     * @return true if the weapon set has size 0, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return game.getWeaponCount() == 0;
    }
}
