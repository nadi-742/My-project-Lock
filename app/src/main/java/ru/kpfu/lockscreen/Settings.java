package ru.kpfu.lockscreen;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Switch;

public class Settings extends Activity {

    private static final String LOCK_ENABLE_KEY = "CHK_ENABLE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Switch lockSwitch = findViewById(R.id.lock_switch);

        boolean lockScreenEnable = preferences.getBoolean(LOCK_ENABLE_KEY, false);
        processService(lockScreenEnable);

        lockSwitch.setChecked(lockScreenEnable);
        lockSwitch.setOnCheckedChangeListener((view, isChecked) -> changeLockScreenEnable(isChecked, preferences));

        findViewById(R.id.setup_password).setOnClickListener(view -> openSetupPassword());
    }

    private void processService(boolean needStart) {
        Intent intent = new Intent(this, MyService.class);
        if (needStart) {
            startService(intent);
        } else {
            stopService(intent);
        }
    }

    private void changeLockScreenEnable(boolean isEnable, SharedPreferences preferences) {
        preferences.edit()
                .putBoolean(LOCK_ENABLE_KEY, isEnable)
                .apply();
        processService(isEnable);
    }

    private void openSetupPassword() {
        Intent newIntent = new Intent(this, MainActivity.class);
        newIntent.putExtra(MainActivity.MODE_KEY, true);
        startActivity(newIntent);
    }
}
