package letswave.co.in.wave.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import letswave.co.in.wave.Activities.MainActivity;
import letswave.co.in.wave.Activities.SignInActivity;
import letswave.co.in.wave.Models.User;
import letswave.co.in.wave.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    @BindView(R.id.profileCancelTextView)
    TextView profileCancelTextView;
    @BindView(R.id.profileDoneTextView)
    TextView profileDoneTextView;
    @BindView(R.id.profileImageView)
    CircleImageView profileImageView;
    @BindView(R.id.profileChangePictureTextView)
    TextView profileChangePictureTextView;
    @BindView(R.id.profileNameEditText)
    EditText profileNameEditText;
    @BindView(R.id.profileEmailEditText)
    EditText profileEmailEditText;
    @BindView(R.id.profileMatricEditText)
    EditText profileMatricEditText;
    @BindView(R.id.profilePhoneEditText)
    EditText profilePhoneEditText;
    @BindView(R.id.profileSignOutTextView)
    TextView profileSignOutTextView;
    @BindString(R.string.placeholder_image)
    String placeholderImageUrl;
    @BindString(R.string.domain)
    String baseServerUrl;

    private View rootView;
    private Unbinder unbinder;
    private FirebaseAuth firebaseAuth;
    private User currentUser;
    private RequestQueue requestQueue;
    private MaterialDialog materialDialog;
    private SharedPreferences.Editor editor;
    private String profilePictureUrl;
    private StorageReference storageReference;
    private static final int RC_PICK_IMAGE = 141;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeViews();
        initializeComponents();
        return rootView;
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(ProfileFragment.this, rootView);
        currentUser = ((MainActivity) Objects.requireNonNull(getActivity())).getCurrentUser();
        profileNameEditText.setText(currentUser.getName());
        profileEmailEditText.setText(currentUser.getEmail());
        profileMatricEditText.setText(currentUser.getAuthorityIssuedId());
        profilePhoneEditText.setText(currentUser.getPhone());
        if (currentUser.getPhoto()==null || TextUtils.isEmpty(currentUser.getPhoto()) || currentUser.getPhoto().equals("null")) {
            profilePictureUrl = placeholderImageUrl;
            Glide.with(rootView.getContext()).load(placeholderImageUrl).into(profileImageView);
        }
        else Glide.with(rootView.getContext()).load(currentUser.getPhoto()).into(profileImageView);
    }

    private void initializeComponents() {
        firebaseAuth = FirebaseAuth.getInstance();
        editor = ((MainActivity) Objects.requireNonNull(getActivity())).getEditor();
        requestQueue = Volley.newRequestQueue(rootView.getContext());
        storageReference = FirebaseStorage.getInstance().getReference("profiles/images/"+currentUser.getId()+"/dp.jpg");
    }

    @OnClick(R.id.profileSignOutTextView)
    public void onSignOutTextViewPress() {
        firebaseAuth.signOut();
        editor.putString("email", null);
        editor.apply();
        Objects.requireNonNull(getActivity()).startActivity(new Intent(rootView.getContext(), SignInActivity.class));
        Objects.requireNonNull(getActivity()).finish();
    }

    @OnClick(R.id.profileDoneTextView)
    public void onDoneTextViewPress() {
        String name = profileNameEditText.getText().toString();
        String matric = profileMatricEditText.getText().toString();
        String phone = profilePhoneEditText.getText().toString();
        updateUser(name, matric, phone, profilePictureUrl);
    }

    private void updateUser(String name, String matric, String phone, String profilePictureUrl) {
        try {
            if (materialDialog!=null && materialDialog.isShowing()) materialDialog.dismiss();
            materialDialog = new MaterialDialog.Builder(rootView.getContext())
                    .title("Wave")
                    .content("Updating profile..")
                    .progress(true, 0)
                    .titleColorRes(android.R.color.black)
                    .contentColorRes(R.color.colorTextDark)
                    .show();
            JSONObject updateJson = new JSONObject();
            updateJson.put("name", name);
            updateJson.put("authority_id", matric);
            updateJson.put("phone", phone);
            updateJson.put("photo", profilePictureUrl);
            String requestUrl = baseServerUrl+"/users/"+currentUser.getId();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, requestUrl, updateJson, response -> {
                try {
                    String userId = response.getString("_id");
                    String nameNew = response.getString("name");
                    String email = response.getString("email");
                    String authorityName = response.getString("authority_name");
                    String authorityIssuedId = response.getString("authority_id");
                    String photo = response.getString("photo");
                    String phoneNew = response.getString("phone");
                    User currentUser = new User(userId, authorityName, authorityIssuedId, nameNew, email, photo, phoneNew);
                    notifyMessage("Profile updated!");
                    ((MainActivity) Objects.requireNonNull(getActivity())).setCurrentUser(currentUser);
                } catch (JSONException e) { notifyMessage(e.getMessage()); }
            }, error -> {
                notifyMessage(error.getMessage());
                Log.v("UPDATE_USER", error.getMessage());
            });
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            notifyMessage(e.getMessage());
        }
    }

    private void notifyMessage(String message) {
        if (materialDialog!=null && materialDialog.isShowing()) materialDialog.dismiss();
        Snackbar.make(profileImageView, message, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.profileChangePictureTextView)
    public void profilePictureImageViewPress() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a new display picture for your profile!"), RC_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) notifyMessage("No image selected!");
            else uploadImage(data.getData());
        }
    }

    private void uploadImage(Uri data) {
        materialDialog = new MaterialDialog.Builder(rootView.getContext())
                .title(R.string.app_name)
                .content("Uploading image!")
                .progress(true, 0)
                .titleColor(Color.BLACK)
                .contentColorRes(R.color.colorTextDark)
                .show();
        storageReference.putFile(data).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(rootView.getContext()).load(uri).into(profileImageView);
            profilePictureUrl = uri.toString();
            updateUser(profileNameEditText.getText().toString(), profileMatricEditText.getText().toString(), profilePhoneEditText.getText().toString(), profilePictureUrl);
        }).addOnFailureListener(e -> notifyMessage(e.getMessage()))).addOnFailureListener(e -> notifyMessage(e.getMessage()));
    }

    @Override
    public void onStart() {
        super.onStart();
        unbinder = ButterKnife.bind(ProfileFragment.this, rootView);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
