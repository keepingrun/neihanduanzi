package com.example.BaseLibrary.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.BaseLibrary.R;

/**
 * <p>Title: AlertDialog</p>
 * <p>Description: </p>
 * <p>Copyright: DS Technology Co.Ltd  Copyright (c) 2020</p>
 * <p>Company: www.ds.cn</p>
 *
 * @author wuguangcheng@ds.cn
 * @version 1.0
 * @date 2021/7/23 17:47
 */
public class AlertDialog extends Dialog {

    public AlertDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private final AlertController.AlertParams P;
        public Builder(Context context) {
            this(context, R.style.dialog);
        }

        public Builder(Context context, int themeResId) {
            P = new AlertController.AlertParams();
        }

    }
}
