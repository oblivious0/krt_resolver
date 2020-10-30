package com.wid.applib.util;


import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wid.applib.R;
import com.wid.applib.bean.ListBean;

import java.util.List;

import krt.wid.util.MGlideUtil;

/**
 * author:Marcus
 * create on:2019/11/14 14:28
 * description 各个小adpter合集
 */
public class AdapterUtil {
    public static class ListMenusAdapter extends BaseQuickAdapter<ListBean, BaseViewHolder> {

        public ListMenusAdapter(@Nullable List<ListBean> data) {
            super(R.layout.item_listmenu_child, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, ListBean item) {
            helper.setText(R.id.text, item.getText());
            MGlideUtil.load(mContext, item.getIcon(), (ImageView) helper.getView(R.id.img));
        }
    }
}
