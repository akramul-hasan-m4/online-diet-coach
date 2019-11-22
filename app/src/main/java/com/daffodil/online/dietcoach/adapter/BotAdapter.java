package com.daffodil.online.dietcoach.adapter;


import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daffodil.online.dietcoach.R;
import com.daffodil.online.dietcoach.model.Conversation;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BotAdapter extends RecyclerView.Adapter<BotAdapter.ChatView>{

    private List<Conversation> messageList;
    private Activity activity;

    public BotAdapter(List<Conversation> messageList, Activity activity) {
        this.messageList = messageList;
        this.activity = activity;
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
       /* if(msg.getBotMessage() != null){
            holder.botLayout.setVisibility(View.VISIBLE);
            holder.userLayout.setVisibility(View.GONE);
            holder.botMessage.setText(msg.getBotMessage());
        }
        if(msg.getUserMessage() != null){
            holder.botLayout.setVisibility(View.GONE);
            holder.userLayout.setVisibility(View.VISIBLE);
            holder.userMessage.setText(msg.getUserMessage());
        }*/
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class ChatView extends RecyclerView.ViewHolder{

        TextView botMessage;
        TextView userMessage;
        LinearLayout botLayout;
        LinearLayout userLayout;

        ChatView(@NonNull View itemView) {
            super(itemView);

            botMessage = itemView.findViewById(R.id.botMessage);
            userMessage = itemView.findViewById(R.id.user_message);
            botLayout = itemView.findViewById(R.id.bot_layout);
            userLayout = itemView.findViewById(R.id.user_layout);
        }
    }
}
