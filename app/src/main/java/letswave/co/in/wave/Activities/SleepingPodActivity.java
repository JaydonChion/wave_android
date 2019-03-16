package letswave.co.in.wave.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import letswave.co.in.wave.R;

public class SleepingPodActivity extends AppCompatActivity {

    @BindView(R.id.sleepingPodToolbar)
    Toolbar sleepingPodToolbar;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleeping_pod);


        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(SleepingPodActivity.this);
        setSupportActionBar(sleepingPodToolbar);
        sleepingPodToolbar.setTitleTextColor(Color.WHITE);
    }

    private void initializeComponents() {

    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
