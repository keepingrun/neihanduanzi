package com.example.essayjoke.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <p>Title: HookStartActivityUtils</p>
 * <p>Description: </p>
 * <p>Copyright: DS Technology Co.Ltd  Copyright (c) 2020</p>
 * <p>Company: www.ds.cn</p>
 *
 * @author wuguangcheng@ds.cn
 * @version 1.0
 * @date 2021/7/26 9:37
 */
public class HookStartActivityUtils {
    private static final String TAG = "HookStartActivityUtils";

    private Context context;

    public HookStartActivityUtils(Context context) {
        this.context = context;
    }

    public void hookStartActivity() throws Exception{
        // 1.获取ActivityTaskManager里面的IActivityTaskManagerSingleton成员
        @SuppressLint("PrivateApi") Class<?> activityTaskManager = Class.forName("android.app.ActivityTaskManager");
        Field IActivityTaskManagerSingletonField = activityTaskManager.getDeclaredField("IActivityTaskManagerSingleton");
        IActivityTaskManagerSingletonField.setAccessible(true);
        // 因为IActivityTaskManagerSingleton是静态的，所以传null
        Object iActivityTaskManagerSingleton = IActivityTaskManagerSingletonField.get(null);

        // 2.获取IActivityTaskManagerSingleton单例中的mInstance
        @SuppressLint("PrivateApi") Class<?> singletonClass = Class.forName("android.util.Singleton");
        Field mInstanceField = singletonClass.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);
        Object iAtmInstance = mInstanceField.get(iActivityTaskManagerSingleton);

        iAtmInstance = Proxy.newProxyInstance(context.getClass().getClassLoader(),
                iAtmInstance.getClass().getInterfaces(),
                new StartActivityInvocationHandler(iAtmInstance));
        // 3.重新指定
        mInstanceField.set(iActivityTaskManagerSingleton, iAtmInstance);
    }

    private static class StartActivityInvocationHandler implements InvocationHandler {
        // 方法执行者
        private Object object;

        public StartActivityInvocationHandler(Object object) {
            this.object = object;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "动态代理：" + method.getName());
            return method.invoke(object, args);
        }
    }
}
