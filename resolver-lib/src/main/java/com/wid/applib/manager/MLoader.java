package com.wid.applib.manager;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.wid.applib.base.BaseInitLoadActivity;
import com.wid.applib.base.BaseModuleMainActivity;
import com.wid.applib.base.Constants;
import com.wid.applib.bean.AppInfoBean;
import com.wid.applib.bean.MPageInfoBean;
import com.wid.applib.bean.MResourceBean;
import com.wid.applib.bean.MVersionBean;
import com.wid.applib.config.MProConfig;
import com.wid.applib.skin.SkinManager;
import com.wid.applib.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import krt.wid.http.MCallBack;
import krt.wid.http.Result;
import krt.wid.util.ParseJsonUtil;

import static com.wid.applib.MLib.COMPILER_VERSION;
import static com.wid.applib.MLib.TERMINAL;
import static com.wid.applib.MLib.TERMINAL_VERSION;

/**
 * author: MaGua
 * create on:2021/1/21 8:57
 * description 仅用于4.1及更高版本
 */
public class MLoader {

    private static MLoader mLoader;
    private OnLoaderListener listener;

    private BaseInitLoadActivity activity;
    private String project_tag;
    private String version;
    private String publish_version = "-1";

    protected List<String> mDownloads = new ArrayList<>();
    protected List<String> downKeys = new ArrayList<>();
    protected List<File> files;
    protected MVersionBean.VersionInfoBean versionInfoBean;

    protected WeakHashMap<String, MPageInfoBean> md5CodeMap = new WeakHashMap<>();
    protected WeakHashMap<String, MResourceBean> resMap = new WeakHashMap<>();

    boolean isComplete = false;

    private MLoader(BaseInitLoadActivity activity, String project_tag, String version, String publish_version) {
        this.activity = activity;
        this.project_tag = project_tag;
        this.version = version;
        this.publish_version = publish_version;
    }

