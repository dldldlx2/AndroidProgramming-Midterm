package kr.ac.cu.moai.dcumusicplayer;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listViewMP3;
    ArrayList<String> mp3files;
    String selectedMP3;
    ListViewMP3Adapter adapter;

    String mp3path = Environment.getExternalStorageDirectory().getPath() + "/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_MEDIA_AUDIO}, MODE_PRIVATE);

        mp3files = new ArrayList<>();
        File[] files = new File(mp3path).listFiles();
        String filename, ext;
        assert files != null;
        for(File file : files) {
            filename = file.getName();
            ext = filename.substring(filename.length() - 3);
            Log.i("DCU_MP", filename);
            if(ext.equals("mp3")) {
                mp3files.add(mp3path + filename);
            }
        }

        Log.i("DCU_MP", mp3files.toString());

        listViewMP3 = findViewById(R.id.listViewMP3);
        adapter = new ListViewMP3Adapter(this, mp3files);
        listViewMP3.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewMP3.setAdapter(adapter);
        listViewMP3.setOnItemClickListener((parent, view, position, id) -> {
            selectedMP3 = mp3files.get(position);
            Log.i("DCU_MP", selectedMP3);

            Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
            intent.putExtra("mp3", selectedMP3);
            startActivity(intent);
        });

    }
}