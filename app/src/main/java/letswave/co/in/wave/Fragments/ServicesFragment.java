package letswave.co.in.wave.Fragments;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import letswave.co.in.wave.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServicesFragment extends Fragment {

    @BindView(R.id.servicesCardProfilePictureImageView)
    CircleImageView servicesCardProfilePictureImageView;
    @BindView(R.id.servicesCardUserNameTextView)
    TextView servicesCardUserNameTextView;
    @BindView(R.id.servicesCardMatricTextView)
    TextView servicesCardMatricTextView;
    @BindView(R.id.servicesCardEmailTextView)
    TextView servicesCardEmailTextView;
    @BindView(R.id.servicesCardBarCodeImageView)
    ImageView servicesCardBarCodeImageView;
    @BindView(R.id.servicesCardQRCodeImageView)
    ImageView servicesCardQRCodeImageView;

    private Unbinder unbinder;
    private View rootView;
    private BarcodeEncoder barcodeEncoder;

    public ServicesFragment() {
        // Required empty public constructor
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
        placeholderData();
    }

    private void placeholderData() {
        Glide.with(rootView.getContext()).load("http://keenthemes.com/preview/metronic/theme/assets/pages/media/profile/people19.png").into(servicesCardProfilePictureImageView);
        //Glide.with(rootView.getContext()).load("https://internationalbarcodes.com/wp-content/uploads/sites/95/2013/11/Code-128.jpg").into(servicesCardBarCodeImageView);
        //Glide.with(rootView.getContext()).load("https://previews.123rf.com/images/foxindustry/foxindustry1310/foxindustry131000005/23317476-sample-qr-code-ready-to-scan-with-smart-phone.jpg").into(servicesCardQRCodeImageView);
    }

    private void initializeComponents() {
        barcodeEncoder = new BarcodeEncoder();
        new GenerateAndRenderBarCode().execute("abhishek");
        new GenerateAndRenderQRCode().execute("abhishek");
    }

    @SuppressLint("StaticFieldLeak")
    private class GenerateAndRenderBarCode extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                return barcodeEncoder.encodeBitmap(strings[0], BarcodeFormat.CODE_128, 400, 96);
            } catch (WriterException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap!=null) Glide.with(rootView.getContext()).load(bitmap).into(servicesCardBarCodeImageView);
        }
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
            if (bitmap!=null) Glide.with(rootView.getContext()).load(bitmap).into(servicesCardQRCodeImageView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        unbinder = ButterKnife.bind(ServicesFragment.this, rootView);
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
