package com.wid.applib.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.blankj.utilcode.util.Utils;
import com.wid.applib.base.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * author:Marcus
 * create on:2019/3/25 17:03
 * description
 */
public class Util {
    /**
     * Drawable 染色
     *
     * @param drawable 染色对象
     * @param color    颜色
     * @return 染色后的资源
     */
    public static Drawable tinting(Drawable drawable, int color) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        wrappedDrawable.mutate();
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    public static Drawable newDrawable(Drawable drawable) {
        Drawable.ConstantState constantState = drawable.getConstantState();
        return constantState != null ? constantState.newDrawable() : drawable;
    }

    /**
     * 获取colorPrimary的颜色,需要V7包的支持
     *
     * @param context 上下文
     * @return 0xAARRGGBB
     */
    public static int getColorPrimary(Context context) {
        Resources res = context.getResources();
        int attrRes = res.getIdentifier("colorPrimary", "attr", context.getPackageName());
        if (attrRes == 0) {
            return 0xFF009688;
        }
        return ContextCompat.getColor(context, getResourceId(context, attrRes));
    }

    /**
     * 获取自定义属性的资源ID
     *
     * @param context 上下文
     * @param attrRes 自定义属性
     * @return resourceId
     */
    private static int getResourceId(Context context, int attrRes) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrRes, typedValue, true);
        return typedValue.resourceId;
    }

    /**
     * 从asset路径下读取对应文件转String输出
     *
     * @param mContext
     * @return
     */
    public static String getJson(Context mContext, int id) {
        InputStream is = mContext.getResources().openRawResource(id);
        if (is != null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                return sb.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String getJson(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream is = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    public static Bitmap getBitmap(Context context, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        TypedValue value = new TypedValue();
        context.getResources().openRawResource(resId, value);
        options.inTargetDensity = value.density;
        options.inScaled = false;
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }

    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) Utils.getApp().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return Utils.getApp().getResources().getDisplayMetrics().widthPixels;
        }
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.x;
    }

    /**
     * 以750为基准去做兼容性适配
     *
     * @return
     */
    public static int getRealValue(int value) {
        return value * getScreenWidth() / 750;
    }

    /**
     * @return
     */
    public static GradientDrawable getBgDrawable(String color, int shape, float corner) {
        return getBgDrawable(color, shape, new float[]{corner}, 0, "");
    }

    public static GradientDrawable getBgDrawable(String color, int shape, float[] corner) {
        return getBgDrawable(color, shape, corner, 0, "");
    }

    public static GradientDrawable getBgDrawable(String color, int shape, float corner, int stroke, String strokeColor) {
        return getBgDrawable(color, shape, new float[]{corner}, stroke, strokeColor);
    }

    /**
     * @return
     */
    private static GradientDrawable getBgDrawable(String color, int shape, float[] corner, int stroke, String strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        if (TextUtils.isEmpty(color)) color = "#00ffffff";
        drawable.setColor(getRealColor(color));
        drawable.setShape(shape);
        if (corner.length == 1) {
            if (corner[0] != 0) {
                drawable.setCornerRadius(corner[0]);
            }
        } else {
            drawable.setCornerRadii(corner);
        }
        if (stroke != 0 && stroke != -1 && !TextUtils.isEmpty(strokeColor)) {
            drawable.setStroke(stroke, getRealColor(strokeColor));
        }
        return drawable;
    }

    /**
     * rgba 转 16进制
     *
     * @param rgba 格式：rgbargba(142, 138, 138, 1)
     * @return
     */
    private static int rgba2HexString(String rgba) {
        String[] ints = rgba.split("\\(")[1].split("\\)")[0].split(",");
        return Color.argb((int) (Float.parseFloat(ints[3].trim()) * 255),
                Integer.parseInt(ints[0].trim()),
                Integer.parseInt(ints[1].trim()),
                Integer.parseInt(ints[2].trim()));

    }

    /**
     * 获取真正的颜色值
     *
     * @param color
     * @return
     */
    public static int getRealColor(String color) {
        if (TextUtils.isEmpty(color)) {
            return Color.WHITE;
        }
        if (color.contains("#")) {
            return Color.parseColor(color);
        } else if (color.contains("rgb")) {
            return rgba2HexString(color);
        } else {
            return Integer.parseInt(color);
        }
    }

    public static void getAssestsImage(Context context, String name) {

        File nomedia = new File(Constants.basePath, ".nomedia");
        try {
            if (!nomedia.exists()) nomedia.createNewFile();
        } catch (IOException e) {
            Log.e("IOException", "exception in createNewFile() method");
        }

        Bitmap bitmap = null;
        AssetManager assetManager = context.getAssets();
        try {
            //filename是assets目录下的图片名
            InputStream inputStream = assetManager.open(name);
            bitmap = BitmapFactory.decodeStream(inputStream);
            if (!new File(Constants.imgPath).exists()) {
                new File(Constants.imgPath).mkdirs();
            }
            String[] fileDir = name.split("/");
            saveBitmap(bitmap, Constants.path + fileDir[fileDir.length - 1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getAssestsJson(Context context, String name) {

        AssetManager assetManager = context.getAssets();
        try {
            //filename是assets目录下的图片名
            InputStream inputStream = assetManager.open(name);
            if (!new File(Constants.path).exists()) {
                new File(Constants.path).mkdirs();
            }
            String[] fileDir = name.split("/");
            saveJson(Constants.path + fileDir[fileDir.length - 1], inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveJson(String destination, InputStream input)
            throws IOException {
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream downloadFile = new FileOutputStream(destination);
        while ((index = input.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
            downloadFile.flush();
        }
        downloadFile.close();
        input.close();
    }


    private static boolean saveBitmap(Bitmap bmp, String path) {
        File f = new File(path);
        try {
            f.createNewFile();
            FileOutputStream fOut = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }

}
