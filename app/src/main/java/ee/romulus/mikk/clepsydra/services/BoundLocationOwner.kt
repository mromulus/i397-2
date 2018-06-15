package ee.romulus.mikk.clepsydra.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.arch.lifecycle.*
import ee.romulus.mikk.clepsydra.MainActivity
import android.content.Intent
import android.content.Context
import android.location.Location
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import ee.romulus.mikk.clepsydra.R
import ee.romulus.mikk.clepsydra.models.AppViewModel
import android.widget.RemoteViews

internal class BoundLocationOwner(lifecycleOwner: LifecycleOwner, private val context: Context, private val model: AppViewModel) : LifecycleObserver {

  private lateinit var notificationManager: NotificationManager
  private lateinit var mBuilder: NotificationCompat.Builder
  private val notificationId = 654321
  val channelID = "ClepsydraChannel"
  private var observer = Observer<Location> { updateNotification() }

  init {
    lifecycleOwner.lifecycle.addObserver(this)
    initChannel()
    initBuilder()
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  fun addLocationListener() {
    notificationManager.cancel(notificationId)
    model.location.removeObserver(observer)
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  fun removeLocationListener() {
    model.location.observeForever(observer)
    notificationManager.notify(notificationId, mBuilder.build())
  }

  private fun initChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      notificationManager = context.getSystemService(NotificationManager::class.java)
    }

    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_LOW
      val channel = NotificationChannel(channelID, "channel name", importance)
      channel.description = "channel description"
      channel.setSound(null, null)
      // Register the channel with the system; you can't change the importance
      // or other notification behaviors after this
      notificationManager = context.getSystemService(NotificationManager::class.java)
      notificationManager.createNotificationChannel(channel)
    }
  }

  private fun initBuilder() {
    mBuilder = NotificationCompat.Builder(context, channelID)
        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
//        .setContentTitle("Distance travelled | distance from")
//        .setContentText("")
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        .setCustomContentView(generateContentView())
        .setSound(null)
        .setContentIntent(
            PendingIntent.getActivity(
                context,
                20,
                Intent(context, MainActivity::class.java),
                0
            )
        )
  }

  private fun generateContentView(): RemoteViews? {
    val contentView = RemoteViews(context.packageName, R.layout.notification)
    contentView.setTextViewText(R.id.notText, String.format(
        "Start: %s  CP1: %s | %s  CP2: %s | %s",
        formatDistance(model.totalDistance.value),
        formatDistance(model.cp1Distance.value),
        formatDistance(model.cp1Location.value?.distanceTo(model.location.value)),
        formatDistance(model.cp2Distance.value),
        formatDistance(model.cp2Location.value?.distanceTo(model.location.value))
    ))

    contentView.setOnClickPendingIntent(R.id.notcp1, PendingIntent.getBroadcast(context, 1237, Intent("ee.romulus.mikk.clepsydra.cp") ,0))
    contentView.setOnClickPendingIntent(R.id.notcp2, PendingIntent.getBroadcast(context, 8734, Intent("ee.romulus.mikk.clepsydra.cp2") ,0))

    return contentView
  }


  private fun updateNotification() {
    mBuilder.setCustomContentView(generateContentView())
    notificationManager.notify(notificationId, mBuilder.build())
  }

  private fun formatDistance(distance: Float?): String {
    return when {
      distance != null -> when {
        distance > 1000 -> context.getString(R.string.distance_travelled_km, distance / 1000f)
        else -> context.getString(R.string.distance_travelled_m, distance)
      }
      else -> String()
    }
  }
}
