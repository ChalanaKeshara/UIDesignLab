package com.example.UIDesignLab;
import android.content.SharedPreferences;
import android.widget.CheckBox;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import android.content.Intent;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LifecycleTracker";
    public static final String PREFS_NAME = "UserPrefs";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_REMEMBER = "remember";
    private static final int COLOR_NORMAL =
            Color.parseColor("#6200EE");

    private static final int COLOR_PRESSED =
            Color.parseColor("#FF9800");

    private ToneGenerator toneGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toneGenerator = new ToneGenerator(
                AudioManager.STREAM_MUSIC, 100);

        Log.d(TAG, "onCreate: Activity is being created");

        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);
        Button btnSave = findViewById(R.id.btnSave);

        EditText editUsername = findViewById(R.id.editUsername);

        CheckBox chkRemember = findViewById(R.id.chkRemember);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        boolean remember = prefs.getBoolean(KEY_REMEMBER, false);

        chkRemember.setChecked(remember);

        if (remember) {
            editUsername.setText(prefs.getString(KEY_USERNAME, ""));
        }

        btnSave.setOnClickListener(v -> {

            Toast.makeText(this, "Processing...",
                    Toast.LENGTH_SHORT).show();

            btnSave.setBackgroundColor(COLOR_PRESSED);

            new Handler(Looper.getMainLooper()).postDelayed(
                    () -> btnSave.setBackgroundColor(COLOR_NORMAL),
                    1000
            );

            toneGenerator.startTone(
                    ToneGenerator.TONE_PROP_BEEP, 200);

            TextView result = new TextView(this);

            result.setId(View.generateViewId());

            result.setText("Profile Saved Successfully!");

            result.setTextColor(Color.parseColor("#2E7D32"));

            result.setTextSize(18);

            ConstraintLayout.LayoutParams params =
                    new ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.WRAP_CONTENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT
                    );

            params.topToBottom = R.id.chkRemember;
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            params.topMargin = 48;

            mainLayout.addView(result, params);


            String username = editUsername.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter your username",
                        Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences.Editor editor = prefs.edit();

                if (chkRemember.isChecked()) {
                    editor.putString(KEY_USERNAME, username);
                    editor.putBoolean(KEY_REMEMBER, true);
                } else {
                    editor.remove(KEY_USERNAME);
                    editor.putBoolean(KEY_REMEMBER, false);
                }

                editor.apply();

                final String nameToSave = username;

                AppDatabase.databaseExecutor.execute(() -> {

                    long id = AppDatabase
                            .getInstance(getApplicationContext())
                            .userDao()
                            .insert(new UserRecord(nameToSave));

                    Log.d("RoomLab",
                            "Inserted " + nameToSave + ", id = " + id);
                });

                Intent intent = new Intent(MainActivity.this,
                        DashboardActivity.class);

                intent.putExtra("USERNAME", username);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Activity is becoming visible");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Activity is in the foreground");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Activity is losing focus");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Activity is no longer visible");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity is being destroyed");
    }
}