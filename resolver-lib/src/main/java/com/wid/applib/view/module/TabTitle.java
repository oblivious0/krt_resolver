package com.wid.applib.view.module;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.wid.applib.R;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.bottombar.SViewPager;
import com.wid.applib.config.MProConfig;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

import krt.wid.util.ParseJsonUtil;

/**
 * author:Marcus
 * create on:2020/7/7 16:33
 * description
 */
public class TabTitle extends BaseWidget {

    private List<String> mTitles = new ArrayList<>();

    public TabTitle(Context context, Object object) {
        super(context, object);
    }

    public TabTitle(ContextImp context, Object object) {
        super(context, object);
    }

    @Override
    public View generate() {
        final BaseLayoutBean bean = ParseJsonUtil.getBean(ParseJsonUtil.toJson(object), BaseLayoutBean.class);
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(lp);
        LinearLayout.LayoutParams indicatorlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, Util.getRealValue(bean.getStyle().getTabHeight()));
        MagicIndicator magicIndicator = new MagicIndicator(getContext());
        magicIndicator.setLayoutParams(indicatorlp);
        linearLayout.addView(magicIndicator);
        LinearLayout.LayoutParams viewpagerlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final SViewPager viewPager = new SViewPager(getContext());
        PagerAdapter adapter = new PagerAdapter(((AppCompatActivity) getContext()).getSupportFragmentManager());
        for (int i = 0; i < bean.getCommon().getLinks().size(); i++) {
            mTitles.add(bean.getCommon().getLinks().get(i).getText());
            try {
                adapter.addFragment(MProConfig.getInstance().getFragmentClz().newInstance().setJsonFile(
                        bean.getCommon().getLinks().get(i).getPageId()
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        viewPager.setCanScroll(false);
        viewPager.setId(View.generateViewId());
        viewPager.setOffscreenPageLimit(bean.getCommon().getLinks().size());
        viewPager.setAdapter(adapter);
        viewPager.setLayoutParams(viewpagerlp);
        linearLayout.addView(viewPager);
        CommonNavigator commonNavigator = new CommonNavigator(getContext());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitles.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int i) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mTitles.get(i));
                simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_14));
                simplePagerTitleView.setGravity(Gravity.CENTER);
                simplePagerTitleView.setNormalColor(Util.getRealColor(bean.getStyle().getTextColor()));
                simplePagerTitleView.setSelectedColor(Util.getRealColor(bean.getStyle().getSelectTextColor()));
                simplePagerTitleView.setOnClickListener(v -> {
                    viewPager.setCurrentItem(i);
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setLineHeight(UIUtil.dip2px(context, 3));
                indicator.setLineWidth(UIUtil.dip2px(context, 50));
                indicator.setRoundRadius(UIUtil.dip2px(context, 3));
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(Util.getRealColor(bean.getStyle().getIndicatorColor()));
                return indicator;
            }
        });
        commonNavigator.setAdjustMode(false);
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
        return linearLayout;
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();

        public PagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        public void addFragment(Fragment fragment) {
            fragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        }
    }
}
