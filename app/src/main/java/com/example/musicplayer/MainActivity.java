package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String[] items;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    listView = findViewById(R.id.list);
    runtime();
  }

  public void runtime()
  {
    Dexter.withActivity(this)
      .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
      .withListener(new PermissionListener() {
        @Override
        public void onPermissionGranted(PermissionGrantedResponse response) {
          display();
        }

        @Override
        public void onPermissionDenied(PermissionDeniedResponse response)
        {

        }

        @Override
        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token)
        {
          token.continuePermissionRequest();
        }
      }).check();
  }

  public ArrayList<File> findsong(File file)
  {
    ArrayList<File> arrayList = new ArrayList<>();
    File[] files = file.listFiles();
    for (File singlefile: files)
    {
      if(singlefile.isDirectory() && !singlefile.isHidden())
      {
        arrayList.addAll(findsong(singlefile));
      }
      else
      {
        if(singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav"))
        {
          arrayList.add(singlefile);
        }
      }
    }
    return arrayList;
  }

  void display() {
    final ArrayList<File> mysongs = findsong(Environment.getExternalStorageDirectory());
    items = new String[mysongs.size()];
    for (int i = 0; i < mysongs.size(); i++) {
      items[i] = mysongs.get(i).getName().replace(".mp3", "").replace(".wav", "");
    }
    ArrayAdapter<String> myadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, items) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        TextView ListItemShow = (TextView) view.findViewById(android.R.id.text1);

        ListItemShow.setTextColor(Color.parseColor("#efefef"));

        return view;
      }
    };
    listView.setAdapter(myadapter);

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

        String songname = listView.getItemAtPosition(i).toString();
        Intent play = new Intent(getApplicationContext(),Player.class);
        play.putExtra("songs",mysongs).putExtra("Songname",songname).putExtra("position",i);
        startActivity(play);
      }
    });
  }
}
