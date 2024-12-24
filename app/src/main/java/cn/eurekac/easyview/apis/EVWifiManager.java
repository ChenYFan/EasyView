package cn.eurekac.easyview.apis;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class EVWifiManager {

    private static final String TAG = "EVWifiManager";
    private WifiManager wifiManager;
    private Context context;

    public EVWifiManager(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }
    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    context.checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                    context.checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((androidx.appcompat.app.AppCompatActivity) context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_WIFI_STATE,
                                Manifest.permission.CHANGE_WIFI_STATE}, 1);
            }
        }
    }

    public boolean startScan() {
        if (!wifiManager.isWifiEnabled()) {
            Log.e(TAG, "WiFi is disabled");
            return false;
        }
        return wifiManager.startScan();
    }

    public List<ScanResult> getScannedNetworks() {
        return wifiManager.getScanResults();
    }
}
