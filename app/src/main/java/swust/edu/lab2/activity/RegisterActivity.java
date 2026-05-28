package swust.edu.lab2.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.google.android.material.imageview.ShapeableImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import swust.edu.lab2.R;
import swust.edu.lab2.database.DatabaseHelper;
import swust.edu.lab2.entity.User;

public class RegisterActivity extends AppCompatActivity {
    // 这个变量用来存最终结果，可以是 "0"、"1"、"2" 或者自定义文件的绝对路径
    private String finalAvatarSource = "0";
    private ActivityResultLauncher<Intent> albumLauncher;

    private EditText etRegUsername;
    private EditText etRegPassword;
    private EditText etRegConfirm;
    private EditText etRegNickname;
    private AppCompatButton btnRegister;
    private ShapeableImageView ivAvatar0, ivAvatar1, ivAvatar2, ivAvatarUploadShape;
    private ImageView ivAvatarUpload;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        // 初始化视图
        etRegUsername = findViewById(R.id.et_reg_username);
        etRegPassword = findViewById(R.id.et_reg_password);
        etRegConfirm = findViewById(R.id.et_reg_confirm);
        etRegNickname = findViewById(R.id.et_reg_nickname);
        btnRegister = findViewById(R.id.btn_register);
        
        ivAvatar0 = findViewById(R.id.iv_avatar_0);
        ivAvatar1 = findViewById(R.id.iv_avatar_1);
        ivAvatar2 = findViewById(R.id.iv_avatar_2);
        ivAvatarUploadShape = findViewById(R.id.iv_avatar_upload);
        ivAvatarUpload = findViewById(R.id.iv_avatar_upload);
        
        // 初始化：默认选中第一个头像
        updateAvatarSelection(ivAvatar0);

        // 注册相册返回的回调（新版 Android 标准写法，不需要管老旧的 RequestCode）
        albumLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            // 核心：把相册图片拷贝到 App 内部，拿到安全、永久的绝对路径
                            String localPath = saveToInternalStorage(selectedImageUri);
                            if (localPath != null) {
                                finalAvatarSource = localPath; // 记录路径
                                // 1. 让上传按钮显示这张选中的新图片
                                ivAvatarUpload.setImageURI(Uri.fromFile(new File(localPath)));
                                
                                // 🔥【核心修复】清除 XML 带来的绿色滤镜，还你照片本来面目！
                                ivAvatarUpload.setImageTintList(null);
                                
                                // 2. 移除加号的内边距，让照片铺满
                                ivAvatarUpload.setPadding(0, 0, 0, 0);
                                
                                // 💡【体验优化】让用户上传的照片居中裁剪，防止图片被压扁或拉长变形
                                ivAvatarUpload.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                
                                // 3. 高亮选中自定义头像（添加青绿色边框）
                                updateAvatarSelection(ivAvatarUploadShape);
                            }
                        }
                    }
                }
        );

        // 点击内置头像
        ivAvatar0.setOnClickListener(v -> {
            finalAvatarSource = "0";
            updateAvatarSelection(ivAvatar0);
        });
        
        ivAvatar1.setOnClickListener(v -> {
            finalAvatarSource = "1";
            updateAvatarSelection(ivAvatar1);
        });
        
        ivAvatar2.setOnClickListener(v -> {
            finalAvatarSource = "2";
            updateAvatarSelection(ivAvatar2);
        });

        // 点击自定义上传按钮 -> 唤起相册
        ivAvatarUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            albumLauncher.launch(intent);
        });

        // 提交注册按钮
        btnRegister.setOnClickListener(v -> handleRegister());
        
        // 返回登录页
        findViewById(R.id.tv_back_login).setOnClickListener(v -> finish());
        
        // 密码可见性切换（与登录页完全一致）
        ImageView ivRegPwdToggle = findViewById(R.id.iv_reg_pwd_toggle);
        final boolean[] isRegPwdVisible = {false};
        
        ivRegPwdToggle.setOnClickListener(v -> {
            if (isRegPwdVisible[0]) {
                etRegPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivRegPwdToggle.setImageResource(R.drawable.ic_eye_hidden);
                isRegPwdVisible[0] = false;
            } else {
                etRegPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivRegPwdToggle.setImageResource(R.drawable.ic_eye_visible);
                isRegPwdVisible[0] = true;
            }
            etRegPassword.setSelection(etRegPassword.getText().length());
        });
    }

    /**
     * 统一的高亮选中方法：给选中的头像添加青绿色边框，其他头像边框清零
     * @param selectedAvatar 被选中的头像控件
     */
    private void updateAvatarSelection(View selectedAvatar) {
        // 建立一个数组，包含所有头像控件
        View[] allAvatars = {ivAvatar0, ivAvatar1, ivAvatar2, ivAvatarUploadShape};
        
        for (View avatar : allAvatars) {
            if (avatar instanceof ShapeableImageView) {
                ShapeableImageView shapeView = (ShapeableImageView) avatar;
                if (shapeView == selectedAvatar) {
                    // 如果是被选中的头像：加上 6 像素粗的青绿色边框
                    shapeView.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#89C5B5")));
                    shapeView.setStrokeWidth(6f);
                } else {
                    // 没被选中的头像：边框清零
                    shapeView.setStrokeWidth(0f);
                }
            }
        }
    }

    // 将相册图片拷贝到 App 内部私有存储空间的灵魂方法
    private String saveToInternalStorage(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            // 在本应用私有目录下创建一个叫 custom_avatar_时间戳.jpg 的文件
            File file = new File(getFilesDir(), "custom_avatar_" + System.currentTimeMillis() + ".jpg");
            OutputStream os = new FileOutputStream(file);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.close();
            is.close();
            return file.getAbsolutePath(); // 返回它的绝对路径
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void handleRegister() {
        String username = etRegUsername.getText().toString().trim();
        String password = etRegPassword.getText().toString().trim();
        String confirm = etRegConfirm.getText().toString().trim();
        String nickname = etRegNickname.getText().toString().trim(); // 获取昵称

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 校验两次密码是否一致
        if (!password.equals(confirm)) {
            Toast.makeText(this, "两次输入的密码不一致！", Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查用户名是否已注册
        if (dbHelper.getUserByUsername(username) != null) {
            Toast.makeText(this, "该用户名已被注册", Toast.LENGTH_SHORT).show();
            return;
        }

        // 如果用户没填昵称，我们就贴心地默认让 昵称 = 用户名
        if (nickname.isEmpty()) {
            nickname = username;
        }

        // 创建新用户（包含真正的昵称和头像路径）
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname); // 使用真正的昵称
        user.setAvatar(finalAvatarSource); // 使用最终选择的头像（"0"/"1"/"2" 或绝对路径）

        // 修复：返回值改为 boolean
        boolean isSuccess = dbHelper.insertUser(user);
        if (isSuccess) {
            Toast.makeText(this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "注册失败，请检查数据库", Toast.LENGTH_SHORT).show();
        }
    }
}
