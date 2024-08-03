package com.minibox.minideveloper.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class MyPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragmentList;
        private final String[] title;


    public MyPagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragmentList, String[] title) {
        super(fm);
        this.fragmentList = fragmentList;
        this.title = title;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList != null ? fragmentList.size():0;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

}
