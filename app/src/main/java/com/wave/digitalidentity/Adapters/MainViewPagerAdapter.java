package com.wave.digitalidentity.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wave.digitalidentity.Fragments.IdentityFragment;
import com.wave.digitalidentity.Fragments.ProfileFragment;
import com.wave.digitalidentity.Fragments.ServicesFragment;

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0: return new IdentityFragment();
            case 1: return new ServicesFragment();
            default: return new ProfileFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "Identity";
            case 1: return "Services";
            default: return "Profile";
        }
    }
}
