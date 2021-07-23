package com.example.essayjoke;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.EnvironmentCompat;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BaseLibrary.fixbug.FixDexManager;
import com.example.BaseLibrary.ioc.OnClick;
import com.example.BaseLibrary.ioc.ViewById;
import com.example.BaseLibrary.ioc.ViewUtils;
import com.example.FrameLibrary.BaseSkinActivity;

import java.io.File;
import java.io.IOException;

public class MainActivity extends BaseSkinActivity {


    @ViewById(R.id.tv_test)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注入
        ViewUtils.inject(this);
        // 热修复
        //fixDexBug();
        AlertDialog
    }

    /**
     * 自己的修复方式
     */
    private void fixDexBug() {
        File file = new File(getExternalCacheDir(), "fix.dex");
        if (file.exists()) {
            try {
                FixDexManager fixDexManager = new FixDexManager(this);
                fixDexManager.fixDex(file.getAbsolutePath());
                Toast.makeText(this, "FixDexManager修复成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "FixDexManager修复失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick({R.id.tv_test})
    public void clickTest(View v) {

    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initTitle() {

    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        // fix.apatch
        File file = new File(getExternalCacheDir(), "fix.apatch");
        Log.d("测试", getExternalCacheDir() + "");
        if (file.exists()) {
            try {
                BaseApplication.patchManager.addPatch(file.getAbsolutePath());
                Toast.makeText(this, "修复成功", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "修复失败", Toast.LENGTH_SHORT).show();
            }
        }


    }

}