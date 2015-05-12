package info.dylansymons.rpsduel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences preferences = getSharedPreferences(PlayerManager.PLAYER_PREFS, MODE_PRIVATE);
        final String name = preferences.getString(PlayerManager.NAME, null);

        if (name != null) {
            logIn();
        }

        RelativeLayout contentView = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_welcome, null);
        final EditText nameBox = (EditText) contentView.findViewById(R.id.nameInput);
        nameBox.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submitName(nameBox, preferences);
                    return true;
                } else {
                    return false;
                }
            }
        });

        Button submitButton = (Button) contentView.findViewById(R.id.nameSubmitButton);
        submitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitName(nameBox, preferences);
            }
        });

        setContentView(contentView);
    }

    private void submitName(EditText nameBox, SharedPreferences preferences) {
        String name = nameBox.getText().toString().trim();
        if (!name.isEmpty()) {
            preferences.edit().putString(PlayerManager.NAME, name).apply();
            logIn();
        } else {
            Toast.makeText(this,
                    getResources().getString(R.string.error_missing_name),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void logIn() {
        Intent intent = new Intent(this, StatsActivity.class);
        startActivity(intent);
        finish();
    }
}
