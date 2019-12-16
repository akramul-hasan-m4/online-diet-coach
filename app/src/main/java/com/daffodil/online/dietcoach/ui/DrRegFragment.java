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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.daffodil.online.dietcoach.R;
import com.daffodil.online.dietcoach.activity.RegActivity;
import com.daffodil.online.dietcoach.db.repository.UserRepository;
import com.daffodil.online.dietcoach.model.Users;
import com.daffodil.online.dietcoach.utils.Base4Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.daffodil.online.dietcoach.db.local.ShareStoreConstants.DOCTOR;
import static com.daffodil.online.dietcoach.db.local.ShareStoreConstants.USER;

/**
 * A simple {@link Fragment} subclass.
 */
public class DrRegFragment extends Fragment {

    private CircleImageView profileImage;
    private TextInputLayout firstNameLayout;
    private TextInputEditText inFirstName;
    private TextInputLayout lastNameLayout;
    private TextInputEditText lastName;
    private TextInputLayout passwordLayout;
    private TextInputEditText password;
    private TextInputLayout confirmPassLayout;
    private TextInputEditText confirmPass;
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
    private RadioGroup gender;
    private TextInputEditText eduFirst;
    private TextInputEditText eduSecond;
    private TextInputEditText edu3rd;
    private TextInputEditText edu4th;
    private TextInputEditText pro1stExp;
    private TextInputEditText pro2ndExp;
    private TextInputEditText pro3rdExp;
    private FloatingActionButton btnReg;
    private ProgressBar regProgress;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            WRITE_EXTERNAL_STORAGE, CAMERA, READ_EXTERNAL_STORAGE
    };
    private static final int IMAGE_RESULT = 200;

    private Bitmap selectedImage;
    private String genderText;

    private static final String TAG = "DrRegFragment";

    public DrRegFragment() { /* Required empty public constructor*/}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dr_reg, container, false);
        initView(view);

        verifyStoragePermissions(getActivity());

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindUser();
            }
        });

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedGender = group.findViewById(checkedId);
                if(checkedGender.isChecked()){
                    genderText = checkedGender.getText().toString();
                }
            }
        });

        return view;
    }

    private void bindUser() {

        String firstNameText = inFirstName.getText() == null? "" : inFirstName.getText().toString();
        String lastNameText = lastName.getText() == null? "" : lastName.getText().toString();
        String phoneText = phone.getText() == null? "" : phone.getText().toString();
        String emailText = email.getText() == null? "" : email.getText().toString().trim();
        String ageText = age.getText() == null? "" : age.getText().toString();
        String bloodGroupText = bloodGroup.getText() == null? "" : bloodGroup.getText().toString();
        String addressText = address.getText() == null? "" : address.getText().toString();
        String passwordText = password.getText() == null? "" : password.getText().toString().trim();
        String conPasswordText = confirmPass.getText() == null? "" : confirmPass.getText().toString();

        String eduFirstText = eduFirst.getText() == null? "" : eduFirst.getText().toString();
        String eduSecondText = eduSecond.getText() == null? "" : eduSecond.getText().toString();
        String edu3rdText = edu3rd.getText() == null? "" : edu3rd.getText().toString();
        String edu4thText = edu4th.getText() == null? "" : edu4th.getText().toString();
        String pro1stExpText = pro1stExp.getText() == null? "" : pro1stExp.getText().toString();
        String pro2ndExpText = pro2ndExp.getText() == null? "" : pro2ndExp.getText().toString();
        String pro3rdExpText = pro3rdExp.getText() == null? "" : pro3rdExp.getText().toString();

        if(phoneText.length() == 0){
            phoneLayout.setError("Please add phone number");
            phone.requestFocus();
        }else if(passwordText.length() == 0){
            passwordLayout.setError("Please enter your password");
            password.requestFocus();
        }else if(!passwordText.equalsIgnoreCase(conPasswordText)){
            confirmPassLayout.setError("Password doesn't match");
            confirmPass.requestFocus();
        }else {
            regProgress.setVisibility(View.VISIBLE);
            Users users = new Users();
            if(selectedImage != null){
                String imageData = Base4Utils.getBase64String(selectedImage);
                users.setProfileImage(imageData);
            }

            users.setFirstName(firstNameText);
            users.setLastName(lastNameText);
            users.setPhone(phoneText);
            users.setEmail(emailText);
            users.setAge(ageText);
            users.setBloodGroup(bloodGroupText);
            users.setAddress(addressText);
            users.setPassword(passwordText);
            users.setGender(genderText);
            users.setUserType(DOCTOR);
            users.setFirstEdu(eduFirstText);
            users.setSecondEdu(eduSecondText);
            users.setThirdEcu(edu3rdText);
            users.setFourthEdu(edu4thText);
            users.setFirstExp(pro1stExpText);
            users.setSecondExp(pro2ndExpText);
            users.setThirdExp(pro3rdExpText);

            new UserRepository(getActivity()).addUser(users, new UserRepository.UserAddListener() {
                @Override
                public void saveUserStatus(String message) {
                    regProgress.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    clear();
                }
            });
        }
    }

    private void clear(){
        try {
            Objects.requireNonNull(inFirstName.getText()).clear();
            Objects.requireNonNull(lastName.getText()).clear();
            Objects.requireNonNull(phone.getText()).clear();
            Objects.requireNonNull(email.getText()).clear();
            Objects.requireNonNull(age.getText()).clear();
            Objects.requireNonNull(bloodGroup.getText()).clear();
            Objects.requireNonNull(address.getText()).clear();
            Objects.requireNonNull(password.getText()).clear();
            Objects.requireNonNull(confirmPass.getText()).clear();

            Objects.requireNonNull(eduFirst.getText()).clear();
            Objects.requireNonNull(eduSecond.getText()).clear();
            Objects.requireNonNull(edu3rd.getText()).clear();
            Objects.requireNonNull(edu4th.getText()).clear();
            Objects.requireNonNull(pro1stExp.getText()).clear();
            Objects.requireNonNull(pro2ndExp.getText()).clear();
            Objects.requireNonNull(pro3rdExp.getText()).clear();

            profileImage.setImageDrawable(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.ic_person_black_24dp));
        }catch (Exception e){
            Log.d(TAG, "clear: " + e.getMessage());
        }
    }

    private static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
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

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = Objects.requireNonNull(getActivity()).getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_RESULT) {
            String filePath = getImageFromFilePath(data);
            if (filePath != null) {
                selectedImage = BitmapFactory.decodeFile(filePath);
                profileImage.setImageBitmap(selectedImage);
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
        Cursor cursor = Objects.requireNonNull(getActivity()).getContentResolver().query(contentUri, proj, null, null, null);
        int columnIndex = Objects.requireNonNull(cursor).getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    private void initView(View view) {
        profileImage = view.findViewById(R.id.profile_image);
        firstNameLayout = view.findViewById(R.id.firstName_layout);
        inFirstName = view.findViewById(R.id.in_firstName);
        lastNameLayout = view.findViewById(R.id.last_name_layout);
        lastName = view.findViewById(R.id.last_name);
        passwordLayout = view.findViewById(R.id.password_layout);
        password = view.findViewById(R.id.password);
        confirmPassLayout = view.findViewById(R.id.confirm_pass_layout);
        confirmPass = view.findViewById(R.id.confirm_pass);
        phoneLayout = view.findViewById(R.id.phone_layout);
        phone = view.findViewById(R.id.phone);
        emailLayout = view.findViewById(R.id.email_layout);
        email = view.findViewById(R.id.email);
        ageLayout = view.findViewById(R.id.age_layout);
        age = view.findViewById(R.id.age);
        bloodLayout = view.findViewById(R.id.blood_layout);
        bloodGroup = view.findViewById(R.id.blood_group);
        addressLayout = view.findViewById(R.id.address_layout);
        address = view.findViewById(R.id.address);
        gender = view.findViewById(R.id.gender);
        eduFirst = view.findViewById(R.id.edu_first);
        eduSecond = view.findViewById(R.id.edu_second);
        edu3rd = view.findViewById(R.id.edu_3rd);
        edu4th = view.findViewById(R.id.edu_4th);
        pro1stExp = view.findViewById(R.id.pro_1st_exp);
        pro2ndExp = view.findViewById(R.id.pro_2nd_exp);
        pro3rdExp = view.findViewById(R.id.pro_3rd_exp);
        btnReg = view.findViewById(R.id.btn_reg);
        regProgress = view.findViewById(R.id.reg_progress);
    }
}
