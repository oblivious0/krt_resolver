package com.krt.wid.resolver.module;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.krt.wid.resolver.R;
import com.krt.wid.resolver.TestFragment;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.wid.applib.base.BaseInitLoadActivity;
import com.wid.applib.base.Constants;
import com.wid.applib.bean.AppInfoBean;
import com.wid.applib.bean.MPageInfoBean;
import com.wid.applib.bean.MResourceBean;
import com.wid.applib.bean.MVersionBean;
import com.wid.applib.config.MProConfig;
import com.wid.applib.manager.AppLibManager;

import java.util.List;

import krt.wid.http.MCallBack;
import krt.wid.http.Result;
import krt.wid.util.MToast;
import krt.wid.util.ParseJsonUtil;

import static com.wid.applib.MLib.COMPILER_VERSION;
import static com.wid.applib.MLib.TERMINAL;
import static com.wid.applib.MLib.TERMINAL_VERSION;

/**
 * author: MaGua
 * create on:2020/10/27 9:56
 * description
 */
public class InitLoadActivity extends BaseInitLoadActivity {

    private TextView tv_res, tv_size;
    private ProgressBar bar;

    String krtCode, krtVer;

    @Override
    protected void beforeBindLayout() {

    }

    @Override
    protected void initView() {

        krtCode = getIntent().getStringExtra("krtCode");
        krtVer = getIntent().getStringExtra("krtVer");
        MProConfig.build()
                .setKrtCode(krtCode)
                .setFragmentClz(BaseFragment.class)
                .generate();

        checkVer();
    }

    @Override
    protected void init() {
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_init;
    }

    @Override
    protected TextView resourceNameView() {
        if (tv_res == null)
            tv_res = findViewById(R.id.tv_name);
        return tv_res;
    }

    @Override
    protected TextView resourceDownSizeView() {
        if (tv_size == null)
            tv_size = findViewById(R.id.tv_size);
        return tv_size;
    }

    @Override
    protected ProgressBar resourceDownProgressBar() {
        if (bar == null) {
            bar = findViewById(R.id.progress);
            bar.setMax(100);
        }
        return bar;
    }

    @Override
    protected void gotoMainActivity() {

        switch (appInfoBean.getStartType()) {
            case "tabbar":
                startActivity(new Intent(InitLoadActivity.this, BaseMainActivity.class));
                break;
            case "singlePage":
                startActivity(new Intent(InitLoadActivity.this, BaseActivity.class)
                        .putExtra("name", appInfoBean.getStartPageId()));
                break;
        }


        finish();
    }

    //**********************************************************************************************************


    /**
     * 以下方法为展示特意重写，正式开发场合默认使用父类方法即可
     */

    @Override
    protected void checkVer() {
        if (resourceNameView() != null)
            resourceNameView().setText("版本信息获取中...");


        OkGo.<Result<MVersionBean>>get(Constants.getUrl("getLastVersion2"))
                .params("tag", krtCode)
                .params("terminalCode", TERMINAL)
                .params("terminalVersion", TERMINAL_VERSION)
                .params("interpreterCode", COMPILER_VERSION)
                .params("version", krtVer)
                .params("get_base64", 1)
//                .params("is_publish", MProConfig.getInstance().getIs_publish())
                .params("t", System.currentTimeMillis())
                .execute(new MCallBack<Result<MVersionBean>>(this, false) {
                    @Override
                    public void onSuccess(Response<Result<MVersionBean>> response) {
                        if (response.body().isSuccess()) {
                            mVersionBean = response.body().data;
                            checkRes();
                        } else {
                            if (resourceNameView() != null)
                                resourceNameView().setText("版本信息获取失败");

                            gotoMainActivity();
                        }
                    }

                    @Override
                    public void onError(Response<Result<MVersionBean>> response) {
                        super.onError(response);
                    }
                });

    }
}
