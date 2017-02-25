package com.example.andrey.myapplication2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Адаптер для вкладок для активити регистрации
 */
public class LoginTabsPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment>  mFragmentList = new ArrayList<>();
    private final List<String>  mFragmentTitleList = new ArrayList<>();

    public LoginTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fr, String title) {
        mFragmentList.add(fr);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
