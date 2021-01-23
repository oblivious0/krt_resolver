package com.wid.applib.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.wid.applib.bean.AppInfoBean;
import com.wid.applib.bean.MPageInfoBean;
import com.wid.applib.bean.MResourceBean;
import com.wid.applib.bean.MVersionBean;
import com.wid.applib.config.MProConfig;
import com.wid.applib.manager.AppLibManager;
import com.wid.applib.manager.MLoader;
import com.wid.applib.skin.SkinManager;
import com.wid.applib.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import krt.wid.http.MCallBack;
import krt.wid.http.Result;
import krt.wid.util.MDown;
import krt.wid.util.MPermissions;
import krt.wid.util.ParseJsonUtil;
import okhttp3.internal.Version;

import static com.wid.applib.MLib.COMPILER_VERSION;
import static com.wid.applib.MLib.TERMINAL;
import static com.wid.applib.MLib.TERMINAL_VERSION;


/**
 * @author hyj
 * @time 2020/9/14 16:00
 * @class describe
 */
public abstract class BaseInitLoadActivity extends AppCompatActivity {

    public AppInfoBean appInfoBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeBindLayout();
        setContentView(bindLayout());

        initView();

        File nomedia = new File(Constants.path, ".nomedia");
        try {
            if (!nomedia.exists()) nomedia.createNewFile();
        } catch (IOException e) {
            Log.e("IOException", "exception in createNewFile() method");
        }

        MPermissions.getInstance().request(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                value -> {
                    if (value) {
                        init();
                    } else {
                        finish();
                    }
                });
    }

    protected abstract void beforeBindLayout();

    protected abstract void initView();

    protected abstract void init();

    public abstract int bindLayout();

    public abstract TextView resourceNameView();

    public abstract TextView resourceDownSizeView();

    public abstract ProgressBar resourceDownProgressBar();

    public abstract void gotoMainActivity();
}
