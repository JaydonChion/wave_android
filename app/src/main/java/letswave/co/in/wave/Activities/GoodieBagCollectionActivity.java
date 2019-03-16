package letswave.co.in.wave.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import letswave.co.in.wave.R;

public class GoodieBagCollectionActivity extends AppCompatActivity {

    @BindView(R.id.goodieBagCollectionToolbar)
    Toolbar goodieBagCollectionToolbar;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goodie_bag_collection);


        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(GoodieBagCollectionActivity.this);
        setSupportActionBar(goodieBagCollectionToolbar);
        goodieBagCollectionToolbar.setTitleTextColor(Color.WHITE);
    }

    private void initializeComponents() {

    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
