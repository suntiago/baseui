package com.suntiago.baseui.broadcast;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.suntiago.baseui.R;
import com.suntiago.baseui.utils.log.Slog;
import com.suntiago.network.network.download.DataChanger;
import com.suntiago.network.network.download.DataWatcher;
import com.suntiago.network.network.download.DownloadEntry;
import com.suntiago.network.network.download.DownloadManager;

import org.kymjs.kjframe.KJDB;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by zy on 2019/1/29.
 *
 * 需要调用的接口
 * activityResume  需要调用，涉及视频，web等的生命周期切换
 * destory  需要调用，销毁资源
 * refreshData 更新界面数据
 */

public class BroadcastView extends FrameLayout {
  private final String TAG = getClass().getSimpleName();
  final static int MSG_PLAY_NEXT = 100001;
  //轮播数据
  List<BroadcastData> mBroadcastDataList;
  //加载完成之后再显示
  boolean showDownloaded = true;
  //是否需要缓存数据
  boolean enableCache = true;

  Handler mHandler = null;

  Context mContext;
  BroadcastData broadcastDataCurrent = null;

  BroadcastImage mBroadcastImage;
  BroadcastVideo mBroadcastVideo;
  BroadcastUrl mBroadcastUrl;

  public BroadcastView(Context context) {
    super(context);
    aBroadcastView(context);
  }

