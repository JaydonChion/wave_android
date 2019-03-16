package letswave.co.in.wave.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import letswave.co.in.wave.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServicesFragment extends Fragment {


    public ServicesFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_services, container, false);
    }

}
