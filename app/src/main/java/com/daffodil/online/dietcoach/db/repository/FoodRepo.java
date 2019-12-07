package com.daffodil.online.dietcoach.db.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.daffodil.online.dietcoach.model.Food;
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

public class FoodRepo {
    private DatabaseReference reference;
    private List<Food> foodList = new ArrayList<>();
    private Context context;

    public FoodRepo(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference("foods");
        this.context = context;
    }

    public interface FoodStatus{
        void foodIsLoaded(List<Food> foods, List<String> keys);
    }

    public interface FoodAddListener{
        void saveFoodStatus(String message);
    }

    public void getAllFoods(final FoodStatus status){
        Log.d("mtest", "getAllFoods: <<<<<<<<<<<<<<<==> " );
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foodList.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    Food food = keyNode.getValue(Food.class);
                    foodList.add(food);
                    Log.d("mtest", "foodIsLoaded: " + food.getFoodName());
                }
                status.foodIsLoaded(foodList, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Call cancel", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addFood(Food food, final FoodAddListener addListener){
        String key = reference.push().getKey();
        reference.child(Objects.requireNonNull(key)).setValue(food)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("success", "successfully saved");
                        addListener.saveFoodStatus("Food Add successfully");
                    }
                });
    }

}
