package com.daffodil.online.dietcoach.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daffodil.online.dietcoach.activity.MainActivity;
import com.daffodil.online.dietcoach.R;

import java.util.Objects;


public class DoctorProfile extends Fragment {

    public DoctorProfile() { /*Required empty public constructor*/}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Dietitian Profile");
        return inflater.inflate(R.layout.fragment_doctor_profile, container, false);
    }



}
