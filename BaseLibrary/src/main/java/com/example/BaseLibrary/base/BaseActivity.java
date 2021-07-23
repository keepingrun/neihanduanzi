package com.example.BaseLibrary.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.BaseLibrary.ioc.ViewUtils;

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
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * intent之间传递bundle的Key
     */
    public String BUNDLE = "BUNDLE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 模板方法
        setContentView();
        // 注入
        ViewUtils.inject(this);
        // 初始化头部
        initTitle();
        // 初始化界面
        initView();
        // 初始化数据
        initData();

    }
    public abstract void setContentView();

    public abstract void initTitle();

    public abstract void initView();

    public abstract void initData();

    /**
     * 手动初始化控件
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T viewById(@IdRes int viewId) {
        return (T)findViewById(viewId);
    }

    public void startActivity(Class<?> clazz) {
        startActivity(clazz, null);
    }

    public void startActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtra(BUNDLE, bundle);
        }
        startActivity(intent);
    }
}
