package com.example.essayjoke;

import android.app.Application;

import com.alipay.euler.andfix.patch.PatchManager;
import com.example.BaseLibrary.exception.ExceptionCrashHandler;
import com.example.essayjoke.utils.PackageManagerUtil;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: DS Technology Co.Ltd  Copyright (c) 2020</p>
 * <p>Company: www.ds.cn</p>
 *
 * @author wuguangcheng@ds.cn
 * @version 1.0
 * @date :
 */
public class BaseApplication extends Application {

    public static PatchManager patchManager;

    @Override
    public void onCreate() {
        super.onCreate();
        // 崩溃
        ExceptionCrashHandler.getInstance().init(this);
        // 热修复
        initAndFix();

    }

    private void initAndFix() {
        patchManager = new PatchManager(this);
        patchManager.init(PackageManagerUtil.getVersion(this));//current version
        // 加载之前的apatch
        patchManager.loadPatch();
    }
}
