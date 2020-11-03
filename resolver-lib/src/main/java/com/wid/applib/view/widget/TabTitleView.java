package com.wid.applib.view.widget;

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

/**
 * author: MaGua
 * create on:2020/11/3 14:53
 * description
 */
public class TabTitleView extends BaseView<LinearLayout> {

    private List<String> mTitles = new ArrayList<>();

    public TabTitleView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public TabTitleView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected boolean bindInNewThread() {
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        LinearLayout linearLayout = new LinearLayout(contextImp.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(lp);
        LinearLayout.LayoutParams indicatorlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, Util.getRealValue(bean.getStyle().getTabHeight()));
        MagicIndicator magicIndicator = new MagicIndicator(contextImp.getContext());
        magicIndicator.setLayoutParams(indicatorlp);
        linearLayout.addView(magicIndicator);
        LinearLayout.LayoutParams viewpagerlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final SViewPager viewPager = new SViewPager(contextImp.getContext());
        PagerAdapter adapter = new PagerAdapter(((AppCompatActivity) contextImp.getContext()).getSupportFragmentManager());
        for (int i = 0; i < bean.getCommon().getLinks().size(); i++) {
            mTitles.add(bean.getCommon().getLinks().get(i).getText());
            try {
                adapter.addFragment(MProConfig.getInstance().getFragmentClz().newInstance().setJsonFile(
                        bean.getCommon().getLinks().get(i).getPageId()
                ));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        viewPager.setCanScroll(false);
        viewPager.setId(View.generateViewId());
        viewPager.setOffscreenPageLimit(bean.getCommon().getLinks().size());
        viewPager.setAdapter(adapter);
        viewPager.setLayoutParams(viewpagerlp);
        linearLayout.addView(viewPager);
        CommonNavigator commonNavigator = new CommonNavigator(contextImp.getContext());
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
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
        return true;
    }

    @Override
    protected void bindInMainThread() {

    }

    @Override
    public void bindData(String key, String val) {

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
