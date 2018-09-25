package com.example.rvb.rvmusic.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import com.example.rvb.rvmusic.R;

/**
 * @author abc
 * @data 2018/9/18
 * desc
 */
public class MusicActivity extends BaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.module_activity_muisc);

    }

}
