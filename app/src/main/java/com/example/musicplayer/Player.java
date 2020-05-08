package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;

public class Player extends AppCompatActivity {

  Button bt_next, bt_pause, bt_previous;
  TextView songNameText;
  SeekBar sb;

  static MediaPlayer mMediaPlayer;
  int position;
  String sname;
  ArrayList<File> mysongs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_player);

    bt_next = findViewById(R.id.next);
    bt_pause = findViewById(R.id.pause);
    bt_previous = findViewById(R.id.previous);

    songNameText = findViewById(R.id.label);
    sb = findViewById(R.id.seek);

    if (mMediaPlayer != null) {
      mMediaPlayer.stop();
    }

    Intent playerData = getIntent();
    Bundle b = playerData.getExtras();

    mysongs = (ArrayList) b.getParcelableArrayList("songs");
    position = b.getInt("position", 0);
    initPlayer(position);

    bt_pause.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sb.setMax(mMediaPlayer.getDuration());
        if (mMediaPlayer.isPlaying()) {
          bt_pause.setBackgroundResource(R.drawable.play);
          mMediaPlayer.pause();

        } else {
          bt_pause.setBackgroundResource(R.drawable.pause);
          mMediaPlayer.start();
        }
      }
    });

    bt_previous.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if (position <= 0) {
          position = mysongs.size() - 1;
        } else {
          position--;
        }

        initPlayer(position);

      }
    });

    bt_next.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (position < mysongs.size() - 1) {
          position++;
        } else {
          position = 0;

        }
        initPlayer(position);
      }
    });

  }


  public void initPlayer(final int position1) {

    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
      mMediaPlayer.reset();
    }

    String sname = mysongs.get(position1).getName().replace(".mp3", "").replace(".wav", "");
    songNameText.setText(sname);
    songNameText.setSelected(true);

    Uri songResourceUri = Uri.parse(mysongs.get(position1).toString());

    mMediaPlayer = MediaPlayer.create(getApplicationContext(), songResourceUri); // create and load mediaplayer with song resources
    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
      @Override
      public void onPrepared(MediaPlayer mp) {
        sb.setMax(mMediaPlayer.getDuration());
        mMediaPlayer.start();
      }
    });

    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      @Override
      public void onCompletion(MediaPlayer mp) {
        // code to repeat songs until the
        if (position < mysongs.size() - 1) {
          position++;
          initPlayer(position);
        } else {
          position = 0;
          initPlayer(position);
        }
      }
    });

    sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (fromUser) {
          mMediaPlayer.seekTo(progress);
          sb.setProgress(progress);
        }

      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });


    new Thread(new Runnable() {
      @Override
      public void run() {
        while (mMediaPlayer != null) {
          try {
            if (mMediaPlayer.isPlaying()) {
              Message msg = new Message();
              msg.what = mMediaPlayer.getCurrentPosition();
              handler.sendMessage(msg);
              Thread.sleep(1000);
            }
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
  }

  @SuppressLint("HandlerLeak")
  private Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      int current_position = msg.what;
      sb.setProgress(current_position);
    }
  };
}
