package com.krt.wid.resolver.module;

import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.krt.wid.resolver.R;
import com.wid.applib.base.BaseInitLoadActivity;
import com.wid.applib.bean.MVersionBean;
import com.wid.applib.config.MProConfig;
import com.wid.applib.manager.MLoader;


/**
 * author: MaGua
 * create on:2020/10/27 9:56
 * description
 */
public class InitLoadActivity extends BaseInitLoadActivity implements MLoader.OnLoaderListener {

    private TextView tv_res, tv_size;
    private ProgressBar bar;

    String krtCode, krtVer;

    @Override
    protected void beforeBindLayout() {

    }

    @Override
    protected void initView() {

        krtCode = getIntent().getStringExtra("krtCode");
        krtVer = getIntent().getStringExtra("krtVer");
        MProConfig.build()
                .setKrtCode(krtCode)
                .setFragmentClz(BaseFragment.class)
                .generate();

        new MLoader.Builder(this,krtCode)
                .setVer(krtVer)
                .generate(this);
    }

    @Override
    protected void init() {
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_init;
    }

    @Override
    public TextView resourceNameView() {
        if (tv_res == null)
            tv_res = findViewById(R.id.tv_name);
        return tv_res;
    }

    @Override
    public TextView resourceDownSizeView() {
        if (tv_size == null)
            tv_size = findViewById(R.id.tv_size);
        return tv_size;
    }

    @Override
    public ProgressBar resourceDownProgressBar() {
        if (bar == null) {
            bar = findViewById(R.id.progress);
            bar.setMax(100);
        }
        return bar;
    }

    @Override
    public void gotoMainActivity() {

        switch (appInfoBean.getStartType()) {
            case "tabbar":
                startActivity(new Intent(InitLoadActivity.this, BaseMainActivity.class));
                break;
            case "singlePage":
                startActivity(new Intent(InitLoadActivity.this, BaseActivity.class)
                        .putExtra("name", appInfoBean.getStartPageId()));
                break;
        }


        finish();
    }

    @Override
    public MVersionBean.VersionInfoBean onGotVersionFail() {
        return null;
    }

    @Override
    public void onApiComplete() {

    }

    @Override
    public void onFinish() {
        gotoMainActivity();
    }
}
