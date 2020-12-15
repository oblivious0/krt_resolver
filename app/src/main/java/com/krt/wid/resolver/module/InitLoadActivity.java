package com.krt.wid.resolver.module;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.krt.wid.resolver.R;
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

    MVersionBean.VersionInfoBean versionInfoBean;
    AppInfoBean appInfoBean = null;

    @Override
    protected void beforeBindLayout() {

    }

    @Override
    protected void initView() {

        krtCode = getIntent().getStringExtra("krtCode");
        krtVer = getIntent().getStringExtra("krtVer");
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

        OkGo.<Result<MVersionBean>>get(Constants.getUrl("getLastVersion"))
                .params("tag", krtCode)
                .params("terminalCode", 1)
                .params("terminalVersion", TERMINAL_VERSION)
                .params("interpreterCode", COMPILER_VERSION)
                .execute(new MCallBack<Result<MVersionBean>>(this, false) {
                    @Override
                    public void onSuccess(Response<Result<MVersionBean>> response) {
                        if (response.body().isSuccess()) {
                            mVersionBean = response.body().data;
                            checkRes();
                        } else {
                            if (resourceNameView() != null)
                                resourceNameView().setText("版本信息获取失败");

                            finish();
                        }
                    }

                    @Override
                    public void onError(Response<Result<MVersionBean>> response) {
                        super.onError(response);
                    }
                });
    }

    @Override
    protected void checkRes() {

        for (MVersionBean.VersionInfoBean bean : mVersionBean.getEnable_version()) {
            if (krtVer.equals(bean.getVersion())) {
                appInfoBean = ParseJsonUtil.getBean(bean.getApp_info(), AppInfoBean.class);
                mDownloads.add(bean.getBase_skin());
                mDownloads.add(bean.getCustom_skin());
                versionInfoBean = bean;

                MProConfig.build()
                        .setKrtCode(krtCode)
                        .setFragmentClz(BaseFragment.class)
                        .generate();

                MProConfig.btx_json_name = appInfoBean.getStartPageId();

                break;
            }
        }

        if (versionInfoBean == null) {
            MToast.showToast(this, "未检测到可用版本");
            finish();
            return;
        }

        if (appInfoBean != null) {
            AppLibManager.defaultPath = appInfoBean.getDefaultPath();
            for (AppInfoBean.BasePathBean basePathBean : appInfoBean.getBasePath()) {
                AppLibManager.putBasePath(basePathBean.getProd());
                AppLibManager.putBetaPath(basePathBean.getDev());
            }
        }

        if (resourceNameView() != null)
            resourceNameView().setText("界面清单获取中...");

        OkGo.<Result<List<MPageInfoBean>>>get(Constants.getUrl("getPageList"))
                .params("withConfig", 1)
                .params("pageSize", 100)
                .params("tag", krtCode)
                .params("currentPage", 0)
                .params("version", krtVer)
                .execute(new MCallBack<Result<List<MPageInfoBean>>>(this, false) {
                    @Override
                    public void onSuccess(Response<Result<List<MPageInfoBean>>> response) {
                        if (response.body().isSuccess()) {
                            List<MPageInfoBean> list = response.body().data;
                            for (MPageInfoBean bean : list) {
                                md5CodeMap.put(bean.getPage_config_md5(), bean);
                            }
                            loadResList();
                        } else {
                            if (resourceNameView() != null)
                                resourceNameView().setText("界面清单获取失败");

                            gotoMainActivity();
                        }
                    }

                    @Override
                    public void onError(Response<Result<List<MPageInfoBean>>> response) {
                        super.onError(response);
                        gotoMainActivity();
                    }
                });

    }


    @Override
    protected void loadResList() {
        if (resourceNameView() != null)
            resourceNameView().setText("正在检查资源列表...");

        OkGo.<Result<List<MResourceBean>>>get(Constants.getUrl("getProjectRes"))
                .params("tag", krtCode)
                .execute(new MCallBack<Result<List<MResourceBean>>>(this, false) {
                    @Override
                    public void onSuccess(Response<Result<List<MResourceBean>>> response) {
                        if (response.body().isSuccess()) {
                            List<MResourceBean> list = response.body().data;
                            if (list != null) {
                                for (MResourceBean res : list) {
                                    resMap.put(res.getFile_name(), res);
                                }
                            }
                            filterResLoad();
                        } else {
                            gotoMainActivity();
                        }
                    }
                });
    }
}
