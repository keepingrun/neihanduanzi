# 热修复

#### 依赖引入： 
implementation 'com.alipay.euler:andfix:0.5.0@aar'

#### 热修复相关：
1.旧包-新包-》得到差分包, fix.apatch;
2.热修复时机：每次启动app，或者定时校验差分包；

#### 注意点：
1.尽量不要multidex， 命名尽量不要混淆
2.差分包一定要在加固包之前去生成。
3.是修复方法，但不能增加成员变量，也不能新增方法


#### 压缩图片：
1.NDK
2.传统使用BitmapFactory压缩

# Activity启动流程
startActivity的时候可以设置请求requestCode
1.startActivity->2.startActivityForResult->3.Instrumentation.execStartActivity->3.ActivityTaskManager.getService().startActivity->4.ActivityThread.performLaunchActivity

##### performLaunchActivity方法：

```java
activity = mInstrumentation.newActivity(
                    cl, component.getClassName(), r.intent);
```

```java
// cl是ClassLoader 通过类加载器从dex文件加载目标Actvity，然后通过反射newInstance()实例化对象
return (Activity) cl.loadClass(className).newInstance();
```

##### ClassLoader:

```java
// 继承关系
PathClassLoader->BaseDexClassLoader->ClassLoader
```
```java
// ClassLoader.loadClass方法
protected Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
            // First, check if the class has already been loaded
            // 查找是否已加载过
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                try {
                    if (parent != null) {
                    	// 没有加载过，调用父加载器加载
                        c = parent.loadClass(name, false);
                    } else {
                        c = findBootstrapClassOrNull(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }

                if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
                    c = findClass(name);
                }
            }
            return c;
    }

```

##### 类的加载机制流程：

![image-20210723105345326](pic/类的加载机制.png)

```java
// DexPathList对象
public Class<?> findClass(String name, List<Throwable> suppressed) {
        for (Element element : dexElements) {
            Class<?> clazz = element.findClass(name, definingContext, suppressed);
            // 遍历，一查找到就返回。 利用这个特点，如果我们有修复后的dex，app下载后，可以插入到                   // dexEelment数组的头部，实现修复bug。
            if (clazz != null) {
                return clazz;
            }
        }

        if (dexElementsSuppressedExceptions != null) {
            suppressed.addAll(Arrays.asList(dexElementsSuppressedExceptions));
        }
        return null;
    }
```
##### dex修复过程：
![image-20210723105345326](pic/dex修复.png)



总结： 类都是通过ClassLoader加载的

