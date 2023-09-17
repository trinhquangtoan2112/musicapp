package com.example.btl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE=1;
    ViewPager viewPager;
    TabLayout tabLayout;
    ViewPageAdpter viewPageAdpter;
    static ArrayList<MusicFiles> musicFiles;
    static boolean suffleBoolean =false ,repeatBoolean=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         permisson();

    }

    private void permisson(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    ,REQUEST_CODE);
        }
        else {

            musicFiles =getAllAudio(this);

            initViewPage();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode ==REQUEST_CODE){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){

                musicFiles =getAllAudio(this);
                initViewPage();
            }
            else {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        ,REQUEST_CODE);
            }
        }
    }

    private void initViewPage(){
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout =(TabLayout) findViewById(R.id.tab_layout);

        viewPageAdpter= new ViewPageAdpter(getSupportFragmentManager());
       viewPageAdpter.addFragment(new SongsFragment(),"Songs");
       viewPageAdpter.addFragment(new AlbumFragment(),"Album");
        viewPageAdpter.addFragment(new DownloadFragment(),"Download");
       viewPager.setAdapter(viewPageAdpter);
       tabLayout.setupWithViewPager(viewPager);
    }

    public static class ViewPageAdpter  extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;
        public ViewPageAdpter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles =new ArrayList<>();
        }

            void addFragment(Fragment fragment,String title){

            fragments.add(fragment);
            titles.add(title);
            }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        @NonNull
        @Override
        public CharSequence getPageTitle(int position){
                     return titles.get(position);
        }
    }


    public static ArrayList<MusicFiles> getAllAudio(Context context){
        ArrayList<MusicFiles> tempAudioList =new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection  = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,

        };
        Cursor cursor = context.getContentResolver().query(uri,projection,null,null,null);
        if(cursor!=null){
            while (cursor.moveToNext()){
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                  MusicFiles musicFiles =new MusicFiles(path,title,artist,album,duration);
                Log.e("Path"+path,"Album"+album +"title"+ title +"duration"+duration);
                  tempAudioList.add(musicFiles);
            }
            cursor.close();
        }
        return tempAudioList;
    }

}