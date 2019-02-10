package letswave.co.in.wave.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import letswave.co.in.wave.Fragments.DiscoverFragment;
import letswave.co.in.wave.Fragments.ProfileFragment;
import letswave.co.in.wave.Fragments.ServicesFragment;

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0: return new ServicesFragment();
            case 1: return new DiscoverFragment();
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
            case 0: return "Services";
            case 1: return "Discover";
            default: return "Profile";
        }
    }
}
