package com.danylnysom.rpsduel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A Fragment implementation to be displayed upon first run of the program, or when the user's data
 * has been cleared.
 *
 * A textbox is provided for the user to enter a name, with an "OK" button to confirm.
 */
public class NewUserFragment extends Fragment {
    String name = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_newuser, null, false);
        final EditText textBox = (EditText) view.findViewById(R.id.name_box);
        final Button ok = (Button) view.findViewById(R.id.ok_button);
        ((RPSActivity) getActivity()).getSupportActionBar().hide();
        textBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                ok.setEnabled(s.length() > 0);
            }
        });

        textBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (textBox.length() > 0) {
                        return ok.performClick();
                    } else {
                        Toast toast = Toast.makeText(getActivity(), "Please enter a name", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return true;
                    }
                }
                return false;
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = textBox.getText().toString();
                initializePlayer();
                ((RPSActivity) getActivity()).initializeActionBar(name);
                getFragmentManager().popBackStack();
            }
        });

        textBox.requestFocus();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (name == null) {
            getActivity().finish();
        }
    }

    private void initializePlayer() {
        SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        editor.putString(Player.NAME, name);
        editor.commit();
    }
}
