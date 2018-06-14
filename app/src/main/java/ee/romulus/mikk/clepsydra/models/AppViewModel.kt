package ee.romulus.mikk.clepsydra.models

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.location.Location

class AppViewModel : ViewModel() {
  // for permissions
  val permissionGranted: MutableLiveData<Boolean> = MutableLiveData()
  val locationEnabled: MutableLiveData<Boolean> = MutableLiveData()
  val deviceReady = Transformations.map(permissionGranted) { it.and(locationEnabled.value!!) }!!

  val enabled: MutableLiveData<Boolean> = MutableLiveData()
  val location: MutableLiveData<Location> = MutableLiveData()
//  val lastLocation: MutableLiveData<Location> = MutableLiveData()

  val startLocation: MutableLiveData<Location> = MutableLiveData()
  val totalDistance: MutableLiveData<Float> = MutableLiveData()

  val cp1Location: MutableLiveData<Location> = MutableLiveData()
  val cp1Distance: MutableLiveData<Float> = MutableLiveData()

  val cp2Location: MutableLiveData<Location> = MutableLiveData()
  val cp2Distance: MutableLiveData<Float> = MutableLiveData()

  fun setPermissions(value: Boolean) {
    permissionGranted.postValue(value)
  }

  fun setLocationEnabled(value: Boolean) {
    locationEnabled.postValue(value)
  }

  fun addDistanceTravelled(from: Location?, to: Location?) {
    var distance = from?.distanceTo(to)
    distance?.let {
      totalDistance.postValue(totalDistance.value?.plus(it))
      if(cp1Location.value != null) {
        cp1Distance.postValue(cp1Distance.value?.plus(it))
      }

      if(cp2Location.value != null) {
        cp2Distance.postValue(cp2Distance.value?.plus(it))
      }
    }
  }

  fun clickCP(i: Int) {
    when(i) {
      1 -> {
        cp1Distance.postValue(0f)
        cp1Location.postValue(location.value)
      }
      2 -> {
        cp2Distance.postValue(0f)
        cp2Location.postValue(location.value)
      }
    }
  }

  init {
    enabled.value = false
    permissionGranted.value = false
    locationEnabled.value = false
    totalDistance.value = 0f
    cp1Distance.value = 0f
    cp2Distance.value = 0f
  }
}