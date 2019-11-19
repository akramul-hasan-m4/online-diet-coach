package com.daffodil.online.dietcoach.ui;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daffodil.online.dietcoach.activity.MainActivity;
import com.daffodil.online.dietcoach.R;

import java.util.Objects;


public class UserProfile extends Fragment {

    public UserProfile() { /* Required empty public constructor*/}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("User Profile");
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

}
