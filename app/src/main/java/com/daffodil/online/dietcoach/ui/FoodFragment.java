package com.daffodil.online.dietcoach.ui;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.daffodil.online.dietcoach.R;
import com.daffodil.online.dietcoach.adapter.FoodAdapter;
import com.daffodil.online.dietcoach.db.repository.FoodRepo;
import com.daffodil.online.dietcoach.model.Food;
import com.daffodil.online.dietcoach.utils.Base4Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoodFragment extends Fragment {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            WRITE_EXTERNAL_STORAGE, CAMERA, READ_EXTERNAL_STORAGE
    };
    private static final int IMAGE_RESULT = 200;

    private RecyclerView foodRecView;
    private ProgressBar foodProgressbar;
    private List<Food> foodList = new ArrayList<>();
    private FoodAdapter adapter;
    private ImageView foodImageInsert;
    private String msgImage;

    public FoodFragment() { /* Required empty public constructor*/}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food, container, false);

        setHasOptionsMenu(true);
        verifyStoragePermissions(getActivity());
        foodRecView = view.findViewById(R.id.food_rec_view);
        foodProgressbar = view.findViewById(R.id.food_progressbar);
        foodProgressbar.setVisibility(View.VISIBLE);
        new FoodRepo(getActivity()).getAllFoods(new FoodRepo.FoodStatus() {
            @Override
            public void foodIsLoaded(List<Food> foods, List<String> keys) {
                Log.d("mtest", "foodIsLoaded: " + foods.size());
                foodList = foods;
                adapter = new FoodAdapter(foodList, getActivity());
                RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
                foodRecView.setLayoutManager(manager);
                foodRecView.setHasFixedSize(true);
                foodRecView.setAdapter(adapter);
                foodProgressbar.setVisibility(View.GONE);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.add(0, 11, Menu.NONE, "Add New Item");
        MenuItem item = menu.findItem(11);
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == 11) {
            addFoodDialog();
        }
    /*    switch (id) {
            case R.id.action_settings:
                // do stuff, like showing settings fragment
                return true;
        }*/

        return super.onOptionsItemSelected(item); // important line
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_RESULT) {
            String filePath = getImageFromFilePath(data);
            if (filePath != null) {
                Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                msgImage = Base4Utils.getBase64String(selectedImage);

                foodImageInsert.setImageBitmap(selectedImage);
            }
        }
    }

    private static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;
        if (isCamera) return Objects.requireNonNull(getCaptureImageOutputUri()).getPath();
        else return getPathFromURI(data.getData());
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = Objects.requireNonNull(getActivity()).getContentResolver().query(contentUri, proj, null, null, null);
        int columnIndex = Objects.requireNonNull(cursor).getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = Objects.requireNonNull(getActivity()).getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    private Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = Objects.requireNonNull(getActivity()).getPackageManager();

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (Objects.requireNonNull(intent.getComponent()).getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        int size = allIntents.size();
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[size]));

        return chooserIntent;
    }


    private void addFood(Food food) {
        foodProgressbar.setVisibility(View.VISIBLE);
        new FoodRepo(getActivity()).addFood(food, new FoodRepo.FoodAddListener() {
            @Override
            public void saveFoodStatus(String message) {
                foodProgressbar.setVisibility(View.GONE);
                Toast.makeText(Objects.requireNonNull(getActivity()), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addFoodDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final ViewGroup root = null;
        final View dialogView = inflater.inflate(R.layout.add_food, root, false);
        dialogBuilder.setView(dialogView);
        final AlertDialog progressDialog = dialogBuilder.create();

        foodImageInsert = dialogView.findViewById(R.id.food_image_insert);
        final TextInputLayout foodNameLayout = dialogView.findViewById(R.id.foodName_layout);
        final TextInputEditText inFoodName = dialogView.findViewById(R.id.in_foodName);
        final Spinner dietType = dialogView.findViewById(R.id.spinner_diet_type);
        final Spinner qualifier = dialogView.findViewById(R.id.spinner_qualifier);
        Button btnAddFood = dialogView.findViewById(R.id.add_food);

        List<String> list = new ArrayList<>();
        list.add("Select Diet Type");
        list.add("Breakfast");
        list.add("Dinner");
        list.add("Supper");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dietType.setAdapter(dataAdapter);

        List<String> list2 = new ArrayList<>();
        list2.add("Select Diet Qualifier");
        list2.add("KG");
        list2.add("Pound");
        list2.add("Cups");
        list2.add("Glass");
        list2.add("Piece");
        list2.add("Gram");
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list2);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        qualifier.setAdapter(dataAdapter2);

        foodImageInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
            }
        });

        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foodName = inFoodName.getText() == null ? "" : inFoodName.getText().toString();
                String selectedDietType = dietType.getSelectedItemPosition() == 0 ? "" : (String)dietType.getSelectedItem();
                String selectedQualifier = qualifier.getSelectedItemPosition() == 0 ? "" : (String)qualifier.getSelectedItem();
                if(foodName.length() == 0 ){
                    foodNameLayout.setError("Enter Food Name");
                }else {
                    Food food = new Food();
                    food.setFoodName(foodName);
                    food.setFoodType(selectedDietType);
                    food.setQuantity("126 Cals");
                    food.setQuantityQualifier(selectedQualifier);
                    food.setFoodImage(msgImage);
                    addFood(food);
                    progressDialog.dismiss();
                }
            }
        });

        Objects.requireNonNull(progressDialog.getWindow()).getAttributes().windowAnimations = R.style.animation;
        progressDialog.show();
    }


}
