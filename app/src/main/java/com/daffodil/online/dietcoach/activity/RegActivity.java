package com.daffodil.online.dietcoach.activity;

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
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daffodil.online.dietcoach.R;
import com.daffodil.online.dietcoach.db.repository.UserRepository;
import com.daffodil.online.dietcoach.model.Users;
import com.daffodil.online.dietcoach.utils.Base4Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class RegActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            WRITE_EXTERNAL_STORAGE, CAMERA, READ_EXTERNAL_STORAGE
    };

    private static final int IMAGE_RESULT = 200;
    private CircleImageView imageView;
    private TextInputLayout firstNameLayout;
    private TextInputEditText firstName;
    private TextInputLayout lastNameLayout;
    private TextInputEditText lastName;
    private TextInputLayout userNameLayout;
    private TextInputEditText userName;
    private TextInputLayout phoneLayout;
    private TextInputEditText phone;
    private TextInputLayout emailLayout;
    private TextInputEditText email;
    private TextInputLayout ageLayout;
    private TextInputEditText age;
    private TextInputLayout bloodLayout;
    private TextInputEditText bloodGroup;
    private TextInputLayout addressLayout;
    private TextInputEditText address;
    private TextInputLayout passwordLayout;
    private TextInputEditText password;
    private TextInputLayout conPasswordLayout;
    private TextInputEditText conPassword;
    private Button btnLogin;
    private ProgressBar regProgress;

    private Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        initView();

        verifyStoragePermissions(this);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindUser();
            }
        });
    }

    private void bindUser() {
        Users users = new Users();
        if(selectedImage != null){
            String imageData = Base4Utils.getBase64String(selectedImage);
            users.setProfileImage(imageData);
        }
        String firstNameText = firstName.getText() == null? "" : firstName.getText().toString();
        String lastNameText = lastName.getText() == null? "" : lastName.getText().toString();
        String phoneText = phone.getText() == null? "" : phone.getText().toString();
        String userNameText = userName.getText() == null? "" : userName.getText().toString();
        String emailText = email.getText() == null? "" : email.getText().toString();
        String ageText = age.getText() == null? "" : age.getText().toString();
        String bloodGroupText = bloodGroup.getText() == null? "" : bloodGroup.getText().toString();
        String addressText = address.getText() == null? "" : address.getText().toString();
        String passwordText = password.getText() == null? "" : password.getText().toString();
        String conPasswordText = conPassword.getText() == null? "" : conPassword.getText().toString();

        users.setFirstName(firstNameText);
        users.setLastName(lastNameText);
        users.setPhone(phoneText);
        users.setUserName(userNameText);
        users.setEmail(emailText);
        users.setAge(ageText);
        users.setBloodGroup(bloodGroupText);
        users.setAddress(addressText);
        users.setPassword(passwordText);

        new UserRepository(this).addUser(users, new UserRepository.UserAddListener() {
            @Override
            public void saveUserStatus(String message) {
                Toast.makeText(RegActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

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

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_RESULT) {
            String filePath = getImageFromFilePath(data);
            if (filePath != null) {
                selectedImage = BitmapFactory.decodeFile(filePath);
                imageView.setImageBitmap(selectedImage);
            }
        }
    }

    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;
        if (isCamera) return Objects.requireNonNull(getCaptureImageOutputUri()).getPath();
        else return getPathFromURI(data.getData());
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int columnIndex = Objects.requireNonNull(cursor).getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    private static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void initView() {
        imageView = findViewById(R.id.profile_image);
        firstNameLayout = findViewById(R.id.firstName_layout);
        firstName = findViewById(R.id.in_firstName);
        lastNameLayout = findViewById(R.id.last_name_layout);
        lastName = findViewById(R.id.last_name);
        userNameLayout = findViewById(R.id.user_name_layout);
        userName = findViewById(R.id.user_name);
        phoneLayout = findViewById(R.id.phone_layout);
        phone = findViewById(R.id.phone);
        emailLayout = findViewById(R.id.email_layout);
        email = findViewById(R.id.email);
        ageLayout = findViewById(R.id.age_layout);
        age = findViewById(R.id.age);
        bloodLayout = findViewById(R.id.blood_layout);
        bloodGroup = findViewById(R.id.blood_group);
        addressLayout = findViewById(R.id.address_layout);
        address = findViewById(R.id.address);
        passwordLayout = findViewById(R.id.password_layout);
        password = findViewById(R.id.password);
        conPasswordLayout = findViewById(R.id.confirm_pass_layout);
        conPassword = findViewById(R.id.confirm_pass);
        btnLogin = findViewById(R.id.btn_login);
        regProgress = findViewById(R.id.reg_progress);
    }
}
