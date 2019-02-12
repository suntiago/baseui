package com.suntiago.dblibDemo.rxjavaTest;

import android.os.Bundle;

import com.jakewharton.rxbinding2.view.RxView;
import com.suntiago.baseui.activity.base.ActivityBase;
import com.suntiago.baseui.activity.base.theMvp.model.IModel;
import com.suntiago.baseui.utils.log.Slog;
import com.suntiago.dblibDemo.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Timed;
import rx.Emitter;
import rx.functions.Action1;

/**
 * Created by zy on 2018/12/27.
 * <p>
 * Rxjava 1.x, 2.x 的用法示例
 * map, flatmap, just, timer, from, filter,take,doOnNext,zip,defer,merge,range,interval
 * rxbinding2 的用法示例
 */

public class RxJavaActivity extends ActivityBase<RxJavaDelegate, IModel> {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    rx1();
  }

  @Override
  protected void bindEvenListener() {
    super.bindEvenListener();
    RxView.clicks(viewDelegate.bindView(R.id.btn_double_click))
        //打印时间间隔
        .timeInterval(TimeUnit.MILLISECONDS)
        //一秒防抖动
        .throttleFirst(1, TimeUnit.SECONDS)
        .subscribe(new Observer<Timed<Object>>() {
          @Override
          public void onSubscribe(Disposable d) {

          }

          @Override
          public void onNext(Timed<Object> objectTimed) {
            Slog.d(TAG, "bindView onNext  [o]:click" + objectTimed.time() + "");

          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onComplete() {

          }
        });
  }

  @Override
  protected Class<RxJavaDelegate> getDelegateClass() {
    return RxJavaDelegate.class;
  }

  @Override
  protected Class<IModel> getModelClass() {
    return null;
  }

  public void rx1() {

    Observer observer = new Observer() {
      @Override
      public void onSubscribe(Disposable d) {

      }

      @Override
      public void onNext(Object o) {

      }

      @Override
      public void onError(Throwable e) {

      }

      @Override
      public void onComplete() {

      }
    };

    Observable observable = new Observable() {
      @Override
      protected void subscribeActual(Observer observer) {

      }
    };


    rx.Observable<String> observable1 = rx.Observable.create(
        new rx.Observable.OnSubscribe<String>() {
          @Override
          public void call(rx.Subscriber<? super String> sub) {
            sub.onNext("Hello, world!");
            sub.onCompleted();
          }
        }
    );

    rx.Observable<String> myObservable = rx.Observable.create(
        new Action1<Emitter<String>>() {
          @Override
          public void call(Emitter<String> stringEmitter) {
            stringEmitter.onNext("nihao");
            stringEmitter.onNext("hhhhhhh");
            stringEmitter.onNext(stringEmitter.requested() + "");
            stringEmitter.onCompleted();
          }
        }, rx.Emitter.BackpressureMode.BUFFER);

    rx.Observer<String> subscriber = new rx.Observer<String>() {
      @Override
      public void onNext(String s) {
        Slog.d(TAG, "rx1 onNext  [s]:" + s);
      }

      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        Slog.d(TAG, "onError  [e]:");
        e.printStackTrace();
      }
    };
    myObservable.subscribe(subscriber);
  }


}
