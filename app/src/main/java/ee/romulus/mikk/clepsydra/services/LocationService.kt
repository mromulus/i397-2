package ee.romulus.mikk.clepsydra.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.os.Build



class LocationService: Service() {
  override fun onBind(intent: Intent?): IBinder {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun onCreate() {
    super.onCreate()
    Log.d("service", "create")

    if (Build.VERSION.SDK_INT >= 26) {
      val channel = NotificationChannel(BoundLocationOwner::channelID.toString(),
          "Channel human readable title",
          NotificationManager.IMPORTANCE_DEFAULT)

      (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)

      val notification = NotificationCompat.Builder(this, BoundLocationOwner::channelID.toString())
          .setContentTitle("")
          .setContentText("").build()

      startForeground(78103, notification)
    }
  }
}
