package com.example.rvb.rvmusic.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.rvb.rvmusic.R;
import com.example.rvb.rvmusic.adapter.HomePagerAdapter;
import com.example.rvb.rvmusic.service.MediaPlaybackService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author abc
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabLayouts)
    TabLayout mTabLayout;
    @BindView(R.id.appbar)
    AppBarLayout mAppbar;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.navigationView)
    NavigationView mNavigationView;
    @BindView(R.id.main_drawerLayout)
    DrawerLayout mDrawerLayout;
    HomePagerAdapter mHomePagerAdapter;
    TabLayout.Tab mOne;
    TabLayout.Tab mTwo;
    TabLayout.Tab mThree;
    private List<MediaBrowser.MediaItem> list;
    private MediaBrowser mMediaBrowser;
    private MediaController mController;
    Context mContext = MainActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_activity_main);
        mMediaBrowser = new MediaBrowser(
                this,
                new ComponentName(this, MediaPlaybackService.class),
                mBrowserConnectionCallback,
                null
        );
        initSystemBar();
        initView();
        initListener();
        ButterKnife.bind(this);


    }
    private void initView() {
        //设定Toolbar取代原本的actionbar;
        setSupportActionBar(mToolbar);
        //显示toolbar的返回按钮左上角图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.removeAllTabs();
        //设置tab模式，当前为系统默认模式
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        // 将TabLayout和ViewPager关联起来.
        mTabLayout.setupWithViewPager(mViewpager);
        mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        // 给Tabs设置适配器*/
        mViewpager.setAdapter(mHomePagerAdapter);

        mOne = mTabLayout.getTabAt(0);
        mTwo = mTabLayout.getTabAt(1);
        mThree = mTabLayout.getTabAt(2);
        mViewpager.setCurrentItem(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Browser发送连接请求
        mMediaBrowser.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMediaBrowser.connect();
    }

    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.viewpager:
                if(mController!=null){
                    handlerPlayEvent();
                }
                break;
                default:
        }

    }
    /**
     * 处理播放按钮事件
     */
    private void handlerPlayEvent(){
        switch (mController.getPlaybackState().getState()){
            case PlaybackState.STATE_PLAYING:
                mController.getTransportControls().pause();
                break;
            case PlaybackState.STATE_PAUSED:
                mController.getTransportControls().play();
                break;
            default:
                mController.getTransportControls().playFromSearch("", null);
                break;
        }
    }

    /**
     * 连接状态的回调接口，连接成功时会调用onConnected()方法
     */
    private final MediaBrowser.ConnectionCallback mBrowserConnectionCallback =
            new MediaBrowser.ConnectionCallback() {
                Context mContext;

                @Override
                public void onConnected() {
                    Log.e(TAG, "onConnected------");
                    if (mMediaBrowser.isConnected()) {
                        //mediaId即为MediaBrowserService.onGetRoot的返回值
                        //若Service允许客户端连接，则返回结果不为null，其值为数据内容层次结构的根ID
                        //若拒绝连接，则返回null
                        String mediaId = mMediaBrowser.getRoot();

                        //Browser通过订阅的方式向Service请求数据，发起订阅请求需要两个参数，其一为mediaId
                        //而如果该mediaId已经被其他Browser实例订阅，则需要在订阅之前取消mediaId的订阅者
                        //虽然订阅一个 已被订阅的mediaId 时会取代原Browser的订阅回调，但却无法触发onChildrenLoaded回调

                        //ps：虽然基本的概念是这样的，但是Google在官方demo中有这么一段注释...
                        // This is temporary: A bug is being fixed that will make subscribe
                        // consistently call onChildrenLoaded initially, no matter if it is replacing an existing
                        // subscriber or not. Currently this only happens if the mediaID has no previous
                        // subscriber or if the media content changes on the service side, so we need to
                        // unsubscribe first.
                        //大概的意思就是现在这里还有BUG，即只要发送订阅请求就会触发onChildrenLoaded回调
                        //所以无论怎样我们发起订阅请求之前都需要先取消订阅
                        mMediaBrowser.unsubscribe(mediaId);
                        //之前说到订阅的方法还需要一个参数，即设置订阅回调SubscriptionCallback
                        //当Service获取数据后会将数据发送回来，此时会触发SubscriptionCallback.onChildrenLoaded回调
                        mMediaBrowser.subscribe(mediaId, mBrowserSubscriptionCallback);

                        mController = new MediaController(mContext, mMediaBrowser.getSessionToken());
                        mController.registerCallback(ControllerCallback);
                    }
                }

                @Override
                public void onConnectionFailed() {
                    Log.e(TAG, "连接失败！");
                }
            };
    /**
     * 向媒体浏览器服务(MediaBrowserService)发起数据订阅请求的回调接口
     */
    private final MediaBrowser.SubscriptionCallback mBrowserSubscriptionCallback =
            new MediaBrowser.SubscriptionCallback(){
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowser.MediaItem> children) {
                    Log.e(TAG,"onChildrenLoaded------");
                    //children 即为Service发送回来的媒体数据集合
                    for (MediaBrowser.MediaItem item:children){
                        Log.e(TAG,item.getDescription().getTitle().toString());
                        list.add(item);
                    }
                    //MusicAdapter.notifyDataSetChanged();
                }
            };

    /**
     * 媒体控制器控制播放过程中的回调接口，可以用来根据播放状态更新UI
     */
    private final MediaController.Callback ControllerCallback =
            new MediaController.Callback() {
                /***
                 * 音乐播放状态改变的回调
                 */
                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackState state) {
                    //这里根据播放状态的改变，本地ui做相应的改变，例如播放模式，播放、暂停，进度条等
                    switch (state.getState()){
                        case PlaybackState.STATE_NONE://无任何状态
                           // textTitle.setText("");
                          //  btnPlay.setText("开始");
                            break;
                        case PlaybackState.STATE_PAUSED:
                           // btnPlay.setText("开始");
                            break;
                        case PlaybackState.STATE_PLAYING:
                           // btnPlay.setText("暂停");
                            break;
                            default:
                    }
                }




                /**
                 * 播放音乐改变的回调
                 */
                @Override
                public void onMetadataChanged(MediaMetadata metadata) {
                    //textTitle.setText(metadata.getDescription().getTitle());
                }
            };

    private Uri rawToUri(int id){
        String uriStr = "android.resource://" + getPackageName() + "/" + id;
        return Uri.parse(uriStr);
    }

    private void initListener() {
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    private MenuItem mPreMenuItem;

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (mPreMenuItem != null) {
                            mPreMenuItem.setChecked(false);
                        }
                        switch((item.getItemId())){


                            case R.id.music_act:
                                startActivity(new Intent(MainActivity.this, MusicActivity.class));
                                break;
                            case R.id.finish_a:
                                killAll();
                               // closeAnimator(deltaX);
                                break;
                            case R.id.photo:
                                startActivity(new Intent(MainActivity.this, MusicActivity.class));
                                break;
                            default:
                                break;

                        }
                        Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();

                        item.setChecked(true);
                        //关闭菜单
                        mDrawerLayout.closeDrawers();
                        mPreMenuItem = item;
                        return false;
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //打开抽屉侧滑菜单
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
}
