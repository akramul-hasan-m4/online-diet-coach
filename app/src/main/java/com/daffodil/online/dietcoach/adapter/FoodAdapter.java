package com.daffodil.online.dietcoach.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daffodil.online.dietcoach.R;
import com.daffodil.online.dietcoach.model.Food;
import com.daffodil.online.dietcoach.utils.Base4Utils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodView> {

    private List<Food> foodList;
    private Activity activity;

    public FoodAdapter(List<Food> foodList, Activity activity) {
        this.foodList = foodList;
        this.activity =activity;
    }

    @NonNull
    @Override
    public FoodView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        return new FoodView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodView holder, int position) {
        Food food = foodList.get(position);
        if (food != null) {
            if(food.getFoodImage() != null){
                final Bitmap image = Base4Utils.decodeBase64(food.getFoodImage(), activity);
                holder.foodImage.setImageBitmap(image);
            }
            holder.foodName.setText(food.getFoodName());
            String foodQty = "<font color='#2ECC71'>" + food.getQuantity() + "</font>" + "/" + food.getQuantityQualifier();
            holder.foodQuantity.setText(Html.fromHtml(foodQty));
        }
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    class FoodView extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView foodName;
        TextView foodQuantity;

        FoodView(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.food_image);
            foodName = itemView.findViewById(R.id.food_name);
            foodQuantity = itemView.findViewById(R.id.food_quantity);
        }
    }
}
