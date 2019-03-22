package com.wave.digitalidentity.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wave.digitalidentity.Activities.MainActivity;
import com.wave.digitalidentity.Models.User;

import net.glxn.qrgen.android.QRCode;

import java.util.Objects;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import letswave.co.in.wave.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class IdentityFragment extends Fragment {

    @BindView(R.id.identityCardProfilePictureImageView)
    CircleImageView identityCardProfilePictureImageView;
    @BindView(R.id.identityCardUserNameTextView)
    TextView identityCardUserNameTextView;
    @BindView(R.id.servicesCardMatricTextView)
    TextView identityCardMatricTextView;
    @BindView(R.id.identityCardEmailTextView)
    TextView identityCardEmailTextView;
    @BindView(R.id.identityCardQRCodeImageView)
    ImageView identityCardQRCodeImageView;
    @BindString(R.string.placeholder_image)
    String placeholderImageUrl;

    private Unbinder unbinder;
    private View rootView;
    private User currentUser;

    public IdentityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_identity, container, false);
        initializeViews();
        initializeComponents();
        return rootView;
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(IdentityFragment.this, rootView);
        placeholderData();
    }

    private void placeholderData() {
        Glide.with(rootView.getContext()).load("http://keenthemes.com/preview/metronic/theme/assets/pages/media/profile/people19.png").into(identityCardProfilePictureImageView);
    }

    private void initializeComponents() {

    }

    private void generateQrCode(String content) {
        Glide.with(rootView.getContext()).load(QRCode.from(content).withSize(300,300).bitmap()).into(identityCardQRCodeImageView);
    }

    @Override
    public void onStart() {
        super.onStart();
        unbinder = ButterKnife.bind(IdentityFragment.this, rootView);
        currentUser = ((MainActivity) Objects.requireNonNull(getActivity())).getCurrentUser();
        generateQrCode(currentUser.getAuthorityIssuedId());
        identityCardUserNameTextView.setText(currentUser.getName());
        identityCardMatricTextView.setText(currentUser.getAuthorityIssuedId());
        identityCardEmailTextView.setText(currentUser.getEmail());
        if (currentUser.getPhoto() == null || TextUtils.isEmpty(currentUser.getPhoto()) || currentUser.getPhoto().equals("null"))
            Glide.with(rootView.getContext()).load(placeholderImageUrl).into(identityCardProfilePictureImageView);
        else
            Glide.with(rootView.getContext()).load(currentUser.getPhoto()).into(identityCardProfilePictureImageView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
