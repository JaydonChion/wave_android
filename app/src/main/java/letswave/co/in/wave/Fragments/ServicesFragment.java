package letswave.co.in.wave.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import letswave.co.in.wave.Activities.GoodieBagCollectionActivity;
import letswave.co.in.wave.Activities.HallAccessActivity;
import letswave.co.in.wave.Activities.HiveActivity;
import letswave.co.in.wave.Activities.LeeWeeNamActivity;
import letswave.co.in.wave.Activities.SleepingPodActivity;
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
        startActivity(new Intent(rootView.getContext(), LeeWeeNamActivity.class));
    }


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
        startActivity(new Intent(rootView.getContext(), GoodieBagCollectionActivity.class));
    }

}
