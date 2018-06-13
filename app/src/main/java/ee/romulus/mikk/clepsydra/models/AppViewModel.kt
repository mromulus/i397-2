package ee.romulus.mikk.clepsydra.models

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.location.Location

class AppViewModel : ViewModel() {
  // for permissions
  val permissionGranted: MutableLiveData<Boolean> = MutableLiveData()
  val locationEnabled: MutableLiveData<Boolean> = MutableLiveData()
  val deviceReady = Transformations.map(permissionGranted, { it.and(locationEnabled.value!!) })!!

  val enabled: MutableLiveData<Boolean> = MutableLiveData()
  val location: MutableLiveData<Location> = MutableLiveData()
  val lastLocation: MutableLiveData<Location> = MutableLiveData()

  val totalDistance: MutableLiveData<Float> = MutableLiveData()

  fun setPermissions(value: Boolean) {
    permissionGranted.postValue(value)
  }

  fun setLocationEnabled(value: Boolean) {
    locationEnabled.postValue(value)
  }

  fun addDistanceTravelled(from: Location?, to: Location?) {
    var distance = from?.distanceTo(to)
    distance?.let { totalDistance.postValue(totalDistance.value?.plus(it)) }
  }

  init {
    enabled.value = false
    permissionGranted.value = false
    locationEnabled.value = false
    totalDistance.value = 0f
  }
}