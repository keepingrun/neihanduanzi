package com.example.BaseLibrary.ioc;

import android.app.Activity;
import android.app.backup.BackupAgent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
public class ViewUtils {

    private static final String TAG = "ViewUtils";

    // activity
    public static void inject(Activity activity) {
        inject(new ViewFinder(activity), activity);
    }
    // view控件
    public static void inject(View view) {
        inject(new ViewFinder(view), view);
    }
    // fragment
    public static void inject(View view, Object object) {
        inject(new ViewFinder(view), object);
    }
    // object是当前实例
    private static void inject(ViewFinder viewFinder, Object object) {
        injectFiled(viewFinder, object);
        injectEvent(viewFinder, object);
    }

    /**
     * 注入事件
     * @param viewFinder
     * @param object
     */
    private static void injectEvent(ViewFinder viewFinder, Object object) {
        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        // 遍历所有method方法
        for (Method method : methods) {
            // 获取OnClick注解的方法
            OnClick onClick = method.getAnnotation(OnClick.class);
            if (onClick != null) {
                // 获取注解的id数组
                int[] viewIds = onClick.value();
                for (int viewId : viewIds) {
                    View view = viewFinder.findViewById(viewId);
                    // 当前method是否需要检查网络
                    boolean isCheckNet = method.getAnnotation(CheckNet.class) != null;
                    // 给当前view设置点击事件
                    if (view != null) {
                        view.setOnClickListener(new DeclaredOnClickListener(object, method, isCheckNet));
                    }
                }
            }
        }

    }

    /**
     * 注入属性
     * @param viewFinder
     * @param object
     */
    private static void injectFiled(ViewFinder viewFinder, Object object) {
        // 获取类的属性
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        // 获取ViewById里面的value值
        for (Field field : fields) {
            ViewById viewById = field.getAnnotation(ViewById.class);
            if (viewById != null) {
                int viewId = viewById.value();
                // findViewById找到View
                View view = viewFinder.findViewById(viewId);
                // 动态注入找到的view
                if (view != null) {
                    field.setAccessible(true);
                    try{
                        field.set(object, view);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static class DeclaredOnClickListener implements View.OnClickListener {
        private Object mObject;
        private Method mMethod;
        private boolean mIsCheckNet;


        public DeclaredOnClickListener(Object mObject, Method mMethod) {
            this.mObject = mObject;
            this.mMethod = mMethod;
        }

        public DeclaredOnClickListener(Object mObject, Method mMethod, boolean mIsCheckNet) {
            this.mObject = mObject;
            this.mMethod = mMethod;
            this.mIsCheckNet = mIsCheckNet;
        }

        @Override
        public void onClick(View v) {
            // 判断是否需要检查网络
            if (mIsCheckNet) {
                if (!networkAvailable(v.getContext())) {
                    Toast.makeText(v.getContext(), "网络不给力!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            //点击会调用该方法

            // 反射执行方法
            mMethod.setAccessible(true);
            try {
                // 有参的方法   try catch方法会防止方法执行时的崩溃
                mMethod.invoke(mObject, v);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    // 无参的方法
                    mMethod.invoke(mObject);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    Log.e(TAG, "onClick: ", e);
                }
            }

        }

        /**
         * 判断当前网络是否可用
         */
        private static boolean networkAvailable(Context context) {
            // 得到连接管理器对象
            try {
                ConnectivityManager connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager
                        .getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}