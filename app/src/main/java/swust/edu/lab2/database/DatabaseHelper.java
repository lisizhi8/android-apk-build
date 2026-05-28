package swust.edu.lab2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import swust.edu.lab2.entity.User;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_NICKNAME = "nickname";
    private static final String COLUMN_AVATAR = "avatar";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_NICKNAME + " TEXT, " +
                COLUMN_AVATAR + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_NICKNAME, user.getNickname());
        values.put(COLUMN_AVATAR, user.getAvatar());
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public User login(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{username, password}, null, null, null);
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME));
            String avatar = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR));
            user = new User(id, username, password, nickname, avatar);
            cursor.close();
        }
        db.close();
        return user;
    }

    public boolean isUserExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USERNAME},
                COLUMN_USERNAME + " = ?", new String[]{username}, null, null, null);
        boolean exists = cursor != null && cursor.moveToFirst();
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);
        int rowsUpdated = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
        db.close();
        return rowsUpdated > 0;
    }

    public boolean updateUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_NICKNAME, user.getNickname());
        values.put(COLUMN_AVATAR, user.getAvatar());
        int rowsUpdated = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{user.getUsername()});
        db.close();
        return rowsUpdated > 0;
    }

    public User getUserByUsername(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USERNAME + " = ?", new String[]{username}, null, null, null);
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            String nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME));
            String avatar = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR));
            user = new User(id, username, password, nickname, avatar);
            cursor.close();
        }
        db.close();
        return user;
    }
}