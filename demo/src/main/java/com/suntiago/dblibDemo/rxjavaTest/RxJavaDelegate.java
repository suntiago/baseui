package com.suntiago.dblibDemo.rxjavaTest;

import android.support.v7.widget.Toolbar;

import com.suntiago.baseui.activity.base.AppDelegateBase;
import com.suntiago.dblibDemo.R;

/**
 * Created by zy on 2018/12/27.
 */

public class RxJavaDelegate extends AppDelegateBase {
    @Override
    public int getRootLayoutId() {
        return R.layout.layout_activity_rxjava;
    }

    @Override
    public void initWidget() {

    }


    public void setTitle(String title) {
        Toolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setTitle(title);
        }

    }

    public void getaaa(){}
}
