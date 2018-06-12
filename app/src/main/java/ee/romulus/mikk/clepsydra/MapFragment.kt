package ee.romulus.mikk.clepsydra

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import ee.romulus.mikk.clepsydra.models.AppViewModel
import kotlinx.android.synthetic.main.fragment_map.*
import ee.romulus.mikk.clepsydra.R.id.mapView
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.mapboxsdk.Mapbox.getApplicationContext
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import ee.romulus.mikk.clepsydra.services.BoundLocationManager
import ee.romulus.mikk.clepsydra.R.id.mapView
import java.util.*

class MapFragment : Fragment(), LocationEngineListener {
  private lateinit var vm: AppViewModel

  private var map: MapboxMap? = null
  private var locationPlugin: LocationLayerPlugin? = null
  private var locationEngine: LocationEngine? = null

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
    mapViewInstance.getMapAsync({
      map = it

      map!!.uiSettings.isZoomControlsEnabled = false
      map!!.uiSettings.setAllGesturesEnabled(false)

      enableLocation()

      locationPlugin = LocationLayerPlugin(mapView, it, locationEngine)
      locationPlugin!!.renderMode = RenderMode.GPS
      locationPlugin!!.cameraMode = CameraMode.TRACKING_GPS
    })

    bindObservers()
  }

  private fun bindObservers() {
    vm.totalDistance.observe(this, Observer {
      val formatter = Formatter(StringBuilder(), Locale.UK)

      distance.text = formatter.format("Travelled: %.02f m", it).toString()
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
          27.0
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
