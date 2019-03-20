package com.suntiago.baseui.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.suntiago.baseui.R;
import com.suntiago.baseui.utils.log.Slog;


/**
 * Created by zy on 2019/1/29.
 * url 轮播， 已webView的方式展现
 */

public class BroadcastUrl implements BroadcastViewItem<BroadcastData> {
  private final String TAG = getClass().getSimpleName();
  WebView mWebView;

  Context mContext;
  WifiReceiver wifiReceiver;

  public BroadcastUrl(Context context) {
    mContext = context;
  }

  private void initWeb() {
    Slog.d(TAG, "initWeb []:");
    mWebView = new WebView(mContext);
    mWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));
    WebSettings settings = mWebView.getSettings();
    settings.setJavaScriptEnabled(true);

    mWebView.getSettings().setBlockNetworkImage(false);
    mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
    mWebView.getSettings().setDomStorageEnabled(true);
    mWebView.getSettings().setUseWideViewPort(true);
    mWebView.getSettings().setAllowFileAccess(true);
    mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
    mWebView.setWebChromeClient(new WebChromeClient() {
      @Override
      public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        Slog.d(TAG, "onReceivedTitle [view, title]:" + title);
        //171016 处理404错误 android 6.0 以下通过title获取
        mWebView.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
          if (title.contains("404")
              || title.contains("500")
              || title.contains("Error")
              || title.contains("无法打开")
              || title.contains("找不到")
              || title.contains("not found")
              || title.contains("超时")
              || title.contains("about:blank")) {
            Slog.d(TAG, "onReceivedTitle [view, title]:error");
            mWebView.setVisibility(View.GONE);
          }
        }
        if (!wifiReceiver.getWifiConnected()) {
          mWebView.setVisibility(View.GONE);
        }
      }
    });
    mWebView.getSettings().setLoadWithOverviewMode(true);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    }
    mWebView.setWebViewClient(new WebViewClient() {

      @Override
      public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed(); // 接受网站证书
      }

      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //该方法在Build.VERSION_CODES.LOLLIPOP以前有效，从Build.VERSION_CODES.LOLLIPOP起，建议使用shouldOverrideUrlLoading(WebView, WebResourceRequest)} instead
        //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址），均交给webView自己处理，这也是此方法的默认处理
        //返回true，说明你自己想根据url，做新的跳转，比如在判断url符合条件的情况下，我想让webView加载http://ask.csdn.net/questions/178242
        return false;
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        Slog.d(TAG, "onPageFinished");
        mWebView.setVisibility(View.VISIBLE);
        //加载完成
        if (!wifiReceiver.getWifiConnected()) {
          mWebView.setVisibility(View.GONE);
        }
      }

      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Slog.e(TAG, "onPageStarted");
      }

      @Override
      public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        Slog.e(TAG, "onReceivedError：" + error.toString());
        //加载失败
        mWebView.setVisibility(View.GONE);
      }

      @Override
      public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Slog.e(TAG, "onReceivedError");
        //加载失败
        mWebView.setVisibility(View.GONE);
      }

      @Override
      public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址），均交给webView自己处理，这也是此方法的默认处理
        //返回true，说明你自己想根据url，做新的跳转，比如在判断url符合条件的情况下，我想让webView加载http://ask.csdn.net/questions/178242
        return false;
      }
    });
    mWebView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        return true;
      }
    });
  }

  @Override
  public View bindView(BroadcastData data) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    }
    mWebView.loadUrl(data.image_url);
    return mWebView;
  }

  @Override
  public View createView() {
    wifiReceiver = new WifiReceiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
    filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    mContext.registerReceiver(wifiReceiver, filter);

    initWeb();
    mWebView.setTag(R.id.tag_first, this);
    return mWebView;
  }

  @Override
  public View getView() {
    return mWebView;
  }

  @Override
  public void start() {
    mWebView.setVisibility(View.VISIBLE);
  }

  @Override
  public void resume() {

  }

  @Override
  public void pause() {

  }

  @Override
  public void stop() {
    mWebView.setVisibility(View.GONE);
  }

  @Override
  public void destoryView() {
    mContext.unregisterReceiver(wifiReceiver);
  }

  @Override
  public void setBroadcastProgress(BroadcastProgress progress) {

  }

  class WifiReceiver extends BroadcastReceiver {
    private static final String TAG = "wifiReceiver";

    private boolean isWifiConnected = false;

    public boolean getWifiConnected() {
      return isWifiConnected;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
      //wifi连接上与否
      if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
          Slog.i(TAG, "WifiReceiver:wifi断开");
          isWifiConnected = false;

        } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
          WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
          WifiInfo wifiInfo = wifiManager.getConnectionInfo();
          //获取当前wifi名称
          Slog.i(TAG, "WifiReceiver:连接到网络 " + wifiInfo.getSSID());
          isWifiConnected = true;
        }
      }
      //wifi打开与否
      if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
        int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
        if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
          Slog.i(TAG, "WifiReceiver:系统关闭wifi");
          isWifiConnected = false;
        } else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
          Slog.i(TAG, "WifiReceiver:系统开启wifi");
        }
      }
    }
  }

}
