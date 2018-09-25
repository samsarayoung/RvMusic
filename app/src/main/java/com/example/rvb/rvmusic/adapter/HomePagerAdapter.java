package com.example.rvb.rvmusic.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.rvb.rvmusic.fragment.LocalFragment;
import com.example.rvb.rvmusic.fragment.MoreFragment;
import com.example.rvb.rvmusic.fragment.NetFragment;

/**
 * @author abc
 * @data 2018/9/18
 * desc
 */
public class HomePagerAdapter extends FragmentPagerAdapter{
    private String[] mTitles = new String[]{"诸葛亮", " 张良"," 陈平" };
    public HomePagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 1){
            return new NetFragment();
        }else if(position == 2){
            return  new MoreFragment();
        }else if(position == 3){
            return  new LocalFragment();
        }
        return new LocalFragment();
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }
    /**
     * 联动 TabLayout 上的 Title
     * @param position
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
