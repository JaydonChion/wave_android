package letswave.co.in.wave.Fragments;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Objects;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import letswave.co.in.wave.Activities.MainActivity;
import letswave.co.in.wave.Models.User;
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
    private BarcodeEncoder barcodeEncoder;
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
        barcodeEncoder = new BarcodeEncoder();
    }

    @SuppressLint("StaticFieldLeak")
    private class GenerateAndRenderQRCode extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                return barcodeEncoder.encodeBitmap(strings[0], BarcodeFormat.QR_CODE, 256, 256);
            } catch (WriterException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null && identityCardQRCodeImageView != null)
                Glide.with(rootView.getContext()).load(bitmap).into(identityCardQRCodeImageView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        unbinder = ButterKnife.bind(IdentityFragment.this, rootView);
        currentUser = ((MainActivity) Objects.requireNonNull(getActivity())).getCurrentUser();
        new GenerateAndRenderQRCode().execute(currentUser.getAuthorityIssuedId());
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
