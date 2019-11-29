package com.daffodil.online.dietcoach.db.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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

import androidx.annotation.NonNull;

public class UserRepository {

    private DatabaseReference reference;
    private List<Users> userList = new ArrayList<>();
    private Context context;

    public interface UserStatus{
        void userIsLoaded(List<Users> users, List<String> keys);
    }

    public interface UserAddListener{
        void saveUserStatus(String message);
    }

    public UserRepository(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");
        this.context = context;
    }

    public void getAllUsers(final UserStatus status){
        Log.d("mtest", "getAllUsers: <<<<<<<<<<<<<<<==> " );
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    Users users = keyNode.getValue(Users.class);
                    userList.add(users);
                }
                status.userIsLoaded(userList, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Call cancel", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void findUser(Users users, final UserStatus status){
        Log.d("mtest", "onDataChange: <<<<<<<<<<<<<<<==> " );
        reference.orderByChild("phone").startAt(users.getPhone()).endAt(users.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    Users users = keyNode.getValue(Users.class);
                    Log.d("mtest", "onDataChange: ==> " +users);
                    userList.add(users);
                }
                status.userIsLoaded(userList, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Call cancel", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addUser(Users users, final UserAddListener addListener){
        String key = reference.push().getKey();
        reference.child(Objects.requireNonNull(key)).setValue(users)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("success", "successfully saved");
                       addListener.saveUserStatus("Registration successfully");
                    }
                });
    }

    public void deleteUser(String key){
        try {
            reference.child(key).setValue(null)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "successfully Deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception e){
            Log.d("DeleteException", e.getMessage());
        }

    }

    /*
    *
    *        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference();
        reference.child("specimens")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                        for (DataSnapshot child: children)
                        {
                            SpecimenDTO specimenDTO = child.getValue(SpecimenDTO.class);
                            Toast.makeText(GPSAPlant.this, "Data: " + specimenDTO.toString(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    *
    *
    *
    * */
    /*
    https://firebase.google.com/docs/reference/js/firebase.database.Query#equalTo
    * public void addBook(Book book){
       String key = reference.push().getKey();
       reference.child(key).setValue(book)
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       Log.d("success", "successfully saved");
                   }
               });
    }

    public void updateBook(String key, Book book){
        reference.child(key).setValue(book)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("success", "successfully update");
                        Toast.makeText(context, "successfully update", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void deleteBook(String key){
        try {
            reference.child(key).setValue(null)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "successfully Deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception e){
            Log.d("DeleteException", e.getMessage());
        }

    }
    * */
}
