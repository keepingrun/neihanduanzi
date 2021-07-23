package com.example.BaseLibrary.fixbug;

import android.content.Context;
import android.util.Log;

import com.example.BaseLibrary.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.BaseDexClassLoader;

/**
 * <p>Title: FixDexManager</p>
 * <p>Description: 自己实现dex修复bug, 这里不使用单例模式</p>
 * <p>Copyright: DS Technology Co.Ltd  Copyright (c) 2020</p>
 * <p>Company: www.ds.cn</p>
 *
 * @author wuguangcheng@ds.cn
 * @version 1.0
 * @date 2021/7/23 11:23
 */
public class FixDexManager {
    private Context context;
    private File dexDir;

    public FixDexManager(Context context) {
        this.context = context;
        // 获取应用可以访问的dex目录
        this.dexDir = context.getDir("odex", Context.MODE_PRIVATE);
    }

    /**
     * 修复dex包
     * @param fixDexPath dex路径
     */
    public void fixDex(String fixDexPath) throws Exception{
        // 1.先获取已经运行的dexElements
        ClassLoader applicationClassLoader = context.getClassLoader();
        Object dexElements = getDexElementsByClassLoader(applicationClassLoader);

        // 2.获取dex文件 （补丁）
        // 2.1 移动到系统能够访问的dex目录下
        File srcFile = new File(fixDexPath);
        if (!srcFile.exists()) {
            throw new FileNotFoundException(fixDexPath);
        }
        // 一般来说，这个文件是有版本号的
        File destFile = new File(dexDir, srcFile.getName());
        if (destFile.exists()) {
            // 已经被加载修复过了
            Log.d("TAG", "patch " + fixDexPath + " has be loaded!");
            return;
        }
        FileUtils.copyFile(srcFile, destFile);
        // 2.2 ClassLoader读取fixDexPath路径  为什么加入到集合？
        List<File> fixDexFiles = new ArrayList<>();
        fixDexFiles.add(destFile);
        // 解压路径
        File optimizedDirectory = new File(dexDir, "odex");
        // 修复 （创建fixDexFile的ClassLoader）
        for (File fixDexFile : fixDexFiles) {
            ClassLoader fixDexClassLoader = new BaseDexClassLoader(fixDexFile.getAbsolutePath() // fixDexFile路径
                    , optimizedDirectory // 解压路径
                    , null// .so文件路径
                    , applicationClassLoader // 父classLoader
                    );

            // 3.把补丁的dexElements插入到数组头部
            Object fixDexElements = getDexElementsByClassLoader(fixDexClassLoader);
            dexElements = combineArray(dexElements, fixDexElements);
            // 注入到原来的classLoader(applicationClassLoader)中的pathList
            injectDexElements(applicationClassLoader, dexElements);
        }

    }

    /**
     * 合并后的dexElements注入到原来的classLoader(applicationClassLoader)中的pathList
     * @param applicationClassLoader
     * @param newDexElements
     */
    private void injectDexElements(ClassLoader applicationClassLoader, Object newDexElements) throws Exception{
        // 1.先获取DexPathList
        Field pathListField = BaseDexClassLoader.class.getDeclaredField("pathList");
        pathListField.setAccessible(true);
        Object pathList = pathListField.get(applicationClassLoader);
        // 2.获取DexPathList的dexElements数组
        Field dexElements = pathList.getClass().getDeclaredField("dexElements");
        dexElements.setAccessible(true);
        dexElements.set(applicationClassLoader, newDexElements);
    }

    /**
     * 拿到dexElements
     * @param applicationClassLoader
     * @return
     * @throws Exception
     */
    private Object getDexElementsByClassLoader(ClassLoader applicationClassLoader) throws Exception{
        // 1.先获取DexPathList
        Field pathListField = BaseDexClassLoader.class.getDeclaredField("pathList");
        pathListField.setAccessible(true);
        Object pathList = pathListField.get(applicationClassLoader);
        // 2.获取DexPathList的dexElements数组
        Field dexElements = pathList.getClass().getDeclaredField("dexElements");
        dexElements.setAccessible(true);
        return dexElements.get(pathList);
    }


    /**
     * 合并两个数组
     *
     * @param arrayLhs
     * @param arrayRhs
     * @return
     */
    private static Object combineArray(Object arrayLhs, Object arrayRhs) {
        // 拿到泛型
        Class<?> localClass = arrayLhs.getClass().getComponentType();
        int i = Array.getLength(arrayLhs);
        int j = i + Array.getLength(arrayRhs);
        Object result = Array.newInstance(localClass, j);
        for (int k = 0; k < j; ++k) {
            if (k < i) {
                Array.set(result, k, Array.get(arrayLhs, k));
            } else {
                Array.set(result, k, Array.get(arrayRhs, k - i));
            }
        }
        return result;
    }
}
