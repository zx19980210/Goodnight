package xin.softdev.kuleuven.goodnight.Activity;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import xin.softdev.kuleuven.goodnight.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String DB_URL = "jdbc:mysql://mysql.studev.groept.be:3306/a18_sd610";
    private static final String USER = "a18_sd610";
    private static final String PASS = "a18_sd610";

    private EditText phoneNumber;
    private EditText oldPassword;
    private EditText newPassword;
    private EditText confirmNewPassword;
    private RequestQueue queue;
    private boolean result;
    private boolean matchPasswordResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        phoneNumber=(EditText)findViewById(R.id.changePhoneNumber);
        oldPassword=(EditText)findViewById(R.id.changeOldPassword);
        newPassword=(EditText)findViewById(R.id.changeNewPassword);
        confirmNewPassword=(EditText)findViewById(R.id.changeConfirmNewPassword);
        result=false;
        matchPasswordResult=false;
        queue= Volley.newRequestQueue(this);
    }

    public void confirmChangePassword(View view)
    {
        this.jsonparse();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(result==false)
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ChangePasswordActivity.this);
                    dialog.setMessage("Old phone number or password is wrong, please try again");
                    dialog.setTitle("Failed to change password");
                    dialog.setCancelable(true);
                    dialog.show();
                    phoneNumber.getText().clear();
                    oldPassword.getText().clear();
                    newPassword.getText().clear();
                    confirmNewPassword.getText().clear();
                }
                else if(matchPasswordResult==false)
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ChangePasswordActivity.this);
                    dialog.setMessage("Entered new passwords are different, please try again");
                    dialog.setTitle("Failed to change password");
                    dialog.setCancelable(true);
                    dialog.show();
                    newPassword.getText().clear();
                    confirmNewPassword.getText().clear();
                }
                else
                {
                    Update update=new Update();
                    update.execute("");
                    startActivity(new Intent(getApplicationContext(),LoginByPhoneActivity.class));
                    finish();

                }

            }
        },500);
    }

    public void jsonparse()
    {
        String url = "https://studev.groept.be/api/a18_sd610/tests";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject user = response.getJSONObject(i);
                                String username = user.getString("USERNAME");
                                String correctPassword = user.getString("PASSWORD");
                                if ((phoneNumber.getText().toString().equals(username)) && (oldPassword.getText().toString().equals(correctPassword)) && (phoneNumber.getText().toString().length()!=0)&&(oldPassword.getText().toString().length()!=0)&&(newPassword.getText().toString().length()!=0) ) {

                                    result=true;
                                    if(newPassword.getText().toString().equals(confirmNewPassword.getText().toString())) {
                                        matchPasswordResult = true;

                                    }

                                    else
                                    {
                                        matchPasswordResult=false;
                                    }
                                    break;
                                } else {
                                    result = false;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(request);
    }

    private class Update extends AsyncTask<String, String, String> {
        String USERNAME = phoneNumber.getText().toString();
        String NEWPASSWORD = newPassword.getText().toString();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                if (conn == null) {

                } else {
                    String query = "UPDATE REGISTER SET PASSWORD='" + NEWPASSWORD + "' WHERE USERNAME='" + USERNAME + "' ";
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate(query);
                }
                conn.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
    }
}
