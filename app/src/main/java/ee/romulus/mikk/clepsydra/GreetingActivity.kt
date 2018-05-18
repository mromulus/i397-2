package ee.romulus.mikk.clepsydra

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.mapbox.mapboxsdk.Mapbox
import kotlinx.android.synthetic.main.activity_main.*
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import ee.romulus.mikk.clepsydra.models.MapViewModel

class GreetingActivity : AppCompatActivity() {
  private val REQUEST_LOCATION_PERMISSION = 10

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    checkPermissions()

//    Mapbox.getInstance(applicationContext, getString(R.string.access_token))
//      val model = ViewModelProviders.of(this).get(MapViewModel::class.java)
//    mapView.onCreate(savedInstanceState)

//        mapView.getMapAsync({})
  }

  private fun checkPermissions() {
    if(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) && hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
    } else {
      permissions_not_enabled.visibility = View.VISIBLE
      request_permissions.visibility = View.VISIBLE
    }
  }

  private fun hasPermission(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
  }

  fun requestPermissions(view: View?) {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
        REQUEST_LOCATION_PERMISSION
    )
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    Log.d("results", grantResults.toString())
  }

//    override fun onStart() {
//        super.onStart()
//        mapView.onStart()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        mapView.onResume()
//
//    }
//    override fun onPause() {
//        super.onPause()
//        mapView.onPause()
//    }
//    override fun onStop() {
//        super.onStop()
//        mapView.onStop()
//
//    }
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        mapView.onSaveInstanceState(outState)
//
//    }
//    override fun onLowMemory() {
//        super.onLowMemory()
//        mapView.onLowMemory()
//
//    }
//    override fun onDestroy() {
//        super.onDestroy()
//        mapView.onDestroy()
//    }
}
