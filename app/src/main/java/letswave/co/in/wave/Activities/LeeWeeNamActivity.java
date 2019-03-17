package letswave.co.in.wave.Activities;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import letswave.co.in.wave.R;

public class LeeWeeNamActivity extends AppCompatActivity {

    @BindView(R.id.leeWeeNamToolbar)
    Toolbar leeWeeNamToolbar;
    @BindView(R.id.leeWeeNamBarCodeImageView)
    ImageView leeWeeNamBarCodeImageView;

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
        generateBarCode("abcde12345");
    }

    private void generateBarCode(String content) {
        try {
            Glide.with(getApplicationContext()).load(createBarcodeBitmap(content,80,32)).into(leeWeeNamBarCodeImageView);
        } catch (WriterException e) {
            Log.v("GEN_BAR", e.getMessage());
        }
    }

    private Bitmap createBarcodeBitmap(String data, int width, int height) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        String finalData = Uri.encode(data);
        BitMatrix bm = writer.encode(finalData, BarcodeFormat.CODE_128, width, 1);
        int bmWidth = bm.getWidth();
        Bitmap imageBitmap = Bitmap.createBitmap(bmWidth, height, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < bmWidth; i++) {
            int[] column = new int[height];
            Arrays.fill(column, bm.get(i, 0) ? Color.BLACK : Color.WHITE);
            imageBitmap.setPixels(column, 0, 1, i, 0, 1, height);
        }
        return imageBitmap;
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

}
