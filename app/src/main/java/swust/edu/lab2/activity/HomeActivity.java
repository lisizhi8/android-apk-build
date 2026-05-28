package swust.edu.lab2.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import java.io.File;
import swust.edu.lab2.R;
import swust.edu.lab2.database.DatabaseHelper;
import swust.edu.lab2.entity.User;

public class HomeActivity extends AppCompatActivity {

    private ImageView ivAvatar;
    private TextView tvNickname;
    private TextView tvUsername;
    private AppCompatButton btnToChangePwd;
    private AppCompatButton btnLogout;

    private DatabaseHelper dbHelper;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DatabaseHelper(this);

        // 1. 安全获取登录页传过来的用户名
        currentUsername = getIntent().getStringExtra("CURRENT_USER");

        initViews();
        loadUserData();

        // 修改密码跳转
        btnToChangePwd.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ChangePasswordActivity.class);
            intent.putExtra("CURRENT_USER", currentUsername);
            startActivity(intent);
        });

        // 退出登录
        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void initViews() {
        ivAvatar = findViewById(R.id.iv_home_avatar);
        tvNickname = findViewById(R.id.tv_home_nickname);
        tvUsername = findViewById(R.id.tv_home_username);
        btnToChangePwd = findViewById(R.id.btn_to_change_pwd);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void loadUserData() {
        if (currentUsername == null || currentUsername.isEmpty()) {
            Toast.makeText(this, "未获取到登录信息", Toast.LENGTH_SHORT).show();
            return;
        }

        // 从数据库中真实读取数据
        User user = dbHelper.getUserByUsername(currentUsername);
        if (user != null) {
            tvNickname.setText(user.getNickname());
            tvUsername.setText("欢迎回来，" + user.getUsername() + "！");

            String avatarSource = user.getAvatar();

            // 判断是内置索引还是自定义路径
            if (avatarSource.equals("0") || avatarSource.equals("1") || avatarSource.equals("2")) {
                // 情况 A：属于内置自带头像
                int[] avatarResIds = {R.drawable.avatar_0, R.drawable.avatar_1, R.drawable.avatar_2};
                int index = Integer.parseInt(avatarSource);
                ivAvatar.setImageResource(avatarResIds[index]);
            } else {
                // 情况 B：属于用户自己上传的本地绝对路径图片
                File imgFile = new File(avatarSource);
                if (imgFile.exists()) {
                    // 使用 URI 加载本地图片
                    ivAvatar.setImageURI(Uri.fromFile(imgFile));
                } else {
                    // 容错处理：如果文件不小心被删了，降级显示默认头像
                    ivAvatar.setImageResource(R.drawable.avatar_0);
                }
            }
        } else {
            Toast.makeText(this, "用户数据加载失败", Toast.LENGTH_SHORT).show();
        }
    }
}
