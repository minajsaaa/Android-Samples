package com.blueinno.android.alcoholsensor.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.blueinno.android.alcoholsensor.R;

import java.util.ArrayList;

public class PagerAdapter extends FragmentPagerAdapter {

    public static int pos = 0;

    private Context mContext;
    private ArrayList<Fragment> fragments;

    public PagerAdapter(Context context, FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        mContext = context;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        setPos(position);

        String PageTitle = "";

        switch(pos) {
            case 0: PageTitle = mContext.getString(R.string.title_section1);    break;
            case 1: PageTitle = mContext.getString(R.string.title_section2);    break;
            case 2: PageTitle = mContext.getString(R.string.title_section3);    break;
            case 3: PageTitle = mContext.getString(R.string.title_section4);    break;
            case 4: PageTitle = mContext.getString(R.string.title_section5);    break;
        }
        return PageTitle;
    }

    public static int getPos() {
        return pos;
    }

    public static void setPos(int pos) {
        PagerAdapter.pos = pos;
    }

}
