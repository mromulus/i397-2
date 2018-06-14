package ee.romulus.mikk.clepsydra

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import ee.romulus.mikk.clepsydra.models.AppViewModel
import ee.romulus.mikk.clepsydra.services.BoundLocationOwner
import ee.romulus.mikk.clepsydra.services.LocationService

class MainActivity : FragmentActivity(), LocationListener {
  private lateinit var model: AppViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    model = ViewModelProviders.of(this).get(AppViewModel::class.java)

    ContextCompat.startForegroundService(applicationContext, Intent(
        applicationContext,
        LocationService::class.java
    ))

    BoundLocationOwner(this, applicationContext, model)

    insertGreeting()
    observe()
    registerReceivers()
  }

  private fun registerReceivers() {
    registerReceiver(object : BroadcastReceiver() {
      override fun onReceive(contxt: Context?, intent: Intent?) {
        model.clickCP(1)
      }
    }, IntentFilter("ee.romulus.mikk.clepsydra.cp"))
    registerReceiver(object : BroadcastReceiver() {
      override fun onReceive(contxt: Context?, intent: Intent?) {
        model.clickCP(2)
      }
    }, IntentFilter("ee.romulus.mikk.clepsydra.cp2"))
  }

  private fun insertGreeting() {
    val beginTransaction = supportFragmentManager.beginTransaction()
    beginTransaction.add(R.id.fragment_container, GreetingFragment())
    beginTransaction.commit()
  }

  private fun observe() {
    observeModel()
  }

  @SuppressLint("MissingPermission")
  private fun observeModel() {
    model.deviceReady.observe(this, Observer {
      if(it!!) {
        val mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 2f, this)

        val lastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (lastLocation != null) this.onLocationChanged(lastLocation)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, MapFragment())
        transaction.commit()
      }
    })
  }

  override fun onLocationChanged(location: Location?) {
    Log.d("onloc", location.toString())
    if(model.enabled.value!!) {
      model.addDistanceTravelled(model.location.value, location)
    }

    model.location.postValue(location)
  }

  override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
  }

  override fun onProviderEnabled(provider: String?) {
  }

  override fun onProviderDisabled(provider: String?) {
  }


}
