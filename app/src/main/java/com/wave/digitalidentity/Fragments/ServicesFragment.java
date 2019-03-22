package com.wave.digitalidentity.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wave.digitalidentity.Activities.GoodieBagCollectionActivity;
import com.wave.digitalidentity.Activities.HallAccessActivity;
import com.wave.digitalidentity.Activities.HiveActivity;
import com.wave.digitalidentity.Activities.LeeWeeNamActivity;
import com.wave.digitalidentity.Activities.MainActivity;
import com.wave.digitalidentity.Activities.SleepingPodActivity;
import com.wave.digitalidentity.Models.User;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import letswave.co.in.wave.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServicesFragment extends Fragment {


    @BindView(R.id.servicesHiveMobileAccessLayout)
    LinearLayout servicesHiveMobileAccessLayout;
    @BindView(R.id.servicesLeeWeeNamMobileAccessLayout)
    LinearLayout servicesLeeWeeNamMobileAccessLayout;
    @BindView(R.id.servicesSleepingPodBookingLayout)
    LinearLayout servicesSleepingPodBookingLayout;
    @BindView(R.id.servicesHallAccessLayout)
    LinearLayout servicesHallAccessLayout;
    @BindView(R.id.servicesGoodieBagCollectionLayout)
    LinearLayout servicesGoodieBagCollectionLayout;

    private Unbinder unbinder;
    private View rootView;
    private User currentUser;

    public ServicesFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_services, container, false);
        initializeViews();
        initializeComponents();
        return rootView;
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(ServicesFragment.this, rootView);
    }

    private void initializeComponents() {
        currentUser = ((MainActivity) Objects.requireNonNull(getActivity())).getCurrentUser();
    }

    @Override
    public void onStart() {
        super.onStart();
        unbinder = ButterKnife.bind(ServicesFragment.this, rootView);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @OnClick(R.id.servicesHiveMobileAccessLayout)
    public void onHiveLayoutPress() {
        startActivity(new Intent(rootView.getContext(), HiveActivity.class));
    }


    @OnClick(R.id.servicesLeeWeeNamMobileAccessLayout)
    public void onLeeWeeNamLayoutPress() {
        Intent leeWeeNamIntent = new Intent(rootView.getContext(), LeeWeeNamActivity.class);
        leeWeeNamIntent.putExtra("USER", currentUser);
        startActivity(leeWeeNamIntent);    }


    @OnClick(R.id.servicesSleepingPodBookingLayout)
    public void onSleepingPodLayoutPress() {
        startActivity(new Intent(rootView.getContext(), SleepingPodActivity.class));
    }


    @OnClick(R.id.servicesHallAccessLayout)
    public void onHallAccessLayoutPress() {
        startActivity(new Intent(rootView.getContext(), HallAccessActivity.class));
    }


    @OnClick(R.id.servicesGoodieBagCollectionLayout)
    public void onGoodieBagCollectionLayoutPress() {
        Intent goodieBagIntent = new Intent(rootView.getContext(), GoodieBagCollectionActivity.class);
        goodieBagIntent.putExtra("USER", currentUser);
        startActivity(goodieBagIntent);
    }

}
