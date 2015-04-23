package com.fuzzproductions.fuzzapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by oliverbud on 4/22/15.
 */
public class myFragmentPagerAdapter extends FragmentPagerAdapter {

    List<myListFragment> fragments;

    public myFragmentPagerAdapter(FragmentManager fm){
        super(fm);
    }

    public myFragmentPagerAdapter(FragmentManager fm, List<myListFragment> fragments){
        super(fm);
        Log.d("..........", "instantiate myFragmentPagerAdapter");
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("..........", "getItem myFragmentPagerAdapter: " + position);

        switch(position){
            case 0:
                Log.d("..........", "firstFragmebt");
                return fragments.get(0);
            case 1:
                Log.d("..........", "secondFragmebt");
                return fragments.get(1);
            case 2:
                Log.d("..........", "thirdFragmebt");
                return fragments.get(2);
        }
        return new myListFragment();
    }

    @Override
    public int getCount() {
        return 3;
    }

}
