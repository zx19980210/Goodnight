package xin.softdev.kuleuven.goodnight.Activity;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

import xin.softdev.kuleuven.goodnight.R;

public class CollectMusicActivity extends AppCompatActivity {

    private String parseUser;
    ListView listView;
    private ArrayList<String> localSongUri = new ArrayList<String>();
    private ArrayList<String> localSongName = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_music);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        parseUser = b.getString("parseUser");

        listView = (ListView) findViewById(R.id.local_List);

        localSongName = findSongsName(Environment.getExternalStorageDirectory());
        localSongUri = findSongsUri(Environment.getExternalStorageDirectory());
        ArrayAdapter<String> adp = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_element, R.id.list_element, localSongName);
        listView.setAdapter(adp);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(),PlayingActivity.class).putExtra("parseUser",parseUser).putExtra("musicName",localSongName.get(position)).putExtra("Uri",localSongUri.get(position)));
            }
        });
    }

        public ArrayList<String> findSongsUri(File root)
        {
            ArrayList<String> al = new ArrayList<String>();
            File[] files = root.listFiles();
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    al.addAll(findSongsUri(singleFile));
                } else {
                    if (singleFile.getName().endsWith(".mp3")) {
                        al.add(singleFile.toString());
                    }
                }
            }
            return al;
        }

        public ArrayList<String> findSongsName (File root)
        {
            ArrayList<String> al = new ArrayList<String>();
            File[] files = root.listFiles();
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    al.addAll(findSongsName(singleFile));
                } else {
                    if (singleFile.getName().endsWith(".mp3")) {
                        al.add(singleFile.getName().toString().replace(".mp3", ""));
                    }
                }
            }
            return al;
        }

    }

