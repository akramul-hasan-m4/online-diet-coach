package com.daffodil.online.dietcoach.ui;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daffodil.online.dietcoach.R;
import com.daffodil.online.dietcoach.adapter.DoctorChatAdapter;
import com.daffodil.online.dietcoach.db.local.SharedPreferencesConfig;
import com.daffodil.online.dietcoach.db.repository.AppointmentRepo;
import com.daffodil.online.dietcoach.db.repository.ConversationRepo;
import com.daffodil.online.dietcoach.db.repository.UserRepository;
import com.daffodil.online.dietcoach.model.Appointment;
import com.daffodil.online.dietcoach.model.Conversation;
import com.daffodil.online.dietcoach.model.Users;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.daffodil.online.dietcoach.db.local.ShareStoreConstants.CURRENT_USER;
import static com.daffodil.online.dietcoach.db.local.ShareStoreConstants.DOCTOR_CHAT;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatRoomFragment extends Fragment implements DoctorChatAdapter.ChatListener {

    private RecyclerView doctorChatList;
    private ProgressBar doctorChatProgressbar;
    private List<Users> usersList = new ArrayList<>();
    private DoctorChatAdapter adapter;


    public ChatRoomFragment() { /* Required empty public constructor*/}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_room, container, false);

        doctorChatList = view.findViewById(R.id.doctor_chat_list);
        doctorChatProgressbar = view.findViewById(R.id.doctor_chat_progressbar);
        doctorChatProgressbar.setVisibility(View.VISIBLE);

        new UserRepository(Objects.requireNonNull(getActivity())).getAllUsers(new UserRepository.UserStatus() {
            @Override
            public void userIsLoaded(List<Users> users, List<String> keys) {
                usersList = users;
                doctorChatProgressbar.setVisibility(View.GONE);
                adapter = new DoctorChatAdapter(usersList, ChatRoomFragment.this, Objects.requireNonNull(getActivity()));
                RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
                doctorChatList.setLayoutManager(manager);
                doctorChatList.setHasFixedSize(true);
                doctorChatList.setAdapter(adapter);
            }
        });

        return view;
    }

    @Override
    public void onChatClick(Users doctor) {
        SharedPreferencesConfig.saveStringData(Objects.requireNonNull(Objects.requireNonNull(getActivity())), DOCTOR_CHAT, doctor.getPhone());
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new ChatFragment()).addToBackStack(null).commit();
    }

    @Override
    public void onAppointmentClick(Users doctor) {
        doctorAppointment(doctor);
    }

    @Override
    public void onInfoClick(Users doctor) {
        showDoctorInfo(doctor);
    }

    private void showDoctorInfo(Users doctor) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final ViewGroup root = null;
        final View dialogView = inflater.inflate(R.layout.fragment_doctor_profile, root, false);
        dialogBuilder.setView(dialogView);
        AlertDialog progressDialog = dialogBuilder.create();
        // int width = ViewGroup.LayoutParams.MATCH_PARENT;
        //https://github.com/miskoajkula/Firebase-RealtimeDatabase-App-Example
        // int height = ViewGroup.LayoutParams.MATCH_PARENT;
        // progressDialog.getWindow().setLayout(width, height);
         Objects.requireNonNull(progressDialog.getWindow()).getAttributes().windowAnimations = R.style.animation;
        progressDialog.show();
    }


    private void doctorAppointment(Users doctor) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final ViewGroup root = null;
        final View dialogView = inflater.inflate(R.layout.appointment_dialog, root, false);
        dialogBuilder.setView(dialogView);
        final AlertDialog progressDialog = dialogBuilder.create();

        ImageView close = dialogView.findViewById(R.id.close);
        TextView doctorName = dialogView.findViewById(R.id.doctor_name);
        final TextView appointmentDate = dialogView.findViewById(R.id.appointment_date);
        ImageButton okAppointment = dialogView.findViewById(R.id.ok_appointment);
        final ProgressBar appointmentProgress = dialogView.findViewById(R.id.appoint_progress);

        String doctorFirstName = doctor == null || doctor.getFirstName() == null ? "" : doctor.getFirstName();
        String doctorLastName = doctor == null || doctor.getLastName() == null ? "" : doctor.getLastName();
        final String doctorFullName = String.format("%s %s", doctorFirstName, doctorLastName);
        doctorName.setText(doctorFullName);

        appointmentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(appointmentDate);
            }
        });

        okAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = appointmentDate.getText() == null ? "" : appointmentDate.getText().toString();

                if(TextUtils.isEmpty(date) || date.equalsIgnoreCase("Select Your Appointment Date")){
                    appointmentDate.setError("Please Select Date");
                }else {
                    appointmentProgress.setVisibility(View.VISIBLE);
                    Gson gson = new Gson();
                    String user = SharedPreferencesConfig.getStringData(Objects.requireNonNull(getContext()), CURRENT_USER);
                    Users userObj = gson.fromJson(user, Users.class);
                    String userNameText = userObj.getFirstName() + " " + userObj.getLastName();

                    Appointment appointment = new Appointment();
                    appointment.setAppointmentDate(date);
                    appointment.setDoctorName(doctorFullName);
                    appointment.setExpired(1);
                    appointment.setPatientName(userNameText);
                    appointment.setSerialNumber(10);
                    appointment.setPatientMobile(userObj.getPhone());

                    new AppointmentRepo(Objects.requireNonNull(getActivity())).addAppointment(appointment, new AppointmentRepo.AppointmentAddListener() {
                        @Override
                        public void saveAppointmentStatus(String message) {
                            appointmentProgress.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.dismiss();
            }
        });
        Objects.requireNonNull(progressDialog.getWindow()).getAttributes().windowAnimations = R.style.animation;
        progressDialog.show();
    }

    private void datePicker(final TextView dateFiled) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String dayConverted = "" + dayOfMonth;
                        if (dayOfMonth < 10) {
                            dayConverted = "0" + dayConverted;
                        }

                        int monthInt = (monthOfYear + 1);
                        String monthConverted = "" + monthInt;
                        if (monthInt < 10) {
                            monthConverted = "0" + monthConverted;
                        }

                        String date = String.format(Locale.US, "%d-%s-%s", year, monthConverted, dayConverted);
                        dateFiled.setText(date);
                        dateFiled.setError(null);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }


}
