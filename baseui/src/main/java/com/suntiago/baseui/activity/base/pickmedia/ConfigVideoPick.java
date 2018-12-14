package com.suntiago.baseui.activity.base.pickmedia;

/**
 * Created by zy on 2018/12/14.
 */

public class ConfigVideoPick {
    public int quality = 0;
    public int durationLimit = 10;

    public ConfigVideoPick(int quality, int durationLimit) {
        this.quality = quality;
        this.durationLimit = durationLimit;
    }

    public ConfigVideoPick() {
    }
}
