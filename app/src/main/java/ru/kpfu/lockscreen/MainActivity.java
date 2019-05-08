package ru.kpfu.lockscreen;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements View.OnTouchListener, View.OnDragListener {

    public static final String TAG = MainActivity.class.getName();

    public static final String MODE_KEY = "set_password_mode";

    private List<Integer> correct = new ArrayList<>();
    private List<Integer> selected = new ArrayList<>();
    private TextView msg;
    private Random random = new Random();
    private Point displaySize;
    private View btnSave;

    // http://unicode.org/emoji/charts/full-emoji-list.html
    private String[] smiles = {
            "\uD83D\uDE00",
            "\uD83D\uDE01",
            "\uD83D\uDE02",
            "\uD83D\uDE06",
            "\uD83D\uDE0E",
            "\uD83D\uDE48",
            "\uD83D\uDE49",
            "\uD83D\uDE4A",
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

        findViewById(R.id.top_container).setOnDragListener(this);
        findViewById(R.id.bottom_container).setOnDragListener(this);
        btnSave = findViewById(R.id.btn_save);

        msg = (TextView) findViewById(R.id.textView2);

        for (int i = 0; i < smiles.length; i++) {
            TextView smile = (TextView) findViewById(smileIds[i]);
            smile.setText(smiles[i]);
            smile.setVisibility(View.VISIBLE);
            smile.setOnTouchListener(this);
        }

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
    }

    private void setRandomPos(View view) {
        int x = random.nextInt(displaySize.x - view.getWidth());
        int y = random.nextInt((4 * displaySize.y / 5) - view.getHeight());

        view.setX(x);
        view.setY(y);
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
    public boolean onDrag(View v, DragEvent e) {
        if (e.getAction() == DragEvent.ACTION_DROP) {
            View view = (View) e.getLocalState();
            ViewGroup from = (ViewGroup) view.getParent();
            from.removeView(view);
            ViewGroup to = (ViewGroup) v;
            to.addView(view);

            view.setVisibility(View.VISIBLE);

            if (from != to) {
                if (to.getId() == R.id.bottom_container) {
                    if (selected.indexOf(view.getId()) == -1) {
                        selected.add(view.getId());
                        btnSave.setEnabled(true);
                        to.removeView(msg);
                    }
                    view.setX(0);
                    view.setY(0);
                } else if (from.getId() == R.id.bottom_container) {
                    selected.remove(Integer.valueOf(view.getId()));
                    if (selected.isEmpty()) {
                        from.addView(msg);
                        btnSave.setEnabled(false);
                    }
                    view.setX(e.getX() - view.getWidth() / 2);
                    view.setY(e.getY() - view.getHeight() / 2);
                }

                if (correct.equals(selected)) {
                    finish();
                }
            } else {
                view.setX(e.getX() - view.getWidth() / 2);
                view.setY(e.getY() - view.getHeight() / 2);
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
}
