package swust.edu.lab2.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import swust.edu.lab2.R;
import swust.edu.lab2.database.DatabaseHelper;
import swust.edu.lab2.entity.User;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private CheckBox cbRemember;
    private ImageView ivPwdToggle;
    private AppCompatButton btnLogin;
    private TextView tvGoRegister;

    private DatabaseHelper dbHelper;
    private SharedPreferences rememberSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        dbHelper = new DatabaseHelper(this);
        rememberSp = getSharedPreferences("RememberPrefs", MODE_PRIVATE);

        // 自动回显记住的账号密码
        boolean isRemembered = rememberSp.getBoolean("isRemembered", false);
        if (isRemembered) {
            etUsername.setText(rememberSp.getString("saved_user", ""));
            etPassword.setText(rememberSp.getString("saved_pwd", ""));
            cbRemember.setChecked(true);
        }

        // 密码显隐切换逻辑
        final boolean[] isPwdVisible = {false};
        ivPwdToggle.setOnClickListener(v -> {
            if (isPwdVisible[0]) {
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivPwdToggle.setImageResource(R.drawable.ic_eye_hidden);
                isPwdVisible[0] = false;
            } else {
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivPwdToggle.setImageResource(R.drawable.ic_eye_visible);
                isPwdVisible[0] = true;
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        btnLogin.setOnClickListener(v -> handleLogin());
        tvGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        cbRemember = findViewById(R.id.cb_remember);
        ivPwdToggle = findViewById(R.id.iv_pwd_toggle);
        btnLogin = findViewById(R.id.btn_login);
        tvGoRegister = findViewById(R.id.tv_go_register);
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 修正：从数据库查询用户，不再走错误的 userSp
        User user = dbHelper.getUserByUsername(username);
        if (user != null) {
            if (user.getPassword().equals(password)) {
                // 处理记住密码逻辑
                SharedPreferences.Editor editor = rememberSp.edit();
                if (cbRemember.isChecked()) {
                    editor.putBoolean("isRemembered", true);
                    editor.putString("saved_user", username);
                    editor.putString("saved_pwd", password);
                } else {
                    editor.clear();
                }
                editor.apply();

                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("CURRENT_USER", username);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "用户不存在", Toast.LENGTH_SHORT).show();
        }
    }
}
