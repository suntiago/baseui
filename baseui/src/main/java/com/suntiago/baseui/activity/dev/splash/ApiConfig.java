package com.suntiago.baseui.activity.dev.splash;

import com.google.gson.annotations.SerializedName;

public class ApiConfig {
    @SerializedName("api")
    public String api;
    @SerializedName("netty_host")
    public String netty_host;
    @SerializedName("netty_port")
    public int netty_port;
}
