package com.wid.applib.base;

import android.Manifest;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.jaeger.library.StatusBarUtil;
import com.wid.applib.R;
import com.wid.applib.bottombar.BottomLayout;
import com.wid.applib.config.MProConfig;
import com.wid.applib.event.BroadCastMessageWarp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import krt.wid.util.MPermissions;

/**
 * @author hyj
 * @time 2020/8/19 11:25
 * @class describe
 */
public abstract class BaseModuleMainActivity extends AppCompatActivity implements BottomLayout.InstanceFragmentImp {

    private BottomLayout bottomLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeBindLayout();
        setContentView(R.layout.layout_frame);
        StatusBarUtil.setTranslucentForImageView(this, 20, null);
        EventBus.getDefault().register(this);
        initViews();
        init();
    }

    protected abstract void beforeBindLayout();

    protected abstract void init();

    private void initViews() {

        FrameLayout frameLayout = findViewById(R.id.content);

        bottomLayout = new BottomLayout(this, MProConfig.getInstance().getFragmentClz(),
                MProConfig.btx_json_name + ".json", this);
        frameLayout.addView(bottomLayout);
        MPermissions.getInstance().request(this,
                getApplyPermissions()
                , value -> {

                });
    }

    /**
     * 申请界面需要的权限
     * 对SD卡读写权限已在缓存Asset目录时申请
     *
     * @return
     */
    protected String[] getApplyPermissions() {
        return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
    }

    public BaseModuleFragment getFragmentByCid(String cid) {
        BaseModuleFragment module = null;
        for (Fragment fragment : bottomLayout.getFragments()) {
            if (fragment instanceof BaseModuleFragment) {
                module = (BaseModuleFragment) fragment;
                if (module.getPageId().equals(cid)) return module;
            }
        }
        return module;
    }

    /**
     * 如果不在基类进行注册
     *
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveBroadCast(BroadCastMessageWarp bean) {
        try {
            if (bean.getBroadCastName().equals("tabChange")) {
                int index = Integer.parseInt(bean.getParams().get("index"));
                page2Index(index);
            }
        } catch (Exception e) {

        }
    }

    public void page2Index(int index) {
        bottomLayout.setCurrentPage(index);
    }
}
