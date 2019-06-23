package ru.kpfu.lockscreen;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Display;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements View.OnTouchListener, View.OnDragListener, View.OnClickListener {

    public static final String MODE_KEY = "set_password_mode";

    private static final float IMAGE_SIZE = 46f;
    private static final float TOP_CONTAINER_PROPORTIONS = 0.8f;
    public static final int RESULT_ENABLE = 11;

    private List<Integer> correct = new ArrayList<>();
    private List<Integer> selected = new ArrayList<>();
    private TextView msg;
    private Random random = new Random();
    private Point displaySize;
    private View btnSave;
    private ComponentName compName;

    private ViewGroup topContainer;

    private int[] icons = {
            R.drawable.elephant,
            R.drawable.fox,
            R.drawable.owl,
            R.drawable.rabbit,
            R.drawable.raccoon,
            R.drawable.squirrel,
            R.drawable.turtle,
            R.drawable.zebra
    };

    int[] smileIds = {
            R.id.smile_1,
            R.id.smile_2,
            R.id.smile_3,
            R.id.smile_4,
            R.id.smile_5,
            R.id.smile_6,
            R.id.smile_7,
            R.id.smile_8
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        displaySize = new Point();
        display.getSize(displaySize);

        topContainer = findViewById(R.id.top_container);
        topContainer.setOnDragListener(this);
        findViewById(R.id.bottom_container).setOnDragListener(this);
        btnSave = findViewById(R.id.btn_save);

        compName = new ComponentName(this, MainActivity.class);

        msg = findViewById(R.id.textView2);

        for (int i = 0; i < icons.length; i++) {
            ImageView smile = findViewById(smileIds[i]);
            smile.setImageResource(icons[i]);
            smile.setVisibility(View.VISIBLE);
            smile.setOnTouchListener(this);
        }

        View rootLayout = findViewById(R.id.rootLayout);
        rootLayout.setOnClickListener(this);


        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        if (getIntent().getBooleanExtra(MODE_KEY, false)) {
            btnSave.setVisibility(View.VISIBLE);
            btnSave.setEnabled(false);
            btnSave.setOnClickListener(v -> {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                StringBuilder ids = new StringBuilder();
                for (Integer integer : selected) {
                    if (ids.length() != 0) {
                        ids.append(',');
                    }
                    ids.append(integer);
                }
                editor.putString("PASS", ids.toString());
                editor.apply();
                finish();
            });
        }

        String pass = sharedPrefs.getString("PASS", "");
        if (pass.isEmpty()) {
            correct.add(smileIds[5]);
            correct.add(smileIds[6]);
            correct.add(smileIds[7]);
        } else {
            String[] ids = pass.split(",");
            for (String id : ids) {
                correct.add(Integer.valueOf(id));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int smileId : smileIds) {
            View viewById = findViewById(smileId);
            setRandomPos(viewById);
        }
        hideAllNavigationElements();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideAllNavigationElements();
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(null, shadowBuilder, v, 0);
            v.setVisibility(View.INVISIBLE);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        hideAllNavigationElements();
    }

    @Override
    public boolean onDrag(View draggedView, DragEvent dragEvent) {
        if (dragEvent.getAction() == DragEvent.ACTION_DROP) {
            View view = (View) dragEvent.getLocalState();
            ViewGroup fromViewGroup = (ViewGroup) view.getParent();
            fromViewGroup.removeView(view);
            ViewGroup toViewGroup = (ViewGroup) draggedView;
            toViewGroup.addView(view);

            view.setVisibility(View.VISIBLE);

            if (fromViewGroup != toViewGroup) {
                if (toViewGroup.getId() == R.id.bottom_container) {
                    if (selected.indexOf(view.getId()) == -1) {
                        selected.add(view.getId());
                        btnSave.setEnabled(true);
                        toViewGroup.removeView(msg);
                    }
                    view.setX(0);
                    view.setY(0);
                } else if (fromViewGroup.getId() == R.id.bottom_container) {
                    selected.remove((Integer) view.getId());
                    if (selected.isEmpty()) {
                        btnSave.setEnabled(false);
                    }
                    view.setX(dragEvent.getX() - view.getHeight() / 2);
                    view.setY(dragEvent.getY() - view.getWidth() / 2);
                }

                if (correct.equals(selected)) {
                    finish();
                }
            } else {
                view.setX(dragEvent.getX() - view.getHeight() / 2);
                view.setY(dragEvent.getY() - view.getWidth() / 2);
            }
        }
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void hideAllNavigationElements() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void setRandomPos(View view) {
        int px = toPx(IMAGE_SIZE);
        int x = random.nextInt(displaySize.x - px);
        int y = random.nextInt((int) ((displaySize.y * TOP_CONTAINER_PROPORTIONS) - px));

        view.setX(x);
        view.setY(y);
    }

    private int toPx(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
