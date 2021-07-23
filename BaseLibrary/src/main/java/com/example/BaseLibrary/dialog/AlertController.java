package com.example.BaseLibrary.dialog;

import android.content.Context;

/**
 * <p>Title: AlertController</p>
 * <p>Description: </p>
 * <p>Copyright: DS Technology Co.Ltd  Copyright (c) 2020</p>
 * <p>Company: www.ds.cn</p>
 *
 * @author wuguangcheng@ds.cn
 * @version 1.0
 * @date 2021/7/23 17:48
 */
class AlertController {

    public static class AlertParams {

        private Context context;
        private int themeResId;

        public AlertParams(Context context, int themeResId) {
            this.context = context;
            this.themeResId = themeResId;
        }
    }
}
