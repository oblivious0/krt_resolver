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

    protected MVersionBean mVersionBean;
    protected List<String> mDownloads = new ArrayList<>();
    protected List<String> downKeys = new ArrayList<>();
    protected List<File> files;
    protected AppInfoBean appInfoBean;
    protected MVersionBean.VersionInfoBean versionInfoBean;

    protected WeakHashMap<String, MPageInfoBean> md5CodeMap = new WeakHashMap<>();
    protected WeakHashMap<String, MResourceBean> resMap = new WeakHashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeBindLayout();
        setContentView(bindLayout());

        initView();

        File nomedia = new File(Constants.basePath, ".nomedia");
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

    /**
     * 检查版本信息
     */
    protected void checkVer() {
        if (resourceNameView() != null)
            resourceNameView().setText("版本信息获取中...");

        OkGo.<Result<MVersionBean>>get(Constants.getUrl("getLastVersion2"))
                .params("tag", MProConfig.getInstance().getKrt_pro_code())
                .params("terminalCode", TERMINAL)
                .params("terminalVersion", TERMINAL_VERSION)
                .params("interpreterCode", COMPILER_VERSION)
                .params("get_base64", "1")
                .params("is_publish", MProConfig.getInstance().getIs_publish())
//                .params("version", "13")
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

    /**
     * 检查资源清单
     */
    protected void checkRes() {
        SkinManager.Builder builder = new SkinManager.Builder();
        String url = "", ver = "";
        if (mVersionBean.getInterpreter_last_version() != null && mVersionBean.getInterpreter_last_version().getVersion() != null) {
            versionInfoBean = mVersionBean.getInterpreter_last_version();
        } else {
            versionInfoBean = mVersionBean.getLast_version();
        }

        appInfoBean = ParseJsonUtil.getBean
                (versionInfoBean.getApp_info(), AppInfoBean.class);
        ver = versionInfoBean.getSkin_version();
        if (Integer.parseInt(COMPILER_VERSION) < 4) {
            url = versionInfoBean.getBase_skin();
        } else {
            url = versionInfoBean.getCustom_skin();
            builder.setSkinCode(versionInfoBean.getSkin_code())
                    .setSkinList(versionInfoBean.getSkin_icon())
                    .generate();
        }

        if (appInfoBean != null) {
            AppLibManager.defaultPath = appInfoBean.getDefaultPath();
            for (AppInfoBean.BasePathBean basePathBean : appInfoBean.getBasePath()) {
                AppLibManager.putBasePath(basePathBean.getProd());
                AppLibManager.putBetaPath(basePathBean.getDev());
            }
            MProConfig.btx_json_name = appInfoBean.getStartPageId();
        }

        if (!TextUtils.isEmpty(url)) {
            String[] area = url.split("/");
            String fileName = area[area.length - 1];
            MProConfig.skin_name = fileName;
            String current = AppLibManager.getStorageVal("skinVer", this);

            //如果皮肤版本号不匹配或者皮肤文件不存在，需要重新下载
            if (!ver.equals(current) || FileUtils.isDir(Constants.basePath + "/" + fileName)) {
                mDownloads.add(url);
                downKeys.add(fileName);
                AppLibManager.putStorageVal("skinVer", ver, this);
                //每次更新了皮肤文件，删除所有切图
                FileUtils.deleteFilesInDir(Constants.path);
            }
        } else {
            MProConfig.skin_name = "customSkin.png";
            String base64 = versionInfoBean.getSkin_base64();
            if (base64.contains("data:image/png;base64,")) {
                String png = base64.replace("data:image/png;base64,", "");
                byte[] img = EncodeUtils.base64Decode(png);
                File f = new File(Constants.basePath + "/customSkin.png");
                FileUtils.createFileByDeleteOldFile(f);
                FileOutputStream fOut = null;
                try {
                    fOut = new FileOutputStream(f);
                    ConvertUtils.bytes2Bitmap(img).compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {

                } finally {
                }
            }
        }

        if (resourceNameView() != null)
            resourceNameView().setText("界面清单获取中...");

        OkGo.<Result<List<MPageInfoBean>>>get(Constants.getUrl("getPageList"))
                .params("withConfig", 1)
                .params("pageSize", 100)
                .params("tag", MProConfig.getInstance().getKrt_pro_code())
                .params("currentPage", 0)
                .params("t", System.currentTimeMillis())
                .params("version", versionInfoBean.getVersion())
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

    /**
     * 加载资源清单列表
     */
    protected void loadResList() {
        if (resourceNameView() != null)
            resourceNameView().setText("正在检查资源列表...");

        OkGo.<Result<List<MResourceBean>>>get(Constants.getUrl("getProjectRes"))
                .params("t", System.currentTimeMillis())
                .params("tag", MProConfig.getInstance().getKrt_pro_code())
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

    /**
     * 过滤资源文件
     */
    protected void filterResLoad() {

        FileUtils.createOrExistsDir(Constants.path);

        files = FileUtils.listFilesInDirWithFilter(Constants.path,
                file -> !file.isDirectory(), false);

        if (resourceNameView() != null)
            resourceNameView().setText("正在检查更新...");

        for (String pageId : md5CodeMap.keySet()) {
            if ("-1".equals(MProConfig.getInstance().getIs_publish()) ||
                    "0".equals(MProConfig.getInstance().getIs_publish())) {
                String filePath = Constants.path + md5CodeMap.get(pageId).getPage_code() + ".json";
                FileUtils.createFileByDeleteOldFile(filePath);
                FileIOUtils.writeFileFromString(filePath, md5CodeMap.get(pageId).getPage_config());
            } else {
                if (md5CodeMap.get(pageId).getFile_url() != null) {
                    mDownloads.add(md5CodeMap.get(pageId).getFile_url());
                    downKeys.add(md5CodeMap.get(pageId).getPage_code() + ".json");
                }
            }
        }

        for (MResourceBean bean : resMap.values()) {
            if (!TextUtils.isEmpty(bean.getImage_url())) {
                String[] area = bean.getImage_url().split("/");
                String fileName = area[area.length - 1];

                mDownloads.add(bean.getImage_url());
                downKeys.add(fileName);
            }
        }

        down();
    }

    protected void down() {

        if (mDownloads.size() == 0) {
            //加载完成...
            gotoMainActivity();
            return;
        }

        if (resourceNameView() != null)
            resourceNameView().setText("资源加载中...");


        String url = mDownloads.get(0);
        String page = downKeys.get(0);

        OkGo.<File>get(url)
                .execute(new FileCallback(Constants.basePath, page) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(Response<File> response) {
                        if (response.isSuccessful()) {

                        } else {
                            if (resourceNameView() != null)
                                resourceNameView().setText("资源加载失败【"
                                        + response.code() + ":" + response.message() + "】");
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onError(Response<File> response) {
                        super.onError(response);
                        if (resourceNameView() != null)
                            resourceNameView().setText("资源加载失败【" +
                                    +response.code() + ":" + response.message() + "】");
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void downloadProgress(Progress progress) {
                        super.downloadProgress(progress);
                        if (resourceDownSizeView() != null)
                            resourceDownSizeView().setText(((int) (progress.fraction * 100)) + "%");

                        if (resourceDownProgressBar() != null)
                            resourceDownProgressBar().setProgress((int) (progress.fraction * 100));
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mDownloads.remove(0);
                        downKeys.remove(0);
                        down();
                    }
                });
    }

    public abstract int bindLayout();

    protected abstract TextView resourceNameView();

    protected abstract TextView resourceDownSizeView();

    protected abstract ProgressBar resourceDownProgressBar();

    protected abstract void gotoMainActivity();
}
