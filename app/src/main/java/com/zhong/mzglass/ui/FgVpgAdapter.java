package com.zhong.mzglass.ui;

import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class FgVpgAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mFgList;

    public FgVpgAdapter(@NonNull FragmentManager fm, int behavior, ArrayList<Fragment> fgList) {
        super(fm, behavior);
        mFgList = fgList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFgList.get(position);
    }

    @Override
    public int getCount() {
        return mFgList.size();
    }
}
