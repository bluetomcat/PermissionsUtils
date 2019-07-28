package com.tomcat.lib;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import com.tomcat.lib.interfaces.FullCallback;
import com.tomcat.lib.interfaces.ResultListener;
import com.tomcat.lib.interfaces.SimpleCallback;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission_group.*;
import static com.tomcat.lib.PermissionGroup.*;

/**
 * 创建者：   TomCat0916
 * 创建时间:  2019/7/28
 * 功能描述:  权限请求工具类
 */
@SuppressLint("InlinedApi")
public class PermissionUtils implements LifecycleObserver {

    private static final String TAG = "permissionFragment";

    @StringDef({CALENDAR, CAMERA, CONTACTS, LOCATION, MICROPHONE, PHONE, SENSORS, SMS, STORAGE,})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Permission {
    }

    public static String[] getPermissionsConstants(@Permission final String permission) {
        switch (permission) {
            case CALENDAR:
                return GROUP_CALENDAR;
            case CAMERA:
                return GROUP_CAMERA;
            case CONTACTS:
                return GROUP_CONTACTS;
            case LOCATION:
                return GROUP_LOCATION;
            case MICROPHONE:
                return GROUP_MICROPHONE;
            case PHONE:
                return GROUP_PHONE;
            case SENSORS:
                return GROUP_SENSORS;
            case SMS:
                return GROUP_SMS;
            case STORAGE:
                return GROUP_STORAGE;
        }
        return new String[]{permission};
    }

    private List<String> manifestPermissions;
    private static Builder builder;
    private PermissionFragment fragment;
    private SimpleCallback mSimpleCallback = null;
    private FullCallback mFullCallback = null;
    private List<String> mPermissions;
    private List<String> mPermissionsRequest = null;
    private List<String> mPermissionsGranted = null;
    private List<String> mPermissionsDenied = null;
    private List<String> mPermissionsRationale = null;
    private List<String> mPermissionsDeniedForever = null;

    public static Builder builder() {
        if (builder == null) {
            synchronized (PermissionUtils.class) {
                if (builder == null) {
                    builder = new Builder();
                }
            }
        }
        return builder;
    }

    public static class Builder {
        private List<String> permissions = null;
        private FullCallback fullCallback = null;
        private SimpleCallback simpleCallback = null;
        private boolean isRationale = false;
        private List<String> rationales = null;

        private Builder() {
        }

        private void onDestroy() {
            simpleCallback = null;
            fullCallback = null;
            if (permissions != null) {
                permissions.clear();
                permissions = null;
            }
        }

        public Builder permission(@Permission String... permissions) {
            this.permissions = new ArrayList<>(Arrays.asList(Objects.requireNonNull(permissions)));
            return this;
        }

        public Builder setResultListener(ResultListener listener) {
            if (listener != null) {
                if (listener instanceof FullCallback) {
                    fullCallback = (FullCallback) listener;
                } else if (listener instanceof SimpleCallback) {
                    simpleCallback = (SimpleCallback) listener;
                }
            }
            return this;
        }

        public Builder setRationale(@Nullable @Permission String... permissions) {
            if (permissions != null) {
                rationales = new ArrayList<>(Arrays.asList(permissions));
            }
            isRationale = true;
            return this;
        }

        private PermissionUtils build(PermissionUtils permissionUtils) {
            if (fullCallback != null) {
                permissionUtils.callback(fullCallback);
            } else if (simpleCallback != null) {
                permissionUtils.callback(simpleCallback);
            }
            if (isRationale){
                permissionUtils.setRationale(rationales);
            }
            return permissionUtils;
        }

        public PermissionUtils build(FragmentActivity activity) {
            PermissionUtils permissionUtils = new PermissionUtils(Objects.requireNonNull(activity),
                    Objects.requireNonNull(activity).getSupportFragmentManager(),
                    Objects.requireNonNull(permissions));
            return build(permissionUtils);
        }

        public PermissionUtils build(Fragment fragment) {
            PermissionUtils permissionUtils = new PermissionUtils(Objects.requireNonNull(fragment),
                    Objects.requireNonNull(fragment).getChildFragmentManager(),
                    Objects.requireNonNull(permissions));
            return build(permissionUtils);
        }
    }

    

    private PermissionUtils(@NonNull LifecycleOwner lifecycleOwner, FragmentManager manager, List<String> permissions) {
        lifecycleOwner.getLifecycle().addObserver(this);
        fragment = new FragmentUtils<PermissionFragment>(){
            @Override
            PermissionFragment getNewFragment() {
                return new PermissionFragment();
            }
        }.getLazySingleton(manager).get();
        manifestPermissions = new ArrayList<>(getPermissions(Objects.requireNonNull(fragment.getContext()).getPackageName()));
        mPermissions = new ArrayList<>();
        for (String permission : permissions) {
            for (String aPermission : getPermissionsConstants(permission)) {
                if (manifestPermissions.contains(aPermission)) {
                    mPermissions.add(aPermission);
                }
            }
        }
    }

    /**
     * 获取manifest中使用的权限
     */

