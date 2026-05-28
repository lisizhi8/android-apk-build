package swust.edu.lab2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import swust.edu.lab2.database.DatabaseHelper;
import swust.edu.lab2.utils.SessionManager;

public class BaseActivity extends AppCompatActivity {

    protected DatabaseHelper dbHelper;
    protected SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(this);
        sessionManager = SessionManager.getInstance(this);
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    protected void navigateToLogin() {
        sessionManager.logout();
        finishAffinity();
        startActivity(new Intent(this, LoginActivity.class));
    }
}