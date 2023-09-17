package com.example.btl;

import static com.example.btl.MainActivity.musicFiles;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

public class MusicAdpter extends RecyclerView.Adapter<MusicAdpter.MyViewHolder> {

     private Context context;
     private ArrayList<MusicFiles> mfile;

     MusicAdpter(Context mcontext,ArrayList<MusicFiles> mfiles){
         this.context=mcontext;
         this.mfile=mfiles;

     }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(context).inflate(R.layout.music_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                   holder.tx1.setText(mfile.get(position).getTitle());

                   holder.itemView.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           Intent intent =new Intent(context,PlayerActivity.class);
                           intent.putExtra("position",position);
                           context.startActivity(intent);
                       }
                   });
                   holder.menuMore.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           PopupMenu popupMenu =new PopupMenu(context,view);
                           popupMenu.getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                           popupMenu.show();
                           popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                               @Override
                               public boolean onMenuItemClick(MenuItem menuItem) {
                                  if (menuItem.getItemId() == R.id.delete){
                                      Toast.makeText(context,"Xoa bai hat",Toast.LENGTH_LONG).show();
                                      mfile.remove(position);

                               notifyItemRemoved(position);
                               notifyItemRangeChanged(position,mfile.size());

                                  }
                                   return true;
                               }

                           });
                       }
                   });

    }

    @Override
    public int getItemCount() {
        return mfile.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

         TextView tx1 ;
         ImageView img1,menuMore;
        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            img1 =itemView.findViewById(R.id.music_img);
            tx1 = itemView.findViewById(R.id.music_name);
            menuMore=itemView.findViewById(R.id.optionSong);
        }
    }
    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever =new MediaMetadataRetriever();

        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        try {
            retriever.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return art;
    }
}
