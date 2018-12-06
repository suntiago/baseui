package com.suntiago.dblibDemo.themvpTest;

import android.view.View;

import com.suntiago.baseui.activity.base.ActivityBase;
import com.suntiago.dblibDemo.R;

/**
 * Created by zy on 2018/12/4.
 */

public class ActivityMvpTest extends ActivityBase<MvpTestDelegate> {
    @Override
    protected Class getDelegateClass() {
        return MvpTestDelegate.class;
    }

    @Override
    protected void bindEvenListener() {
        viewDelegate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDelegate.showProgress();
            }
        }, R.id.btn_show_progress);

    }
}
