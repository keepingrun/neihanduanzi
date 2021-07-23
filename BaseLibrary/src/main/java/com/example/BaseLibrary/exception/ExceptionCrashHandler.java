package com.example.BaseLibrary.exception;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.os.EnvironmentCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: </p>
 * <p>Description: 记录异常信息   单例模式</p>
 * <p>Copyright: DS Technology Co.Ltd  Copyright (c) 2020</p>
 * <p>Company: www.ds.cn</p>
 *
 * @author wuguangcheng@ds.cn
 * @version 1.0
 * @date :
 */
public class ExceptionCrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "ExceptionCrashHandler";
    private static ExceptionCrashHandler mInstance;
    public static String CRASH_FILE_NAME = "CRASH_FILE_NAME";
    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    private Context mContext;

    public void init(Context context) {
        this.mContext = context;
        // 获取默认的异常处理类
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置全局异常类为当前类
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private ExceptionCrashHandler() {

    }
    public static ExceptionCrashHandler getInstance() {
        if (null == mInstance) {
            synchronized(ExceptionCrashHandler.class) {
                if (null == mInstance) {
                    mInstance = new ExceptionCrashHandler();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        Log.e(TAG, "异常来了", e);

        //
        String crashFileName = saveInfoToSD(e);
        // 缓存崩溃日志文件
        cacheCrashFile(crashFileName);
        // 让系统默认处理
        mDefaultUncaughtExceptionHandler.uncaughtException(t, e);
    }

    private void cacheCrashFile(String crashFileName) {
        SharedPreferences sp = mContext.getSharedPreferences("crash", Context.MODE_PRIVATE);
        sp.edit().putString(CRASH_FILE_NAME, crashFileName).commit();
    }

    public File getCacheCrashFile() {
        SharedPreferences sp = mContext.getSharedPreferences("crash", Context.MODE_PRIVATE);
        String fileName = sp.getString(CRASH_FILE_NAME, "");
        return new File(fileName);
    }

    /**
     * 信息保存到sd卡
     * @param e
     * @return
     */
    private String saveInfoToSD(Throwable e) {
        String fileName = null;
        StringBuffer sb = new StringBuffer();

        for (Map.Entry<String, String> entry : obtainSimpleInfo(mContext).entrySet()) {
            sb.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
        }
        // 收集崩溃详细信息
        sb.append(obtainExceptionInfo(e));

        // 6.0需要动态申请权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(mContext.getFilesDir() + File.separator + "crash" + File.separator);
            // 删除之前的异常信息
            if (dir.exists()) {
                deleteFile(dir);
            }
            // 在重新创建
            if (!dir.exists()) {
                dir.mkdir();
            }
            try {
                fileName = dir.toString() + File.separator + getAssignTime("yyyy_MM_dd_HH_mm") + ".txt";
                FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                fileOutputStream.write(sb.toString().getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        return fileName;
    }

    private String getAssignTime(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        long currentTime = System.currentTimeMillis();
        return dateFormat.format(currentTime);
    }


    private String obtainExceptionInfo(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        printWriter.close();
        return stringWriter.toString();
    }


    /**
     *  获取简单信息，手机版本，型号，软件版本
     * @param mContext
     * @return
     */
    private HashMap<String, String> obtainSimpleInfo(Context mContext) {
        HashMap<String, String> hashMap = new HashMap();
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packageInfo = null;
        try{
            packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        hashMap.put("versionName", packageInfo.versionName);
        hashMap.put("versionCode", packageInfo.versionCode + "");
        // 设备名
        hashMap.put("MODEL", Build.MODEL);
        hashMap.put("SDK_INT", Build.VERSION.SDK_INT + "");
        // 整个产品的名称
        hashMap.put("PRODUCT", Build.PRODUCT);
        hashMap.put("MOBILE_INFO", getMobileInfo());

        return hashMap;
    }

    private String getMobileInfo() {
        // 线程安全
        StringBuffer sb = new StringBuffer();
        try {
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                // get(null) 静态字段获取成功，如果不是静态字段则nullptr
                String value = field.get(null).toString();
                sb.append(name).append(" = ").append(value).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void deleteFile(File file) {
        if (file.isDirectory()) {
            file.delete();
            File[] files = file.listFiles();
            for (File temp : files) {
                deleteFile(temp);
            }
            file.delete();
        } else if (file.exists()) {
            file.delete();
        }
    }
}
