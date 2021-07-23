package com.example.BaseLibrary.ioc;

import android.app.Activity;
import android.view.View;

import androidx.annotation.IdRes;

/**
 * <p>Title: </p>
 * <p>Description: findViewById的辅助类</p>
 * <p>Copyright: DS Technology Co.Ltd  Copyright (c) 2020</p>
 * <p>Company: www.ds.cn</p>
 *
 * @author wuguangcheng@ds.cn
 * @version 1.0
 * @date :
 */
public class ViewFinder {
    private Activity mActivity;
    private View mView;

    public ViewFinder(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public ViewFinder(View mView) {
        this.mView = mView;
    }
    public View findViewById(@IdRes int id) {
        return mActivity != null ? mActivity.findViewById(id) : mView.findViewById(id);
    }
}
