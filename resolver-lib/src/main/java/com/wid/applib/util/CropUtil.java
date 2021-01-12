package com.wid.applib.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.wid.applib.base.Constants;
import com.wid.applib.bean.BottomBean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * author:Marcus
 * create on:2019/3/26 17:24
 * description
 */
public class CropUtil {
    private static CropUtil cropUtil;

    public static CropUtil getInstance() {
        if (cropUtil == null) {
            cropUtil = new CropUtil();
        }
        return cropUtil;
    }

    @SuppressLint("checkResult")
    public void cropBottomImg(final Context context, List<BottomBean.CommonBean.LinksBean> list, final File file, final CallBack callBack) {
        Observable.fromIterable(list)
                .map(linksBean -> {
                    if (TextUtils.isEmpty(linksBean.getOriginSkin())) {
                        return new Object();
                    }
                    if (linksBean.isSkinIcon()) {
                        LogUtils.e(file.getPath());
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                        String[] origin = linksBean.getOriginSkin().split("_");
                        Bitmap originImg = Bitmap.createBitmap(bitmap,
                                Integer.parseInt(origin[2]) > 1 ? Integer.parseInt(origin[2]) - 1 : Integer.parseInt(origin[2]),
                                Integer.parseInt(origin[3]),
                                Integer.parseInt(origin[0]) - 1,
                                Integer.parseInt(origin[1]) - 1);
                        String[] select = linksBean.getSelectSkin().split("_");
                        Bitmap selectImg = Bitmap.createBitmap(bitmap,
                                Integer.parseInt(select[2]) > 1 ? Integer.parseInt(select[2]) - 1 : Integer.parseInt(select[2]),
                                Integer.parseInt(select[3]),
                                Integer.parseInt(select[0]) - 1,
                                Integer.parseInt(select[1]) - 1);
                        linksBean.setOriginImg(saveBitmap(originImg, linksBean.getOriginSkin() + ".png"));
                        linksBean.setSelectImg(saveBitmap(selectImg, linksBean.getSelectSkin() + ".png"));

                    }
                    return new Object();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> callBack.callback())
                .subscribe(aVoid -> {

                });
    }

    @SuppressLint("checkResult")
    public void cropImg(final Context context, final String content, final CropListener listener) {
        final String path = SpUtil.getIconSkinPath(context);
        if (TextUtils.isEmpty(path)) return;
        Observable.create((ObservableOnSubscribe<Bitmap>) emitter -> {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            String[] contents = content.split("_");
            Bitmap cropImg = Bitmap.createBitmap(bitmap, Integer.parseInt(contents[2]), Integer.parseInt(contents[3]), Integer.parseInt(contents[0]), Integer.parseInt(contents[1]) - 1);
            emitter.onNext(cropImg);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(b -> {
                    if (b != null) {
                        listener.callback(b);
                    }
                });
    }

    @SuppressLint("checkResult")
    public void cropImg(final Context context, final String name, final String content, final CropListener listener) {
        final File file = new File(Constants.path + name);
        if (!file.exists()) return;
        Observable.create((ObservableOnSubscribe<Bitmap>) emitter -> {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                String[] contents = content.split("_");
                Bitmap cropImg = Bitmap.createBitmap(bitmap,
                        Integer.parseInt(contents[2]),
                        Integer.parseInt(contents[3]) != 0 ? Integer.parseInt(contents[3]) - 1 :
                                Integer.parseInt(contents[3]),
                        Integer.parseInt(contents[0]),
                        Integer.parseInt(contents[1]));
                emitter.onNext(cropImg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(b -> {
                    if (b != null) {
                        listener.callback(b);
                    }
                });

    }

    public interface CallBack {
        void callback();
    }


    public interface CropListener {
        void callback(Bitmap bitmap);

    }

    private byte[] getByte(Bitmap bitmap) {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes = baos.toByteArray();
            baos.close();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String saveBitmap(Bitmap bmp, String fileName) {
        try {
            if (!new File(Constants.imgPath).exists()) {
                new File(Constants.imgPath).mkdirs();
            }
            Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
            int quality = 100;
            OutputStream stream = null;
            stream = new FileOutputStream(new File(Constants.imgPath + fileName));
            bmp.compress(format, quality, stream);
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bmp.recycle();
        }
//        File file = new File(Constants.imgPath + fileName);
//        if (file.exists()) {
//            return file.renameTo(new File((Constants.imgPath + fileName).replace(".jpg", "krt"))) ? (Constants.imgPath + fileName).replace(".jpg", "krt") : Constants.imgPath + fileName;
//        }
        return Constants.imgPath + fileName;
    }
}
