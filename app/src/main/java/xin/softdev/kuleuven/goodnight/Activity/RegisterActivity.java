package xin.softdev.kuleuven.goodnight.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import xin.softdev.kuleuven.goodnight.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private EditText password;
    private EditText confirmPassword;

    private static final String DB_URL = "jdbc:mysql://mysql.studev.groept.be:3306/a18_sd610";
    private static final String USER = "a18_sd610";
    private static final String PASS = "a18_sd610";
    private boolean result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        phoneNumber=(EditText)findViewById(R.id.registerPhoneNumber);
        password=(EditText)findViewById(R.id.registerPassword);
        confirmPassword=(EditText)findViewById(R.id.registerConfirmPassword);
    }

    public void confirmRegister(View view) {
        Send send = new Send();
        send.execute("");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (result==true)
                {
                    Intent intent=new Intent(RegisterActivity.this, LoginByPhoneActivity.class);
                    startActivity(intent);
                }
                else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
                    dialog.setMessage("Your phone number has been registered");
                    dialog.setTitle("Register failed");
                    dialog.setCancelable(true);
                    dialog.show();
                    phoneNumber.getText().clear();
                    confirmPassword.getText().clear();
                    password.getText().clear();
                }


            }
        },500);
    }

    private class Send extends AsyncTask<String, String, String>{
        String USERNAME = phoneNumber.getText().toString();
        String PASSWORD = password.getText().toString();
        String COMFIRMPASSWORD=confirmPassword.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings){
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                if (conn == null) {
                    result=false;
                }
                else if(PASSWORD.equals(COMFIRMPASSWORD)) {
                    String query = "INSERT INTO REGISTER (USERNAME,PASSWORD) VALUES('" + USERNAME + "','" + PASSWORD + "')";
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate(query);
                    result=true;
                }
                else
                {
                    result=false;
                }
                conn.close();
            }
            catch (Exception e)
            {

                e.printStackTrace();
                result=false;
            }
            return "a";
        }
        @Override
        protected void onPostExecute(String msg){
        }
    }
}
