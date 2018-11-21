package com.suntiago.baseui.activity.dev.splash;

import android.content.Context;

import com.google.gson.Gson;
import com.suntiago.network.network.Api;
import com.suntiago.network.network.BaseRspObserver;
import com.suntiago.network.network.rsp.BaseResponse;
import com.suntiago.network.network.utils.MacUtil;
import com.suntiago.network.network.utils.SPUtils;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Jeremy on 2018/8/2.
 */

public class ConfigDevice {
    private final static String TAG = "ConfigDevice";
    private static String GETAPI_URL = "http://viotapi.iyuyun.net:9300/";

    //获取设备ip地址
    public static String getDeviceId(Context context) {
        return MacUtil.getLocalMacAddressFromIp();
    }

    //配置ip地址
    public static Subscription configIp(final Context context, String type,
                                        final Action1<BaseResponse> action) {
        return Api.get().getApi(IpConfig.class, GETAPI_URL)
                .api(ConfigDevice.getDeviceId(context), "default")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseRspObserver<ApiResponse>(ApiResponse.class, new Action1<ApiResponse>() {
                    @Override
                    public void call(ApiResponse rsp) {
                        if (rsp.error_code == 1000) {
                            if (!SPUtils.getInstance(context).get("api_config").equals(rsp.apiModel.config)) {
                                Gson gson = new Gson();
                                ApiConfig ac = gson.fromJson(rsp.apiModel.config, ApiConfig.class);

                                SPUtils.getInstance(context).put("api_config", rsp.apiModel.config);
                                SPUtils.getInstance(context).put("mac", ConfigDevice.getDeviceId(context));

                                Api.get().setApiConfig(ac.api + "/", ac.netty_host, ac.netty_port);
                            }
                        }
                        if (action != null) {
                            action.call(rsp);
                        }
                    }
                }));
    }

    interface IpConfig {
        /**
         * 获取api
         *
         * @param mac
         * @param app_name
         * @return
         */
        @GET("devmonitor/dev/{mac}/api-config")
        Observable<ApiResponse> api(@Path("mac") String mac, @Query("app_name") String app_name);
    }
}
