package ru.kpfu.lockscreen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

@TargetApi(16)
public class Settings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        RelativeLayout rel5 = (RelativeLayout) findViewById(R.id.relEnabled);
        ImageView txtEna = (ImageView) findViewById(R.id.btnEnaOnOff);
        if (sharedPrefs.getInt("CHK_ENABLE", 0) == 1) {
            txtEna.setImageResource(R.drawable.chek);
            startService(new Intent(this, MyService.class));
        } else {
            txtEna.setImageResource(R.drawable.unchek);
            stopService(new Intent(this, MyService.class));
        }
        rel5.setOnClickListener(v -> {
            Editor editor = sharedPrefs.edit();
            if (sharedPrefs.getInt("CHK_ENABLE", 0) == 1) {
                stopService(new Intent(Settings.this, MyService.class));
                editor.putInt("CHK_ENABLE", 0);
                txtEna.setImageResource(R.drawable.unchek);
            } else {
                startService(new Intent(Settings.this, MyService.class));
                editor.putInt("CHK_ENABLE", 1);
                txtEna.setImageResource(R.drawable.chek);
            }
            editor.apply();
        });

        RelativeLayout rel6 = (RelativeLayout) findViewById(R.id.setPassword);
        rel6.setOnClickListener(v -> {
            Intent newIntent = new Intent(Settings.this, MainActivity.class)
                    .putExtra(MainActivity.MODE_KEY, true);
            Settings.this.startActivity(newIntent);
        });
    }

}
