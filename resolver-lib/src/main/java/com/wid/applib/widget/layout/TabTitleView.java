package com.wid.applib.widget.layout;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.wid.applib.widget.BaseView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

import krt.wid.util.MGlideUtil;

/**
 * author: MaGua
 * create on:2020/11/3 14:53
 * description
 */
public class TabTitleView extends BaseView<LinearLayout> {

    private List<String> mTitles;
    private MagicIndicator magicIndicator;
    private SViewPager viewPager;

    public TabTitleView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public TabTitleView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected void initView() {
        type = "tab";
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        view = new LinearLayout(contextImp.getContext());
        view.setOrientation(LinearLayout.VERTICAL);
        view.setLayoutParams(lp);
        LinearLayout.LayoutParams indicatorlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, Util.getRealValue(bean.getStyle().getTabHeight()));
        magicIndicator = new MagicIndicator(contextImp.getContext());
        if(bean.getStyle().isCenter()) {
            indicatorlp.gravity = Gravity.CENTER_HORIZONTAL;
        }
        magicIndicator.setLayoutParams(indicatorlp);
        viewPager = new SViewPager(contextImp.getContext());
        LinearLayout.LayoutParams viewpagerlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        viewPager.setCanScroll(false);
        viewPager.setId(View.generateViewId());
        viewPager.setOffscreenPageLimit(bean.getCommon().getLinks().size());
        viewPager.setLayoutParams(viewpagerlp);
        PagerAdapter adapter = new PagerAdapter(((AppCompatActivity) contextImp.getContext()).getSupportFragmentManager());

        mTitles = new ArrayList<>();
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

        CommonNavigator commonNavigator = new CommonNavigator(contextImp.getContext());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitles.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int i) {
//                if(bean.getStyle().getIndicatorImg()==null) {
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
//                }else{
//                    return showIndicatorImg( context,i);
//                }
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                if(bean.getStyle().getIndicatorImg()==null) {
                    LinePagerIndicator indicator = new LinePagerIndicator(context);
                    indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                    indicator.setLineHeight(UIUtil.dip2px(context, 3));
                    indicator.setLineWidth(UIUtil.dip2px(context, 50));
                    indicator.setRoundRadius(UIUtil.dip2px(context, 3));
                    indicator.setStartInterpolator(new AccelerateInterpolator());
                    indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                    indicator.setColors(Util.getRealColor(bean.getStyle().getIndicatorColor()));
                    return indicator;
                }else{
                    return null;
                }
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        viewPager.setAdapter(adapter);
        view.addView(magicIndicator);
        view.addView(viewPager);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    public CommonPagerTitleView showIndicatorImg(Context context, final int i){
        CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(contextImp.getContext());
        commonPagerTitleView.setPadding(30, 0, 30, 0);

        // load custom layout
        View customLayout = LayoutInflater.from(contextImp.getContext()).inflate(R.layout.title_viewpage, null);
        final TextView name = customLayout.findViewById(R.id.name);
        final ImageView line = customLayout.findViewById(R.id.line);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(bean.getStyle().getTabItemWidth(), bean.getStyle().getTabHeight());
        customLayout.setLayoutParams(lp);
        name.setText(mTitles.get(i));
        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, Util.getRealValue(bean.getStyle().getTextFont()));
        line.setLayoutParams(new LinearLayout.LayoutParams(bean.getStyle().getIndicatorWidth(),bean.getStyle().getIndicatorHeight()));
        line.setScaleType(ImageView.ScaleType.FIT_XY);
        MGlideUtil.load(contextImp.getContext(), bean.getStyle().getIndicatorImg(), line);
        commonPagerTitleView.setContentView(customLayout);
        commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {
            @Override
            public void onSelected(int index, int totalCount) {

            }

            @Override
            public void onDeselected(int index, int totalCount) {

            }

            @Override
            public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
                name.setTextColor(Util.getRealColor(bean.getStyle().getTextColor()));
                name.setTextSize(TypedValue.COMPLEX_UNIT_PX, Util.getRealValue(bean.getStyle().getTextFont()));
                line.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
                name.setTextColor(Util.getRealColor(bean.getStyle().getSelectTextColor()));
                name.setTextSize(TypedValue.COMPLEX_UNIT_PX, Util.getRealValue(bean.getStyle().getSelectTextFont()));
                line.setVisibility(View.VISIBLE);

            }
        });
        commonPagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(i);
            }
        });


        return commonPagerTitleView;
    }

    @Override
    protected boolean bindInNewThread() {
        return true;
    }

    @Override
    protected void bindInMainThread() {

    }

    @Override
    public void bindData(String cid, String key, String val) {

    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();

        public PagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_SET_USER_VISIBLE_HINT);
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
            container.removeView((View) object);
        }
    }
}
