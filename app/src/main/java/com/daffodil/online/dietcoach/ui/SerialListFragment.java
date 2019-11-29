package com.daffodil.online.dietcoach.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.daffodil.online.dietcoach.R;
import com.daffodil.online.dietcoach.activity.MainActivity;
import com.daffodil.online.dietcoach.adapter.DoctorChatAdapter;
import com.daffodil.online.dietcoach.adapter.SerialAdapter;
import com.daffodil.online.dietcoach.db.repository.AppointmentRepo;
import com.daffodil.online.dietcoach.model.Appointment;
import com.daffodil.online.dietcoach.model.Users;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SerialListFragment extends Fragment {

    private RecyclerView serialList;
    private ProgressBar serialProgressbar;
    private SerialAdapter adapter;
    private List<Appointment> appointmentList = new ArrayList<>();

    public SerialListFragment() { /* Required empty public constructor*/}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Appointment List");
        View view = inflater.inflate(R.layout.fragment_serial_list, container, false);
        serialList = view.findViewById(R.id.serial_list);
        serialProgressbar = view.findViewById(R.id.serial_progressbar);
        serialProgressbar.setVisibility(View.VISIBLE);

        new AppointmentRepo(Objects.requireNonNull(getActivity())).getAllAppointment(new AppointmentRepo.AppointmentStatus() {
            @Override
            public void appointmentIsLoaded(List<Appointment> appointments, List<String> keys) {
                appointmentList.clear();
                appointmentList.addAll(appointments);
                serialProgressbar.setVisibility(View.GONE);
                adapter = new SerialAdapter(appointmentList);
                RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
                serialList.setLayoutManager(manager);
                serialList.setHasFixedSize(true);
                serialList.setAdapter(adapter);
            }
        });
        return view;
    }


}
