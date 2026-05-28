package swust.edu.lab2.utils;

import android.content.Context;
import android.content.SharedPreferences;

import swust.edu.lab2.entity.User;

public class SessionManager {

    private static SessionManager instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String PREF_NAME = "user_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_AVATAR = "avatar";

    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public void createLoginSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_NICKNAME, user.getNickname());
        editor.putString(KEY_AVATAR, user.getAvatar());
        editor.commit();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public User getCurrentUser() {
        if (!isLoggedIn()) {
            return null;
        }
        int id = sharedPreferences.getInt(KEY_USER_ID, 0);
        String username = sharedPreferences.getString(KEY_USERNAME, "");
        String nickname = sharedPreferences.getString(KEY_NICKNAME, "");
        String avatar = sharedPreferences.getString(KEY_AVATAR, "");
        return new User(id, username, "", nickname, avatar);
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }
}