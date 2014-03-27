package com.danylnysom.rpsduel.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danylnysom.rpsduel.R;
import com.danylnysom.rpsduel.activity.ActionbarCallback;
import com.danylnysom.rpsduel.player.Player;

/**
 * A Fragment implementation to be displayed upon first run of the program, or when the user's data
 * has been cleared.
 * <p/>
 * A textbox is provided for the user to enter a name, with an "OK" button to confirm.
 */
public class NewUserFragment extends Fragment {
    private String name = null;

    /**
     * Inflates a view from the fragment_newuser.xml layout.
     * <p/>
     * The view contains a message instructing the user to specify a name, with a text box and OK
     * button to be used by the user for such.
     *
     * @param inflater           the inflater to inflate the layout with
     * @param container          not used
     * @param savedInstanceState not used
     * @return the inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_newuser, null, false);
        ((ActionBarActivity) getActivity()).getSupportActionBar().hide();
        if (view != null) {
            final EditText textBox = (EditText) view.findViewById(R.id.name_box);
            final Button ok = (Button) view.findViewById(R.id.ok_button);
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
                    if (textBox != null && textBox.getText() != null) {
                        name = textBox.getText().toString();
                        InputMethodManager imm = (InputMethodManager) getActivity()
                                .getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textBox.getWindowToken(), 0);
                    }
                    Player.getPlayer().setName(name);
                    ((ActionbarCallback) getActivity()).initializeActionBar();
                    getFragmentManager().popBackStack();
                }
            });

            textBox.requestFocus();
        }
        return view;
    }

    /**
     * If this fragment is detached before a name has been provided by the user, exits the program.
     * If a name has been provided, nothing out of the ordinary happens.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        if (name == null) {
            getActivity().finish();
        }
    }
}
