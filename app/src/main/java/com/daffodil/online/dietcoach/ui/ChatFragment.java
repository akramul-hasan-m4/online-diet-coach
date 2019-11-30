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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.daffodil.online.dietcoach.R;
import com.daffodil.online.dietcoach.activity.MainActivity;
import com.daffodil.online.dietcoach.adapter.BotAdapter;
import com.daffodil.online.dietcoach.db.local.SharedPreferencesConfig;
import com.daffodil.online.dietcoach.db.repository.ConversationRepo;
import com.daffodil.online.dietcoach.model.Conversation;
import com.daffodil.online.dietcoach.model.Users;
import com.daffodil.online.dietcoach.utils.Base4Utils;
import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.daffodil.online.dietcoach.db.local.ShareStoreConstants.CURRENT_USER;
import static com.daffodil.online.dietcoach.db.local.ShareStoreConstants.DOCTOR;
import static com.daffodil.online.dietcoach.db.local.ShareStoreConstants.DOCTOR_CHAT;
import static com.daffodil.online.dietcoach.db.local.ShareStoreConstants.USER;
import static com.daffodil.online.dietcoach.db.local.ShareStoreConstants.USER_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment implements BotAdapter.ImageClickListener {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            WRITE_EXTERNAL_STORAGE, CAMERA, READ_EXTERNAL_STORAGE
    };
    private static final int IMAGE_RESULT = 200;

    private EditText botInput;
    private BotAdapter adapter;
    private List<Conversation> messageList;
    private EditText msgInput;
    private Users userObj;
    private RecyclerView conversation;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Chat Room");
        View view = inflater.inflate(R.layout.chat_room, container, false);
        ImageButton imageSend = view.findViewById(R.id.image_send);
        Button send = view.findViewById(R.id.send);
        conversation = view.findViewById(R.id.conversation);
        msgInput = view.findViewById(R.id.bot_input);
        verifyStoragePermissions(Objects.requireNonNull(getActivity()));

        imageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
            }
        });

        final String doctorPhone = SharedPreferencesConfig.getStringData(Objects.requireNonNull(getContext()), DOCTOR_CHAT);

        Gson gson = new Gson();
        String user = SharedPreferencesConfig.getStringData(Objects.requireNonNull(getContext()), CURRENT_USER);
        userObj = gson.fromJson(user, Users.class);

        messageList = new ArrayList<>();

        notifyUser();
        if(messageList.isEmpty()){
            Conversation msg = new Conversation();
            msg.setUserType(DOCTOR);
            String gender = userObj == null ? "" : userObj.getGender();
            String person = "";
            if(TextUtils.isEmpty(gender)){
                person = "Sir / Ma'am";
            }else if (gender.equalsIgnoreCase("male")){
                person = "Sir" ;
            }else {
                person = "Ma'am";
            }
            msg.setMessage("Hello " + person + ", How Can i help you?");
            messageList.add(msg);
        }
        
        adapter = new BotAdapter(messageList, getActivity(), ChatFragment.this, userObj);
        LinearLayoutManager manager =  new LinearLayoutManager(getActivity());
        conversation.setLayoutManager(manager);
        conversation.setHasFixedSize(true);
        conversation.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputMsg = msgInput.getText().toString();
                Conversation msg = new Conversation();

                if(userObj != null){
                    String userType = userObj.getUserType();
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm a", Locale.getDefault()).format(new Date());
                    String userFullName = userObj.getFirstName() + " " + userObj.getLastName();

                    msg.setUserType(userType);
                    msg.setMessage(inputMsg);
                    if(userType.equalsIgnoreCase(USER)){
                        msg.setConnectFromPhone(userObj.getPhone());
                    }
                    msg.setConnectToPhone(doctorPhone);
                    msg.setDate(currentDate);
                    msg.setUserName(userFullName);

                }

                new ConversationRepo(Objects.requireNonNull(getActivity())).addConversation(msg, new ConversationRepo.MsgAddListener() {
                    @Override
                    public void saveMsgStatus(String message) {
                        Log.d("mtest", "saveMsgStatus: " + message);
                    }
                });
                messageList.add(msg);

                adapter.notifyDataSetChanged();
                conversation.smoothScrollToPosition(messageList.size() - 1);

                msgInput.getText().clear();
                botMessage(inputMsg);

            }
        });

        return view;
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
                Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                String msgImage = Base4Utils.getBase64String(selectedImage);
                Conversation msg = new Conversation();
                if(userObj != null){
                    msg.setUserType(userObj.getUserType());
                }

                msg.setImageData(msgImage);

                messageList.add(msg);
                adapter.notifyDataSetChanged();
                conversation.smoothScrollToPosition(messageList.size() - 1);
                //imageView.setImageBitmap(selectedImage);
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

    private void botMessage(String userMsg) {
        Conversation msg = new Conversation();
        msg.setUserType(DOCTOR);
        if (userMsg.contains("name")) {
            msg.setMessage("My name is Chat bot");
        } else if (userMsg.contains("hospital")) {
            msg.setMessage("Dhaka medical college hospital");
        } else if (userMsg.contains("doctor")) {
            msg.setMessage("Go to DR. Pran gopal datta");
        } else {
            msg.setMessage("Sorry sir, I do not understand your question");
        }

        messageList.add(msg);
        adapter.notifyDataSetChanged();
    }

    private static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onImageClick(Bitmap image) {
        showImage(image);
    }

    private void showImage(Bitmap image) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final ViewGroup root = null;
        final View dialogView = inflater.inflate(R.layout.show_image, root, false);
        dialogBuilder.setView(dialogView);
        AlertDialog progressDialog= dialogBuilder.create();

        ImageView imageView = dialogView.findViewById(R.id.image_view);
        imageView.setImageBitmap(image);
        Objects.requireNonNull(progressDialog.getWindow()).getAttributes().windowAnimations = R.style.animation;
        progressDialog.show();
    }

    private void notifyUser(){
        new ConversationRepo(Objects.requireNonNull(getActivity())).listenerOnDataChange(new ConversationRepo.ConversationStatus() {
            @Override
            public void notifyOpponent(List<Conversation> conversations, List<String> keys) {
                messageList.clear();
                messageList.addAll(conversations);
                conversation.smoothScrollToPosition(messageList.size() - 1);
                Log.d("mtest", "notifyOpponent: notify doctor");
            }
        });
    }
}
