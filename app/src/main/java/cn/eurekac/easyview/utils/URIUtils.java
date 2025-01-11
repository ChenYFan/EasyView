package cn.eurekac.easyview.utils;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class URIUtils {
    public static String decodeURI(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8").replace("+", "%2B");
        }catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    public static String encodeURI(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("%21", "!")
                    .replaceAll("%27", "'")
                    .replaceAll("%28", "(")
                    .replaceAll("%29", ")")
                    .replaceAll("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
