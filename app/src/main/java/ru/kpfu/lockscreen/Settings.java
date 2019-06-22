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
//внутреннее хранилище. зн
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Switch lockSwitch = findViewById(R.id.lock_switch);
        //получаем из памяти данные о том, включена ли блакировка переключен ли ползунок в активное положение)
        boolean lockScreenEnable = preferences.getBoolean(LOCK_ENABLE_KEY, false);
        processService(lockScreenEnable);

        lockSwitch.setChecked(lockScreenEnable);//передали значение ползунку, которое достали из памяти
        lockSwitch.setOnCheckedChangeListener((view, isChecked) -> changeLockScreenEnable(isChecked, preferences));//добавил действие, которое
        //должно применяться при смене значения ползунка

        findViewById(R.id.setup_password).setOnClickListener(view -> openSetupPassword());//находим кнопку "задать пароль"  и при нажатии не нее запустить метод openSetupPassword(
    }
//в зависимости от полученного значения (установлен флаг или нет ) мы либо запускаем сервис, либо нет
    private void processService(boolean needStart) {
        Intent intent = new Intent(this, MyService.class);
        if (needStart) {

            startService(intent);
        } else {
            stopService(intent);
        }
    }

    //записывает в хранилище текущее(новое) значение ползунка

    private void changeLockScreenEnable(boolean isEnable, SharedPreferences preferences) {
        preferences.edit()
                .putBoolean(LOCK_ENABLE_KEY, isEnable)
                .apply();
        processService(isEnable);
    }
//открывает экран Main activity
    private void openSetupPassword() {
        Intent newIntent = new Intent(this, MainActivity.class);
        newIntent.putExtra(MainActivity.MODE_KEY, true);
        startActivity(newIntent);
    }
}
