package com.krt.wid.resolver.module;

import androidx.fragment.app.Fragment;

import com.krt.wid.resolver.TestFragment;
import com.wid.applib.base.BaseModuleMainActivity;

/**
 * author: MaGua
 * create on:2020/10/27 9:05
 * description
 */
public class BaseMainActivity extends BaseModuleMainActivity {

    @Override
    protected void beforeBindLayout() {

    }

    @Override
    protected void init() {

    }

    @Override
    public Fragment instanceFragment(int idx, String url) {
        return new TestFragment();
    }
}
