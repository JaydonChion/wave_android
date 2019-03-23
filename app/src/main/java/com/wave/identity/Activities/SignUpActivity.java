package com.wave.identity.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.wave.identity.Models.User;

import org.json.JSONException;
import org.json.JSONObject;

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
    private MaterialDialog materialDialog;
    private FirebaseAuth firebaseAuth;
    private RequestQueue requestQueue;
    private String requestUrl;

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
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestUrl = getString(R.string.domain)+"/users";
    }

    private void notifyMessage(String message) {
        if (materialDialog!=null && materialDialog.isShowing()) materialDialog.dismiss();
        Snackbar.make(signUpLogoImageView, message, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.signUpNextButton)
    public void onSignUpNextButtonPress() {
        String name = Objects.requireNonNull(signUpNameEditText.getText()).toString();
        String email = Objects.requireNonNull(signUpEmailEditText.getText()).toString();
        String matric = Objects.requireNonNull(signUpMatricEditText.getText()).toString();
        String password = Objects.requireNonNull(signUpPasswordEditText.getText()).toString();
        String confirmPassword = Objects.requireNonNull(signUpConfirmPasswordEditText.getText()).toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(matric) || TextUtils.isEmpty(password))
            Snackbar.make(signUpLogoImageView, "Please fill all the fields", Snackbar.LENGTH_LONG).show();
        else {
            if (password.equals(confirmPassword)) {
                materialDialog = new MaterialDialog.Builder(SignUpActivity.this)
                        .title(getString(R.string.app_name))
                        .content("Creating account")
                        .progress(true, 0)
                        .titleColorRes(android.R.color.black)
                        .contentColorRes(R.color.colorTextDark)
                        .show();
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) performNetworkRequest(name, email, matric);
                    else notifyMessage(Objects.requireNonNull(task.getException()).getMessage());
                });
            } else Snackbar.make(signUpLogoImageView, "Passwords do not match", Snackbar.LENGTH_LONG).show();
        }
    }

    private void performNetworkRequest(String name, String email, String matric) {
        try {
            JSONObject userObject = new JSONObject();
            userObject.put("id", Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
            userObject.put("name", name);
            userObject.put("authority_name", "Nanyang Technological University");
            userObject.put("authority_id", matric);
            userObject.put("email", email);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, requestUrl, userObject, response -> {
                try {
                    String userId = response.getString("_id");
                    String nameOnServer = response.getString("name");
                    String authorityName = response.getString("authority_name");
                    String authorityIssuedId = response.getString("authority_id");
                    String emailOnServer = response.getString("email");
                    String photo = response.getString("photo");
                    String phone = response.getString("phone");
                    User currentUser = new User(userId, authorityName, authorityIssuedId, nameOnServer, emailOnServer, photo, phone);
                    Intent phoneNumberActivityIntent = new Intent(SignUpActivity.this, MainActivity.class);
                    phoneNumberActivityIntent.putExtra("USER", currentUser);
                    startActivity(phoneNumberActivityIntent);
                } catch (JSONException e) {
                    notifyMessage(e.getMessage());
                }
            }, error -> notifyMessage(error.getMessage()));
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            notifyMessage(e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
