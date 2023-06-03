package com.mama.components;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.mama.R;
import com.mama.utils.AlarmUtils;
import com.mama.utils.SensorTool;
import com.mama.utils.MediaPlayerHelper;

public class MyForegroundService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "MyForegroundServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 在这里执行后台任务
        doMyJob();
        // 创建前台服务通知
        createNotificationChannel();
        Notification notification = buildNotification();

        // 将服务设置为前台服务
        startForeground(NOTIFICATION_ID, notification);

        // 如果服务被杀死，系统将尝试重新启动服务
        return START_STICKY;
    }



    private void doMyJob() {
        SensorTool.SetShakeListener(this, new SensorTool.ShakeListener() {
            @Override
            public void onShake() {
                if (!hasRecentAlarm()) {
                    setAlarmIfNeeded();
                }else {
                   MediaPlayerHelper.getInstance().playRawAudio(getApplicationContext(), R.raw.exit_alarm);
                }
            }
        });
    }

    private boolean hasRecentAlarm() {
        // 从SharedPreferences中获取上次设置闹钟的时间戳
        SharedPreferences sharedPreferences = getSharedPreferences("AlarmPrefs2", Context.MODE_PRIVATE);
        long lastAlarmTime = sharedPreferences.getLong("LastAlarmTime", 0);

        // 获取当前时间
        long currentTime = System.currentTimeMillis();

        // 判断上次设置闹钟的时间是否在最近的10分钟内
        return (currentTime - lastAlarmTime) <= (10 * 60 * 1000); // 10分钟的毫秒数
    }

    private void setAlarmIfNeeded() {
        if (!hasRecentAlarm()) {
            // 未设置过最近的闹钟，设置一个2小时后的闹钟
            long twoHoursLater = System.currentTimeMillis() + (2 * 60 * 60 * 1000); // 2小时的毫秒数

            // TODO: 在这里设置闹钟，例如使用AlarmManager或其他闹钟相关的机制
            // 这里只是一个示例，具体的设置闹钟逻辑需要根据你的应用和需求进行实现
            AlarmUtils.setAlarm(getApplicationContext());
            MediaPlayerHelper.getInstance().playRawAudio(getApplicationContext(), R.raw.voice_2_hour);

            // 更新上次设置闹钟的时间戳
            SharedPreferences sharedPreferences = getSharedPreferences("AlarmPrefs2", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("LastAlarmTime", twoHoursLater);
            editor.apply();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // 在服务销毁时执行清理操作
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("血糖闹钟")
                .setContentText("为了更准时提醒请不要关闭")
                .setSmallIcon(R.drawable.ic_clock);
        return builder.build();
    }
}
