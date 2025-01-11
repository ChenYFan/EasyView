package cn.eurekac.easyview.utils;
public class Base64Utils {
    public static String encode(byte[] input) {
        return android.util.Base64.encodeToString(input, android.util.Base64.DEFAULT);
    }
    public static String encode(String input) {
        return encode(input.getBytes());
    }
    public static byte[] decode(String input) {
        return android.util.Base64.decode(input, android.util.Base64.DEFAULT);
    }
}
