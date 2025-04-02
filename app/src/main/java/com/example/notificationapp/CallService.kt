package com.example.notificationapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.Person

class CallService : Service() {

    private  val channelId = "ongoing_call";
    private  val notificationId = 10220

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channel = NotificationChannel(
                channelId,
                "Ongoing Call",
                NotificationManager.IMPORTANCE_LOW // disable popup notification
                );
            val  manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.action === "STOP_CALL_ACTION"){
            stopSelf()
            return  START_NOT_STICKY
        }

        val notification = buildOngoingCallNotification();
        startForeground(notificationId, notification, FOREGROUND_SERVICE_TYPE_PHONE_CALL)
        return START_STICKY
    }

    private val caller = Person.Builder().setName("Sangam").build()

    private  fun buildOngoingCallNotification(): Notification {
        return  NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setContentTitle("Ongoing Call")
            .setContentText("Tap to open the app.")
            .setOngoing(true)
            .setContentIntent(createContentIntent())
            .setStyle(NotificationCompat.CallStyle.forOngoingCall(caller, createIntent()))
            .build()
    }

    private fun createIntent(): PendingIntent {
        val intent = Intent(this, CallService::class.java).apply {
            action = "STOP_CALL_ACTION"
        }
        return PendingIntent.getService(this, 0 , intent, PendingIntent.FLAG_IMMUTABLE)
    }
    private fun createContentIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_IMMUTABLE)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return  null;
    }

}

