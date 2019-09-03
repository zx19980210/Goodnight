package xin.softdev.kuleuven.goodnight.Activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import xin.softdev.kuleuven.goodnight.R;

public class PlayingActivity extends AppCompatActivity {

    private String uriString;
    private String phoneNumber;
    private String musicName;
    private int timeInMilisecond;
    private static int rotationTime=10000;
    private long currentPlayTime;
    private Uri uri;
    private int currentHour;
    private int currentMinute;
    private boolean stopMusicTimerIndication=false;
    private boolean alarmTimerIndication=false;
    private int sleepMilisecond;
//    private double sleepHour;
    private Uri alarmUri;
    private String alarmUriString;

    private ImageView startButton;
    private Button btn_setAlarm;
    private EditText timeInMinute;
    private TextView timeUtilFinish;
    private TextView tv_musicName;

    private TextView alarm_indication;
    private ImageView open_Timepicker;
    private ImageView cancel_alarm;
    private CountDownTimer stopMusicTimer;
    private CountDownTimer alarmTimer;

    private MediaPlayer mediaPlayer;

    private ObjectAnimator objectAnimator;

    private static final String DB_URL = "jdbc:mysql://mysql.studev.groept.be:3306/a18_sd610"  ;
    private static final String USER = "a18_sd610";
    private static final String PASS = "a18_sd610";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        startButton=(ImageView)findViewById(R.id.startButton);
        btn_setAlarm=(Button) findViewById(R.id.btn_setAlarm);
        timeInMinute=(EditText)findViewById(R.id.timeInMinute);
        timeUtilFinish=(TextView)findViewById(R.id.timeUntilFinish);
        tv_musicName=(TextView)findViewById(R.id.musicName);

        alarm_indication=(TextView)findViewById(R.id.alarm_indication);
        open_Timepicker=(ImageView) findViewById(R.id.alarm_set);
        cancel_alarm=(ImageView) findViewById(R.id.alarm_cancel);

        Intent i=getIntent();

        Bundle b= i.getExtras();
        uriString=b.getString("Uri");
        phoneNumber=b.getString("parseUser");
        musicName=b.getString("musicName");

        uri=Uri.parse(uriString);
        mediaPlayer= new MediaPlayer();
        play(uri);
        mediaPlayer.setLooping(true);
        setAnimation();
        tv_musicName.setText(musicName);

