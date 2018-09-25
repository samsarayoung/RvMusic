package com.example.rvb.rvmusic.service;

import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.service.media.MediaBrowserService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author abc
 * @data 2018/9/19
 * desc
 */
public class MediaPlaybackService extends MediaBrowserService {
    private MediaSession mMediaSession;
    private MediaPlayer mMediaPlayer;
    private PlaybackState mPlaybackState;


    private static final String TAG = "MediaPlaybackService";
    public static final String MEDIA_ID_ROOT = "__ROOT__";


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"onCreate--------");

        mPlaybackState = new PlaybackState.Builder()
                .setState(PlaybackState.STATE_NONE,0,1.0f)
                .build();

        mMediaSession.setCallback(SessionCallback);
        mMediaSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setPlaybackState(mPlaybackState);

        setSessionToken(mMediaSession.getSessionToken());

       // mMediaPlayer.setOnPreparedListener(PreparedListener);
        //mMediaPlayer.setOnCompletionListener(CompletionListener);
        // 1. 初始化 MediaSession
        // 2. 设置 MedisSessionCallback
        // 3. 开启 MediaButton 和 TransportControls 的支持
        // 4. 初始化 PlaybackState
        // 5. 关联 SessionToken
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mMediaSession != null){
            mMediaSession.release();
            mMediaSession = null;
        }
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        // 对每个访问端做一些访问权限判断等
        Log.e(TAG,"onGetRoot-----------");
        return new BrowserRoot(MEDIA_ID_ROOT, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowser.MediaItem>> result) {
        // 根据访问权限返回播放列表相关信息
        Log.e(TAG,"onLoadChildren--------");
        //将信息从当前线程中移除，允许后续调用sendResult方法
        result.detach();
        //我们模拟获取数据的过程，真实情况应该是异步从网络或本地读取数据
       /* MediaMetadata mediaMetadata = new MediaMetadata.Builder()
                .putString((MediaMetadata.METADATA_KEY_MEDIA_ID, ""+R.raw.jinglebells)
                .putString(MediaMetadata.METADATA_KEY_TITLE, "圣诞歌")
                .build();
                */
        ArrayList<MediaBrowser.MediaItem> mediaItems = new ArrayList<>();
       // mediaItems.add(createMediaItem(mediaMetadata));

        //向Browser发送数据
        result.sendResult(mediaItems);
    }
    private MediaBrowser.MediaItem createMediaItem(MediaMetadata metadata){
        return new MediaBrowser.MediaItem(
                metadata.getDescription(),
                MediaBrowser.MediaItem.FLAG_PLAYABLE
        );
    }

    /**
     * 响应控制器指令的回调
     */
   private MediaSession.Callback SessionCallback = new MediaSession.Callback() {
        /**
         * 响应MediaController.getTransportControls().play
         */
        @Override
        public void onPlay() {
            Log.e(TAG, "onPlay");
            if (mPlaybackState.getState() == PlaybackState.STATE_PAUSED) {
                mMediaPlayer.start();
                mPlaybackState = new PlaybackState.Builder()
                        .setState(PlaybackState.STATE_PLAYING, 0, 1.0f)
                        .build();
                mMediaSession.setPlaybackState(mPlaybackState);
            }

        }

        /**
         * 响应onPause
         */
        @Override
        public void onPause() {
            Log.e(TAG, "onPause");
            if (mPlaybackState.getState() == PlaybackState.STATE_PLAYING) {
                mMediaPlayer.pause();
                mPlaybackState = new PlaybackState.Builder()
                        .setState(PlaybackState.STATE_PAUSED, 0, 1.0f)
                        .build();
                mMediaSession.setPlaybackState(mPlaybackState);
            }
        }

        /**
         *  响应playFromUri
         * @param uri
         * @param extras
         */
        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            Log.e(TAG,"onPlayFromUri");
            try {
                switch (mPlaybackState.getState()){
                    case PlaybackState.STATE_PLAYING:
                    case PlaybackState.STATE_PAUSED:
                    case PlaybackState.STATE_NONE:
                        mMediaPlayer.reset();
                        mMediaPlayer.setDataSource(MediaPlaybackService.this,uri);
                        mMediaPlayer.prepare();//准备同步
                        mPlaybackState = new PlaybackState.Builder()
                                .setState(PlaybackState.STATE_CONNECTING,0,1.0f)
                                .build();
                        mMediaSession.setPlaybackState(mPlaybackState);
                        //我们可以保存当前播放音乐的信息，以便客户端刷新UI
                        mMediaSession.setMetadata(new MediaMetadata.Builder()
                                .putString(MediaMetadata.METADATA_KEY_TITLE,extras.getString("title"))
                                .build()
                        );
                        break;
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        /**
         * 监听MediaPlayer.prepare()
         */
        private MediaPlayer.OnPreparedListener PreparedListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer.start();
                mPlaybackState = new PlaybackState.Builder()
                        .setState(PlaybackState.STATE_PLAYING,0,1.0f)
                        .build();
                mMediaSession.setPlaybackState(mPlaybackState);
            }
        };

        /**
         * 监听播放结束的事件
         */
        private MediaPlayer.OnCompletionListener CompletionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mPlaybackState = new PlaybackState.Builder()
                        .setState(PlaybackState.STATE_NONE,0,1.0f)
                        .build();
                mMediaSession.setPlaybackState(mPlaybackState);
                mMediaPlayer.reset();
            }
        };
    };
}
