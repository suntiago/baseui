package com.suntiago.baseui.activity.base.theMvp;

import com.suntiago.baseui.activity.base.theMvp.databind.DataBinder;
import com.suntiago.baseui.activity.base.theMvp.model.IModel;
import com.suntiago.baseui.activity.base.theMvp.view.AppDelegate;

/**
 * Created by zy on 2018/12/27.
 */

public class DataBinderBase implements DataBinder {
    @Override
    public void viewBindModel(AppDelegate viewDelegate, IModel data) {
        if (viewDelegate != null) {
            viewDelegate.viewBindModel(data);
        }
    }
}
