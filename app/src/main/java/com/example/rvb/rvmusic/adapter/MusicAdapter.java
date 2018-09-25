package com.example.rvb.rvmusic.adapter;

import android.content.Context;
import android.media.browse.MediaBrowser;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rvb.rvmusic.OnItemClickListener;
import com.example.rvb.rvmusic.R;

import java.util.List;

/**
 * @author abc
 * @data 2018/9/24
 * desc
 */
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private Context mContext;
    private List<MediaBrowser.MediaItem> mMediaItemList;
    private OnItemClickListener mOnItemClickListener;
    class ViewHolder extends RecyclerView.ViewHolder {
        // TODO: 声明组件
        TextView textTitle;
        TextView mTextView;
        public ViewHolder(View view) {
            super(view);
            // TODO: 注册组件,view.findViewById(R.id.xxx)
            textTitle = view.findViewById(R.id.tv_song);
            mTextView = view.findViewById(R.id.tv_sing);
        }

    }

    public MusicAdapter(Context context, List<MediaBrowser.MediaItem> list) {
        this.mContext = context;
        this.mMediaItemList = list;
    }
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int itemViewId = R.layout.recycler_view_music;
        ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(itemViewId, parent, false));
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        // TODO: 绑定组件的事件
        holder.textTitle.setText(mMediaItemList.get(position).getDescription().getTitle());
        holder.mTextView.setText(mMediaItemList.get(position).getDescription().getTitle());
        // 如果设置了回调，则设置点击事件
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mMediaItemList.size();
    }
}
