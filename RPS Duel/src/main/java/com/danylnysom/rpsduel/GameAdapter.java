package com.danylnysom.rpsduel;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;

/**
 * Adapter for generating clickable items that the user can use to select a weapon.
 * <p/>
 * Right now they're buttons with text, but eventually they should be images.
 */
public class GameAdapter implements ListAdapter {
    private Game game;
    private GameCallBack myCallBack;

    public GameAdapter(Game game, GameCallBack callBack) {
        this.game = game;
        this.myCallBack = callBack;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return game.getWeaponCount();
    }

    @Override
    public Object getItem(int position) {
        return game.getWeapon(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Button button;
        if (convertView == null) {
            button = new Button(parent.getContext());
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
        button.setHeight(200);
        return button;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return game.getWeaponCount() == 0;
    }
}
