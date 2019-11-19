package com.daffodil.online.dietcoach.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import androidx.annotation.NonNull;

public class Base4Utils {

    private static final String TAG = "Base4Utils";

    private Base4Utils() {
    }

    public static String getBase64String(Bitmap image){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 60, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    public static Bitmap decodeBase64(@NonNull String input, @NonNull Context context) {
        byte[] decodedByte = Base64.decode(input, 0);

        boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        File sdCardDirectory;
        if (isSDPresent) {
            // yes SD-card is present
            sdCardDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "IMG");
            if (!sdCardDirectory.exists() && !sdCardDirectory.mkdirs()) {
                    Log.d("MySnaps", "failed to create directory");
            }
        } else {
            // Sorry
            sdCardDirectory = new File(context.getCacheDir(),"");
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());

        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((1000) + 1);

        String nw = "IMG_" + timeStamp + randomNum+".txt";
        File image = new File(sdCardDirectory, nw);

        // Encode the file as a PNG image.
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(image);
            outStream.write(input.getBytes());

            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            Log.e(TAG, "decodeBase64: " + e.getMessage());
        }

        Log.i("Compress bitmap path", image.getPath());
        Bitmap bitmap = null;
        try{
            bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        }catch(OutOfMemoryError | Exception e){
            Log.e(TAG, "decodeBase64: OutOfMemoryError " + e.getMessage());
        }

        return bitmap;

    }
}
