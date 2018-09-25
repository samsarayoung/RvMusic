package com.example.rvb.rvmusic.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rvb.rvmusic.R;

/**
 * @author abc
 * @data 2018/9/18
 * desc
 */
public class NetFragment extends BaseFragment {
    View mView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.module_fragment_net, container, false);
        return mView;
    }
}
