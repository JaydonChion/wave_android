package letswave.co.in.wave.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import letswave.co.in.wave.Activities.SignInActivity;
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
    @BindView(R.id.profileSchoolSpinner)
    Spinner profileSchoolSpinner;
    @BindView(R.id.profileSignOutTextView)
    TextView profileSignOutTextView;

    private View rootView;
    private Unbinder unbinder;
    private FirebaseAuth firebaseAuth;

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
    }

    private void initializeComponents() {
        firebaseAuth = FirebaseAuth.getInstance();
        String schoolsArray[] = getResources().getStringArray(R.array.schools);
        ArrayAdapter<String> schoolsStringDropDownAdapter = new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_spinner_dropdown_item, schoolsArray);
        profileSchoolSpinner.setAdapter(schoolsStringDropDownAdapter);
        profileSchoolSpinner.setSelection(0);
    }

    @OnClick(R.id.profileSignOutTextView)
    public void onSignOutTextViewPress() {
        firebaseAuth.signOut();
        Objects.requireNonNull(getActivity()).startActivity(new Intent(rootView.getContext(), SignInActivity.class));
        Objects.requireNonNull(getActivity()).finish();
    }

    @OnClick(R.id.profileDoneTextView)
    public void onDoneTextViewPress() {
        String name = profileNameEditText.getText().toString();
        String matric = profileMatricEditText.getText().toString();
        String phone = profilePhoneEditText.getText().toString();
        String school = profileSchoolSpinner.getSelectedItem().toString();
    }

    @Override
    public void onStart() {
        super.onStart();
        unbinder = ButterKnife.bind(ProfileFragment.this, rootView);
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
