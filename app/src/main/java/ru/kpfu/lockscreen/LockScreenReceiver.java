package ru.kpfu.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LockScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent newIntent;
        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_SCREEN_OFF:
                newIntent = new Intent(context, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(newIntent);
                break;
            case Intent.ACTION_SCREEN_ON:
                new Intent(context, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            case Intent.ACTION_BOOT_COMPLETED:
                newIntent = new Intent(context, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(newIntent);
                break;
        }
    }
}
