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



