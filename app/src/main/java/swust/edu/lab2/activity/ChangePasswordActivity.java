package swust.edu.lab2.activity;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import swust.edu.lab2.R;
import swust.edu.lab2.database.DatabaseHelper;
import swust.edu.lab2.entity.User;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etConfirmNewPassword;
    private AppCompatButton btnSubmitChange;
    private TextView tvCancelChange;

    private DatabaseHelper dbHelper;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        dbHelper = new DatabaseHelper(this);
        // 获取从主页传过来的当前登录用户名
        currentUsername = getIntent().getStringExtra("CURRENT_USER");

        initViews();

        btnSubmitChange.setOnClickListener(v -> handleChangePassword());
        tvCancelChange.setOnClickListener(v -> finish());
        
        ImageView ivChangePwdToggle = findViewById(R.id.iv_change_pwd_toggle);
        final boolean[] isNewPwdVisible = {false};
        
        ivChangePwdToggle.setOnClickListener(v -> {
            if (isNewPwdVisible[0]) {
                etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivChangePwdToggle.setImageResource(R.drawable.ic_eye_hidden);
                isNewPwdVisible[0] = false;
            } else {
                etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivChangePwdToggle.setImageResource(R.drawable.ic_eye_visible);
                isNewPwdVisible[0] = true;
            }
            etNewPassword.setSelection(etNewPassword.getText().length());
        });
    }

    private void initViews() {
        etOldPassword = findViewById(R.id.et_old_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmNewPassword = findViewById(R.id.et_confirm_new_password);
        btnSubmitChange = findViewById(R.id.btn_submit_change);
        tvCancelChange = findViewById(R.id.tv_cancel_change);
    }

    private void handleChangePassword() {
        String oldPwd = etOldPassword.getText().toString().trim();
        String newPwd = etNewPassword.getText().toString().trim();
        String confirmPwd = etConfirmNewPassword.getText().toString().trim();

        if (oldPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
            Toast.makeText(this, "所有字段均不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPwd.equals(confirmPwd)) {
            Toast.makeText(this, "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        // 从数据库安全获取当前用户对象
        User user = dbHelper.getUserByUsername(currentUsername);
        if (user == null) {
            Toast.makeText(this, "用户信息异常，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }

        // 校验原密码是否正确
        if (!user.getPassword().equals(oldPwd)) {
            Toast.makeText(this, "原密码错误", Toast.LENGTH_SHORT).show();
            return;
        }

        // 更新密码并保存到数据库
        user.setPassword(newPwd);
        boolean isSuccess = dbHelper.updateUser(user);

        if (isSuccess) {
            Toast.makeText(this, "密码修改成功，请重新登录", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "密码修改失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
}
