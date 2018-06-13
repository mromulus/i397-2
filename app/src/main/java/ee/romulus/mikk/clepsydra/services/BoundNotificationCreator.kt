package ee.romulus.mikk.clepsydra.services

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat


object BoundNotificationCreator {
    fun bindNotificationManagerIn(lifecycleOwner: LifecycleOwner, context: Context) {
        BoundNotificationManager(lifecycleOwner, context)
    }

    internal class BoundNotificationManager(lifecycleOwner: LifecycleOwner, private val mContext: Context) : LifecycleObserver {
        private val notificationManager: NotificationManagerCompat
        private val notificationId = 654321
        private val channelID = "ClepsydraChannel"

        init {
            lifecycleOwner.lifecycle.addObserver(this)
            notificationManager = NotificationManagerCompat.from(mContext)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun addLocationListener() {
            notificationManager.cancel(notificationId)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun removeLocationListener() {
            val mBuilder = NotificationCompat.Builder(mContext, channelID)
                    .setContentTitle("test")
                    .setContentText("test2")
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(notificationId, mBuilder.build());
        }
    }
}


