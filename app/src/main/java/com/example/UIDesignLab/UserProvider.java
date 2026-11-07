
package com.example.UIDesignLab;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.ContentUris;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class UserProvider extends ContentProvider {

    public static final String AUTHORITY =
            "com.example.uidesignlab.provider";

    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/users");

    private static final int USERS = 1;
    private static final int USER_ID = 2;

    private static final UriMatcher URI_MATCHER =
            new UriMatcher(UriMatcher.NO_MATCH);

    // URI Matcher Patterns
    static {
        URI_MATCHER.addURI(AUTHORITY, "users", USERS);
        URI_MATCHER.addURI(AUTHORITY, "users/#", USER_ID);
    }

    private UserDao userDao;

    @Override
    public boolean onCreate() {

        // Initialize Room Database DAO
        userDao = AppDatabase.getInstance(getContext()).userDao();

        // URI Matcher Testing
        Log.d("UriTest", "Test 1: " +
                URI_MATCHER.match(Uri.parse(
                        "content://com.example.uidesignlab.provider/users")));

        Log.d("UriTest", "Test 2: " +
                URI_MATCHER.match(Uri.parse(
                        "content://com.example.uidesignlab.provider/users/3")));

        Log.d("UriTest", "Test 3: " +
                URI_MATCHER.match(Uri.parse(
                        "content://com.example.uidesignlab.provider/people")));

        Log.d("UriTest", "Test 4: " +
                URI_MATCHER.match(Uri.parse(
                        "content://com.example.uidesignlab.provider/users/abc")));

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor cursor;

        switch (URI_MATCHER.match(uri)) {

            case USERS:
                cursor = userDao.getAllCursor();
                break;

            case USER_ID:
                cursor = userDao.getByIdCursor(
                        ContentUris.parseId(uri));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        if (cursor != null) {
            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    CONTENT_URI);
        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        switch (URI_MATCHER.match(uri)) {

            case USERS:
                return "vnd.android.cursor.dir/vnd."
                        + AUTHORITY + ".users";

            case USER_ID:
                return "vnd.android.cursor.item/vnd."
                        + AUTHORITY + ".users";

            default:
                throw new IllegalArgumentException("Unknown URI");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        if (URI_MATCHER.match(uri) != USERS) {
            throw new IllegalArgumentException("Invalid URI");
        }

        String name = values == null ? null :
                values.getAsString("name");

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name required");
        }

        long id = userDao.insert(new UserRecord(name));

        getContext().getContentResolver()
                .notifyChange(CONTENT_URI, null);

        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {

        if (URI_MATCHER.match(uri) != USER_ID) {
            throw new IllegalArgumentException("Invalid URI");
        }

        String name = values == null ? null :
                values.getAsString("name");

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name required");
        }

        int count = userDao.updateName(
                ContentUris.parseId(uri), name);

        getContext().getContentResolver()
                .notifyChange(CONTENT_URI, null);

        return count;
    }

    @Override
    public int delete(Uri uri, String selection,
                      String[] selectionArgs) {

        if (URI_MATCHER.match(uri) != USER_ID) {
            throw new IllegalArgumentException("Invalid URI");
        }

        int count = userDao.deleteById(
                ContentUris.parseId(uri));

        getContext().getContentResolver()
                .notifyChange(CONTENT_URI, null);

        return count;
    }
}
