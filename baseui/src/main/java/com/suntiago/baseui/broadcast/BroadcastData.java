package com.suntiago.baseui.broadcast;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.kymjs.kjframe.database.annotate.Id;
import org.kymjs.kjframe.database.annotate.Property;
import org.kymjs.kjframe.database.annotate.Table;

/**
 * Created by zy on 2019/1/29.
 */
@Table(name = "media")
public class BroadcastData {
  @SerializedName("uid")
  @Id(column = "id", autoInc = false)
  public int id;

  @SerializedName("duration")
  @Property(column = "duration")
  public int duration = 15;

  //image or media
  @SerializedName("media_type")
  @Property(column = "media_type")
  public String type;

  //图片或者视频的下载地址
  @SerializedName("image_url")
  @Property(column = "image_url")
  public String image_url;

  //下载完成后的本地路径
  @Property(column = "image_url_path")
  public String image_url_path;

  public boolean isMieda() {
    if (isImage() || isTextImage() || isVideo()) {
      return true;
    }
    return false;
  }

  public boolean isImage() {
    return "image".equals(type);
  }

  public boolean isVideo() {
    return "video".equals(type);
  }

  public boolean isTextImage() {
    return "image-text".equals(type);
  }

  public boolean isText(){
    return "text".equals(type);
  }

  public boolean isHtml(){
    return "html".equals(type);
  }


  @SerializedName("full_media_url")
  @Property(column = "full_url")
  public String fullurl;


  @SerializedName("mode")
  @Property(column = "top")
  public int top = 0;

  @SerializedName("text")
  @Property(column = "title")
  public String title = "";

  //视频缩略图
  public String thumb_url;

  //视频缩略图本地下载地址
  public String thumb_url_path;

  public String tvtitle;
  public String tvcontent;
  public String tvtitleSize;
  public String tvtitleColor;
  public String tvcontSize;
  public String tvcontColor;

  public String start_date;// "2019-03-05",
  public String end_date;//"2019-03-05",
  public String start_time;// "02:02:00",
  public String end_time;// "09:19:00"

  public int maxwidthpercent;
  public  int contentGappercent;

  public void updata(BroadcastData media) {
    this.top = media.top;
    if (media.duration == 0) {
      //轮播间隔默认15秒
      this.duration = 15;
    } else {
      this.duration = media.duration;
    }
    this.title = media.title;
    this.type = media.type;
    this.image_url = media.image_url;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof BroadcastData) {
      BroadcastData d = (BroadcastData) o;
      return com(id, d.id)
          && com(image_url, d.image_url)
          && com(duration, d.duration)
          && com(top, d.top)
          && com(type, d.type)
          && com(title, d.title)
          && com(tvtitle, d.tvtitle)
          && com(tvcontent, d.tvcontent)
          && com(tvtitleSize, d.tvtitleSize)
          && com(tvtitleColor, d.tvtitleColor)
          && com(tvcontSize, d.tvcontSize)
          && com(start_date, d.start_date)
          && com(end_date, d.end_date)
          && com(end_time, d.end_time);
    }
    return false;
  }

  private boolean com(String a, String b) {
    if (TextUtils.isEmpty(a) && TextUtils.isEmpty(b)) {
      return true;
    }

    if (TextUtils.isEmpty(a) || TextUtils.isEmpty(b)) {
      return false;
    }

    if (a.equals(b)) {
      return true;
    }
    return false;
  }

  private boolean com(int a, int b) {
    return a == b;
  }
}
