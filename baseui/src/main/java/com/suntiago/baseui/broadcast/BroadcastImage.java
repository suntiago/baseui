package com.suntiago.baseui.broadcast;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.suntiago.baseui.R;
import com.suntiago.baseui.utils.DensityUtil;
import com.suntiago.baseui.utils.log.Slog;

/**
 * Created by zy on 2019/1/29.
 * 图片轮播
 */

public class BroadcastImage implements BroadcastViewItem<BroadcastData> {

  private final String TAG = getClass().getSimpleName();
  Context mContext = null;
  ImageView mImageView;

  TextView tvTitle;
  TextView tvContent;
  View view;
  boolean isDestory = false;

  public BroadcastImage(Context context) {
    mContext = context;
  }

  @Override
  public View bindView(BroadcastData data) {
    if (isDestory) {
      return view;
    }
    Slog.d(TAG, "bindView  [data]:");
    Glide.with(mContext).load(data.image_url_path)
        .into(mImageView);
    int w = view.getWidth();
//    if (data.maxwidthpercent != 0) {
//      tvTitle.setWidth((int) (w * (data.maxwidthpercent / 100f)));
//      tvContent.setWidth((int) (w * (data.maxwidthpercent / 100f)));
//    }
    if (TextUtils.isEmpty(data.tvtitle)) {
      tvTitle.setVisibility(View.GONE);
      tvContent.setVisibility(View.GONE);
    } else {
      tvTitle.setVisibility(View.VISIBLE);
      tvTitle.setText(data.tvtitle);
      try {
        tvTitle.setTextColor(Color.parseColor(data.tvtitleColor));
        tvTitle.setTextSize(DensityUtil.px2dip(mContext, Integer.parseInt(data.tvtitleSize)));
      } catch (NumberFormatException e) {
        e.printStackTrace();
      }
      if (!TextUtils.isEmpty(data.tvcontent)) {
        tvContent.setVisibility(View.VISIBLE);
        tvContent.setText(data.tvcontent);
        try {
          tvContent.setTextColor(Color.parseColor(data.tvcontColor));
          tvContent.setTextSize(DensityUtil.px2dip(mContext, Integer.parseInt(data.tvcontSize)));
        } catch (NumberFormatException e) {
          e.printStackTrace();
        }
      } else {
        tvContent.setVisibility(View.GONE);
      }
    }
    return view;
  }

  @Override
  public View createView() {
    Slog.d(TAG, "createView  []:");
    view = LayoutInflater.from(mContext).inflate(R.layout.broadcast__image_item, null);
    view.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
    mImageView = (ImageView) view.findViewById(R.id.iv_item_broadcast);

    view.setTag(R.id.tag_first, this);
    mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
    tvTitle = (TextView) view.findViewById(R.id.tv_title_item_broadcast);
    tvContent = (TextView) view.findViewById(R.id.tv_content_item_broadcast);
    return view;
  }

  @Override
  public View getView() {
    return view;
  }

  @Override
  public void start() {
    Slog.d(TAG, "start  []:");
    view.setVisibility(View.VISIBLE);
  }

  @Override
  public void resume() {
    Slog.d(TAG, "resume  []:");
  }

  @Override
  public void pause() {
    Slog.d(TAG, "pause  []:");
  }

  @Override
  public void stop() {
    Slog.d(TAG, "stop  []:");
    view.setVisibility(View.INVISIBLE);
  }

  @Override
  public void destoryView() {
    Slog.d(TAG, "destoryView  []:");
    isDestory = true;
  }

  @Override
  public void setBroadcastProgress(BroadcastProgress progress) {

  }

  public void showdefault() {
    if (mContext != null && !((Activity) mContext).isFinishing()) {
      Glide.with(mContext).load(R.mipmap.ic_defaults).into(mImageView);
    }
    tvTitle.setVisibility(View.GONE);
    tvContent.setVisibility(View.GONE);
    start();
    resume();
  }
}
