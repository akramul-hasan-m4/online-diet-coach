package com.daffodil.online.dietcoach.adapter;


import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daffodil.online.dietcoach.R;
import com.daffodil.online.dietcoach.model.Conversation;
import com.daffodil.online.dietcoach.utils.Base4Utils;

import java.util.List;
import static com.daffodil.online.dietcoach.db.local.ShareStoreConstants.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class BotAdapter extends RecyclerView.Adapter<BotAdapter.ChatView> {

    private List<Conversation> messageList;
    private Activity activity;
    private ImageClickListener listener;

    public BotAdapter(List<Conversation> messageList, Activity activity, ImageClickListener listener) {
        this.messageList = messageList;
        this.activity = activity;
        this.listener = listener;
    }

    public interface ImageClickListener{
        void onImageClick(Bitmap image);
    }

    @NonNull
    @Override
    public ChatView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatView(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatView holder, int position) {
        Conversation msg = messageList.get(position);

        if(msg != null){
            if(msg.getUserType() != null && msg.getUserType().equalsIgnoreCase(USER)){
                holder.botLayout.setVisibility(View.GONE);
                holder.userLayout.setVisibility(View.VISIBLE);
                if(msg.getMessage() != null){
                    holder.userMessage.setVisibility(View.VISIBLE);
                    holder.userMessage.setText(msg.getMessage());
                }

                if(msg.getImageData() != null){
                    final Bitmap image = Base4Utils.decodeBase64(msg.getImageData(), activity);
                    holder.patientSendImage.setVisibility(View.VISIBLE);
                    holder.patientSendImage.setImageBitmap(image);

                    holder.patientSendImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onImageClick(image);
                        }
                    });
                }

            } else if(msg.getUserType() != null && msg.getUserType().equalsIgnoreCase(DOCTOR)){
                holder.botLayout.setVisibility(View.VISIBLE);
                holder.userLayout.setVisibility(View.GONE);
                holder.doctorMessage.setText(msg.getMessage());
            }


        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class ChatView extends RecyclerView.ViewHolder {

         LinearLayout botLayout;
         CircleImageView doctorProfileImage;
         ImageView doctorSendImage;
         TextView doctorMessage;
         LinearLayout userLayout;
         ImageView patientSendImage;
         TextView userMessage;
         CircleImageView userProfileImage;

        ChatView(@NonNull View view) {
            super(view);

            botLayout = view.findViewById(R.id.bot_layout);
            doctorProfileImage = view.findViewById(R.id.doctor_profile_image);
            doctorSendImage = view.findViewById(R.id.doctor_send_image);
            doctorMessage = view.findViewById(R.id.doctorMessage);
            userLayout = view.findViewById(R.id.user_layout);
            patientSendImage = view.findViewById(R.id.patient_send_image);
            userMessage = view.findViewById(R.id.user_message);
            userProfileImage = view.findViewById(R.id.user_profile_image);
        }
    }
}
