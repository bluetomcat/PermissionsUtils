package com.tomcat.permissionsrequest

import android.Manifest.permission_group.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.tomcat.lib.PermissionUtils
import com.tomcat.lib.interfaces.FullCallback

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val request = PermissionUtils.builder()
            .permission(STORAGE, CALENDAR, PHONE)
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
            .setRationale(PHONE)
            .build(this)
            .request()
    }
}
