package swust.edu.lab2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "login_pref";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_IS_SAVE = "isSave";

    private boolean isPasswordVisible = false;
    private EditText Euser;
    private EditText Epassword;
    private CheckBox cbSaveLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_layout);

        Euser = findViewById(R.id.editTextText);
        Epassword = findViewById(R.id.editTextNumberPassword);
        Button btnLogin = findViewById(R.id.button2);
        ImageView ivTogglePassword = findViewById(R.id.ivTogglePassword);
        TextView tvError = findViewById(R.id.tvError);
        cbSaveLogin = findViewById(R.id.cb_save_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Euser.getText().toString();
                String password = Epassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    tvError.setText("用户名或密码不能为空");
                    tvError.setVisibility(View.VISIBLE);
                } else if (username.equals("孙悟空") && password.equals("108000")) {
                    tvError.setVisibility(View.GONE);
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    startActivity(intent);
                    Toast toast = Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    tvError.setText("用户名或密码有误");
                    tvError.setVisibility(View.VISIBLE);
                }
            }
        });

        ivTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    Epassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    ivTogglePassword.setImageResource(R.drawable.ic_eye_invisible);
                } else {
                    Epassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ivTogglePassword.setImageResource(R.drawable.ic_eye_visible);
                }
                Epassword.setSelection(Epassword.getText().length());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadLoginInfo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveLoginInfo();
    }

    private void saveLoginInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean isSave = cbSaveLogin.isChecked();
        String username = Euser.getText().toString();
        String password = Epassword.getText().toString();

        if (isSave) {
            editor.putString(KEY_USERNAME, username);
            editor.putString(KEY_PASSWORD, password);
            editor.putBoolean(KEY_IS_SAVE, true);
        } else {
            editor.remove(KEY_USERNAME);
            editor.remove(KEY_PASSWORD);
            editor.putBoolean(KEY_IS_SAVE, false);
        }

        editor.apply();
    }

    private void loadLoginInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isSave = sharedPreferences.getBoolean(KEY_IS_SAVE, false);

        if (isSave) {
            String username = sharedPreferences.getString(KEY_USERNAME, "");
            String password = sharedPreferences.getString(KEY_PASSWORD, "");
            Euser.setText(username);
            Epassword.setText(password);
            cbSaveLogin.setChecked(true);
        } else {
            cbSaveLogin.setChecked(false);
        }
    }
}