package xin.softdev.kuleuven.goodnight.Activity;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
import java.util.ArrayList;

import xin.softdev.kuleuven.goodnight.R;

public class MainPageActivity extends AppCompatActivity {

    private ImageView bird_image;
    private ImageView rain_image;
    private ImageView wind_image;
    private ImageView piano_image;
    private ImageView local_music;

    private String parseUser;

    private String bird_uri;
    private String rain_uri;
    private String wind_uri;
    private String piano_uri;

    private ArrayList<String> collectUri=new ArrayList<String>();
    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        bird_image=(ImageView)findViewById(R.id.bird_image);
        rain_image=(ImageView)findViewById(R.id.rain_image);
        wind_image=(ImageView)findViewById(R.id.wind_image);
        piano_image=(ImageView)findViewById(R.id.piano_image);
        local_music=(ImageView)findViewById(R.id.local_music);

        Intent i=getIntent();
        Bundle b=i.getExtras();
        parseUser=b.getString("parseUser");

        queue= Volley.newRequestQueue(this);
        this.getUri();
    }

    public void showChart(View view)
    {
        startActivity(new Intent(getApplicationContext(),ShowChartActivity.class).putExtra("parseUser",parseUser));
    }

    public void start_wind(View view)
    {
                startActivity(new Intent(getApplicationContext(),PlayingActivity.class).putExtra("parseUser",parseUser).putExtra("musicName","Wind").putExtra("Uri",collectUri.get(3)));
    }

    public void start_rain(View view)
    {
                startActivity(new Intent(getApplicationContext(),PlayingActivity.class).putExtra("parseUser",parseUser).putExtra("musicName","Rain").putExtra("Uri",collectUri.get(2)));
    }

    public void start_localMusic(View view)
    {
        startActivity(new Intent(this,CollectMusicActivity.class).putExtra("parseUser",parseUser));
    }

    public void start_piano(View view)
    {
                startActivity(new Intent(getApplicationContext(),PlayingActivity.class).putExtra("parseUser",parseUser).putExtra("musicName","Piano").putExtra("Uri",collectUri.get(1)));
    }

    public void start_bird(View view)
    {
                startActivity(new Intent(getApplicationContext(),PlayingActivity.class).putExtra("parseUser",parseUser).putExtra("musicName","Bird").putExtra("Uri",collectUri.get(0)));
    }

    private void getUri(){
        String url="https://studev.groept.be/api/a18_sd610/GETURI";
        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            for(int i=0;i<response.length();i++) {
                                JSONObject music = response.getJSONObject(i);
                                String mName = music.getString("MUSICNAME");
                                String mUri = music.getString("URI");
                                collectUri.add(mUri);
                                }
                            }
                         catch (JSONException e) {
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
