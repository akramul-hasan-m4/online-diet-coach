package com.daffodil.online.dietcoach.ui;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daffodil.online.dietcoach.R;
import com.daffodil.online.dietcoach.activity.MainActivity;
import com.daffodil.online.dietcoach.db.local.SharedPreferencesConfig;
import com.daffodil.online.dietcoach.model.Users;
import com.daffodil.online.dietcoach.utils.Base4Utils;
import com.google.gson.Gson;

import java.util.Objects;

import androidx.fragment.app.Fragment;

import static com.daffodil.online.dietcoach.db.local.ShareStoreConstants.CURRENT_USER;


public class UserProfile extends Fragment {

    private ImageView profileImage;
    private TextView userName;
    private TextView tvAddress;
    private TextView tvMobileNo;
    private TextView tvEmail;
    private TextView tvBloodGroup;
    private TextView tvAge;
    private TextView tvGender;
    private TextView tvUserType;

    public UserProfile() { /* Required empty public constructor*/}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("User Profile");
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        initView(view);
        setUserData();
        return view;
    }

    private void initView(View view) {
        profileImage = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.user_name);
        tvAddress = view.findViewById(R.id.tv_address);
        tvMobileNo = view.findViewById(R.id.tv_mobile_no);
        tvEmail = view.findViewById(R.id.tv_email);
        tvBloodGroup = view.findViewById(R.id.tv_blood_group);
        tvAge = view.findViewById(R.id.tv_age);
        tvGender = view.findViewById(R.id.tv_gender);
        tvUserType = view.findViewById(R.id.tv_userType);
    }

    private void setUserData() {
        Gson gson = new Gson();
        String user = SharedPreferencesConfig.getStringData(Objects.requireNonNull(getContext()), CURRENT_USER);
        Users userObj = gson.fromJson(user, Users.class);

        if (userObj != null) {
            if (userObj.getProfileImage() != null) {
                Bitmap image = Base4Utils.decodeBase64(userObj.getProfileImage(), Objects.requireNonNull(getActivity()));
                profileImage.setImageBitmap(image);
            }

            String userNameText = userObj.getFirstName() + " " + userObj.getLastName();
            String addressText = "Address :: " + userObj.getAddress();
            String phoneText = "Phone No :: " + userObj.getPhone();
            String emailText = "Email :: " + userObj.getEmail();
            String bloodText = "Blood Group :: " + userObj.getBloodGroup();
            String ageText = "Age :: " + userObj.getAge();
            String genderText = "Gender :: " + userObj.getGender();
            String userTypeText = "User Type :: " + userObj.getUserType();
            this.userName.setText(userNameText);
            tvAddress.setText(addressText);
            tvMobileNo.setText(phoneText);
            tvEmail.setText(emailText);
            tvBloodGroup.setText(bloodText);
            tvAge.setText(ageText);
            tvGender.setText(genderText);
            tvUserType.setText(userTypeText);

        }
    }
}
