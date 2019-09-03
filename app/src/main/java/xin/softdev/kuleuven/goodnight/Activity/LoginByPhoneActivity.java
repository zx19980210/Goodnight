package xin.softdev.kuleuven.goodnight.Activity;

import android.content.Intent;
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

import xin.softdev.kuleuven.goodnight.R;

public class LoginByPhoneActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private EditText password;
    private RequestQueue queue;
    private boolean result;
    private String parseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_phone);
        phoneNumber=(EditText)findViewById(R.id.enterPhoneNumber);
        password=(EditText)findViewById(R.id.enterPassword);
        queue= Volley.newRequestQueue(this);
    }

    public void confirmLogin(View v)
    {
        this.jsonParse();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                parseUser=phoneNumber.getText().toString();
                if(result==true)
                {
                    startActivity(new Intent(getApplicationContext(),MainPageActivity.class).putExtra("parseUser",parseUser));
                }
                else
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(LoginByPhoneActivity.this);
                    dialog.setMessage("Phone number or password is wrong, please try again");
                    dialog.setTitle("Login failed");
                    dialog.setCancelable(true);
                    dialog.show();
                    phoneNumber.getText().clear();
                    password.getText().clear();
                }
            }
        },500);
    }

    private void jsonParse(){
        String url="https://studev.groept.be/api/a18_sd610/tests";
        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            for(int i=0;i<response.length();i++) {
                                JSONObject user = response.getJSONObject(i);
                                String username = user.getString("USERNAME");
                                String correctPassword = user.getString("PASSWORD");
                                if ((phoneNumber.getText().toString().equals(username)) && (phoneNumber.getText().toString().length()!= 0)
                                        && (password.getText().toString().length() != 0) && (password.getText().toString().equals(correctPassword))) {
                                    result = true;
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
}