    private List<String> getPermissions(final String packageName) {
        PackageManager pm = Objects.requireNonNull(fragment.getContext()).getPackageManager();
        ArrayList<String> result = new ArrayList<>();
        try {
            result.addAll(Arrays.asList(pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS).requestedPermissions));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断是否已获取所有权限
     */
    public boolean isGranted(final String... permissions) {
        for (String permission : permissions) {
            if (!isGranted(permission)) {
                return false;
            }
        }
        return true;
    }

    public boolean isGranted(final String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || PackageManager.PERMISSION_GRANTED ==
                Objects.requireNonNull(fragment.getActivity()).checkSelfPermission(permission);
    }

    /**
     * 开始请求
     */
    public PermissionUtils request() {
        Objects.requireNonNull(fragment).request(this);
        return this;
    }

    void startRequestPermission() {
        mPermissionsGranted = new ArrayList<>();
        mPermissionsRequest = new ArrayList<>();
        mPermissionsDenied = new ArrayList<>();
        mPermissionsDeniedForever = new ArrayList<>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mPermissionsGranted.addAll(mPermissions);
            requestCallback();
        } else {
            for (String permission : mPermissions) {
                if (isGranted(permission)) {
                    mPermissionsGranted.add(permission);
                    removeRationale(permission);
                } else {
                    mPermissionsRequest.add(permission);
                }
            }
            if (mPermissionsRequest.isEmpty()) {
                requestCallback();
            } else {
                requestPermission(mPermissionsRequest);
            }
        }
    }

    private void requestPermission(List<String> requestList) {
        mPermissionsDenied.clear();
        mPermissionsDeniedForever.clear();
        if (null != requestList) {
            int size = requestList.size();
            if (size > 0 && null != fragment) {
                fragment.requestPermissions(requestList.toArray(new String[size]), 1);
            }
        }
    }

    /**
     * 指定权限再次请求(强制同意或永久拒绝)
     *
     * @param permissions 指定重复请求的权限,为null全部重新请求
     */
    private void setRationale(@Nullable List<String> permissions) {
        mPermissionsRationale = new ArrayList<>();
        if (null == permissions) {
            for (String permission : mPermissions) {
                if (!isGranted(permission)) {
                    mPermissionsRationale.add(permission);
                }
            }
        } else {
            for (String permission : permissions) {
                for (String aPermission : getPermissionsConstants(permission)) {
                    if (manifestPermissions.contains(aPermission) && !isGranted(permission)) {
                        mPermissionsRationale.add(aPermission);
                    }
                }
            }
        }
    }

    /**
     * 再次请求
     */
    private void rationale() {
        if (null != mPermissionsRationale && !mPermissionsRationale.isEmpty()) {
            for (String permission : mPermissionsRationale) {
                if (mPermissionsDenied.contains(permission)) {
                    requestFullCallback();
                    requestPermission(mPermissionsRationale);
                    break;
                }
            }
        } else {
            requestCallback();
        }
    }

    private void removeRationale(String permission) {
        if (null != mPermissionsRationale && !mPermissionsRationale.isEmpty()) {
            mPermissionsRationale.remove(permission);
        }
    }

    /**
     * 获取权限请求状态
     */
    private void getPermissionsStatus() {
        for (String permission : mPermissionsRequest) {
            if (isGranted(permission)) {
                mPermissionsGranted.add(permission);
                removeRationale(permission);
            } else {
                mPermissionsDenied.add(permission);
                if (!fragment.shouldShowRequestPermissionRationale(permission)) {
                    mPermissionsDeniedForever.add(permission);
                    removeRationale(permission);
                }
            }
        }
        mPermissionsRequest.clear();
        mPermissionsRequest.addAll(mPermissionsDenied);
    }

    /**
     * 请求权限回调
     */
    private void requestCallback() {
        if (mSimpleCallback != null) {
            if (mPermissionsRequest.size() == 0 || mPermissions.size() == mPermissionsGranted.size()) {
                mSimpleCallback.onGranted();
            } else {
                if (!mPermissionsDenied.isEmpty()) {
                    mSimpleCallback.onDenied();
                }
            }
        }
        requestFullCallback();
        if (null != mFullCallback) {
            mFullCallback.onFinish();
        }
        mFullCallback = null;
        mSimpleCallback = null;
    }

    private void requestFullCallback() {
        if (mFullCallback != null) {
            if (mPermissionsRequest.size() == 0 || !mPermissionsGranted.isEmpty() ||
                    mPermissions.size() == mPermissionsGranted.size()) {
                mFullCallback.onGranted(mPermissionsGranted);
            }
            if (!mPermissionsDenied.isEmpty()) {
                mFullCallback.onDenied(mPermissionsDeniedForever, mPermissionsDenied);
            }
        }
    }

    /**
     * 请求结果 需要在{@link PermissionFragment#onRequestPermissionsResult(int, String[], int[])}方法中使用
     */
    void onRequestPermissionsResult() {
        getPermissionsStatus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rationale();
        } else {
            requestCallback();
        }
    }

    /**
     * 设置请求结果监听(用户同意/拒绝)
     */
    private void callback(final SimpleCallback callback) {
        mSimpleCallback = callback;
    }

    private void callback(final FullCallback callback) {
        mFullCallback = callback;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        if (builder != null) {
            builder.onDestroy();
        }
        clearList(manifestPermissions);
        clearList(mPermissions);
        clearList(mPermissionsRequest);
        clearList(mPermissionsGranted);
        clearList(mPermissionsDenied);
        clearList(mPermissionsRationale);
        clearList(mPermissionsDeniedForever);
        manifestPermissions = null;
        mPermissions = null;
        mPermissionsRequest = null;
        mPermissionsGranted = null;
        mPermissionsDenied = null;
        mPermissionsRationale = null;
        mPermissionsDeniedForever = null;
        mFullCallback = null;
        mSimpleCallback = null;
        if (fragment != null) {
            fragment.onDestroy();
        }
        Log.e(TAG,"onDestroy");
    }

    private void clearList(List list) {
        if (list != null) {
            list.clear();
        }
    }
}