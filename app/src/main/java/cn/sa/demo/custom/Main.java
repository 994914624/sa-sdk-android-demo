package cn.sa.demo.custom;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;


/**
 * Created by yzk on 2019/6/20
 */

public class Main {


    public static void main(String[] args) {
        System.out.println("---------------:"+java.util.UUID.nameUUIDFromBytes(" ".getBytes()));

        //decodeData(DD);

//        System.out.println("1111: "+UUID.nameUUIDFromBytes("".getBytes()));
//        System.out.println("2222: "+UUID.nameUUIDFromBytes("".getBytes()));
//
//        MessageDigest md;
//        try {
//            md = MessageDigest.getInstance("MD5");
//            byte[] md5Bytes = md.digest("".getBytes());
//            System.out.println("3333: "+new String(md5Bytes));
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }

//        // 求 A - B 多少条路
//        // 横竖6条路，总共五个交叉点，每个交叉点有几条前进的路
//        List<Integer> allXPoint = new ArrayList<>();
//        allXPoint.add(2);//1
//        allXPoint.add(2);//2
//        allXPoint.add(3);//3
//        allXPoint.add(2);//4
//        allXPoint.add(2);//5
//        for(int i =0;i<allXPoint.size();i++){
//            int now = allXPoint.get(i);
//            System.out.println(" -- i ---: "+i);
//            for(int j =0;j<now;j++){
//                System.out.println(" -- j ---: "+j);
//                List<Integer> nextXPoint = allXPoint;
//                nextXPoint.remove(i);
//            }
//        }


    }


    private static final String DD = "H4sIAAAAAAAAAI2SwUrEMBCGX0XiHnfLNu3abm970buCCCIhNlM7bJuUJLvLsgj6Dp70Gbx78W0U9C1MYhdWccFeQr%2BZf%2BafTC43hFnNyzlDQYpRkicJnaRJPiQWWyBFPJlMaUbzaTYdJw6uOwdJUJAhEWgsytIGMYmTI3oNkMZlnmS0mrqEBq9JsSGDcJKZFFq51GEAbAnaoJIukERxlHrMu24HxxHd5rZga%2BWblErAFgqwHBsPZWR4JKBVES8tLtGuo2PNb1qQdtaDw%2FCR2yGBpcNONZh13TnC6qzUANIV7bTqQFsEE0wLWGIJe2cbtM6K734KosWD9MIzZXb8Z1E%2FwZ9jtVwuKmd3oUE7eoFctegDJvhhNeBN7XzGNB%2BHyj9vsORaY1C%2BvTy%2FP71%2B3D983j3u6FcobE2KjI733%2FfvvXijkvvFE8NHRsxH%2FDs28pfrFSuskBQVbwy4Pwl2pfSc9e8iPdnp39f5z268yqJtfP425hkaVqE2lgm%2B7pu6%2FbGqWZia%2FXqgOY3z26sv0LPkhtACAAA%3D";

    @TargetApi(Build.VERSION_CODES.O)
    private static void decodeData(String str) {
        try {
            // URLDecoder
            String urlData = java.net.URLDecoder.decode(str, "UTF-8");
            // Base64 decode
            byte[] base64Bytes = Base64.getDecoder().decode(urlData.getBytes());
            //byte[] base64Bytes = Base64Coder.decode(urlData);
            // unGzip
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(base64Bytes);
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[2048];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            String rawString = out.toString();
            System.out.print("----------------:" + rawString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void click(View view) {
        System.out.print("SA.Sen --------------lambda 2-------------");
    }

    @JavascriptInterface
    public void testJSCallJava() {
        System.out.print("SA.Sen --------------JavaScript call -------------");

    }

}
