package com.minibox.minideveloper.Adapter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.minibox.minideveloper.Entity.WyMusicEntity;
import com.minibox.minideveloper.R;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.mViewHolder> {
    private final Context mContext;
    private List<WyMusicEntity.ResultBean.SongsBean> eapiData;
    private OnItemClickListener monItemClickListener;

    public MusicAdapter(Context context){
        this.mContext = context;
    }
    public void SetData(List<WyMusicEntity.ResultBean.SongsBean> eapiData){
        this.eapiData = eapiData;
    }

    public void setOnRecyclerItemClickListener(OnItemClickListener listener){
        this.monItemClickListener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(int songId,String url,String name,String author);
    }


    @NonNull
    @Override
    public MusicAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new mViewHolder(LayoutInflater.from(mContext).inflate(R.layout.music_search_recycler,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.mViewHolder holder, int position) {
        WyMusicEntity.ResultBean.SongsBean musicEntity = eapiData.get(position);

        //获取作者名字
        List<WyMusicEntity.ResultBean.SongsBean.ArBean> author = eapiData.get(position).getAr();
        //获取歌曲图片等信息
        WyMusicEntity.ResultBean.SongsBean.AlBean musicMsg = eapiData.get(position).getAl();

        Glide.with(mContext).load(musicMsg.getPicUrl()).into(holder.img);
        holder.title.setText(musicEntity.getName());
        holder.author.setText(author.size()<2 ?
                author.get(0).getName():
                author.get(0).getName()+" "+ author.get(1).getName());
        /****点击事件****/
        holder.itemView.setOnClickListener(v -> {
            int songId = musicEntity.getId();
            monItemClickListener.onItemClick(songId,musicMsg.getPicUrl(),
                    musicEntity.getName(),author.get(0).getName());
        });

    }

    @Override
    public int getItemCount() {
        return eapiData != null ? eapiData.size() : 0;
    }

    public static class mViewHolder extends RecyclerView.ViewHolder{
        private final ShapeableImageView img;
        private final TextView title;
        private final TextView author;

        public mViewHolder(@NonNull View v) {
            super(v);
            author = v.findViewById(R.id.search_recycler_author);
            title = v.findViewById(R.id.search_recycler_title);
            img = v.findViewById(R.id.search_recycler_img);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull mViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

    }
}
