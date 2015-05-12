package info.dylansymons.rpsduel;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.google.android.gms.plus.Plus;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PlusBaseActivity {
    private static PlusBaseActivity plusActivity;
    private SettingsFragment fragment;

    @Override
    protected void onPlusClientSignIn() {
        String email = Plus.AccountApi.getAccountName(getPlusClient());
        PlayerManager.getManager(this).setLocalPlayer(email);
    }

    @Override
    protected void onPlusClientSignOut() {
    }

    @Override
    protected void onPlusClientBlockingUI(boolean show) {
    }

    @Override
    protected void updateConnectButtonState() {
        boolean connected = getPlusClient().isConnected();

        if (connected) {
            fragment.googleSignedIn();
        } else {
            fragment.googleSignedOut();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        plusActivity = this;
        fragment = new SettingsFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit();
    }

    @Override
    public void onConnectionSuspended(int i) {
        updateConnectButtonState();
        onPlusClientSignOut();
    }

    public static class SettingsFragment extends PreferenceFragment {
        private EditTextPreference name;
        private Preference account, disconnect, delete;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            addPreferencesFromResource(R.xml.pref_google);

            Resources res = getResources();
            name = (EditTextPreference) findPreference(res.getString(R.string.pref_key_name));
            initName();

            account = findPreference(res.getString(R.string.pref_key_google_account));
            disconnect = findPreference(res.getString(R.string.pref_key_google_disconnect));
            delete = findPreference(res.getString(R.string.pref_key_google_delete));
        }

        private void initName() {
            String nameString = PlayerManager.getManager(getActivity()).getName(getActivity());
            name.setDefaultValue(nameString);
            name.setSummary(nameString);
            name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary((String) newValue);
                    PlayerManager.getManager(getActivity()).updateName(getActivity(), (String) newValue);
                    return true;
                }
            });
        }

        public void googleSignedIn() {
            setButtonVisibility(true);
            account.setSummary(Plus.AccountApi.getAccountName(plusActivity.getPlusClient()));
            disconnect.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    plusActivity.signOut();
                    return true;
                }
            });
            delete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    plusActivity.revokeAccess();
                    PlayerManager.getManager(plusActivity).deleteAccount(plusActivity);
                    return true;
                }
            });
        }

        public void googleSignedOut() {
            setButtonVisibility(false);
            account.setSummary(R.string.no_google_account);
            account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    plusActivity.signIn();
                    return true;
                }
            });
        }

        private void setButtonVisibility(boolean connected) {
            disconnect.setEnabled(connected);
            delete.setEnabled(connected);
        }
    }
}