  public BroadcastView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    aBroadcastView(context);
  }

  public BroadcastView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    aBroadcastView(context);
  }

  /*activity 状态变化，内容状态也会跟着变化*/
  public void activityResume(boolean resume) {
    if (resume && broadcastDataCurrent != null) {
      View view = getBView(broadcastDataCurrent);
      if (view == null) {
        return;
      }
      BroadcastViewItem viewItem = (BroadcastViewItem) view.getTag(R.id.tag_first);
      if (viewItem != null) {
        viewItem.bindView(broadcastDataCurrent);
        viewItem.start();
        viewItem.resume();
      }
    }
  }

  public void destory() {
    if (broadcastDataCurrent != null) {
      View view = onlyGetBView(broadcastDataCurrent);
      if (view == null) {
        return;
      }
      BroadcastViewItem viewItem = (BroadcastViewItem) view.getTag(R.id.tag_first);
      if (viewItem != null) {
        viewItem.destoryView();
      }
    }
  }

  private void aBroadcastView(Context context) {
    mContext = context;
    mHandler = new Handler(context.getMainLooper()) {
      @Override
      public void handleMessage(Message msg) {
        BroadcastView.this.handleMessage(msg);
      }
    };
    mDataWatcher = new DataWatcher() {
      @Override
      public void onDataChanged(DownloadEntry data) {
        Slog.d(TAG, "onDataChanged  [data]:" + "正在下载" + data.percent + "%");
        if (data.percent == 100 && !TextUtils.isEmpty(data.loc_path)) {
          List<BroadcastData> list = KJDB.getDefaultInstance().findAllByWhere(BroadcastData.class,
              "image_url = \"" + data.url + "\"");
          if (list != null && list.size() > 0) {
            for (BroadcastData media : list) {
              media.image_url_path = data.loc_path;
              KJDB.getDefaultInstance().update(media);
              //如果当前没有可以播放的图片，下载完成后立即通知界面刷新
              if (broadcastDataCurrent == null) {
                playNextMedia();
              }
            }
          }
        }
      }
    };
    bindDownload();
  }

  public void refreshData(List<BroadcastData> dataList) {
    Slog.d(TAG, "refreshData  [dataList]:");
    if (dataList == null) {
      dataList = new ArrayList<>();
      Slog.e(TAG, "refreshData: dataList is null");
    }
    boolean needRefresh = false;
    //1 删除缺少的数据
    List<BroadcastData> dbOlds = KJDB.getDefaultInstance().findAll(BroadcastData.class);
    if (dbOlds != null && dbOlds.size() > 0) {
      for (BroadcastData dbOld : dbOlds) {
        boolean deleted = true;
        for (BroadcastData media : dataList) {
          //如果在返回的数据里面没有找到，则认为已经删除， id相同，url相同
          if (media.equals(dbOld)) {
            deleted = false;
            break;
          }
        }
        if (deleted) {
          needRefresh = true;
          KJDB.getDefaultInstance().delete(dbOld);
          try {
            if (null != dbOld.image_url_path && !"".equals(dbOld.image_url_path)) {
              File file = new File(dbOld.image_url_path);
              if (file.isFile()) {
                file.delete();
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }

    //2 更新新添加的数据
    if (dataList != null && dataList.size() > 0) {
      for (BroadcastData media : dataList) {
        if (KJDB.getDefaultInstance().findById(media.id, BroadcastData.class) == null) {
          KJDB.getDefaultInstance().save(media);
          needRefresh = true;
        }
      }
    }

    //3 检查本地是否有文件需要下载
    checkDownload();

    if (broadcastDataCurrent == null) {
      needRefresh = true;
    }
    //4 通知界面刷新
    if (needRefresh) {
      playNextMedia();
    }
  }


  private void modifyCurrent(BroadcastData broadcastDatanew) {
    broadcastDataCurrent = broadcastDatanew;
  }

  //判断播放时间是否是当前时间段
  private boolean checkDateAndTimeIn(int id, String start_time, String end_time,
                                     String start_data, String end_data) {
    boolean c = checkData(start_data, end_data) && checkTime(start_time, end_time);
    if (!c) {
      Slog.d(TAG, "checkDateAndTimeIn  [id, start_time, end_time, start_data, end_data]:" + c + "" +
          ": id is :" + id + "");
    }
    return c;
  }

  private boolean checkData(String start_data, String end_data) {
    if (TextUtils.isEmpty(start_data) && TextUtils.isEmpty(end_data)) {
      return true;
    }
    Calendar calendar = Calendar.getInstance();

    String[] start = start_data.split("-");
    String[] end = end_data.split("-");
    if (start != null && end != null && start.length == 3 && end.length == 3) {
    } else {
      return false;
    }
    int y = calendar.get(Calendar.YEAR);
    int m = calendar.get(Calendar.MONTH) + 1;
    int d = calendar.get(Calendar.DAY_OF_MONTH);
    int sy = Integer.parseInt(start[0]);
    int ey = Integer.parseInt(end[0]);
    int sm = Integer.parseInt(start[1]);
    int em = Integer.parseInt(end[1]);
    int sd = Integer.parseInt(start[2]);
    int ed = Integer.parseInt(end[2]);
    calendar.set(y, m - 1, d, 0, 0, 1);
    Calendar cS = Calendar.getInstance();
    cS.set(sy, sm - 1, sd, 0, 0, 0);
    Calendar cE = Calendar.getInstance();
    cE.set(ey, em - 1, ed, 0, 0, 2);

    if (calendar.after(cS) && cE.after(calendar)) {
      return true;
    } else {
      return false;
    }

  }

  private boolean checkTime(String start_time, String end_time) {
    if (TextUtils.isEmpty(start_time) && TextUtils.isEmpty(end_time)) {
      return true;
    }
    Calendar calendar = Calendar.getInstance();
    String[] start = start_time.split(":");
    String[] end = end_time.split(":");
    if (start != null && end != null && start.length == 3 && end.length == 3) {
    } else {
      return false;
    }
    int y = calendar.get(Calendar.HOUR_OF_DAY);
    int m = calendar.get(Calendar.MINUTE);
    int d = calendar.get(Calendar.SECOND);
    int sy = Integer.parseInt(start[0]);
    int ey = Integer.parseInt(end[0]);
    int sm = Integer.parseInt(start[1]);
    int em = Integer.parseInt(end[1]);
    int sd = Integer.parseInt(start[2]);
    int ed = Integer.parseInt(end[2]);
    int stime = sd + sm * 60 + sy * 3600;
    int time = d + m * 60 + y * 3600;
    int etime = ed + em * 60 + ey * 3600;

    if (stime <= time && time <= etime) {
      return true;
    }
    return false;
  }

  private synchronized void playNextMedia() {
    Slog.d(TAG, "playNextMedia  []:");
    //查找置顶的播放media
    BroadcastData broadcastDatanew = null;
    List<BroadcastData> tops = KJDB.getDefaultInstance().findAllByWhere(BroadcastData.class,
        "top=1");
    if (tops != null && tops.size() > 0) {
      for (BroadcastData top : tops) {
        if (top.image_url_path != null && checkDateAndTimeIn(top.id, top.start_time, top.end_time, top.start_date, top.end_date)) {
          broadcastDatanew = top;
          break;
        }
      }
    } else {
      int currentPlayId = 0;
      if (broadcastDataCurrent != null) {
        currentPlayId = broadcastDataCurrent.id;
      }
      //确定当前播放的还在不在播放列表中，如果不在，就从第一个开始播放
      BroadcastData m = KJDB.getDefaultInstance().findById(currentPlayId, BroadcastData.class);

      boolean tag = false;
      if (m == null) {
        tag = true;
      }
      List<BroadcastData> list = KJDB.getDefaultInstance().findAll(BroadcastData.class);
      if (list != null && list.size() > 0) {
        //查找当前播放的下一个
        for (BroadcastData media : list) {
          if (tag) {
            if (!media.isMieda() || !TextUtils.isEmpty(media.image_url_path)) {
              if (checkDateAndTimeIn(media.id, media.start_time, media.end_time, media.start_date, media.end_date)) {
                broadcastDatanew = media;
                break;
              }
            }
          } else {
            if (media.id == m.id) {
              tag = true;
            }
          }
        }

        //如果没有找到，再循环找一遍，因为当前播放的前几个没有查找
        if (broadcastDatanew == null) {
          for (BroadcastData media : list) {
            if (!media.isMieda() || !TextUtils.isEmpty(media.image_url_path)) {
              if (checkDateAndTimeIn(media.id, media.start_time, media.end_time, media.start_date, media.end_date)) {
                broadcastDatanew = media;
                break;
              }
            }
          }
        }
      }
    }

    if (broadcastDatanew != null) {
      //判断是否是当前正在播放的内容
      if (broadcastDataCurrent != null) {
        if (broadcastDatanew.id == broadcastDataCurrent.id
            && broadcastDatanew.image_url.equals(broadcastDataCurrent.image_url)) {
          Slog.d(TAG, "playNextMedia  []: is playing current media, skip");
          if (!broadcastDatanew.isVideo()) {
            mHandler.sendEmptyMessageDelayed(MSG_PLAY_NEXT, broadcastDataCurrent.duration * 1000);
            return;
          }
        }
      }
    } else {
      Slog.e(TAG, "playNextMedia  []: find null to play");
    }

    modifyCurrent(broadcastDatanew);
    //设置刷新延迟
    int durationPlayNext = 10 * 1000;
    if (broadcastDataCurrent != null) {
      durationPlayNext = broadcastDataCurrent.duration * 1000;
    }
    mHandler.sendEmptyMessageDelayed(MSG_PLAY_NEXT, durationPlayNext);

    boolean tagVideoPlaying = false;

    if (broadcastDataCurrent != null) {
      View view = getBView(broadcastDataCurrent);
      if (view == null) {
        return;
      }
      if (broadcastDataCurrent.isVideo()) {
        tagVideoPlaying = true;
        mHandler.removeMessages(MSG_PLAY_NEXT);
      }
      int count = getChildCount();

      for (int i = 0; i < count; i++) {
        View v = getChildAt(i);
        BroadcastViewItem viewItem = (BroadcastViewItem) v.getTag(R.id.tag_first);
        if (viewItem != null) {
          if (v == view) {
            viewItem.start();
            viewItem.resume();
            viewItem.setBroadcastProgress(new BroadcastProgress() {
              @Override
              public int progress(int progress) {
                if (progress == 100) {
                  mHandler.sendEmptyMessage(MSG_PLAY_NEXT);
                }
                return 0;
              }
            });
          } else {
            viewItem.pause();
            viewItem.stop();
          }
        } else {
          Slog.d(TAG, "playNextMedia  []:BroadcastViewItem tag null");
        }
      }
    } else {
      //所有的view 都不显示了
      int count = getChildCount();
      for (int i = 0; i < count; i++) {
        View v = getChildAt(i);
        BroadcastViewItem viewItem = (BroadcastViewItem) v.getTag(R.id.tag_first);
        if (viewItem != null) {
          viewItem.pause();
          viewItem.stop();
        } else {
          Slog.d(TAG, "playNextMedia  []:BroadcastViewItem tag null");
        }
      }
      showDefault();
    }
  }

  private void showDefault() {
    if (mBroadcastImage == null) {
      mBroadcastImage = new BroadcastImage(getContext());
      addView(mBroadcastImage.createView(), 0);
    }
    mBroadcastImage.showdefault();
  }

  private View getBView(BroadcastData data) {
    Slog.d(TAG, "getBView  [data]:" + data.type);
    if (data.isVideo()) {
      if (mBroadcastVideo == null) {
        mBroadcastVideo = new BroadcastVideo(getContext());
        addView(mBroadcastVideo.createView(), 0);
      }
      View v = mBroadcastVideo.bindView(data);
      return v;
    } else if (data.isImage() || data.isTextImage()) {
      if (mBroadcastImage == null) {
        mBroadcastImage = new BroadcastImage(getContext());
        addView(mBroadcastImage.createView(), 0);
      }
      return mBroadcastImage.bindView(data);
    } else if (data.isHtml()) {
      if (mBroadcastUrl == null) {
        mBroadcastUrl = new BroadcastUrl(getContext());
        addView(mBroadcastUrl.createView(), 0);
      }
      return mBroadcastUrl.bindView(data);
    } else if (data.isText()) {

    }
    return null;
  }


  private View onlyGetBView(BroadcastData data) {
    String type = data.type;
    Slog.d(TAG, "getBView  [data]:" + type);
    if ("video".equals(type)) {
      if (mBroadcastVideo != null) {
        return mBroadcastVideo.getView();
      }
    } else if ("image".equals(type)) {
      if (mBroadcastImage != null) {
        return mBroadcastImage.getView();
      }
    } else if ("html".equals(type)) {
      if (mBroadcastUrl != null) {
        return mBroadcastUrl.getView();
      }
      return mBroadcastUrl.bindView(data);
    } else if ("text".equals(type)) {
      if (mBroadcastImage != null) {
        return mBroadcastImage.getView();
      }
    }
    return null;
  }

  private void checkDownload() {
    List<BroadcastData> list = KJDB.getDefaultInstance().findAll(BroadcastData.class);
    if (list != null && list.size() > 0) {
      for (BroadcastData media : list) {
        if (media.isMieda() && TextUtils.isEmpty(media.image_url_path)) {
          startDownload(media.image_url);
        }
      }
    }
  }

  DataWatcher mDataWatcher = null;
  //下载 监听回调
  private DataChanger dataChanger;

  //初始化下载的数据
  private void bindDownload() {
    dataChanger = DataChanger.getInstance(mContext);
    DownloadManager.getInstance(mContext).addObserver(mDataWatcher);
  }

  //开始下载
  private void startDownload(String url) {
    DownloadEntry entry;
    if (dataChanger.containsDownloadEntry(url)) {
      entry = dataChanger.queryDownloadEntryByUrl(url);
    } else {
      entry = new DownloadEntry(url);
      entry.name = url.substring(url.lastIndexOf("/") + 1);//apk名字
    }
    DownloadManager.getInstance(mContext).add(entry);
  }

  private void handleMessage(Message msg) {
    switch (msg.what) {
      case MSG_PLAY_NEXT:
        mHandler.removeMessages(MSG_PLAY_NEXT);
        playNextMedia();
        break;
    }
  }
}
