package com.krt.wid.resolver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.krt.wid.resolver.module.InitLoadActivity;
import com.krt.wid.resolver.update.MUpdate;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import krt.wid.util.MPermissions;
import krt.wid.util.MToast;
import krt.wid.util.ParseJsonUtil;

/**
 * @author MaGua
 */
public class MainActivity extends AppCompatActivity {

    private long exitTime = 0;
    private final int REQUEST_CODE = 10001;
    private Button btnScan, btnStart;
    private TextView tvKrtCode, tvKrtVer;
    private CardView cardView;

    private String dataJson = "";

    /**
     * xzy参与
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView)findViewById(R.id.version)).setText("解析器版本:"+BuildConfig.VERSION_NAME);
        MPermissions.getInstance().request(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}
                , value -> {
                    if (value) {

                        MUpdate.newBuilder(this)
                                .setUrl("https://www.krtimg.com/file/json/version-APP-000046.json")
                                .setCheckNewVer(true)
                                .build();

                        btnScan = findViewById(R.id.button_scan);
                        btnScan.setOnClickListener(view -> {
                            Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                            startActivityForResult(intent, REQUEST_CODE);
                        });

                        tvKrtCode = findViewById(R.id.krtCode);
                        tvKrtVer = findViewById(R.id.krtVer);
                        cardView = findViewById(R.id.cardView);

                        btnStart = findViewById(R.id.button_start);
                        btnStart.setOnClickListener(view -> {
                            String tag = ParseJsonUtil.getStringByKey(dataJson, "tag"),
                                    ver = ParseJsonUtil.getStringByKey(dataJson, "version");

                            if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(ver)) {
                                MToast.showToast(MainActivity.this, "项目信息有误");
                            } else {
                                startActivity(new Intent(MainActivity.this, InitLoadActivity.class)
                                        .putExtra("krtCode", tag)
                                        .putExtra("krtVer", ver));
                            }
                        });
                    } else {
                        finish();
                    }

                });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            MToast.showToast(this, "再次点击退出App");
            exitTime = System.currentTimeMillis();
        } else {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    dataJson = bundle.getString(CodeUtils.RESULT_STRING);
                    if (!TextUtils.isEmpty(dataJson)) {
                        long timestamp = Long.parseLong(ParseJsonUtil.getStringByKey(dataJson, "timestamp"));
                        Long s = (System.currentTimeMillis() - timestamp) / (1000 * 60);
                        if (s > 10) {
                            MToast.showToast(this, "此二维码信息已超时");
                            cardView.setVisibility(View.GONE);
                            return;
                        } else {
                            tvKrtCode.setText("项目编号：" + ParseJsonUtil.getStringByKey(dataJson, "tag"));
                            tvKrtVer.setText("项目版本：" + ParseJsonUtil.getStringByKey(dataJson, "version"));
                        }
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    dataJson = "";
                    MToast.showToast(this, "解析二维码失败");
                }
            }
        }

        if (TextUtils.isEmpty(dataJson)) {
            cardView.setVisibility(View.GONE);
        } else {
            cardView.setVisibility(View.VISIBLE);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
