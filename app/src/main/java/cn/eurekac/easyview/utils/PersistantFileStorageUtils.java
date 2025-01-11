package cn.eurekac.easyview.utils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PersistantFileStorageUtils {
    private Context context = null;
    public PersistantFileStorageUtils(Context context) {
        this.context = context;
    }

    @SuppressLint("LongLogTag")
    public void save(String filename, String content) {
        save(filename,content.getBytes());
    }
    @SuppressLint("LongLogTag")
    public void save(String filename, byte[] content) {
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(content);
            fos.close();
        } catch (IOException e) {
            Log.e("PersistantFileStorageUtils", "save: " + e.getMessage());
        }
    }

    @SuppressLint("LongLogTag")
    public String loadAsString(String filename) {
        return new String(loadAsByte(filename));
    }

    @SuppressLint("LongLogTag")
    public byte[] loadAsByte(String filename) {
        try {
            if (!context.getFileStreamPath(filename).exists()) {
                return new byte[0];
            }
            FileInputStream fis = context.openFileInput(filename);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            return buffer;
        } catch (IOException e) {
            Log.e("PersistantFileStorageUtils", "load: " + e.getMessage());
            return new byte[0];
        }
    }
}
