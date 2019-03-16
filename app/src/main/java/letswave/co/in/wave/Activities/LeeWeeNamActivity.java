package letswave.co.in.wave.Activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import letswave.co.in.wave.R;

public class LeeWeeNamActivity extends AppCompatActivity {

    @BindView(R.id.leeWeeNamToolbar)
    Toolbar leeWeeNamToolbar;
    @BindView(R.id.leeWeeNamBarCodeImageView)
    ImageView leeWeeNamBarCodeImageView;

    private BarcodeEncoder barcodeEncoder;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lee_wee_nam);

        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(LeeWeeNamActivity.this);
        setSupportActionBar(leeWeeNamToolbar);
        leeWeeNamToolbar.setTitleTextColor(Color.WHITE);
    }

    private void initializeComponents() {
        barcodeEncoder = new BarcodeEncoder();

        new GenerateAndRenderBarCode().execute("abcde12345");
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @SuppressLint("StaticFieldLeak")
    private class GenerateAndRenderBarCode extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                return barcodeEncoder.encodeBitmap(strings[0], BarcodeFormat.CODE_128, 1024, 164);
            } catch (WriterException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null && leeWeeNamBarCodeImageView != null)
                Glide.with(getApplicationContext()).load(bitmap).into(leeWeeNamBarCodeImageView);
        }
    }
}
