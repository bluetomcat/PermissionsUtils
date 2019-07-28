package com.tomcat.lib.interfaces;

import java.util.List;

/**
 *  创建者：   TomCat0916
 *  创建时间:  2019/7/28
 *  功能描述:  请求权限结果回调
 */
public interface FullCallback extends ResultListener{
    void onGranted(List<String> permissionsGranted);//部分同意

    void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied);

    void onFinish();
}