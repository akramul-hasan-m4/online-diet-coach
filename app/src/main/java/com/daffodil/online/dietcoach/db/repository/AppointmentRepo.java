package com.daffodil.online.dietcoach.db.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.daffodil.online.dietcoach.model.Appointment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;

public class AppointmentRepo {

    private DatabaseReference reference;
    private List<Appointment> appointments = new ArrayList<>();
    private Context context;

    public interface AppointmentAddListener{
        void saveAppointmentStatus(String message);
    }

    public interface AppointmentStatus{
        void appointmentIsLoaded(List<Appointment> appointments, List<String> keys);
    }

    public AppointmentRepo(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference("appointment");
        this.context = context;
    }

    public void addAppointment(Appointment appointment, final AppointmentAddListener listener){
        String key = reference.push().getKey();
        reference.child(Objects.requireNonNull(key)).setValue(appointment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("success", "successfully saved");
                        listener.saveAppointmentStatus("Registration successfully");
                    }
                });
    }

    public void getAllAppointment(final AppointmentStatus status){
        Log.d("mtest", "getAllAppointment: <<<<<<<<<<<<<<<==> " );
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointments.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    Appointment appointment = keyNode.getValue(Appointment.class);
                    appointments.add(appointment);
                }
                status.appointmentIsLoaded(appointments, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Call cancel", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
