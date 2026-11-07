
package com.example.UIDesignLab;

import android.os.Bundle;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

public class UsersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewUsers);

        if (recyclerView == null) {
            Toast.makeText(this,
                    "RecyclerView not found in activity_users.xml",
                    Toast.LENGTH_LONG).show();
            return;
        }

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new UserAdapter();
        recyclerView.setAdapter(adapter);

        Toast.makeText(this,
                "Saved Users Opened",
                Toast.LENGTH_SHORT).show();

        // Add John button
        findViewById(R.id.btnAddJohn).setOnClickListener(v -> {

            AppDatabase.databaseExecutor.execute(() -> {

                try {
                    AppDatabase.getInstance(getApplicationContext())
                            .userDao()
                            .insert(new UserRecord("John"));

                    loadUsers();

                } catch (Exception e) {
                    Log.e("UsersActivity", "Insert error", e);
                }
            });
        });

        // Clear All button
        findViewById(R.id.btnClearAll).setOnClickListener(v -> {

            AppDatabase.databaseExecutor.execute(() -> {

                try {
                    AppDatabase.getInstance(getApplicationContext())
                            .userDao()
                            .deleteAll();

                    loadUsers();

                } catch (Exception e) {
                    Log.e("UsersActivity", "Delete error", e);
                }
            });
        });

        // Read Provider button
        findViewById(R.id.btnReadProvider).setOnClickListener(v -> {

            AppDatabase.databaseExecutor.execute(() -> {

                StringBuilder result = new StringBuilder();

                Uri uri = Uri.parse(
                        "content://com.example.uidesignlab.provider/users"
                );

                try (Cursor cursor = getContentResolver().query(
                        uri,
                        null,
                        null,
                        null,
                        null
                )) {

                    if (cursor != null) {

                        while (cursor.moveToNext()) {

                            String name = cursor.getString(
                                    cursor.getColumnIndexOrThrow("name")
                            );

                            result.append(name).append("\n");
                        }
                    }

                } catch (Exception e) {

                    Log.e("UsersActivity",
                            "Content Provider error", e);

                    result.append("Provider error: ")
                            .append(e.getMessage());
                }

                runOnUiThread(() -> {

                    if (result.length() > 0) {

                        Toast.makeText(
                                UsersActivity.this,
                                result.toString(),
                                Toast.LENGTH_LONG
                        ).show();

                    } else {

                        Toast.makeText(
                                UsersActivity.this,
                                "No users found!",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            });
        });

    }

    // Load users from Room Database
    private void loadUsers() {

        AppDatabase.databaseExecutor.execute(() -> {

            try {

                List<UserRecord> users =
                        AppDatabase.getInstance(getApplicationContext())
                                .userDao()
                                .getAll();

                runOnUiThread(() -> adapter.setUsers(users));

            } catch (Exception e) {

                Log.e("UsersActivity",
                        "Loading users failed", e);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }
}
