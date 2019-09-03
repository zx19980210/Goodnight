package xin.softdev.kuleuven.goodnight;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import xin.softdev.kuleuven.goodnight.Activity.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent i= new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();

            }
        },3000);
    }
}
