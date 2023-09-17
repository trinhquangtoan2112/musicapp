package com.example.btl;

import static com.example.btl.MainActivity.musicFiles;
import static com.example.btl.MainActivity.repeatBoolean;
import static com.example.btl.MainActivity.suffleBoolean;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity {
   TextView song_name,artist_name,duration_play,total_play;
   ImageView chevron_left,menu_btn,next_btn,prev_btn,suffle_btn,repeat_btn;
   FloatingActionButton playPausebtn;
   SeekBar seekBar;
   static Uri uri;
   static MediaPlayer mediaPlayer;
   int position =-1;
  static ArrayList<MusicFiles> listSongs =new ArrayList<>();

  private Handler handler =new Handler();

  private Thread playThread ,prevThread,nextThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        getIntentMethod();
         song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist() );
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(mediaPlayer!=null &&b){
                    mediaPlayer.seekTo(i*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!= null){
                    int mCurrentPosition =mediaPlayer.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_play.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });
        suffle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(suffleBoolean){
                    suffleBoolean =false;
                    suffle_btn.setImageResource(R.drawable.baseline_shuffle_24);
                }
                else {
                    suffleBoolean =true;
                    suffle_btn.setImageResource(R.drawable.baseline_shuffle_on_24);
                }
            }
        });
        repeat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(repeatBoolean){
                    repeatBoolean =false;
                    repeat_btn.setImageResource(R.drawable.baseline_repeat_24);
                }
                else {
                    repeatBoolean =true;
                    repeat_btn.setImageResource(R.drawable.baseline_repeat_one_24);
                }
            }
        });
    }

    private String formattedTime(int mCurrentPosition) {
        String totaout ="";
        String totalnew ="";
        String second =String.valueOf(mCurrentPosition %60);
        String minutes =String.valueOf(mCurrentPosition /60);
        totaout = minutes +":"+second;
        totalnew= minutes+":"+"0"+second;
        if(second.length()==1){
            return  totalnew;
        }
        else {
           return   totaout;
        }
    }

    private void initView(){
        song_name =findViewById(R.id.songnamehere);
        artist_name =findViewById(R.id.artist);
        duration_play =findViewById(R.id.duration_player);
        total_play =findViewById(R.id.total_player);
        chevron_left =findViewById(R.id.back_btn);
        menu_btn =findViewById(R.id.menu_btn);
        next_btn =findViewById(R.id.id_next);
        prev_btn =findViewById(R.id.id_previous);
        suffle_btn =findViewById(R.id.suffle_btn);
        repeat_btn =findViewById(R.id.id_repeat);
        playPausebtn =findViewById(R.id.play_pause);
        seekBar =findViewById(R.id.seekbar1);

    }
    private  void getIntentMethod(){
              position =getIntent().getIntExtra("position",position);
              listSongs =musicFiles ;
              if(listSongs!=null){
                  playPausebtn.setImageResource(R.drawable.baseline_pause_24);
                  uri =Uri.parse(listSongs.get(position).getPath());
              }
              if(mediaPlayer!=null){
                  mediaPlayer.stop();
                  mediaPlayer.release();
                  mediaPlayer =MediaPlayer.create(getApplicationContext(),uri);
                  mediaPlayer.start();

              }
              else {
                  mediaPlayer =MediaPlayer.create(getApplicationContext(),uri);
                  mediaPlayer.start();
              }
              seekBar.setMax(mediaPlayer.getDuration()/1000);
              metaData(uri);
    }

    private void metaData(Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal =Integer.parseInt(listSongs.get(position).getDuration() ) /1000;
        total_play.setText(formattedTime(durationTotal));

    }

    @Override
    protected void onResume() {
        playThread();
        prevThread();
        nextThread();

        super.onResume();
    }

    private void nextThread() {
        nextThread=new Thread(){
            @Override
            public void run() {
                super.run();
                next_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        next_btnClick();
                    }
                });
            }
        };
        nextThread.start();

    }

    private void prevThread() {
        prevThread=new Thread(){
            @Override
            public void run() {
                super.run();
                prev_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prev_btnClick();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void prev_btnClick() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if (suffleBoolean  &&!repeatBoolean){
                position = getRandom(listSongs.size()-1);
            } else if (!suffleBoolean && repeatBoolean) {
                position =((position-1)<0 ?(listSongs.size() -1):(position -1));
            }

            uri =Uri.parse(listSongs.get(position).getPath());
            mediaPlayer =mediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!= null){
                        int mCurrentPosition =mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            playPausebtn.setImageResource(R.drawable.baseline_pause_24);
            mediaPlayer.start();
        }
        else {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (suffleBoolean  &&!repeatBoolean){
                position = getRandom(listSongs.size()-1);
            } else if (!suffleBoolean && repeatBoolean) {
                position =((position-1)<0 ?(listSongs.size() -1):(position -1));
            }
            uri =Uri.parse(listSongs.get(position).getPath());
            mediaPlayer =mediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!= null){
                        int mCurrentPosition =mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            playPausebtn.setImageResource(R.drawable.baseline_play_arrow_24);

        }
    }

    private void next_btnClick() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if (suffleBoolean  &&!repeatBoolean){
                position = getRandom(listSongs.size()-1);
            } else if (!suffleBoolean && repeatBoolean) {
                position =((position+1)%listSongs.size());
            }

            uri =Uri.parse(listSongs.get(position).getPath());
            mediaPlayer =mediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!= null){
                        int mCurrentPosition =mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            playPausebtn.setImageResource(R.drawable.baseline_pause_24);
            mediaPlayer.start();
        }
        else {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (suffleBoolean  &&!repeatBoolean){
                position = getRandom(listSongs.size()-1);
            } else if (!suffleBoolean && repeatBoolean) {
                position =((position+1)%listSongs.size());
            }
            uri =Uri.parse(listSongs.get(position).getPath());
            mediaPlayer =mediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!= null){
                        int mCurrentPosition =mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            playPausebtn.setImageResource(R.drawable.baseline_play_arrow_24);

        }
    }

    private int getRandom(int i) {
        Random random =new Random();

        return random.nextInt(i+ 1);
    }

    private void playThread() {
        playThread=new Thread(){
            @Override
            public void run() {
                super.run();
                playPausebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPausebtnClick();
                    }
                });
            }
        };
        playThread.start();

    }

    private void playPausebtnClick() {
         if(mediaPlayer.isPlaying()){
             playPausebtn.setImageResource(R.drawable.baseline_play_arrow_24);
             mediaPlayer.pause();
             seekBar.setMax(mediaPlayer.getDuration()/1000);
             PlayerActivity.this.runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     if(mediaPlayer!= null){
                         int mCurrentPosition =mediaPlayer.getCurrentPosition()/1000;
                         seekBar.setProgress(mCurrentPosition);

                     }
                     handler.postDelayed(this,1000);
                 }
             });
         }
         else {
             playPausebtn.setImageResource(R.drawable.baseline_pause_24);
             mediaPlayer.start();
             seekBar.setMax(mediaPlayer.getDuration()/1000);
             PlayerActivity.this.runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     if(mediaPlayer!= null){
                         int mCurrentPosition =mediaPlayer.getCurrentPosition()/1000;
                         seekBar.setProgress(mCurrentPosition);

                     }
                     handler.postDelayed(this,1000);
                 }
             });
         }

    }
}