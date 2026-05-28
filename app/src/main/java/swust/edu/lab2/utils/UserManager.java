package swust.edu.lab2.utils;

import android.content.Context;
import android.content.SharedPreferences;

import swust.edu.lab2.entity.User;

public class UserManager {
    private static final String PREF_NAME = "UserDatabase";
    private static final String KEY_CURRENT_USER = "current_logged_in_user";
    private final SharedPreferences prefs;

    public UserManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 注册新用户（检查是否重复）
     * @param user 用户对象
     * @return 注册成功返回 true，用户名已存在返回 false
     */
    public boolean registerUser(User user) {
        if (isUserExists(user.getUsername())) {
            return false; // 用户名已存在，注册失败（解决 Bug 2）
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(user.getUsername() + "_pwd", user.getPassword());
        editor.putString(user.getUsername() + "_nickname", user.getNickname());
        editor.putString(user.getUsername() + "_avatar", user.getAvatar());
        editor.apply();
        return true;
    }

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 存在返回 true，否则返回 false
     */
    public boolean isUserExists(String username) {
        return prefs.contains(username + "_pwd");
    }

    /**
     * 验证登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回 true，否则返回 false
     */
    public boolean loginUser(String username, String password) {
        if (!isUserExists(username)) return false;
        String savedPwd = prefs.getString(username + "_pwd", "");
        if (savedPwd.equals(password)) {
            // 登录成功，记录当前登录的用户名
            prefs.edit().putString(KEY_CURRENT_USER, username).apply();
            return true;
        }
        return false;
    }

    /**
     * 获取当前登录的用户完整信息
     * @return 当前登录用户对象，未登录返回 null
     */
    public User getCurrentUser() {
        String username = prefs.getString(KEY_CURRENT_USER, null);
        if (username == null) return null;
        String password = prefs.getString(username + "_pwd", "");
        String nickname = prefs.getString(username + "_nickname", username); // 没昵称则默认显示用户名
        String avatar = prefs.getString(username + "_avatar", "0");
        return new User(username, password, nickname, avatar);
    }

    /**
     * 获取当前登录的用户名
     * @return 用户名，未登录返回 null
     */
    public String getCurrentUsername() {
        return prefs.getString(KEY_CURRENT_USER, null);
    }

    /**
     * 修改密码（验证旧密码）
     * @param username 用户名
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @return 修改成功返回 true，旧密码错误返回 false
     */
    public boolean updatePassword(String username, String oldPwd, String newPwd) {
        String savedPwd = prefs.getString(username + "_pwd", "");
        if (!savedPwd.equals(oldPwd)) {
            return false; // 旧密码输入错误
        }
        prefs.edit().putString(username + "_pwd", newPwd).apply();
        return true;
    }

    /**
     * 退出登录
     */
    public void logout() {
        prefs.edit().remove(KEY_CURRENT_USER).apply();
    }

    /**
     * 检查是否有用户已登录
     * @return 已登录返回 true，否则返回 false
     */
    public boolean isLoggedIn() {
        return prefs.getString(KEY_CURRENT_USER, null) != null;
    }
}
