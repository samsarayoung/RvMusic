package com.example.rvb.rvmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * @author abc
 * @data 2018/9/19
 * desc
 */
public class MusicService  extends Service{
     MediaSession mMediaSession;
    private PlaybackState mPlaybackState;
   // private MultiPlayer mPlayer;

    /**
     * 初始化和设置MediaSessionCompat
     * MediaSessionCompat用于告诉系统及其他应用当前正在播放的内容,以及接收什么类型的播放控制
     */

    private void setUpMediaSession(){
        mMediaSession = new MediaSession(this,"Rv");
        mMediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPause() {
                super.onPause();
            }

            @Override
            public void onPlay() {
                super.onPlay();
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
            }

            @Override
            public void onStop() {
                super.onStop();
            }
        });
        mMediaSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
    private static final class MultiPlayer implements MediaPlayer.OnErrorListener,
            MediaPlayer.OnCompletionListener {
        private final WeakReference<MusicService> mService;
        private MediaPlayer mCurrentMediaPlayer = new  MediaPlayer();
        private MediaPlayer mNextMediaPlayer;
        private boolean mIsInitialized = false;
        private Handler mHandler;
        public MultiPlayer(final MusicService service) {
            mService = new WeakReference<MusicService>(service);
            mCurrentMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);

        }

        public void setDataSource(final String path) {
            mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path);
            if (mIsInitialized) {
                setNextDataSource(null);
            }
        }

        private boolean setDataSourceImpl(final MediaPlayer player,final String path){
            try {
                player.reset();
                player.setOnPreparedListener(null);
            }
        }

    }
*/
}
