package com.tomcat.lib;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 * 创建者：   TomCat0916
 * 创建时间:  2019/7/28
 * 功能描述：  权限请求Fragment
 */
public class PermissionFragment extends Fragment {

    private PermissionUtils utils;

    public void  request(PermissionUtils utils){
        this.utils = utils;
        utils.startRequestPermission();
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Objects.requireNonNull(utils).onRequestPermissionsResult();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
