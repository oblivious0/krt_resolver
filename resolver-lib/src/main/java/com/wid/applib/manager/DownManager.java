package com.wid.applib.manager;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Response;
import com.wid.applib.base.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * @author hyj
 * @time 2020/8/20 15:16
 * @class describe 下载管理
 */
public class DownManager {

    private static WeakHashMap<String, List<MDownCallback>> sequence = new WeakHashMap<>();

    /**
     * 后台下载资源文件
     *
     * @param downPath 下载路劲
     * @param fileName 保存的文件名
     */
    public static void downResOnBack(String downPath, String fileName, MDownCallback callback) {

        if (sequence.containsKey(downPath)) {
            sequence.get(downPath).add(callback);
        } else {
            List<MDownCallback> mDownCallbacks = new ArrayList<>();
            mDownCallbacks.add(callback);
            sequence.put(downPath, mDownCallbacks);
            OkGo.<File>get(downPath)
                    .execute(new FileCallback(Constants.path, fileName) {
                        @Override
                        public void onSuccess(Response<File> response) {
                            if (response.isSuccessful()) {
                                for (MDownCallback cb : sequence.get(downPath)) {
                                    cb.callback(Constants.path + "/" + fileName);
                                }
                            }
                            sequence.remove(downPath);
                        }

                        @Override
                        public void onError(Response<File> response) {
                            super.onError(response);
                            sequence.remove(downPath);
                        }
                    });
        }
    }


    /**
     * 下载完成的回调
     *
     * @filePath 文件全路径
     */
    public interface MDownCallback {
        public void callback(String filePath);
    }

}
