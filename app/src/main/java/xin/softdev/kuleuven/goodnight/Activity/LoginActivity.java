package xin.softdev.kuleuven.goodnight.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import xin.softdev.kuleuven.goodnight.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login (View view){
        Intent intent = new Intent(this, LoginByPhoneActivity.class);
        startActivity(intent);
    }
    public void register(View view) {
        Intent intent=new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    public void changePassword(View view){
        Intent intent=new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }
}
