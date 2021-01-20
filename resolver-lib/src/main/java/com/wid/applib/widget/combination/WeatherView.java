package com.wid.applib.widget.combination;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.LogUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.tool.ModuleViewFactory;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;
import com.wid.applib.widget.BaseView;

import java.util.ArrayList;
import java.util.List;

import krt.wid.http.MCallBack;
import krt.wid.util.MPermissions;
import krt.wid.util.MToast;
import krt.wid.util.ParseJsonUtil;

/**
 * author: MaGua
 * create on:2021/1/8 15:27
 * description
 */
public class WeatherView extends BaseView<FrameLayout> {

    public List<BaseView> childViews;
    double lat;
    double lng;

    public WeatherView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public WeatherView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected void initView() {
        type = "weather";
        childViews = new ArrayList<>();

        view = new FrameLayout(contextImp.getContext());

        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        view.setLayoutParams(lp);
        view.setVisibility(bean.getCommon().isIsHidden() ? View.GONE : View.VISIBLE);

        if (bean.getChildren() != null && !bean.getChildren().isEmpty()) {
            ModuleViewFactory.createViews(bean.getChildren(), contextImp, view, childViews, true);
        }
    }

    @Override
    protected boolean bindInNewThread() {
        return true;
    }

    @Override
    protected void bindInMainThread() {
        MPermissions.getInstance().request((FragmentActivity) contextImp.getContext(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION}
                , value -> {
                    if (value) {
                        String serviceString = Context.LOCATION_SERVICE;
                        LocationManager locationManager =
                                (LocationManager) contextImp.getContext().getSystemService(serviceString);
                        String provider = LocationManager.GPS_PROVIDER;
                        @SuppressLint("MissingPermission")
                        Location location = locationManager.getLastKnownLocation(provider);
                        lat = location.getLatitude();//获取纬度
                        lng = location.getLongitude();//获取经度
                        showWeather("");
                    } else {
                        MToast.showToast(contextImp.getContext(), "请在设置配置GPS定位权限");
                    }
                });
    }

    public void showWeather(String code) {
        if (TextUtils.isEmpty(code)) {
            OkGo.get("https://restapi.amap.com/v3/geocode/regeo?key=2be33124f87ba1f30d7d7a9ea462ed56&location=" + lng + "," + lat)
//                    .params("key", "2be33124f87ba1f30d7d7a9ea462ed56")
//                    .params("location", lat + "," + lng)
                    .execute(new MCallBack<Object>((Activity) contextImp.getContext()) {
                        @Override
                        public void onSuccess(Response<Object> response) {
                            String result = ParseJsonUtil.toJson(response.body());
                            if (!ParseJsonUtil.getStringByKey(result, "infocode").equals("10000")) {
                                MToast.showToast(contextImp.getContext(), "定位获取失败");
                                return;
                            }

                            String regeocode = ParseJsonUtil.getStringByKey(result, "regeocode");
                            String addressComponent = ParseJsonUtil.getStringByKey(regeocode, "addressComponent");
                            String adcode = ParseJsonUtil.getStringByKey(addressComponent, "adcode");

                            showWeather(adcode);

                        }
                    });
        } else {
            OkGo.get("https://restapi.amap.com/v3/weather/weatherInfo")
                    .params("city", code)
                    .params("key", "2be33124f87ba1f30d7d7a9ea462ed56")
                    .execute(new MCallBack<Object>((Activity) contextImp.getContext()) {
                        @Override
                        public void onSuccess(Response<Object> response) {
                            String result = ParseJsonUtil.toJson(response.body());
                            if (!ParseJsonUtil.getStringByKey(result, "infocode").equals("10000")) {
                                MToast.showToast(contextImp.getContext(), "天气信息获取失败");
                                return;
                            }

                            String weather = ParseJsonUtil.toJson(
                                    ParseJsonUtil.getBeanList(
                                            ParseJsonUtil.getStringByKey(result, "lives"),
                                            Object.class).get(0));

                            bindData(bean.getCommon().getCityNameId(), "city", ParseJsonUtil.getStringByKey(weather, "city"));
                            bindData(bean.getCommon().getWeatherTextId(), "weather", ParseJsonUtil.getStringByKey(weather, "weather"));
                            bindData(bean.getCommon().getTemperatureId(), "temperature", ParseJsonUtil.getStringByKey(weather, "temperature"));
                            bindData(bean.getCommon().getIconId(), "weatherIco", ParseJsonUtil.getStringByKey(weather, "weather"));
                        }
                    });
        }

    }


    @Override
    public void bindData(String cid, String key, String val) {
        if (childViews.size() != 0) {
            for (BaseView baseV : childViews) {
                switch (key) {
                    case "temperature":
                    case "city":
                    case "weather":
                        baseV.bindData(cid, "text", "temperature".equals(key) ? val + "°C" : val);
                        break;
                    case "weatherIco":
                        baseV.bindData(cid, "src", getWeatherIco(val));
                        break;
                    default:
                }
            }
        }
    }

    private String getWeatherIco(String weather) {
        switch (weather) {
            case "晴":
                return "http://www.krtimg.com/group1/M00/04/FD/rAA0Kl_z13KAI1J9AAAKkDQKnSg133.png";
            case "云":
                return "http://www.krtimg.com/group1/M00/05/28/rAA0KV_z13KAGszZAAAJTMx92NE395.png";
            case "晴云":
                return "http://www.krtimg.com/group1/M00/04/FD/rAA0Kl_z13KACyG3AAAKtdnwZi4352.png";
            case "阴":
                return "http://www.krtimg.com/group1/M00/04/FD/rAA0Kl_z12SAKjVwAAAKKhh3el4891.png";
            case "雨":
                return "http://www.krtimg.com/group1/M00/05/28/rAA0KV_z12SAQq0_AAAJ8CRIYq0166.png";
            case "雪":
                return "http://www.krtimg.com/group1/M00/05/28/rAA0KV_z12SAe77SAAAKsupO86c622.png";
        }

        return "";

    }
}
