package com.krt.wid.resolver.web;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.widget.LinearLayout;

import com.krt.wid.resolver.App;
import com.krt.wid.resolver.R;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import krt.wid.base.MBaseFragment;

/**
 * @author Marcus
 * @package com.krt.zhdn.fragment
 * @description
 * @time 2018/11/3
 */
public class X5WebFragment extends MBaseFragment {

    X5WebView mWebView;
    LinearLayout invalidLayout;
    private String url;

    public static final int REQUEST_CODE_CAMERA = 1001;
    public static final int REQUEST_CODE_BUSINESS_LICENSE = 1002;
    public static final int REQUEST_CODE_FACE = 1003;

    private String jsName = "";

    /**
     * js定位名称
     */
    private String localName = "";

    private boolean isLoader = false;

    private WebSettings webSettings;

    private String uploadUrl;

    private String token;


    private boolean needReresh = false;

    private boolean showTitle = false;

    private App mApp;

    public X5WebFragment setUrl(String url) {
        this.url = url;
        return this;
    }

    public X5WebFragment setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
        return this;
    }

    public X5WebFragment setNeedRresh(boolean needReresh) {
        this.needReresh = needReresh;
        return this;
    }

    public X5WebView getWebView() {
        return mWebView;
    }

    @Override
    public void bindButterKnife(View view) {

    }

    @Override
    public void unBindButterKnife() {

    }

    @Override
    public void init() {

    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_web_x5;
    }

    @Override
    public void initView(View view) {
        mApp = (App) getActivity().getApplication();
        mWebView = view.findViewById(R.id.web);
        invalidLayout = view.findViewById(R.id.invalidLayout);
        initWebSetting();
        mWebView.loadUrl(url);
    }


    private void initWebSetting() {
        if (url == null || url.equals("")) return;
        if (url.startsWith("http://") || url.startsWith("https://")) {
            webSettings = mWebView.getSetting();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webSettings.setMixedContentMode(2);
            }
            mWebView.setVisibility(View.VISIBLE);
            invalidLayout.setVisibility(View.GONE);
            mWebView.setClickable(true);
            mWebView.setDisplayZoomControls(false);
            mWebView.setBuiltInZoomControls(false);
            mWebView.setJavaScriptEnabled(true);
            mWebView.setDomStorageEnabled(true);
            mWebView.setJavaScriptCanOpenWindowsAutomatically(true);
            mWebView.setCacheMode(WebSettings.LOAD_DEFAULT);
            mWebView.setLoadWithOverviewMode(true);
            mWebView.setUseWideViewPort(true);
            mWebView.setAllowFileAccess(true);
            mWebView.addJavascriptInterface(this, "MyJS");
            mWebView.addJavascriptInterface(this, "BaseJS");
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        view.loadUrl(url);
                    } else {
                        try {
                            Uri uri = Uri.parse(url);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }

                public void onReceivedSslError(WebView view,
                                               SslErrorHandler handler, SslError error) {
                    handler.proceed();
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }
            });
        } else {
            mWebView.setVisibility(View.GONE);
            invalidLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWebView != null && needReresh) {
            mWebView.reload();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.CanGoBack()) {
                mWebView.GoBack();
            } else {
                ((Activity) mContext).finish();
            }
        }
        return true;
    }


    @Override
    public void loadData() {
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isLoader = false;
    }

    @Override
    public void onDestroy() {
        try {
            mWebView.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

}
