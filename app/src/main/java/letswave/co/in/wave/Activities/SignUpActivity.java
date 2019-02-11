package letswave.co.in.wave.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import letswave.co.in.wave.R;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.signUpLogoImageView)
    ImageView signUpLogoImageView;
    @BindView(R.id.signUpNameEditText)
    TextInputEditText signUpNameEditText;
    @BindView(R.id.signUpEmailEditText)
    TextInputEditText signUpEmailEditText;
    @BindView(R.id.signUpMatricEditText)
    TextInputEditText signUpMatricEditText;
    @BindView(R.id.signUpPasswordEditText)
    TextInputEditText signUpPasswordEditText;
    @BindView(R.id.signUpConfirmPasswordEditText)
    TextInputEditText signUpConfirmPasswordEditText;
    @BindView(R.id.signUpNextButton)
    Button signUpNextButton;

    private Unbinder unbinder;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(SignUpActivity.this);
        Glide.with(getApplicationContext()).load(R.drawable.logo).into(signUpLogoImageView);
    }

    private void initializeComponents() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.signUpNextButton)
    public void onSignUpNextButtonPress() {
        String name = Objects.requireNonNull(signUpNameEditText.getText()).toString();
        String email = Objects.requireNonNull(signUpEmailEditText.getText()).toString();
        String matric = Objects.requireNonNull(signUpMatricEditText.getText()).toString();
        String password = Objects.requireNonNull(signUpPasswordEditText.getText()).toString();
        String confirmPassword = Objects.requireNonNull(signUpConfirmPasswordEditText.getText()).toString();
        Intent phoneIntent = new Intent(SignUpActivity.this, PhoneNumberActivity.class);
        phoneIntent.putExtra("NAME", name);
        phoneIntent.putExtra("EMAIL", email);
        phoneIntent.putExtra("MATRIC", matric);
        phoneIntent.putExtra("PASSWORD", password);
        startActivity(phoneIntent);
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
