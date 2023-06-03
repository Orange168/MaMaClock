package com.mama.ui;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.mama.R;
import com.mama.utils.ScreenTool;

import com.mama.components.MyForegroundService;
import com.mama.components.SensorService;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        ContextCompat.startForegroundService(this, serviceIntent);


        //检查是否开通通知权限
        if (!isForegroundServicePermissionGranted()) {
            openForegroundServicePermissionSettings();
        }
    }


    private boolean isForegroundServicePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return getSystemService(NotificationManager.class).areNotificationsEnabled();
        } else {
            String packageName = getPackageName();
            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = getApplicationInfo();
            String op = AppOpsManager.OPSTR_GET_USAGE_STATS;
            int mode = appOps.checkOpNoThrow(op, appInfo.uid, packageName);
            return mode == AppOpsManager.MODE_ALLOWED;
        }
    }

    private void openForegroundServicePermissionSettings() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        } else {
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.fromParts("package", getPackageName(), null));
        }
        startActivity(intent);
    }


    // 启动服务
    public void startService(View v) {
        SensorService.GetInstance().start(this, SensorService.class);
    }

    // 暂时停止服务，服务逻辑会在应用退出后自行重启，并一直运行
    public void stopService(View v) {
        SensorService.GetInstance().stop();
    }


    // 熄灭屏幕
    public void lightOff(View v) {
        ScreenTool.lightOff(this);
    }


    // 键盘锁屏
    public void keyLockOff(View v) {
        ScreenTool.keyLockOff(this);
    }

}
