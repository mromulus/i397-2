package ee.romulus.mikk.clepsydra

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import ee.romulus.mikk.clepsydra.models.AppViewModel
import ee.romulus.mikk.clepsydra.services.BoundLocationManager
import ee.romulus.mikk.clepsydra.services.BoundNotificationCreator

class MainActivity : FragmentActivity(), LocationListener {
  private lateinit var model: AppViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    model = ViewModelProviders.of(this).get(AppViewModel::class.java)

    BoundNotificationCreator.bindNotificationManagerIn(this, applicationContext)
    insertGreeting()
    observe()
  }

  private fun insertGreeting() {
    val beginTransaction = supportFragmentManager.beginTransaction()
    beginTransaction.add(R.id.fragment_container, GreetingFragment())
    beginTransaction.commit()
  }

  private fun observe() {
    observeModel()
  }

  private fun observeModel() {
    model.deviceReady.observe(this, Observer {
      if(it!!) {
        BoundLocationManager.bindLocationListenerIn(this, this, applicationContext)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, MapFragment())
        transaction.commit()
      }
    })
  }

  override fun onLocationChanged(location: Location?) {
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