        alarmUriString="https://drive.google.com/uc?export=download&id=1rp0blv-_2mbyNEsKwL8BBJwEF0PVeKvH";
        alarmUri=Uri.parse(alarmUriString);
    }

    public void cancelAlarm(View view)
    {
        if (alarmTimerIndication==true)
        {
            alarmTimer.cancel();
            alarmTimerIndication=false;

            alarm_indication.setText("No alarm");

            DeleteDuration delete= new DeleteDuration();
            delete.execute("");

            final AlertDialog.Builder dialog = new AlertDialog.Builder(PlayingActivity.this);
            dialog.setMessage("Alarm cancelled successfully");
            dialog.setTitle("Alarm cancelled");
            dialog.setCancelable(true);
            dialog.show();
        }
        else
        {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(PlayingActivity.this);
            dialog.setMessage("No existing alarm");
            dialog.setTitle("Notification");
            dialog.setCancelable(true);
            dialog.show();
        }
    }

    public void openTimePicker(View view)
    {
        if (alarmTimerIndication==false) {
            Calendar calendar = Calendar.getInstance();
            currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            currentMinute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int h, int m) {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, h);
                    c.set(Calendar.MINUTE, m);
                    if ((h * 60 + m) > (currentHour * 60 + currentMinute)) {
                        sleepMilisecond = ((h - currentHour) * 60 + (m - currentMinute)) * 60 * 1000;
                    } else {
                        sleepMilisecond = ((h + 24 - currentHour) * 60 + (m - currentMinute)) * 60 * 1000;
                    }
                    SimpleDateFormat wakeupTime = new SimpleDateFormat("HH:mm");
                    int miliSencond = (h * 60 + m) * 60000;
                    String hm = wakeupTime.format(miliSencond - TimeZone.getDefault().getRawOffset());
                    alarm_indication.setText("Alarm at " + hm);
                    initAlarmCountdown(sleepMilisecond);
                    alarmTimer.start();
                    alarmTimerIndication = true;

                    InsertDuration insert= new InsertDuration();
                    insert.execute("");

                }
            }, currentHour, currentMinute, true);
            timePickerDialog.show();
        }
        else
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(PlayingActivity.this);
            dialog.setMessage("Please cancel old alarm first");
            dialog.setTitle("Alarm set failed");
            dialog.setCancelable(true);
            dialog.show();
        }
    }



    public void initAlarmCountdown(final int milisecond)
    {
        alarmTimer=new CountDownTimer(milisecond,1000) {
            @Override
            public void onTick(long millisUntilAlarm) {
                SimpleDateFormat timeUtilEnd = new SimpleDateFormat("HH:mm:ss");
                String hm= timeUtilEnd.format(millisUntilAlarm- TimeZone.getDefault().getRawOffset());
            }

            @Override
            public void onFinish() {
                if (mediaPlayer.isPlaying())
                {
                    mediaPlayer.stop();
                    stopAnimation();
                }

                play(alarmUri);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mediaPlayer.stop();
                    }
                },10000);

                alarmTimer.cancel();
                alarmTimerIndication=false;
                alarm_indication.setText("No alarm");
            }
        };
    }


    public void initCountdown(final int milisecond)
    {
        stopMusicTimer=new CountDownTimer(milisecond,1000)
        {
            @Override
            public void onTick(long milisUntilFinish)
            {
                SimpleDateFormat timeUtilEnd = new SimpleDateFormat("HH:mm:ss");
                String hms= timeUtilEnd.format(milisUntilFinish- TimeZone.getDefault().getRawOffset());
                timeUtilFinish.setText(hms);
            }

            @Override
            public void onFinish()
            {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                    stopAnimation();
                }
                timeUtilFinish.setText("Time out");
                stopMusicTimerIndication=false;
            }
        };
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
        }
        finish();
    }

    @Override
    protected void onResume(){
        super.onResume();

    }

    public void playMusic(View v)
    {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            stopAnimation();
        }
        else
        {
            mediaPlayer.start();
            startAnimation();
        }
    }

    private void play(Uri uri){
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try{
            mediaPlayer.setDataSource(getApplicationContext(),uri);
            mediaPlayer.prepare();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        mediaPlayer.start();

    }

    public void setAnimation(){
        if(objectAnimator==null)
        {
            objectAnimator=ObjectAnimator.ofFloat(startButton,"rotation",0,360);
            objectAnimator.setDuration(rotationTime);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        }
        startAnimation();
    }

    public void startAnimation()
    {
        objectAnimator.start();
        objectAnimator.setCurrentPlayTime(currentPlayTime);
    }

    public void stopAnimation()
    {
        currentPlayTime=objectAnimator.getCurrentPlayTime();
        objectAnimator.cancel();
    }

    public void countDownSetting(View view) {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            startAnimation();
        }

        if (!timeInMinute.getText().toString().isEmpty())
        {
        timeInMilisecond = Integer.parseInt(timeInMinute.getText().toString()) * 60 * 1000;
            if (stopMusicTimerIndication == false) {
                initCountdown(timeInMilisecond);
                stopMusicTimer.start();
                timeInMinute.getText().clear();
                stopMusicTimerIndication = true;
            } else {
                stopMusicTimer.cancel();
                initCountdown(timeInMilisecond);
                stopMusicTimer.start();
                timeInMinute.getText().clear();
            }
        }
        else
        {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(PlayingActivity.this);
            dialog.setMessage("Playing duration is null");
            dialog.setTitle("Error");
            dialog.setCancelable(true);
            dialog.show();
        }
    }

    private class InsertDuration extends AsyncTask<String,String,String> {

        double sleepHour=new BigDecimal((float)sleepMilisecond/3600000).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
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

                } else{
                    String query = "INSERT INTO DURATION (USERNAME,DURATION) VALUES('" + phoneNumber + "','" + sleepHour + "')";
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate(query);
                }
                conn.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
        @Override
        protected void onPostExecute(String msg) {

        }
    }

    private class DeleteDuration extends AsyncTask<String,String,String> {

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

                } else{
                    String query = "DELETE FROM DURATION WHERE USERNAME='"+phoneNumber+"' ORDER BY ID DESC LIMIT 1";
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate(query);
                }
                conn.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
        @Override
        protected void onPostExecute(String msg) {

        }
    }

}
