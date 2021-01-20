package com.wid.applib.bottombar;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.blankj.utilcode.util.LogUtils;
import com.wid.applib.MLib;
import com.wid.applib.R;
import com.wid.applib.base.BaseModuleFragment;
import com.wid.applib.base.Constants;
import com.wid.applib.bean.BottomBean;
import com.wid.applib.bean.SkinIconBean;
import com.wid.applib.config.MProConfig;
import com.wid.applib.skin.SkinManager;
import com.wid.applib.util.CropUtil;
import com.wid.applib.util.Util;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import krt.wid.util.MGlideUtil;
import krt.wid.util.MUtil;
import krt.wid.util.ParseJsonUtil;

/**
 * author:Marcus
 * create on:2019/3/26 11:02
 * description
 */
public class BottomLayout extends LinearLayout {

    private Context context;

    private SViewPager viewPager;

    private MagicIndicator indicator;

    private BottomAdapter bottomAdapter;

    private List<BottomBean.CommonBean.LinksBean> list, allList;

    private int selectPos;

    private List<Fragment> fragments = new ArrayList<>();

    private String fileName;

    private BottomBean bottomBean;

    private String name;

    private String skinName;

    private Class<? extends BaseModuleFragment> clazz;

    private InstanceFragmentImp imp;

    public BottomLayout(Context context, Class<? extends BaseModuleFragment> claz, String name,
                        InstanceFragmentImp instanceFragmentImp) {
        this(context, null, claz, name, instanceFragmentImp);
    }

    public BottomLayout(Context context, @Nullable AttributeSet attrs,
                        Class<? extends BaseModuleFragment> claz, String name
            , InstanceFragmentImp instanceFragmentImp) {
        super(context, attrs);
        this.context = context;
        this.setOrientation(VERTICAL);
        this.name = name;
        this.clazz = claz;
        this.imp = instanceFragmentImp;
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {
        File resFile = new File(Constants.path + name);
        if (!resFile.exists()) {
            Log.w(MLib.TAG, "app底部菜单不存在");
            return;
        }
        String json = Util.getJson(resFile);
        if (TextUtils.isEmpty(json)) {
            Log.w(MLib.TAG, "app底部菜单配置有误");
            return;
        }
        bottomBean = ParseJsonUtil.getBean(json, BottomBean.class);
        skinName = MProConfig.skin_name;
        allList = bottomBean.getCommon().getLinks();
        if (!TextUtils.isEmpty(bottomBean.getCommon().getSkinName())) {
            String[] names = bottomBean.getCommon().getSkinName().split("/");
            fileName = names[names.length - 1];
        }
        try {
            initFragment();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        addViewPager();
        addIndicator();

        initIndicator();
    }

    /**
     * 初始化fragment
     */
    private void initFragment() throws InstantiationException, IllegalAccessException {

        /**
         * 过滤一遍属于本端的菜单
         */
        list = new ArrayList<>();
        for (int i = 0; i < allList.size(); i++) {
            if (allList.get(i).getTerminal() == null) {
                list.add(allList.get(i));
                continue;
            }
            if (allList.get(i).getTerminal().contains(MLib.TERMINAL)) {
                list.add(allList.get(i));
            }
        }

        /**
         * 开始添加fragment
         */
        if (fragments.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isIfSelect()) selectPos = i;
                Fragment fragment = null;
                //源生界面2
                if (list.get(i).isIfModulePage()) {
//                    Class.forName()
                    fragment = clazz.newInstance().setJsonFile(list.get(i).getPageId());
                } else {
                    fragment = imp.instanceFragment(i, list.get(i).getUrl());
                }
                fragments.add(fragment);
            }
        }
    }

    /**
     * 添加viewpager
     */
    private void addViewPager() {
        viewPager = new SViewPager(context);
        viewPager.setId(View.generateViewId());
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
        viewPager.setLayoutParams(lp);
        viewPager.setOffscreenPageLimit(0);
        addView(viewPager);
    }

    /**
     * 添加指示器
     */
    private void addIndicator() {
        indicator = new MagicIndicator(context);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) MUtil.dpTopx(52));
        indicator.setLayoutParams(lp);
        addView(indicator);
    }

    /**
     * 初始化指示器
     */
    private void initIndicator() {
        CommonNavigator commonNavigator = new CommonNavigator(context);
        commonNavigator.setAdjustMode(true);
        bottomAdapter = new BottomAdapter();
        commonNavigator.setAdapter(bottomAdapter);
        indicator.setNavigator(commonNavigator);
        viewPager.setAdapter(new PagerAdapter(((FragmentActivity) context).getSupportFragmentManager()));
        ViewPagerHelper.bind(indicator, viewPager);
    }

    public void setCurrentPage(int index) {
        viewPager.setCurrentItem(index);
    }

    public List<? extends Fragment> getFragments() {
        return fragments;
    }

    private class BottomAdapter extends CommonNavigatorAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public IPagerTitleView getTitleView(final Context context, final int index) {
            CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(context);
            View customLayout = LayoutInflater.from(context).inflate(R.layout.layout_bottom, null);
            final ImageView titleImg = customLayout.findViewById(R.id.img);
            final TextView titleText = customLayout.findViewById(R.id.title);
            titleText.setText(list.get(index).getLabel());
            MGlideUtil.load(context, list.get(index).getOriginImg(), titleImg);
            commonPagerTitleView.setContentView(customLayout);
            commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {

                @Override
                public void onSelected(int index, int totalCount) {
                    if (list.get(index).isSkinIcon()) {
                        if (SkinManager.isLoadSkin()) {
                            SkinIconBean ico = SkinManager.getInstance().getSkinByCode(list.get(index).getSelectSkin());
                            CropUtil.getInstance().cropImg(context, MProConfig.skin_name,
                                    ico.getFileName(), bitmap -> titleImg.setImageBitmap(bitmap));
                        }
                    } else {
                        MGlideUtil.load(context, list.get(index).getSelectImgUrl(), titleImg);
                    }
                    titleText.setTextColor(Util.getRealColor(bottomBean.getCommon().getHighlightTextColor()));
                }

                @Override
                public void onDeselected(int index, int totalCount) {
                    if (list.get(index).isSkinIcon()) {
                        if (SkinManager.isLoadSkin()) {
                            SkinIconBean ico = SkinManager.getInstance().getSkinByCode(list.get(index).getOriginSkin());
                            CropUtil.getInstance().cropImg(context, MProConfig.skin_name,
                                    ico.getFileName(), bitmap -> titleImg.setImageBitmap(bitmap));
                        }
                    } else {
                        MGlideUtil.load(context, list.get(index).getImgUrl(), titleImg);
                    }
                    titleText.setTextColor(Util.getRealColor(bottomBean.getCommon().getOriginTextColor()));
                }

                @Override
                public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
                    titleImg.setScaleX(1.5f + (0.8f - 1.3f) * leavePercent);
                    titleImg.setScaleY(1.5f + (0.8f - 1.3f) * leavePercent);
                }

                @Override
                public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
                    titleImg.setScaleX(0.7f + (1.3f - 0.9f) * enterPercent);
                    titleImg.setScaleY(0.7f + (1.3f - 0.9f) * enterPercent);
                }
            });

            commonPagerTitleView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(index);
                }
            });
            return commonPagerTitleView;
        }

        @Override
        public IPagerIndicator getIndicator(Context context) {
            return null;
        }
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    public Fragment getCurrentFragment() {
        return fragments.get(viewPager.getCurrentItem());
    }

    public interface InstanceFragmentImp {
        public Fragment instanceFragment(int idx, String url);
    }

}
