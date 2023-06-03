package com.mama.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mama.utils.AlarmUtils;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 在接收到闹钟广播时触发的操作
        // 这里可以添加你想要执行的操作，例如显示通知、播放声音等
        String message = "闹钟时间到了！";
        AlarmUtils.showNotification(context, message);
    }
}
