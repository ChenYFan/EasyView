package cn.eurekac.easyview;

import android.content.Context;
import android.net.wifi.ScanResult;

import java.util.List;

import cn.eurekac.easyview.utils.fuckJSON;


import cn.eurekac.easyview.apis.EVWifiManager;

public class EasyViewAPI {
    private Context contextUI = null;

    public enum AvailableAPI {
        SYSTEM$GET_SYSTEM_INFO,
        UTILS$PING_WITH_TIME,
        WIFI$WIFI_SCAN
    }


    private fuckJSON GET_SYSTEM_INFO() {
        fuckJSON data = new fuckJSON();
        data.set("os", System.getProperty("os.name"));
        data.set("os_version", System.getProperty("os.version"));
        data.set("os_arch", System.getProperty("os.arch"));
        data.set("java_version", System.getProperty("java.version"));
        data.set("java_vendor", System.getProperty("java.vendor"));
        data.set("user_name", System.getProperty("user.name"));
        data.set("user_home", System.getProperty("user.home"));
        data.set("user_dir", System.getProperty("user.dir"));
        data.set("file_separator", System.getProperty("file.separator"));
        data.set("path_separator", System.getProperty("path.separator"));
        data.set("line_separator", System.getProperty("line.separator"));
        return data;
    }

    private fuckJSON PING_WITH_TIME(fuckJSON param) {
        fuckJSON data = new fuckJSON();
        double now = System.currentTimeMillis();
        data.set("time", now);
        data.set("delay", now - Double.parseDouble(param.get("time").toString()));
        return data;
    }

    private fuckJSON WIFI_SCAN() {
        EVWifiManager wifiManager = new EVWifiManager(this.contextUI);
        wifiManager.checkPermissions();
        wifiManager.startScan();
        List<ScanResult> scanResults = wifiManager.getScannedNetworks();
        System.out.println(scanResults);
        fuckJSON data = new fuckJSON();
        data.set("networks", scanResults);
        return data;
    }


    public static Boolean isAPIExist(String api) {
        try {
            AvailableAPI.valueOf(api);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public EasyViewAPI(Context context) {
        contextUI = context;
    }

    public fuckJSON excute(String api, fuckJSON param) {
        switch (AvailableAPI.valueOf(api)) {
            case SYSTEM$GET_SYSTEM_INFO:
                return GET_SYSTEM_INFO();
            case UTILS$PING_WITH_TIME:
                return PING_WITH_TIME(param);
            case WIFI$WIFI_SCAN:
                return WIFI_SCAN();
            default:
                return null;
        }
    }

}
