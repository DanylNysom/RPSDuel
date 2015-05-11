package info.dylansymons.rpsduel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
        final String name = preferences.getString(PlayerManager.NAME_PREF, null);

//        if(name != null) {
//            logIn(name, preferences);
//        }

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
            logIn(name, preferences);
        } else {
            Toast.makeText(this,
                    getResources().getString(R.string.error_missing_name),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void logIn(String name, SharedPreferences preferences) {
        preferences.edit().putString(PlayerManager.NAME_PREF, name).apply();
        Toast.makeText(this,
                "Hello, " + name,
                Toast.LENGTH_SHORT)
                .show();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LogInListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {

        }
    }
}
