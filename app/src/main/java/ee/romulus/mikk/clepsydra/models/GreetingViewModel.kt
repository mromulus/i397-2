package ee.romulus.mikk.clepsydra.models

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel

class GreetingViewModel : ViewModel() {
  val permissionGranted: MutableLiveData<Boolean> = MutableLiveData()
  val locationEnabled: MutableLiveData<Boolean> = MutableLiveData()

  val deviceReady = Transformations.map(permissionGranted, { it.and(locationEnabled.value!!) })!!

  fun setPermissions(value: Boolean) {
    permissionGranted.postValue(value)
  }

  fun setLocationEnabled(value: Boolean) {
    locationEnabled.postValue(value)
  }

  init {
    permissionGranted.value = false
    locationEnabled.value = false
  }
}