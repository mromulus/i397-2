package ee.romulus.mikk.clepsydra.services

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log

object BoundLocationManager {
  fun bindLocationListenerIn(lifecycleOwner: LifecycleOwner,
                             listener: LocationListener, context: Context) {
    BoundLocationListener(lifecycleOwner, listener, context)
  }

  internal class BoundLocationListener(lifecycleOwner: LifecycleOwner,
                                       private val mListener: LocationListener, private val mContext: Context) : LifecycleObserver {
    private var mLocationManager: LocationManager? = null

    init {
      lifecycleOwner.lifecycle.addObserver(this)
    }

    @SuppressLint("MissingPermission")
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun addLocationListener() {
      // Note: Use the Fused Location Provider from Google Play Services instead.
      // https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderApi

      mLocationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
      mLocationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 2f, mListener)
      Log.d("BoundLocationMgr", "Listener added")

      // Force an update with the last location, if available.
      val lastLocation = mLocationManager!!.getLastKnownLocation(
          LocationManager.GPS_PROVIDER)
      if (lastLocation != null) {
        mListener.onLocationChanged(lastLocation)
      }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun removeLocationListener() {
      if (mLocationManager == null) {
        return
      }
      mLocationManager!!.removeUpdates(mListener)
      mLocationManager = null
      Log.d("BoundLocationMgr", "Listener removed")
    }
  }
}