    /**
     * 开始获取版本信息
     */
    private void start() {
        if (activity.resourceNameView() != null)
            activity.resourceNameView().setText("版本信息获取中...");

        OkGo.<Result<MVersionBean>>get(Constants.getUrl("getLastVersion2"))
                .params("tag", project_tag)
                .params("terminalCode", TERMINAL)
                .params("terminalVersion", TERMINAL_VERSION)
                .params("interpreterCode", COMPILER_VERSION)
                .params("get_base64", "1")
                .params("is_publish", publish_version)
                .params("version", version)
                .params("t", System.currentTimeMillis())
                .execute(new MCallBack<Result<MVersionBean>>(activity, false) {


                    @Override
                    public void onSuccess(Response<Result<MVersionBean>> response) {
                        if (response.body().isSuccess()) {
                            isComplete = true;
                            MVersionBean mVersionBean = response.body().data;
                            //获取当前解析器可用的版本
                            if (mVersionBean.getInterpreter_last_version() != null && mVersionBean.getInterpreter_last_version().getVersion() != null) {
                                versionInfoBean = mVersionBean.getInterpreter_last_version();
                            } else {
                                versionInfoBean = mVersionBean.getLast_version();
                            }
                            readVersionInfo();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (!isComplete && listener != null) {
                            //如果因其他问题未能获得可用版本，需要APP自己提供一个版本
                            versionInfoBean = listener.onGotVersionFail();
                            if (versionInfoBean != null) {
                                readVersionInfo();
                            }
                        }
                    }
                });
    }

    /**
     * 解读版本信息
     */
    private void readVersionInfo() {
        SkinManager.Builder builder = new SkinManager.Builder();
        activity.appInfoBean = ParseJsonUtil.getBean
                (versionInfoBean.getApp_info(), AppInfoBean.class);

        String url = null;
        builder.setSkinCode(versionInfoBean.getSkin_code())
                .setSkinList(versionInfoBean.getSkin_icon())
                .generate();


        if (activity.appInfoBean != null) {
            AppLibManager.defaultPath = activity.appInfoBean.getDefaultPath();
            for (AppInfoBean.BasePathBean basePathBean : activity.appInfoBean.getBasePath()) {
                AppLibManager.putBasePath(basePathBean.getProd());
                AppLibManager.putBetaPath(basePathBean.getDev());
            }
            if (listener != null) listener.onApiComplete();
            MProConfig.btx_json_name = activity.appInfoBean.getStartPageId();
        }

        switch (versionInfoBean.getIs_publish()) {
            case "-1":
            case "0":
                url = versionInfoBean.getCdn_url();
                break;
            default:
                url = versionInfoBean.getUrl();
                break;
        }

        if (TextUtils.isEmpty(url)) url = versionInfoBean.getCustom_skin();

        String current = AppLibManager.getStorageVal("skinVer", activity.getApplicationContext());
        //如果皮肤版本号不匹配或者皮肤文件不存在，需要重新下载
        if (!versionInfoBean.getSkin_version().equals(current) || FileUtils.isDir(Constants.path + "/" + MProConfig.skin_name)) {
            mDownloads.add(url);
            downKeys.add("customSkin.png");
            AppLibManager.putStorageVal("skinVer", versionInfoBean.getSkin_version(), activity.getApplicationContext());
            //每次更新了皮肤文件，删除所有切图
            FileUtils.deleteFilesInDir(Constants.path);
        }

        if (isComplete) {
            getPageList();
        } else {
            if (listener != null) listener.onFinish();
        }

    }


    private void getPageList() {
        if (activity.resourceNameView() != null)
            activity.resourceNameView().setText("界面清单获取中...");

        OkGo.<Result<List<MPageInfoBean>>>get(Constants.getUrl("getPageList"))
                .params("withConfig", 1)
                .params("pageSize", 100)
                .params("tag", MProConfig.getInstance().getKrt_pro_code())
                .params("currentPage", 0)
                .params("t", System.currentTimeMillis())
                .params("version", versionInfoBean.getVersion())
                .execute(new MCallBack<Result<List<MPageInfoBean>>>(activity, false) {
                    @Override
                    public void onSuccess(Response<Result<List<MPageInfoBean>>> response) {
                        if (response.body().isSuccess()) {
                            List<MPageInfoBean> list = response.body().data;
                            for (MPageInfoBean bean : list) {
                                md5CodeMap.put(bean.getPage_config_md5(), bean);
                            }
                            loadResList();
                        } else {
                            if (activity.resourceNameView() != null)
                                activity.resourceNameView().setText("界面清单获取失败");
                        }
                    }

                    @Override
                    public void onError(Response<Result<List<MPageInfoBean>>> response) {
                        super.onError(response);
                        activity.gotoMainActivity();
                    }
                });
    }

    /**
     * 加载资源清单列表
     */
    protected void loadResList() {
        if (activity.resourceNameView() != null)
            activity.resourceNameView().setText("正在检查资源列表...");

        OkGo.<Result<List<MResourceBean>>>get(Constants.getUrl("getProjectRes"))
                .params("t", System.currentTimeMillis())
                .params("tag", MProConfig.getInstance().getKrt_pro_code())
                .execute(new MCallBack<Result<List<MResourceBean>>>(activity, false) {
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
                            activity.gotoMainActivity();
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

        if (activity.resourceNameView() != null)
            activity.resourceNameView().setText("正在检查更新...");

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

        if (mDownloads.size() == 0 && listener != null) {
            //加载完成...
            listener.onFinish();
            return;
        }

        if (activity.resourceNameView() != null)
            activity.resourceNameView().setText("资源加载中...");


        String url = mDownloads.get(0);
        String page = downKeys.get(0);

        OkGo.<File>get(url)
                .execute(new FileCallback(Constants.path, page) {

                    boolean complete = false;

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(Response<File> response) {
                        if (response.isSuccessful()) {
                            complete = true;
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void downloadProgress(Progress progress) {
                        super.downloadProgress(progress);
                        if (activity.resourceDownSizeView() != null)
                            activity.resourceDownSizeView().setText(((int) (progress.fraction * 100)) + "%");

                        if (activity.resourceDownProgressBar() != null)
                            activity.resourceDownProgressBar().setProgress((int) (progress.fraction * 100));
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();

                        if (!complete) {
                            if (page.contains(".json")) {
                                Util.getAssestsJson(activity, page);
                            } else {
                                Util.getAssestsImage(activity, page);
                            }
                        }

                        mDownloads.remove(0);
                        downKeys.remove(0);
                        down();
                    }
                });
    }


    public static class Builder {
        private BaseInitLoadActivity mainActivity;
        private String tag;
        private String ver;
        private String publish_ver;

        public Builder(BaseInitLoadActivity mainActivity, String tag) {
            this.mainActivity = mainActivity;
            this.tag = tag;
        }

        public Builder setVer(String ver) {
            this.ver = ver;
            return this;
        }

        public Builder setPublish_ver(String publish_ver) {
            this.publish_ver = publish_ver;
            return this;
        }

        public void generate() {
            mLoader = new MLoader(mainActivity, tag, ver, publish_ver);
            mLoader.start();
        }

        public void generate(OnLoaderListener onLoaderListener) {
            mLoader = new MLoader(mainActivity, tag, ver, publish_ver);
            mLoader.listener = onLoaderListener;
            mLoader.start();
        }

    }

    /**
     * 监听加载状态
     */
    public interface OnLoaderListener {

        /**
         * 获取版本信息失败
         *
         * @return 返回一个版本信息实例，一般从asset文件夹获取预存的版本信息
         */
        public MVersionBean.VersionInfoBean onGotVersionFail();

        /**
         * API地址加载完成后
         */
        public void onApiComplete();

        /**
         * 加载完毕
         */
        public void onFinish();
    }
}
