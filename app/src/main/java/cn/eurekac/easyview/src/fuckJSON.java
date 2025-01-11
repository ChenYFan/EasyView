package cn.eurekac.easyview.src;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class fuckJSON {
    private static final ObjectMapper mapper = new ObjectMapper();
    private HashMap<String, Object> map = null;

    public static String Map2String(HashMap<String, Object> map) {
        try {
            return mapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HashMap<String, Object> fuckJSON2Map(fuckJSON data) {
        HashMap<String, Object> AllMap = new HashMap<>();
        for (String key : data.key()) {
            Object innerdata = data.get(key);
            if (innerdata instanceof fuckJSON) {
                innerdata = fuckJSON2Map((fuckJSON) innerdata);
            }
            AllMap.put(key, innerdata);
        }
        return AllMap;
    }

    public static String fuckJSON2String(fuckJSON data) {
        return Map2String(fuckJSON2Map(data));
    }


    public static HashMap<String, Object> String2Map(String str) throws Exception {
        return mapper.readValue(str, HashMap.class);
    }

    public static fuckJSON Map2fuckJSON(HashMap<String, Object> map) {
        fuckJSON data = new fuckJSON();
        for (String key : map.keySet()) {
            Object innerdata = map.get(key);
            data.set(key, innerdata);
        }
        return data;
    }

    public static fuckJSON String2fuckJSON(String str) throws Exception {
        return Map2fuckJSON(String2Map(str));
    }

    public static String parseString(String str) {
        str = str.replace("\\\\", "\\")
                .replace("\\\"", "\"");
        return str;
    }

    public static String parseString(Object obj) {
        return parseString(obj.toString());
    }


    public void set(String key, Object value) {
        if (value instanceof HashMap) {
            value = Map2fuckJSON((HashMap<String, Object>) value);
        }
        map.put(key, value);
    }

    public Object get(String key) {
        return map.get(key);
    }

    public List<String> key() {
        return new ArrayList<>(map.keySet());
    }

    public List<Object> value() {
        return new ArrayList<>(map.values());
    }


    public HashMap<String, Object> getMap() {
        return fuckJSON2Map(this);
    }

    public String getString() {
        return fuckJSON2String(this);
    }

    public void fromMap(HashMap<String, Object> map) {
        if (map == null) {
            this.map = new HashMap<>();
            return;
        }
        for (String key : map.keySet()) {
            Object innerdata = map.get(key);
            this.set(key, innerdata);
        }
    }

    public void fromString(String str) {
        try {
            if (str == null) {
                this.map = new HashMap<>();
                return;
            }
            this.fromMap(String2Map(str));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public fuckJSON() {
        this.map = new HashMap<>();
    }
}
