# 一个好用的动态权限请求工具类PermissionsUtils
动态权限请求工具
____________
> 工具类参考自GitHub上开源工程Blankj的[AndroidUtilCode](https://github.com/Blankj/AndroidUtilCode)中的[PermissionUtils](https://github.com/Blankj/AndroidUtilCode/blob/master/utilcode/lib/src/main/java/com/blankj/utilcode/util/PermissionUtils.java) 和tbruyelle的[RxPermissions](https://github.com/tbruyelle/RxPermissions)

### 优点
 - 采用敏感权限组和`AndroidManifest`中声明的权限管理动态权限申请，可以省略查找哪些敏感需要动态申请的过程。
 - 采用内置Fragment管理请求回调，使用时无须手动重写`public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)`方法。
 - 采用`builder`设计模式，链式编程

### 使用方法
项目的`build.gradle`配置

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
`module`的`build.gradle`配置
```
dependencies {
	        implementation 'com.github.bluetomcat:PermissionsUtils:1.0'
	}
```

### 请求示例
`AndroidManifest.xml`文件：
```
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_CALENDAR"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>//测试manifest文件中多余动态权限
```
动态权限请求的`Activity/Fragment`中：
```
val request = PermissionUtils.builder()
            .permission(
                Manifest.permission_group.STORAGE,
                Manifest.permission_group.CALENDAR,
                Manifest.permission_group.PHONE,
                Manifest.permission_group.SENSORS//申请时多余动态权限
            )
            .setResultListener(object : FullCallback {
                override fun onDenied(
                    permissionsDeniedForever: MutableList<String>?,
                    permissionsDenied: MutableList<String>?
                ) {
                    if (permissionsDeniedForever != null) {
                        for (permission in permissionsDeniedForever) {
                            Log.e(TAG, "DeniedForever:" + permission)
                        }
                    }
                    if (permissionsDenied != null) {
                        for (permission in permissionsDenied) {
                            Log.e(TAG, "Denied:" + permission)
                        }
                    }
                }
                override fun onFinish() {
                    Log.e(TAG, "onFinish");
                }

                override fun onGranted(permissionsGranted: MutableList<String>?) {
                    if (permissionsGranted != null) {
                        for (permission in permissionsGranted) {
                            Log.e(TAG, "Granted:" + permission)
                        }
                    }
                }
            })
            .setRationale(Manifest.permission_group.PHONE)//可省
            .build(this)
            .request()
```
实际动态申请的权限（设置的需申请的动态权限组合所含权限集合与`AndroidManifest`文件中声明的权限集合的交集）：

```
android.permission.READ_EXTERNAL_STORAGE
android.permission.WRITE_EXTERNAL_STORAGE
android.permission.WRITE_CALENDAR
android.permission.READ_PHONE_STATE
```

### PermissionUtils类中API说明

 - `builder()` ：静态方法，获取Builder对象
 - `permission(@Permission String... permissions)`：`Builder`对象中的方法，设置需要动态请求的敏感权限组（动态申请的权限就是这里设置的权限组中的权限集和`AndroidManifest`文件中声明的权限集的交集）
 -  `setResultListener(ResultListener listener)`：`Builder`对象中的方法，设置权限请求结果监听（内置`FullCallback`和`SimpleCallback`两个接口回调，详情后面单独说明）
 - `setRationale(@Nullable @Permission String... permissions)`：`Builder`对象中的方法，设置需要重复请求的权限组，不常用，可省
 - `build(FragmentActivity activity)` 和`build(Fragment fragment)`：`Builder`对象中的方法，构建`PermissionUtils`对象，参数为支持包中的`FragmentActivity /Fragment` 
 - `isGranted(final String... permissions)`：`PermissionUtils`对象中的方法，判断权限组中所需动态申请的权限是否全部已通过，通过内置的`Fragment`对象判断
 - `isGranted(final String permission)`：`PermissionUtils`对象中的方法，判断动态权限是否申请通过，通过内置的`Fragment`对象判断
 - `request()`：`PermissionUtils`对象中的方法，开始请求动态权限
 - `onDestroy()`：`PermissionUtils`对象中的方法，绑定了`build`中`FragmentActivity /Fragment`的生命周期，自动在其`onDestroy()`方法中调用
 - 
 ### SimpleCallback接口说明
 动态权限请求结果回调，其API仅在权限请求全部结束后调用，具体说明如下：
 - `onDenied()`：申请的权限存在被拒绝的权限时调用
 - `onGranted()`：申请的权限全部通过时调用
 
 ### FullCallback接口说明
 动态权限请求结果回调，其API说明如下：
 - `onGranted(List<String> permissionsGranted)`：每次请求循环结束后，申请的权限存在被通过的权限时调用，参数是通过的权限集合
 - `onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied)`：每次请求循环结束后，申请的权限存在被拒绝的权限时调用，参数是被永久拒绝的权限集合和被拒绝的权限集合
 - `onFinish()`：所有请求结束后调用
### 参考文档
PermissionUtils.Java：[https://github.com/Blankj/AndroidUtilCode/blob/master/utilcode/lib/src/main/java/com/blankj/utilcode/util/PermissionUtils.java](https://github.com/Blankj/AndroidUtilCode/blob/master/utilcode/lib/src/main/java/com/blankj/utilcode/util/PermissionUtils.java)
RxPermissions:
[https://github.com/tbruyelle/RxPermissions](https://github.com/tbruyelle/RxPermissions)
__________
注：能力有限，如有不足欢迎大神指教，万分感谢！！！
