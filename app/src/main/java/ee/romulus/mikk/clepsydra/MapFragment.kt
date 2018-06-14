package ee.romulus.mikk.clepsydra

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import ee.romulus.mikk.clepsydra.models.AppViewModel
import kotlinx.android.synthetic.main.fragment_map.*
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.annotations.MarkerOptions



class MapFragment : Fragment(), LocationEngineListener {
  private lateinit var vm: AppViewModel

  private var map: MapboxMap? = null
  private var locationPlugin: LocationLayerPlugin? = null
  private var locationEngine: LocationEngine? = null

  private var cp1marker: Marker? = null
  private var cp2marker: Marker? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_map, container, false)
  }

  @SuppressLint("MissingPermission")
  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    vm = ViewModelProviders.of(activity!!).get(AppViewModel::class.java)

    Mapbox.getInstance(context!!, getString(R.string.access_token))

    val mapViewInstance = mapView as MapView
    mapViewInstance.onCreate(savedInstanceState)
    mapViewInstance.getMapAsync {
      map = it

      map!!.uiSettings.isZoomControlsEnabled = false
      map!!.uiSettings.setAllGesturesEnabled(false)

      enableLocation()

      locationPlugin = LocationLayerPlugin(mapView, it, locationEngine)
      locationPlugin!!.renderMode = RenderMode.GPS
      locationPlugin!!.cameraMode = CameraMode.TRACKING_GPS
    }

    observe()
    bindHandlers()
  }

  private fun bindHandlers() {
    button1.setOnClickListener {
      vm.totalDistance.postValue(0f)
      vm.enabled.postValue(vm.enabled.value?.not())

      // get the location once
      vm.location.observe(this, object: Observer<Location> {
        override fun onChanged(location: Location?) {
          vm.startLocation.postValue(location)
          vm.location.removeObserver(this)
        }
      })


    }

    button2.setOnClickListener {
      if(cp1marker != null) {
        map!!.removeMarker(cp1marker!!)
      }
      cp1marker = map!!.addMarker(MarkerOptions()
          .position(LatLng(vm.location.value!!.latitude, vm.location.value!!.longitude))
          .title("CP1"))

      vm.clickCP(1)
    }
    button3.setOnClickListener {
      if(cp2marker != null) {
        map!!.removeMarker(cp2marker!!)
      }
      cp2marker = map!!.addMarker(MarkerOptions()
          .position(LatLng(vm.location.value!!.latitude, vm.location.value!!.longitude))
          .title("CP2"))
      vm.clickCP(2)
    }
  }

  private fun observe() {
    observeText()
    observeButtons()
  }

  private fun observeText() {
    vm.totalDistance.observe(this, Observer { travelledFromStart.text = formatDistance(it) })
    vm.cp1Distance.observe(this, Observer { travelledFromCP1.text = formatDistance(it) })
    vm.cp2Distance.observe(this, Observer { travelledFromCP2.text = formatDistance(it) })

    vm.location.observe(this, Observer {
      when {
        vm.cp1Location.value == null -> distanceFromCP1.text = String()
        else -> distanceFromCP1.text = formatDistance(it?.distanceTo(vm.cp1Location.value))
      }

      when {
        vm.cp2Location.value == null -> distanceFromCP1.text = String()
        else -> distanceFromCP2.text = formatDistance(it?.distanceTo(vm.cp2Location.value))
      }
    })
  }

  private fun formatDistance(distance: Float?): String {
    return when {
      distance != null -> when {
        distance > 1000 -> getString(R.string.distance_travelled_km, distance / 1000f)
        else -> getString(R.string.distance_travelled_m, distance)
      }
      else -> String()
    }
  }

  private fun observeButtons() {
    vm.enabled.observe(this, Observer {
      when {
          it!! -> {
            button1.text = getString(R.string.button_stop)
            button2.visibility = View.VISIBLE
            button3.visibility = View.VISIBLE
          }
          else -> {
            button1.text = getString(R.string.button_start)
            button2.visibility = View.GONE
            button3.visibility = View.GONE

            vm.startLocation.postValue(null)
            vm.cp1Location.postValue(null)
            vm.cp2Location.postValue(null)
          }
      }
    })
  }

  @SuppressLint("MissingPermission")
  private fun enableLocation() {
    val locationEngineProvider = LocationEngineProvider(activity)
    locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable()
    locationEngine!!.priority = LocationEnginePriority.HIGH_ACCURACY
    locationEngine!!.activate()
    locationEngine!!.requestLocationUpdates()

    val lastLocation = locationEngine!!.lastLocation
    if (lastLocation != null) {
      setCameraPosition(lastLocation)
    } else {
      locationEngine!!.addLocationEngineListener(this)
    }
  }

  private fun setCameraPosition(location: Location) {
    map?.animateCamera(
        CameraUpdateFactory.newLatLngZoom(
          LatLng(location.latitude, location.longitude),
          22.0
        )
    )
  }

  @SuppressLint("MissingPermission")
  override fun onStart() {
    super.onStart()
    locationEngine?.requestLocationUpdates()
    locationPlugin?.onStart()
    mapView.onStart()
  }

  override fun onResume() {
    super.onResume()
    mapView.onResume()
  }

  override fun onPause() {
    super.onPause()
    mapView.onPause()
  }

  override fun onStop() {
    super.onStop()
    locationEngine?.removeLocationUpdates()
    locationPlugin?.onStop()
    mapView.onStop()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    mapView.onSaveInstanceState(outState)
  }

  override fun onLowMemory() {
    super.onLowMemory()
    mapView.onLowMemory()
  }

  override fun onLocationChanged(location: Location?) {
    if (location != null) {
      setCameraPosition(location)
      locationEngine?.removeLocationEngineListener(this)
    }
  }

  @SuppressLint("MissingPermission")
  override fun onConnected() {
    locationEngine?.requestLocationUpdates()
  }
}
