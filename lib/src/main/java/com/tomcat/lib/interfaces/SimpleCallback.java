package com.tomcat.lib.interfaces;
/**
 *  创建者：   TomCat0916
 *  创建时间:  2019/7/28
 *  功能描述:  请求权限结果回调
 */
public interface SimpleCallback extends ResultListener {
    void onGranted();//全部同意

    void onDenied();//拒绝
}