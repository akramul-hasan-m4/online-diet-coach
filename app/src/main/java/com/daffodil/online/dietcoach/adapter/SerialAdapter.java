package com.daffodil.online.dietcoach.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daffodil.online.dietcoach.R;
import com.daffodil.online.dietcoach.model.Appointment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SerialAdapter extends RecyclerView.Adapter<SerialAdapter.SerialView> {

    private List<Appointment> appointments;


    public SerialAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public SerialView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.serial_item, parent, false);
        return new SerialView(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SerialView holder, int position) {
        Appointment appointment = appointments.get(position);

        if (appointment != null) {
            String patientName = "<b>Patient Name :: </b>" + appointment.getPatientName();
            String patientPhone = "<b>Patient Phone :: </b>" + appointment.getPatientMobile();
            String drName = "<b>Dietitian Name :: </b>" + appointment.getDoctorName();
            String serialDate = "<b>Serial Date :: </b>" + appointment.getAppointmentDate();
            String serial = "<b>Serial No :: </b>" + appointment.getSerialNumber();

            holder.patientName.setText(Html.fromHtml(patientName));
            holder.patientMobileNo.setText(Html.fromHtml(patientPhone));
            holder.drName.setText(Html.fromHtml(drName));
            holder.serialDate.setText(Html.fromHtml(serialDate));
            holder.serialNo.setText(Html.fromHtml(serial));
        }
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }



    class SerialView extends RecyclerView.ViewHolder {
        TextView patientName;
        TextView patientMobileNo;
        TextView drName;
        TextView serialDate;
        TextView serialNo;

        SerialView(@NonNull View itemView) {
            super(itemView);

            patientName = itemView.findViewById(R.id.patient_name);
            patientMobileNo = itemView.findViewById(R.id.patient_mobile_no);
            drName = itemView.findViewById(R.id.dr_name);
            serialDate = itemView.findViewById(R.id.serial_date);
            serialNo = itemView.findViewById(R.id.serial_no);
        }
    }
}
