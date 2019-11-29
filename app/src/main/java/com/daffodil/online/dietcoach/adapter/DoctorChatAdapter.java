package com.daffodil.online.dietcoach.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.daffodil.online.dietcoach.R;
import com.daffodil.online.dietcoach.model.Users;
import com.daffodil.online.dietcoach.utils.Base4Utils;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorChatAdapter extends RecyclerView.Adapter<DoctorChatAdapter.DoctorChatViewHolder> {

    private List<Users> doctorList;
    private ChatListener listener;
    private Context context;

    public DoctorChatAdapter(List<Users> doctorList, ChatListener listener, Context context) {
        this.doctorList = doctorList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public DoctorChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_chat_item, parent, false);
        return new DoctorChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorChatViewHolder holder, int position) {
        final Users doctor = doctorList.get(position);
        if (doctor != null) {
            if (doctor.getProfileImage() != null) {
                Bitmap image = Base4Utils.decodeBase64(doctor.getProfileImage(), Objects.requireNonNull(context));
                holder.doctorProfileImage.setImageBitmap(image);
            }

            String drName = doctor.getFirstName() + " " + doctor.getLastName();
            holder.doctorName.setText(drName);

            holder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onChatClick(doctor);
                }
            });

            holder.appointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onAppointmentClick(doctor);
                }
            });

            holder.info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onInfoClick(doctor);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public interface ChatListener {
        void onChatClick(Users doctor);
        void onAppointmentClick(Users doctor);
        void onInfoClick(Users doctor);
    }

    class DoctorChatViewHolder extends RecyclerView.ViewHolder {
         CircleImageView doctorProfileImage;
         TextView doctorName;
         TextView drDepartment;
         TextView drEducation;
         ImageButton info;
         ImageButton appointment;
         ImageButton chat;
         DoctorChatViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorProfileImage = itemView.findViewById(R.id.doctor_profile_image);
            doctorName = itemView.findViewById(R.id.doctor_name);
            drDepartment = itemView.findViewById(R.id.dr_department);
            drEducation = itemView.findViewById(R.id.dr_education);
            info = itemView.findViewById(R.id.doctor_info);
            appointment = itemView.findViewById(R.id.doctor_appointment);
            chat = itemView.findViewById(R.id.doctor_chat);
         }
    }
}
