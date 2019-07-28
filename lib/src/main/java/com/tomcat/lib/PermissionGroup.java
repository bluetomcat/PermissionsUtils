package com.tomcat.lib;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;

/**
 *  创建者：   TomCat0916
 *  创建时间:  2019/7/28
 *  功能描述:  权限组常量
 */
interface PermissionGroup {
    
    String[] GROUP_CALENDAR = {
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
    };
    
    String[] GROUP_CAMERA = {
            Manifest.permission.CAMERA
    };
    
    String[] GROUP_CONTACTS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS
    };
    
    String[] GROUP_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    
    String[] GROUP_MICROPHONE = {
            Manifest.permission.RECORD_AUDIO
    };
    
    String[] GROUP_PHONE = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.ADD_VOICEMAIL,
            Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS
    };

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    String[] GROUP_SENSORS = {
            Manifest.permission.BODY_SENSORS
    };

    String[] GROUP_SMS = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS,
    };
    
    String[] GROUP_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
}
