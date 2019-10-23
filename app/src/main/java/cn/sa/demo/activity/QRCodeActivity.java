package cn.sa.demo.activity;

import android.os.Bundle;
import com.google.zxing.Result;
import com.vondear.rxfeature.activity.ActivityScanerCode;

/**
 * 二维码的 Activity
 */
public class QRCodeActivity extends ActivityScanerCode {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void handleDecode(Result result) {
        super.handleDecode(result);
        finish();//finish 扫码的 Activity
    }

}
