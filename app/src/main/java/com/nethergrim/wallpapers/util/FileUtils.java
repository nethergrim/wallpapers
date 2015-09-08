package com.nethergrim.wallpapers.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.nethergrim.wallpapers.App;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * @author Andrew Drobyazko (c2q9450@gmail.com) on 07.09.15.
 */
public class FileUtils {

    public static final String JSON_FILE_NAME = "wallpapers.json";

    public static void writeStringAsFile(final String fileContents, String fileName) {
        try {
            FileWriter out = new FileWriter(
                    new File(Environment.getExternalStorageDirectory(), fileName));
            out.write(fileContents);
            out.close();
        } catch (IOException e) {
            Log.e("TAG", e.getMessage());
        }
    }

    public static Uri persistBitmapToDisk(Bitmap bitmap) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString();
        File myDir = new File(root + "/earth_wallpapers");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Wallpaper-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(App.getApp(), new String[] {file.toString()}, null,
                (path, uri) -> {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                });
        return Uri.fromFile(file);

    }


    public static InputStream getAssetFileInputStream(String assetFileName) {
        Context context = App.getApp().getApplicationContext();
        InputStream fIn;
        try {
            fIn = context.getResources()
                    .getAssets()
                    .open(assetFileName, Context.MODE_WORLD_READABLE);
            return fIn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static JSONArray getJSONFromAssets(String assetsName) {
        InputStream is = FileUtils.getAssetFileInputStream(assetsName);

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(is));
            while ((line = in.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            Log.e("TAG", e.getMessage());
        }
        String resultedString = stringBuilder.toString();
        try {
            JSONArray jsonArray = new JSONArray(resultedString);
            return jsonArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
