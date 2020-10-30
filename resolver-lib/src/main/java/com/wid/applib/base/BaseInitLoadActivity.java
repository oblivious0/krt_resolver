package com.wid.applib.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
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
import com.wid.applib.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import krt.wid.http.MCallBack;
import krt.wid.http.Result;
import krt.wid.util.MPermissions;
import krt.wid.util.ParseJsonUtil;

import static com.wid.applib.MLib.COMPILER_VERSION;
import static com.wid.applib.MLib.TERMINAL_VERSION;


/**
 * @author hyj
 * @time 2020/9/14 16:00
 * @class describe
 */
public abstract class BaseInitLoadActivity extends AppCompatActivity {

    protected MVersionBean mVersionBean;
    protected List<String> mDownloads = new ArrayList<>();
    protected List<File> files;
    protected AppInfoBean appInfoBean;

    protected WeakHashMap<String, MPageInfoBean> md5CodeMap = new WeakHashMap<>();
    protected WeakHashMap<String, MResourceBean> resMap = new WeakHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeBindLayout();
        setContentView(bindLayout());

        initView();

        MPermissions.getInstance().request(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                value -> {
                    if (value) {

                        File nomedia = new File(Constants.basePath, ".nomedia");
                        try {
                            if (!nomedia.exists()) nomedia.createNewFile();
                        } catch (IOException e) {
                            Log.e("IOException", "exception in createNewFile() method");
                        }

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

        OkGo.<Result<MVersionBean>>get(Constants.getUrl("getLastVersion"))
                .params("tag", MProConfig.getInstance().getKrt_pro_code())
                .params("terminalCode", 1)
                .params("terminalVersion", TERMINAL_VERSION)
                .params("interpreterCode", COMPILER_VERSION)
                .params("is_publish", MProConfig.getInstance().getIs_publish())
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

        String url = "";
        if (mVersionBean.getInterpreter_last_version() != null) {
            appInfoBean = ParseJsonUtil.getBean
                    (mVersionBean.getInterpreter_last_version().getApp_info(), AppInfoBean.class);
            mDownloads.add(mVersionBean.getInterpreter_last_version().getBase_skin());
            mDownloads.add(mVersionBean.getInterpreter_last_version().getCustom_skin());
            url = mVersionBean.getInterpreter_last_version().getBase_skin();
        } else {
            appInfoBean = ParseJsonUtil.getBean
                    (mVersionBean.getLast_version().getApp_info(), AppInfoBean.class);
            mDownloads.add(mVersionBean.getLast_version().getBase_skin());
            mDownloads.add(mVersionBean.getLast_version().getCustom_skin());
            url = mVersionBean.getLast_version().getBase_skin();
        }

        if (appInfoBean != null) {
            AppLibManager.defaultPath = appInfoBean.getDefaultPath();
            for (AppInfoBean.BasePathBean basePathBean : appInfoBean.getBasePath()) {
                AppLibManager.putBasePath(basePathBean.getProd());
            }
            MProConfig.btx_json_name = appInfoBean.getStartPageId();
        }

//        MProConfig.skin_name = getBase_skin()
        if (!TextUtils.isEmpty(url)) {
            String[] area = url.split("/");
            String fileName = area[area.length - 1];
            MProConfig.skin_name = fileName;
        }

        if (resourceNameView() != null)
            resourceNameView().setText("界面清单获取中...");

        OkGo.<Result<List<MPageInfoBean>>>get(Constants.getUrl("getPageList"))
                .params("withConfig", 1)
                .params("pageSize", 100)
                .params("tag", MProConfig.getInstance().getKrt_pro_code())
                .params("currentPage", 0)
                .params("version", mVersionBean.getInterpreter_last_version() == null
                        ? mVersionBean.getLast_version().getVersion()
                        : mVersionBean.getInterpreter_last_version().getVersion())
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
                        }
                        gotoMainActivity();
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

        for (int i = 0; i < files.size(); i++) {
            //过滤掉不需要更新的文件
            String s = EncryptUtils.encryptMD5File2String(files.get(i));
            String json = Util.getJson(files.get(i));
            String s1 = EncryptUtils.encryptMD5ToString(json);
//            LogUtils.e(files.get(i).getName() + ":" + s + "---" + s1);
//            md5CodeMap.remove(s.toLowerCase());
            resMap.remove(files.get(i).getName());
        }

        for (String pageId : md5CodeMap.keySet()) {
            String filePath = Constants.path + md5CodeMap.get(pageId).getPage_code() + ".json";
            FileUtils.createFileByDeleteOldFile(filePath);
            FileIOUtils.writeFileFromString(filePath, md5CodeMap.get(pageId).getPage_config());
        }

        for (MResourceBean bean : resMap.values()) {
            mDownloads.add(bean.getImage_url());
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

        String[] area = url.split("/");
        String fileName = area[area.length - 1];

        OkGo.<File>get(url)
                .execute(new FileCallback(Constants.basePath, fileName) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(Response<File> response) {
                        if (response.isSuccessful()) {
                            mDownloads.remove(0);
                            down();
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
                });
    }

    public abstract int bindLayout();

    protected abstract TextView resourceNameView();

    protected abstract TextView resourceDownSizeView();

    protected abstract ProgressBar resourceDownProgressBar();

    protected abstract void gotoMainActivity();
}
