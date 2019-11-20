package com.daffodil.online.dietcoach.db.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.daffodil.online.dietcoach.model.Conversation;
import com.daffodil.online.dietcoach.model.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConversationRepo {

    private DatabaseReference reference;
    private List<Conversation> conversationList = new ArrayList<>();
    private Context context;

    public ConversationRepo(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference("conversation");
        this.context = context;
    }

    public interface MsgAddListener{
        void saveMsgStatus(String message);
    }

    public interface ConversationStatus{
        void userIsLoaded(List<Conversation> conversations, List<String> keys);
    }

    public void addConversation(Conversation conversation, final MsgAddListener addListener){
        String key = reference.push().getKey();
        reference.child(Objects.requireNonNull(key)).setValue(conversation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("success", "successfully saved");
                        addListener.saveMsgStatus("conversation save successfully");
                    }
                });
    }

    public void findConversation(Conversation conversation, final ConversationStatus status){
        Log.d("mtest", "onDataChange: <<<<<<<<<<<<<<<==> " );
        reference.orderByChild("email").startAt("akramul@gmail.com").endAt("akramul@gmail.com").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    Conversation con = keyNode.getValue(Conversation.class);
                    Log.d("mtest", "onDataChange: ==> " +con);
                    conversationList.add(con);
                }
                status.userIsLoaded(conversationList, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Call cancel", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
